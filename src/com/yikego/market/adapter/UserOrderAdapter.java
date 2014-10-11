package com.yikego.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.OrderList;
import com.yikego.market.R;

import java.util.List;

/**
 * Created by wanglinglong on 14-10-11.
 */
public class UserOrderAdapter extends BaseAdapter{
    private static final String TAG = "UserOrderAdapter";
    private List<OrderList> orderLists;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ViewHolder viewHolder;

    public UserOrderAdapter(Context context) {
        mContext = context;
        mLayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderLists.size();
    }

    @Override
    public Object getItem(int position) {
        return orderLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            viewHolder  = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.user_order_list_item, null);
            viewHolder.mOrderStatus = (TextView) convertView.findViewById(R.id.user_order_item_status);
            viewHolder.mOrderMoney = (TextView) convertView.findViewById(R.id.user_order_item_money);
            viewHolder.mOrderId = (TextView) convertView.findViewById(R.id.user_order_item_id);
            viewHolder.mOrderTime = (TextView) convertView.findViewById(R.id.user_order_item_time);
            viewHolder.mOrderName = (TextView) convertView.findViewById(R.id.user_order_item_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (orderLists != null && orderLists.size()> 0){
            OrderList orderList = orderLists.get(position);
            viewHolder.mOrderStatus.setText(String.valueOf(orderList.getOrderStatus()));
            viewHolder.mOrderMoney.setText(String.valueOf(orderList.getTotalFee()));
            viewHolder.mOrderId.setText(String.valueOf(orderList.getOrderNo()));
            viewHolder.mOrderTime.setText(orderList.getCreateTime());
            viewHolder.mOrderName.setText(orderList.getSubject());
        }
        return convertView;
    }

    private class ViewHolder{
        TextView mOrderStatus;
        TextView mOrderId;
        TextView mOrderMoney;
        TextView mOrderTime;
        TextView mOrderName;
    }

    public void setOrderLists(List<OrderList> orderLists){
        this.orderLists = orderLists;
    }
}
