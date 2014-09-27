package com.yikego.market.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.yikego.market.LogUtils;
import com.yikego.market.R;
import com.yikego.market.activity.*;

/**
 * Created by wll on 14-9-11.
 *
 * Sliding menu list
 */
public class SlidingMenuFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SlidingMenu";

    private View mView;
    private RelativeLayout loginLayout;
    private RelativeLayout orderSearchLayout;
    private RelativeLayout shopScoreLayout;
    private RelativeLayout shopQuanLayout;
    private RelativeLayout feedBackLayout;
    private RelativeLayout menuSettingLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.v(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.left_menu, null);
        return mView;
    }

    @Override
    public void onResume() {
        LogUtils.v(TAG,"onResume");
        super.onResume();

        initView();
    }

    private void initView() {
        loginLayout = (RelativeLayout) mView.findViewById(R.id.head_image_layout);
        orderSearchLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_order_search);
        shopScoreLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_shop_score);
        shopQuanLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_shop_quan);
        feedBackLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_feedback);
        menuSettingLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_setting);

        loginLayout.setOnClickListener(this);
        orderSearchLayout.setOnClickListener(this);
        shopScoreLayout.setOnClickListener(this);
        shopQuanLayout.setOnClickListener(this);
        feedBackLayout.setOnClickListener(this);
        menuSettingLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id  = view.getId();
        Intent intent = new Intent();
        switch (id){
            case R.id.head_image_layout:
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.left_menu_order_search:
                intent.setClass(getActivity(), UserOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.left_menu_shop_score:
                intent.setClass(getActivity(), UserScoreActivity.class);
                startActivity(intent);

                break;
            case R.id.left_menu_shop_quan:
                intent.setClass(getActivity(), TicketVerifyActivity.class);
                startActivity(intent);

                break;
            case R.id.left_menu_feedback:
                intent.setClass(getActivity(), FeedbackActivtiy.class);
                startActivity(intent);
                break;
            case R.id.left_menu_setting:
                intent.setClass(getActivity(), Setting.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }
}
