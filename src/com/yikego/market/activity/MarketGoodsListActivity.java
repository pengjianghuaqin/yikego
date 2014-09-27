package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.MarketGoodsInfoListData;
import com.yikego.android.rom.sdk.bean.PostProductType;
import com.yikego.android.rom.sdk.bean.ProductListInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

	public MarketGoodsListActivity() {
		isEnd = false;
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);
		initView();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter,// The AdapterView where the
													// click happened
			View view,// The view within the AdapterView that was clicked
			int position,// The position of the view in the adapter
			long arg3// The row id of the item that was clicked
	) {
		Log.v(TAG, "position =" + position);
		Log.v(TAG, "mGoodsListAdapter =" + mGoodsListAdapter.getCount());
		if (position < mGoodsListAdapter.getCount()) {
			Intent intent = new Intent(this, MarketGoodsDetailActivity.class);
			// intent.putExtra("type",1);
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
			}

			return convertView;
		}

		class ViewHolder {
			ImageView mThumbnail;
			// TextView mAuthor;
			TextView mName;
			TextView mDetail;
			TextView mPrice;
			RelativeLayout mListItem;
		}
	}
}