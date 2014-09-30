package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.MarketGoodsInfoListData;
import com.yikego.android.rom.sdk.bean.PostProductType;
import com.yikego.android.rom.sdk.bean.ProductListInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
import com.yikego.market.model.GoodsData;
import com.yikego.market.model.MarketData;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MarketGoodsListActivity extends ListActivity implements
		OnItemClickListener {
	private ListView mListView;
	private Context mContext;
	private GoodsListAdapter mGoodsListAdapter;
	private String TAG = "MarketGoodsListActivity";
	private int productTypeId;
	private int nowPage;
	private int pageCount;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_GOODS_LIST = 1;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private boolean isEnd;
	private ImageView mShoppingcar;
	private TextView mShoppingcarIndex;
	private ImageView img;

	public MarketGoodsListActivity() {
		nowPage = 1;
		pageCount = 25;
		isEnd = false;
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);
		mThemeService = ThemeService.getServiceInstance(mContext);
		productTypeId = getIntent().getIntExtra("productTypeId", 0);
		img = (ImageView)findViewById(R.id.img);
		img.setVisibility(View.INVISIBLE);
		initView();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter,// The AdapterView where the
													// click happened
			View view,// The view within the AdapterView that was clicked
			int position,// The position of the view in the adapter
			long arg3// The row id of the item that was clicked
	) {
		if (position < mGoodsListAdapter.getCount()) {
			GoodsData goodsData = mGoodsListAdapter.getItem(position);
			Intent intent = new Intent(this, MarketGoodsDetailActivity.class);
			intent.putExtra("goodsData", goodsData);
			startActivity(intent);
		}
	}

	private void initView() {
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbar_title);
		actionbarTitle.setText(R.string.text_title_goods_detail);
		mListView = getListView();
		// mListView.setScrollbarFadingEnabled(false); //disable
		// mListView.setVisibility(View.GONE);

		ArrayList<GoodsData> goodstList = new ArrayList<GoodsData>();

		mGoodsListAdapter = new GoodsListAdapter(mContext, goodstList);

		mListView.setAdapter(mGoodsListAdapter);
		mListView.setOnItemClickListener(this);
		mShoppingcar = (ImageView) findViewById(R.id.img_shopping_car);
		mShoppingcarIndex = (TextView) findViewById(R.id.goods_index);

		initHandler();
		GetGoodsList();
	}

	private void GetGoodsList() {
		// TODO Auto-generated method stub
		if (isEnd) {
			return;
		}
		Request request = new Request(0, Constant.TYPE_GET_GOODS_LIST_INFO);
		PostProductType mPostProductType = new PostProductType();
		mPostProductType.nowPage = nowPage;
		mPostProductType.productTypeId = productTypeId;
		mPostProductType.pageCount = pageCount;
		request.setData(mPostProductType);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_GOODS_LIST,
							data);
					mHandler.sendMessage(msg);
				} else {
					Request request = (Request) observable;
					if (request.getStatus() == Constant.STATUS_ERROR) {
						mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
					}
				}
			}
		});
		mCurrentRequest = request;
		mThemeService.getStoreList(request);
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case ACTION_GOODS_LIST:
					ProductListInfo productListInfo = (ProductListInfo) msg.obj;
					if (productListInfo != null) {
						ArrayList<GoodsData> marketGoodsList = new ArrayList<GoodsData>();
						for (int i = 0; i < productListInfo.productList.size(); i++) {
							marketGoodsList.add(new GoodsData(
									productListInfo.productList.get(i)));
						}

						if (mGoodsListAdapter == null) {
							mGoodsListAdapter = new GoodsListAdapter(mContext,
									marketGoodsList);
							mListView.setAdapter(mGoodsListAdapter);
						} else {
							for (int i = 0; i < marketGoodsList.size(); i++) {
								mGoodsListAdapter.add(marketGoodsList.get(i));
							}
							mGoodsListAdapter.notifyDataSetChanged();
							nowPage += 1;
							if (productListInfo.nowPage == productListInfo.totalCount) {
								isEnd = true;
							}
						}

						mListView.setAdapter(mGoodsListAdapter);
					}
					mListView.setVisibility(View.VISIBLE);

					break;

				default:
					break;
				}
			}
		};
	}

	private class GoodsListAdapter extends ArrayAdapter<GoodsData> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;
		private Drawable mAnimationThumb = null;
		private ViewHolder mAnimationViewHolder = null;
		private Bitmap mSelectedItemBitmap;
		private Bitmap mBitmap;

		public GoodsListAdapter(Context context, List<GoodsData> objects) {
			// TODO Auto-generated constructor stub
			super(context, 0, objects);
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GoodsData goodsInfo = null;
			Log.v("asd", "position =" + position);
			if (position >= 0) {
				goodsInfo = getItem(position);
			}
			if (convertView == null) {
				// convertView = mLayoutInflater.inflate(R.layout.app_list_item,
				// null);
				convertView = mLayoutInflater.inflate(R.layout.goods_list_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mName = (TextView) convertView
						.findViewById(R.id.goods_listitem_name);
				viewHolder.mDetail = (TextView) convertView
						.findViewById(R.id.goods_listitem_detail);
				viewHolder.mPrice = (TextView) convertView
						.findViewById(R.id.goods_listitem_price);
				viewHolder.mThumbnail = (ImageView) convertView
						.findViewById(R.id.goods_listitem_thumb);
				viewHolder.mShoppingCar = (ImageView) convertView
						.findViewById(R.id.btn_shopping_car);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// convertView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent(mContext,
			// MarketGoodsDetailActivity.class);
			// mContext.startActivity(intent);
			// }
			// });
			if (goodsInfo != null) {
				viewHolder.mName.setText(goodsInfo.getGoodsName());
				viewHolder.mDetail.setText(goodsInfo.getGoodsDetail());
				viewHolder.mPrice.setText("￥     " + goodsInfo.getGoodsPrice());
				GoodEXInfo exInfo = new GoodEXInfo();
				exInfo.listItem = convertView;
				exInfo.icon = mContext.getResources().getDrawable(
						R.drawable.ic_item_thumb);
				viewHolder.mShoppingCar.setTag(exInfo);
				viewHolder.mShoppingCar
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								IconAnimation(v);
							}
						});
			}

			return convertView;
		}

		class GoodEXInfo {
			Drawable icon;
			View listItem;
		}

		private void IconAnimation(View v) {
			mAnimationThumb = ((GoodEXInfo) v.getTag()).icon;
			mAnimationViewHolder = (ViewHolder) ((GoodEXInfo) v.getTag()).listItem
					.getTag();
			mAnimationViewHolder.mThumbnail.setDrawingCacheEnabled(true);
			mSelectedItemBitmap = Bitmap
					.createBitmap(mAnimationViewHolder.mThumbnail
							.getDrawingCache());
			mAnimationViewHolder.mThumbnail.setDrawingCacheEnabled(false);
			mBitmap = toConformBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.animation_icon),
					mSelectedItemBitmap);
			img.setPadding(0, 0, 0, 0);
			img.setBackgroundDrawable(new BitmapDrawable(mBitmap));
			img.bringToFront();

			int left = ((GoodEXInfo) v.getTag()).listItem.getLeft();
			int bottom = ((GoodEXInfo) v.getTag()).listItem.getBottom();
			int right = ((GoodEXInfo) v.getTag()).listItem.getRight();
			
			int toleft = mShoppingcar.getLeft();
			int tobottom = mShoppingcar.getBottom();
			int toright = mShoppingcar.getRight();
			int totop = mShoppingcar.getTop();
			Log.v(TAG, "left ="+left);
			Log.v(TAG, "bottom ="+bottom);
			Log.v(TAG, "toleft ="+toleft);
			Log.v(TAG, "left ="+left);
			Log.v(TAG, "toright ="+toright);
			Log.v(TAG, "tobottom ="+tobottom);
			Log.v(TAG, "totop ="+totop);
			AnimationSet set = new AnimationSet(false);
			ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.2f, 1.0f,
					0.2f, 0.5f, 0.5f);
			set.addAnimation(scaleAnim);
			TranslateAnimation translateAnimationX = new TranslateAnimation(
					left, toright, 0, 0);
			translateAnimationX.setInterpolator(new LinearInterpolator());
			translateAnimationX.setRepeatCount(0);
			TranslateAnimation translateAnimationY = new TranslateAnimation(0,
					0, bottom, totop);
			translateAnimationY.setInterpolator(new AccelerateInterpolator());
			translateAnimationY.setRepeatCount(0);
			set.addAnimation(translateAnimationX);
			set.addAnimation(translateAnimationY);
			set.setDuration(500);

			img.startAnimation(set);
		}

		private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
			if (background == null) {
				return null;
			}
			int bgWidth = background.getWidth();
			int bgHeight = background.getHeight();
			// int fgWidth = foreground.getWidth();
			// int fgHeight = foreground.getHeight();
			Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight,
					Config.ARGB_8888);
			Canvas cv = new Canvas(newbmp);
			// draw bg into
			cv.drawBitmap(background, 0, 0, null);
			// draw fg into
			cv.drawBitmap(foreground, 0, 0, null);
			// save all clip
			cv.save(Canvas.ALL_SAVE_FLAG);
			// store
			cv.restore();
			return newbmp;
		}

		class ViewHolder {
			ImageView mThumbnail;
			// TextView mAuthor;
			TextView mName;
			TextView mDetail;
			TextView mPrice;
			ImageView mShoppingCar;
			RelativeLayout mListItem;
		}
	}
}