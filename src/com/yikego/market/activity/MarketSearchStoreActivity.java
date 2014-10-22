package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.widget.ImageView;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yikego.android.rom.sdk.bean.MessageRecord;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
import com.yikego.market.fragment.SlidingMenuFragment;
import com.yikego.market.model.Latitude;
import com.yikego.market.model.MarketData;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.yikego.market.yikegoApplication;

public class MarketSearchStoreActivity extends Activity implements
		AdapterView.OnItemClickListener {
	private ListView mListView;
	private MarketListAdapter mMarketListAdapter;
	private ImageView mMenuButton;
	private Context mContext;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private String TAG = "MarketBrowser";
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_USER_LOCAL_INFO = 1;
	private Handler mHandler;
	private boolean isEnd;
	private AbsListView.OnScrollListener mScrollListener;
	private Latitude mLatitude;
	public static List<Latitude> storeLatitude;
	private String mName;
	private EditText mSearchName;

	public MarketSearchStoreActivity() {
		Log.v(TAG, "MarketBrowser");
		isEnd = false;
		mContext = this;
		mLatitude = new Latitude();
		mLatitude.lat = 31.159488f;
		mLatitude.lng = 121.579398f;
		storeLatitude = new ArrayList<Latitude>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
        yikegoApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_market_search_store);
		mThemeService = ThemeService.getServiceInstance(mContext);
		initHandler();
		initListener();
		initView();
	}

	private void initView() {
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		TextView map = (TextView) findViewById(R.id.text_define);
		map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mName = mSearchName.getText().toString();
				PostUserLocalInfo();
			}
		});
		mSearchName = (EditText) findViewById(R.id.search_edittext);
		
	}

	private void initListener() {
		// TODO Auto-generated method stub
		mScrollListener = new AbsListView.OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				return;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					int position = 0;
					// for (int i = 0; i < counts; i++) {
					// View localView = view.getChildAt(i);
					//
					// ViewHolder viewHolder = (ViewHolder) localView.getTag();
					// if (viewHolder != null) {
					// //
					// mAppListAdapter.initBtnStatus(viewHolder,(Application2)viewHolder.mButton.getTag());
					// int id = (int) mAppListAdapter.getItemId(position);
					// Drawable drawable = getThumbnail(position, id);
					// // drawable.setCallback(null);
					// viewHolder.mThumbnail.setImageDrawable(drawable);
					// }
					// }

					// as list scrolled to end, send request to get above data
					if (isEnd) {
						PostUserLocalInfo();
					}
					break;
				case SCROLL_STATE_TOUCH_SCROLL:
				case SCROLL_STATE_FLING:
					// if (!bBusy) {
					// clearPendingThumbRequest();
					// bBusy = true;
					// }
				default:
					break;
				}
			}
		};
	}

	private void PostUserLocalInfo() {
		// TODO Auto-generated method stub

		Request request = new Request(0, Constant.TYPE_POST_USER_LOCAL_INFO);
		// Object[] params = new Object[2];
		PostUserLocationInfo postUserLocationInfo = new PostUserLocationInfo();
		postUserLocationInfo.distance = 25.0f;
		postUserLocationInfo.lat = mLatitude.lat;
		postUserLocationInfo.lng = mLatitude.lng;
		postUserLocationInfo.nowPage = 1;
		postUserLocationInfo.pageCount = 25;
		postUserLocationInfo.name = mName;
		request.setData(postUserLocationInfo);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler,
							ACTION_USER_LOCAL_INFO, data);
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
				case ACTION_USER_LOCAL_INFO:
					PaginationStoreListInfo paginationStoreListInfo = (PaginationStoreListInfo) msg.obj;

					if (paginationStoreListInfo != null) {
						for (int i = 0; i < paginationStoreListInfo.storelist
								.size(); i++) {
							Latitude tmpLatitude = new Latitude();
							tmpLatitude.lat = paginationStoreListInfo.storelist
									.get(i).lat;
							tmpLatitude.lng = paginationStoreListInfo.storelist
									.get(i).lng;
							storeLatitude.add(tmpLatitude);
						}
                        if(mMarketListAdapter!=null){
                            mMarketListAdapter.clear();
                        }
                        mMarketListAdapter = new MarketListAdapter(
                                mContext, paginationStoreListInfo.storelist);
/*						if (mMarketListAdapter == null) {
							mMarketListAdapter = new MarketListAdapter(
									mContext, paginationStoreListInfo.storelist);
							mListView.setAdapter(mMarketListAdapter);
						} else {
							for (int i = 0; i < paginationStoreListInfo.storelist
									.size(); i++) {
								mMarketListAdapter
										.add(paginationStoreListInfo.storelist
												.get(i));
							}
							mMarketListAdapter.notifyDataSetChanged();
							if (paginationStoreListInfo.nowPage >= paginationStoreListInfo.pageCount) {
								isEnd = true;
							}
						}*/
						mListView.setAdapter(mMarketListAdapter);
					}
					mListView.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		StoreInfo storeInfo = mMarketListAdapter.getItem(position);
		Intent intent = new Intent(mContext, MarketDetailActivity.class);
		intent.putExtra("storeInfo", storeInfo);
		mContext.startActivity(intent);
	}
}