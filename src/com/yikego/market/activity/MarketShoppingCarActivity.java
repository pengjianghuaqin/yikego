package com.yikego.market.activity;

import java.util.List;

import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.market.R;
import com.yikego.market.model.GoodsData;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class MarketShoppingCarActivity extends ListActivity implements
		OnItemClickListener {
	private Context mContext;
	private ListView mListView;
	private OrderListAdapter mAdapter;
	public MarketShoppingCarActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_car);
		initView();
	}

	private void initView() {
		Button settlement = (Button) findViewById(R.id.btn_settlement);
		settlement.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MarketSubmitOrderActivity.class);
				startActivity(intent);
			}
			
		});
		mListView = getListView();
		mAdapter = new OrderListAdapter(mContext,MarketDetailActivity.orderDetailList);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}
	
	private class OrderListAdapter extends ArrayAdapter<OrderProductInfo> {
		private ViewHolder viewHolder = null;
		private LayoutInflater mLayoutInflater;

		public OrderListAdapter(Context context, List<OrderProductInfo> objects) {
			// TODO Auto-generated constructor stub
			super(context, 0, objects);
			mContext = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OrderProductInfo orderInfo = null;
			Log.v("asd", "position =" + position);
			if (position >= 0) {
				orderInfo = getItem(position);
			}
			if (convertView == null) {
				// convertView = mLayoutInflater.inflate(R.layout.app_list_item,
				// null);
				convertView = mLayoutInflater.inflate(R.layout.order_list_item,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mName = (TextView) convertView
						.findViewById(R.id.order_listitem_name);
				viewHolder.mDetail = (TextView) convertView
						.findViewById(R.id.order_listitem_detail);
				viewHolder.mPrice = (TextView) convertView
						.findViewById(R.id.order_listitem_price);
				viewHolder.mThumbnail = (ImageView) convertView
						.findViewById(R.id.order_listitem_icon);
				viewHolder.mOrderFlag = (ImageView) convertView
						.findViewById(R.id.order_flag);
				viewHolder.mOrderCout = (TextView) convertView
						.findViewById(R.id.order_goods_index); 
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
			if (orderInfo != null) {
				viewHolder.mName.setText(orderInfo.name);
				viewHolder.mDetail.setText(orderInfo.name);
				viewHolder.mPrice.setText("£¤   " + orderInfo.price);
				viewHolder.mOrderCout.setText(""+orderInfo.count);
				Drawable orderFlag;
				if(orderInfo.count==0){
					orderFlag = mContext.getResources().getDrawable(R.drawable.img_flag_unchecked);
				}else{
					orderFlag = mContext.getResources().getDrawable(R.drawable.img_flag_selected);
				}
				viewHolder.mOrderFlag.setImageDrawable(orderFlag);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView mThumbnail;
			// TextView mAuthor;
			TextView mName;
			TextView mDetail;
			TextView mPrice;
			ImageView mOrderFlag;
			TextView mOrderCout;
		}
	}
}