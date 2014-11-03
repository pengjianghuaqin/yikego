package com.yikego.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.PointList;
import com.yikego.market.R;

import java.util.List;

/**
 * Created by wll on 14-10-12.
 */
public class UserPointAdapter extends BaseAdapter{
    private static final String TAG = "UerPointAdapter";
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ViewHolder viewHolder;

    public void setPointLists(List<PointList> pointLists) {
        this.pointLists = pointLists;
    }

    private List<PointList> pointLists;

    public UserPointAdapter(Context mContext) {
        this.mContext = mContext;
        mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (pointLists!=null&&pointLists.size()>0)
            return pointLists.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return pointLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.user_point_list_item, viewGroup, false);
            viewHolder.mPointTime = (TextView) view.findViewById(R.id.user_point_item_time);
            viewHolder.mPointType = (TextView) view.findViewById(R.id.user_point_item_type);
            viewHolder.mPointDesc = (TextView) view.findViewById(R.id.user_point_item_desc);
            viewHolder.mPointCount = (TextView) view.findViewById(R.id.user_point_item_count);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (pointLists!=null&&pointLists.size()>0){
            PointList pointList = (PointList) getItem(i);
            viewHolder.mPointTime.setText(pointList.getCreateTime());
            viewHolder.mPointType.setText(String.valueOf(pointList.getPointType()));
            viewHolder.mPointDesc.setText(pointList.getPointDescription());
            viewHolder.mPointCount.setText(String.valueOf(pointList.getNumber()));
        }

        return view;
    }

    private class ViewHolder{
        TextView mPointType;
        TextView mPointDesc;
        TextView mPointTime;
        TextView mPointCount;
    }
}
