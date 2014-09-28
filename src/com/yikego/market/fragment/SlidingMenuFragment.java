package com.yikego.market.fragment;


import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yikego.market.LogUtils;
import com.yikego.market.R;
import com.yikego.market.activity.*;
import com.yikego.market.utils.Constant;

/**
 * Created by wll on 14-9-11.
 *
 * Sliding menu list
 */
public class SlidingMenuFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SlidingMenu";
    private boolean isLogin = false;

    private View mView;
    private RelativeLayout loginLayout;
    private RelativeLayout orderSearchLayout;
    private RelativeLayout shopScoreLayout;
    private RelativeLayout shopQuanLayout;
    private RelativeLayout feedBackLayout;
    private RelativeLayout menuSettingLayout;
    private RelativeLayout quitLayout;
    private TextView mUserPhoneTextView;

    private SharedPreferences mSharePreferences;

    private String userId;
    private String userPhone;
    private String passWord;
    private String userAddress;
    private String userStatus;
    private String createTime;

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null && intent.getAction().equals(Constant.LOGIN_OK)){
                updateBottonStatus();
            }
        }
    };

    private void updateBottonStatus() {
        if (isLogin()){
            mUserPhoneTextView.setText(userPhone);
            loginLayout.setEnabled(false);
            quitLayout.setVisibility(View.VISIBLE);

        }else {
            mUserPhoneTextView.setText(R.string.main_left_login_user);
            loginLayout.setEnabled(true);
            quitLayout.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isLogin() {
        mSharePreferences = getActivity().getSharedPreferences("userInfo" ,Context.MODE_PRIVATE);
        userId = mSharePreferences.getString("userId", "");
        userPhone = mSharePreferences.getString("userPhone", "");
        if ((null!=userPhone&&!userPhone.equals("")) &&
                (null!=userId && !userId.equals(""))){
            isLogin = true;
        }else {
            isLogin = false;
        }
        return isLogin;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.v(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.left_menu, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.v(TAG, "onActivityCreated");
        initView();
        updateBottonStatus();
        IntentFilter mLoginFilter = new IntentFilter();
        mLoginFilter.addAction(Constant.LOGIN_OK);
        getActivity().registerReceiver(mLoginReceiver, mLoginFilter);
    }

    @Override
    public void onResume() {
        LogUtils.v(TAG, "onResume");
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
        loginLayout = (RelativeLayout) mView.findViewById(R.id.head_image_layout);
        orderSearchLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_order_search);
        shopScoreLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_shop_score);
        shopQuanLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_shop_quan);
        feedBackLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_feedback);
        menuSettingLayout = (RelativeLayout) mView.findViewById(R.id.left_menu_setting);
        quitLayout = (RelativeLayout) mView.findViewById(R.id.rl_quit);

        mUserPhoneTextView = (TextView) mView.findViewById(R.id.NameTextView);

        loginLayout.setOnClickListener(this);
        orderSearchLayout.setOnClickListener(this);
        shopScoreLayout.setOnClickListener(this);
        shopQuanLayout.setOnClickListener(this);
        feedBackLayout.setOnClickListener(this);
        menuSettingLayout.setOnClickListener(this);
        quitLayout.setOnClickListener(this);

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
            case R.id.rl_quit:
                clearLogin();
                break;
            default:
                break;
        }

    }

   /*
   * clear login status
   * */
    private void clearLogin() {
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.clear().commit();

        updateBottonStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        getActivity().unregisterReceiver(mLoginReceiver);
    }
}
