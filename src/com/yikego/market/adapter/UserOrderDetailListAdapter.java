package com.yikego.market.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.UserOrderDetailInfo;
import com.yikego.market.R;
import com.yikego.market.utils.CachedThumbnails;

import java.util.List;

/**
 * Created by wll on 14-10-20.
 */
public class UserOrderDetailListAdapter extends ArrayAdapter<UserOrderDetailInfo>{
    private static final String TAG= "UserOrderDetailListAdapter";
    private LayoutInflater mLayoutInflater;
    private Drawable mThumb = null;
    private Context mContext;
    private ViewHolder viewHolder;

    public UserOrderDetailListAdapter(Context context, List<UserOrderDetailInfo> objects) {
        // TODO Auto-generated constructor stub
        super(context, 0, objects);
        mContext = context;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserOrderDetailInfo orderInfo = null;
        Log.v("asd", "position =" + position);
        if (position >= 0) {
            orderInfo = getItem(position);
        }
        if (convertView == null) {
            // convertView = mLayoutInflater.inflate(R.layout.app_list_item,
            // null);
            convertView = mLayoutInflater.inflate(R.layout.order_goods_list_item,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) convertView
                    .findViewById(R.id.order_goods_listitem_name);
            viewHolder.mDetail = (TextView) convertView
                    .findViewById(R.id.order_goods_listitem_detail);
            viewHolder.mOrderCout = (TextView) convertView
                    .findViewById(R.id.order_goods_item_count);
            viewHolder.mThumbnail = (ImageView) convertView
                    .findViewById(R.id.order_goods_listitem_thumb);
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
        if (orderInfo == null ){
            convertView.setVisibility(View.INVISIBLE);
        }
        if (orderInfo != null) {
            viewHolder.mName.setText(orderInfo.getName());
            viewHolder.mDetail.setText(orderInfo.getSku());
            viewHolder.mOrderCout.setText("X " + orderInfo.getCount());
            Log.d(TAG, "productID : " + orderInfo.getProductId());
            if (orderInfo.getProductId()!=0)
                viewHolder.mThumbnail.setBackgroundDrawable(CachedThumbnails.getGoodsThumbnail(
                        mContext, orderInfo.getProductId()));
        }
        return convertView;
    }

    class ViewHolder {
        ImageView mThumbnail;
        // TextView mAuthor;
        TextView mName;
        TextView mDetail;
        TextView mOrderCout;
    }
}
