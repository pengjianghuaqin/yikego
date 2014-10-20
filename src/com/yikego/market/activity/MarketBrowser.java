package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.widget.ImageView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
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
import com.yikego.market.model.Image2;
import com.yikego.market.model.Latitude;
import com.yikego.market.model.MarketData;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

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
import android.widget.Toast;

public class MarketBrowser extends SlidingFragmentActivity implements
		AdapterView.OnItemClickListener,OnGetGeoCoderResultListener {

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private ListView mListView;
	private MarketListAdapter mMarketListAdapter;
	private ImageView mMenuButton;
	private Context mContext;
	private Request mCurrentRequest;
    private TextView mTitleSpineer;
	private ThemeService mThemeService;
	private String TAG = "MarketBrowser";
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_USER_LOCAL_INFO = 1;
	private static final int ACTION_MARKET_ICON = 2;
	private Handler mHandler;
	private boolean isEnd;
	private AbsListView.OnScrollListener mScrollListener;
	private Latitude mLatitude;
	public static List<Latitude> storeLatitude;
	private PaginationStoreListInfo mPaginationStoreListInfo = null;
	private Hashtable<Integer, Boolean> mIconStatusMap;
	private boolean bBusy;

	public MarketBrowser() {
		Log.v(TAG, "MarketBrowser");
		isEnd = false;
		mContext = this;
		mLatitude = new Latitude();
		mLatitude.lat = 31.159488f;
		mLatitude.lng = 121.579398f;
		storeLatitude = new ArrayList<Latitude>();
		bBusy = false;
		mIconStatusMap = new Hashtable<Integer, Boolean>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_market_browser);
		mThemeService = ThemeService.getServiceInstance(mContext);
        mLatitude = new Latitude();
        if (getIntent()!=null){
            mLatitude.lat = (float) getIntent().getDoubleExtra("lat", 0);
            mLatitude.lng = (float) getIntent().getDoubleExtra("lng", 0);
        }else {
            mLatitude.lat = 31.159488f;
            mLatitude.lng = 121.579398f;
        }
        Log.d(TAG, "lat : "+mLatitude.lat);
        Log.d(TAG, "lng : " + mLatitude.lng);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        LatLng ptCenter = new LatLng((mLatitude.lat),(mLatitude.lng));
        mSearch.setOnGetGeoCodeResultListener(this);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
		initHandler();
		initListener();
		initView();
		initSlidingMenu();

		// userLogin();
	}

	private void initSlidingMenu() {
		// 设置滑动菜单打开后的视图界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();

		// 设置滑动菜单的属性值
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.25f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}

	private void initView() {
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
        mTitleSpineer = (TextView) findViewById(R.id.title_spineer);
        mMenuButton = (ImageView) findViewById(R.id.btn_menu);
		mMenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggle();
			}
		});
		TextView map = (TextView)findViewById(R.id.title_map);
		map.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,LocationActivity.class);
                if (mPaginationStoreListInfo!=null){
                    intent.putExtra("StoreInfo", mPaginationStoreListInfo);
                }
                startActivity(intent);
            }
        });
		EditText seacch = (EditText)findViewById(R.id.search_edittext);
		seacch.setOnClickListener(new View.OnClickListener() {
			@Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MarketSearchStoreActivity.class);
                startActivity(intent);
            }
        });
		
        PostUserLocalInfo();
	}

	public Drawable getThumbnail(int position, int id) {
		// TODO Auto-generated method stub
		Log.v(TAG, "getThumbnail");
		boolean bThumbExists = mIconStatusMap.containsKey(Integer
				.valueOf(position));
		if (bBusy && !bThumbExists) {
			return CachedThumbnails.getDefaultIcon(this);
		}

		Drawable drawable = CachedThumbnails.getThumbnail(this, id);
		if (drawable == null) {
			boolean bThumbCached = false;
			if (bThumbExists) {
				bThumbCached = mIconStatusMap.get(Integer.valueOf(position))
						.booleanValue();
			}
			if (bThumbExists && !bThumbCached) {
				// cause thumb record existed
				// do not sent thumb request again, just return default icon
				return CachedThumbnails.getDefaultIcon(this);
			} else {
				// cause thumb record not existed
				// or thumb not cached yet,
				// set cached flag as false, and send thumb request
				mIconStatusMap.put(Integer.valueOf(position),
						Boolean.valueOf(false));
				addThumbnailRequest(position, id);
				return CachedThumbnails.getDefaultIcon(this);
			}
		} else {
			// cause thumb has been cached
			// set cached flag as true
			mIconStatusMap
					.put(Integer.valueOf(position), Boolean.valueOf(true));
			return drawable;
		}
	}

	private void addThumbnailRequest(int position, int id) {
		String imgUrl = null;if(mMarketListAdapter.getItem(position).pictures!=null&&mMarketListAdapter.getItem(position).pictures.size()>0){
			int index = mMarketListAdapter.getItem(position).pictures.get(0).picPath.lastIndexOf(".");
			imgUrl = mMarketListAdapter.getItem(position).pictures.get(0).picPath.substring(0, index);
			imgUrl += "_small"+mMarketListAdapter.getItem(position).pictures.get(0).picPath.substring(index);
		}
		
		Log.v(TAG, "imgUrl2 ="+imgUrl);
		if (imgUrl != null) {
			Request request = new Request(0L, Constant.TYPE_APP_ICON);
			Object[] params = new Object[2];

			params[0] = Integer.valueOf(id);
			params[1] = imgUrl;
			request.setData(params);
			request.addObserver(new Observer() {

				@Override
				public void update(Observable observable, Object data) {
					// TODO Auto-generated method stub
					if (data != null) {
						Message msg = Message.obtain(mHandler,
								ACTION_MARKET_ICON, data);
						mHandler.sendMessage(msg);
					}
				}
			});
			mCurrentRequest = request;
			mThemeService.getAppIcon(request);
		}

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
					bBusy = false;
					int start = view.getFirstVisiblePosition();
					int counts = view.getChildCount();
					int position = 0;

					for (int i = 0; i < counts; i++) {
						position = start + i;

						// if
						// (!mIconStatusMap.containsKey(Integer.valueOf(position)))
						// {
						ViewHolder viewHolder = (ViewHolder) view.getChildAt(i)
								.getTag();
						if (viewHolder != null) {
							// mAppListAdapter.initBtnStatus(viewHolder,(Application2)viewHolder.mButton.getTag());
							int id = (int) mMarketListAdapter
									.getItemId(position);
							Drawable drawable = getThumbnail(position, id);
							viewHolder.mThumbnail.setBackgroundDrawable(drawable);
						}
					}

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
        postUserLocationInfo.name = "";
		postUserLocationInfo.nowPage = 1;
		postUserLocationInfo.pageCount = 25;
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
                    mPaginationStoreListInfo = paginationStoreListInfo;

					if (paginationStoreListInfo != null&&paginationStoreListInfo.storelist!=null) {
						for (int i = 0; i < paginationStoreListInfo.storelist
								.size(); i++) {
							Latitude tmpLatitude = new Latitude();
							tmpLatitude.lat = paginationStoreListInfo.storelist
									.get(i).lat;
							tmpLatitude.lng = paginationStoreListInfo.storelist
									.get(i).lng;
							storeLatitude.add(tmpLatitude);
						}
						if (mMarketListAdapter == null) {
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
						}
						mListView.setAdapter(mMarketListAdapter);
					}
					mListView.setVisibility(View.VISIBLE);
					break;
				case ACTION_MARKET_ICON:
					Image2 icInfo = (Image2) msg.obj;
					Log.v(TAG, "icInfo =" + icInfo.mAppIcon);
					if (icInfo.mAppIcon != null) {
						CachedThumbnails.cacheThumbnail(mContext, icInfo._id,
								icInfo.mAppIcon);

						ImageView imageView = (ImageView) mListView
								.findViewWithTag(String.valueOf(icInfo._id));
						if (imageView != null) {
							imageView.setBackgroundDrawable(icInfo.mAppIcon);
						}
						if (mMarketListAdapter != null) {
							mMarketListAdapter.notifyDataSetChanged();
						}
					}
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

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MarketBrowser.this, "定位失败", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Toast.makeText(MarketBrowser.this, reverseGeoCodeResult.getAddress(),
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onGetReverseGeoCodeResult : " + reverseGeoCodeResult.getAddress());
        Log.d(TAG, "onGetReverseGeoCodeResult : " + reverseGeoCodeResult.getBusinessCircle());
        mTitleSpineer.setText(reverseGeoCodeResult.getAddress());
    }
}
