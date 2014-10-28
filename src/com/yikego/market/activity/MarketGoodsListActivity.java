package com.yikego.market.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.MarketGoodsInfoListData;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.PostProductType;
import com.yikego.android.rom.sdk.bean.ProductListInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
import com.yikego.android.rom.sdk.bean.StoreInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketListAdapter.ViewHolder;
import com.yikego.market.model.GoodsData;
import com.yikego.market.model.Image2;
import com.yikego.market.model.MarketData;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import android.view.Display;
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

import com.yikego.market.yikegoApplication;

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
	private static final int ACTION_GOODS_ICON = 2;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private boolean isEnd;
	private ImageView mShoppingcar;
	private TextView mShoppingcarIndex;
	private ImageView img;
	private Display mDisplay;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private Hashtable<Integer, Boolean> mIconStatusMap;
	private boolean bBusy;
	private ProgressDialog mProgressDialog;

	public MarketGoodsListActivity() {
		nowPage = 1;
		pageCount = 25;
		isEnd = false;
		mContext = this;
		bBusy = false;
		mIconStatusMap = new Hashtable<Integer, Boolean>();
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(mContext);
		setContentView(R.layout.activity_goods_list);
		mThemeService = ThemeService.getServiceInstance(mContext);
		yikegoApplication.getInstance().addActivity(this);
		productTypeId = getIntent().getIntExtra("productTypeId", 0);
		img = (ImageView) findViewById(R.id.img);
		img.setVisibility(View.INVISIBLE);
		initView();
	}

	public Drawable getThumbnail(int position, int id) {
		// TODO Auto-generated method stub
		boolean bThumbExists = mIconStatusMap.containsKey(Integer
				.valueOf(position));
		if (bBusy && !bThumbExists) {
			return CachedThumbnails.getGoodsDefaultIcon(this);
		}

		Drawable drawable = CachedThumbnails.getGoodsThumbnail(this, id);
		if (drawable == null) {
			boolean bThumbCached = false;
			if (bThumbExists) {
				bThumbCached = mIconStatusMap.get(Integer.valueOf(position))
						.booleanValue();
			}
			if (bThumbExists && !bThumbCached) {
				// cause thumb record existed
				// do not sent thumb request again, just return default icon
				return CachedThumbnails.getGoodsDefaultIcon(this);
			} else {
				// cause thumb record not existed
				// or thumb not cached yet,
				// set cached flag as false, and send thumb request
				mIconStatusMap.put(Integer.valueOf(position),
						Boolean.valueOf(false));
				addThumbnailRequest(position, id);
				return CachedThumbnails.getGoodsDefaultIcon(this);
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
		String imgUrl = null;
		if (mGoodsListAdapter.getItem(position).getGoodsIconUrl() != null
				&& mGoodsListAdapter.getItem(position).getGoodsIconUrl().size() > 0) {
			int index = mGoodsListAdapter.getItem(position).getGoodsIconUrl()
					.get(0).lastIndexOf(".");
			imgUrl = mGoodsListAdapter.getItem(position).getGoodsIconUrl()
					.get(0).substring(0, index);
			imgUrl += "_small"
					+ mGoodsListAdapter.getItem(position).getGoodsIconUrl()
							.get(0).substring(index);
		}
		Log.v(TAG, "imgUrl =" + imgUrl);
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
								ACTION_GOODS_ICON, data);
						mHandler.sendMessage(msg);
					}
				}
			});
			mCurrentRequest = request;
			mThemeService.getAppIcon(request);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		mShoppingcarIndex.setText(getGoodsCout());
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
		actionbarTitle.setText(R.string.goods_list);
		mListView = getListView();

		ImageView mBack = (ImageView) findViewById(R.id.market_detail_back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		// mListView.setScrollbarFadingEnabled(false); //disable
		// mListView.setVisibility(View.GONE);

		ArrayList<GoodsData> goodstList = new ArrayList<GoodsData>();

		mGoodsListAdapter = new GoodsListAdapter(mContext, goodstList);
		mDisplay = getWindowManager().getDefaultDisplay();
		screenWidth = mDisplay.getWidth();
		screenHeight = mDisplay.getHeight();
		mListView.setAdapter(mGoodsListAdapter);
		mListView.setOnItemClickListener(this);
		mShoppingcar = (ImageView) findViewById(R.id.img_shopping_car);
		mShoppingcar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,
						MarketShoppingCarActivity.class);
				startActivity(intent);
			}

		});

		TextView mSearch = (TextView) findViewById(R.id.market_search);
		mSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MarketGoodsListActivity.this,
						SearchGoodActivity.class);
				intent.putExtra("storeId", MarketDetailActivity.storeID);
				startActivity(intent);
			}
		});

		mShoppingcarIndex = (TextView) findViewById(R.id.goods_index);
		initHandler();
		GetGoodsList();
	}

	private String getGoodsCout() {
		int cout = 0;
		if (MarketDetailActivity.orderDetailList != null
				&& MarketDetailActivity.orderDetailList.size() > 0) {
			for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
				cout += MarketDetailActivity.orderDetailList.get(i).count;
			}
			return cout + "";
		} else {
			return null;
		}

	}

	private void GetGoodsList() {
		// TODO Auto-generated method stub
		if (isEnd) {
			return;
		}
		mProgressDialog.setMessage(getResources()
				.getText(R.string.text_loading));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
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
				case ACTION_GOODS_ICON:
					Image2 icInfo = (Image2) msg.obj;
					Log.v(TAG, "icInfo =" + icInfo.mAppIcon);
					if (icInfo.mAppIcon != null) {
						CachedThumbnails.cacheGoodsIconThumbnail(mContext,
								icInfo._id, icInfo.mAppIcon);

						ImageView imageView = (ImageView) mListView
								.findViewWithTag(String.valueOf(icInfo._id));
						if (imageView != null) {
							imageView.setBackgroundDrawable(icInfo.mAppIcon);
						}
						if (mGoodsListAdapter != null) {
							mGoodsListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
				}
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
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
		private Drawable mThumb = null;

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
				viewHolder.mShoppingCar = (RelativeLayout) convertView
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
				exInfo.price = goodsInfo.getGoodsPrice();
				exInfo.productName = goodsInfo.getGoodsName();
				exInfo.productId = goodsInfo.goodsId;
				viewHolder.mShoppingCar.setTag(exInfo);
				viewHolder.mShoppingCar
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								GoodEXInfo exInfo = (GoodEXInfo) v.getTag();
								Log.v(TAG, "exInfo =" + exInfo.productName);
								OrderProductInfo orderProductInfo = new OrderProductInfo();
								orderProductInfo.productId = exInfo.productId;
								orderProductInfo.price = exInfo.price;
								orderProductInfo.count = 1;
								orderProductInfo.name = exInfo.productName;
								int i = 0;
								if (MarketDetailActivity.orderDetailList != null
										&& MarketDetailActivity.orderDetailList
												.size() > 0) {
									for (i = 0; i < MarketDetailActivity.orderDetailList
											.size(); i++) {
										if (orderProductInfo.productId == MarketDetailActivity.orderDetailList
												.get(i).productId) {
											MarketDetailActivity.orderDetailList
													.get(i).count++;
											break;
										}
									}
									if (i == MarketDetailActivity.orderDetailList
											.size()) {
										MarketDetailActivity.orderDetailList
												.add(orderProductInfo);
									}

								} else {
									MarketDetailActivity.orderDetailList
											.add(orderProductInfo);
								}
								IconAnimation(v);
								mShoppingcarIndex.setText(getGoodsCout());
							}
						});

				mThumb = ((MarketGoodsListActivity) mContext).getThumbnail(
						position, goodsInfo.goodsId);

				viewHolder.mThumbnail.setBackgroundDrawable(mThumb);
			}

			return convertView;
		}

		class GoodEXInfo {
			Drawable icon;
			View listItem;
			int productId;
			String productName;
			float price;
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
			int tobottom = mShoppingcar.getBottom();
			int toright = mShoppingcar.getRight();
			AnimationSet set = new AnimationSet(false);
			ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.2f, 1.0f,
					0.2f, 0.5f, 0.5f);
			set.addAnimation(scaleAnim);
			TranslateAnimation translateAnimationX = new TranslateAnimation(
					left, screenWidth - toright, 0, 0);
			translateAnimationX.setInterpolator(new LinearInterpolator());
			translateAnimationX.setRepeatCount(0);
			TranslateAnimation translateAnimationY = new TranslateAnimation(0,
					0, bottom, screenHeight - tobottom - 70);
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
			RelativeLayout mShoppingCar;
			RelativeLayout mListItem;
		}
	}
}
