package com.yikego.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yikego.android.rom.sdk.bean.LocationHistoryList;
import com.yikego.android.rom.sdk.bean.OrderList;
import com.yikego.market.R;

import java.util.List;

/**
 * Created by pengjiang on 14-10-11.
 */
public class LocHistoryAdapter extends BaseAdapter{
    private static final String TAG = "LocHistoryAdapter";
    private List<LocationHistoryList> mLocHistoryLists;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ViewHolder viewHolder;

    public LocHistoryAdapter(Context context) {
        mContext = context;
        mLayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mLocHistoryLists!=null)
            return mLocHistoryLists.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mLocHistoryLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            viewHolder  = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.loc_history_list_item, parent ,false);
            viewHolder.mStreetName = (TextView) convertView.findViewById(R.id.loc_history_street_name);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mLocHistoryLists != null && mLocHistoryLists.size()> 0){
            LocationHistoryList orderList = mLocHistoryLists.get(position);
            viewHolder.mStreetName.setText(String.valueOf(orderList.getStreetName()));

        }
        return convertView;
    }

    private class ViewHolder{
        TextView mStreetName;

    }

    public void setLocationHistoryLists(List<LocationHistoryList> locHistoryLists){
        this.mLocHistoryLists = locHistoryLists;
    }
}
