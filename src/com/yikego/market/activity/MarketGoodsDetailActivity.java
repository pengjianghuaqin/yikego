package com.yikego.market.activity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.model.EnterShoppingCarDialog;
import com.yikego.market.model.GoodsData;
import com.yikego.market.model.Image2;
import com.yikego.market.model.Latitude;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.yikego.market.yikegoApplication;

public class MarketGoodsDetailActivity extends Activity {
	private final String TAG = "MarketGoodsDetailActivity";
	private Context mContext;
	private GoodsData mGoodsData;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_GOODS_IMG = 1;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private Gallery imgGallery;

	public MarketGoodsDetailActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("MarketGoodsDetailActivity", "onCreate");
		mThemeService = ThemeService.getServiceInstance(mContext);
        yikegoApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_goods_detail);
		mGoodsData = (GoodsData) getIntent().getSerializableExtra("goodsData");
		initHandler();
		initView();
	}

	private void initView() {
		Button confirm = (Button) findViewById(R.id.btn_confirm);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OrderProductInfo orderProductInfo = new OrderProductInfo();
				orderProductInfo.productId = mGoodsData.goodsId;
				orderProductInfo.price = mGoodsData.getGoodsPrice();
				orderProductInfo.count = 1;
				orderProductInfo.name = mGoodsData.getGoodsName();
				if (MarketDetailActivity.orderDetailList != null
						&& MarketDetailActivity.orderDetailList.size() > 0) {
					int i = 0;
					for (i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
						if (orderProductInfo.productId == MarketDetailActivity.orderDetailList
								.get(i).productId) {
							MarketDetailActivity.orderDetailList.get(i).count++;
							break;
						}
					}
					if (i == MarketDetailActivity.orderDetailList.size()) {
						MarketDetailActivity.orderDetailList
								.add(orderProductInfo);
					}
				} else {
					MarketDetailActivity.orderDetailList.add(orderProductInfo);
				}
				Intent intent = new Intent(mContext,
						MarketShoppingCarActivity.class);
				mContext.startActivity(intent);
			}
		});
		ImageView back = (ImageView) findViewById(R.id.goods_detail_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		TextView distance = (TextView) findViewById(R.id.market_area_distance);
		distance.setText("距离" + MarketDetailActivity.storeInfo.aboutDistance
				+ "米");
		TextView price = (TextView) findViewById(R.id.goods_detail_price);
		price.setText("￥  " + mGoodsData.getGoodsPrice());
		TextView detail = (TextView) findViewById(R.id.goods_detail);
		detail.setText(mGoodsData.getGoodsDetail());
		TextView marketName = (TextView) findViewById(R.id.market_name);
		marketName.setText(MarketDetailActivity.storeInfo.name);
		TextView marketSpend = (TextView) findViewById(R.id.market_detail_spend);
		marketSpend.setText(MarketDetailActivity.storeInfo.sendPrice+"元起送");
		RelativeLayout enterShoppingCar = (RelativeLayout) findViewById(R.id.goods_detail_area);
		TextView marketWorkTime = (TextView) findViewById(R.id.market_detail_work_time);
		String openTime,closeTime;
		if (MarketDetailActivity.storeInfo.openMinute < 10) {
			openTime = "0" + MarketDetailActivity.storeInfo.openMinute;
		} else {
			openTime = "" + MarketDetailActivity.storeInfo.openMinute;
		}
		if (MarketDetailActivity.storeInfo.closeMinute < 10) {
			closeTime = "0" + MarketDetailActivity.storeInfo.closeMinute;
		} else {
			closeTime = "" + MarketDetailActivity.storeInfo.closeMinute;
		}
		marketWorkTime.setText("营业时间" + MarketDetailActivity.storeInfo.openHour + ":"
				+ openTime + "-" + MarketDetailActivity.storeInfo.closeHour + ":" + closeTime);TextView marketAddress = (TextView) findViewById(R.id.market_detail_address);
		enterShoppingCar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EnterShoppingCarDialog dialog = new EnterShoppingCarDialog(
						mContext, mGoodsData);
				dialog.show();
			}
		});
		imgGallery = (Gallery) findViewById(R.id.goods_detail_img);
		Drawable tmpImg = getResources().getDrawable(R.drawable.img_bg_goods_detail);
		Log.v(TAG, "MarketGoodsDetailActivity tmpImg="+tmpImg);
		ArrayList<Drawable> icInfo = new ArrayList<Drawable>();
		icInfo.add(tmpImg);
		imgGallery.setAdapter(new GoodsImgListAdapter(mContext,
				icInfo));
		addThumbnailRequest();
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_GOODS_IMG:
					ArrayList<Drawable> icInfo = (ArrayList<Drawable>) msg.obj;
					if (icInfo != null) {
						imgGallery.setAdapter(new GoodsImgListAdapter(mContext,
								icInfo));
						imgGallery
								.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
									@Override
									public void onItemSelected(
											AdapterView<?> parent, View v,
											int position, long id) {
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										// 这里不做响应
									}
								});
					}
					break;
				default:
					break;
				}
			}
		};
	}

	private void addThumbnailRequest() {
		ArrayList<String> imgUrlList = new ArrayList<String>();
		String imgUrl;
		if (mGoodsData.getGoodsIconUrl() != null
				&& mGoodsData.getGoodsIconUrl().size() > 0) {
			for (int position = 0; position < mGoodsData.getGoodsIconUrl()
					.size(); position++) {
				int index = mGoodsData.getGoodsIconUrl().get(position)
						.lastIndexOf(".");
				imgUrl = mGoodsData.getGoodsIconUrl().get(position)
						.substring(0, index);
				imgUrl += "_large"
						+ mGoodsData.getGoodsIconUrl().get(position)
								.substring(index);
				imgUrlList.add(imgUrl);
			}
		}
		Log.v(TAG, "MarketGoodsDetailActivity imgUrlList="+imgUrlList+",imgUrlList.size="+imgUrlList.size());
		if (imgUrlList != null && imgUrlList.size() > 0) {
			Request request = new Request(0L, Constant.TYPE_GOODS_IMG_LIST);
			Object[] params = new Object[1];
			params[0] = imgUrlList;
			request.setData(params);
			request.addObserver(new Observer() {

				@Override
				public void update(Observable observable, Object data) {
					// TODO Auto-generated method stub
					if (data != null) {
						Message msg = Message.obtain(mHandler,
								ACTION_GOODS_IMG, data);
						mHandler.sendMessage(msg);
					}
				}
			});
			mCurrentRequest = request;
			mThemeService.getAppIcon(request);
		}

	}

	public class GoodsImgListAdapter extends ArrayAdapter<Drawable> {
		ArrayList<Drawable> mImgList;

		public GoodsImgListAdapter(Context context, ArrayList<Drawable> objects) {
			super(context, 0, objects);
			// TODO Auto-generated constructor stub
			mContext = context;
			mImgList = objects;
		}

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView iv = new ImageView(mContext);
			iv.setBackgroundDrawable(mImgList.get(position));// 给ImageView设置资源
			iv.setScaleType(ImageView.ScaleType.FIT_XY);// 设置对齐方式
			iv.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			return iv;
		}
	}
}
