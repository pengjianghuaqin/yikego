package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.OrderList;
import com.yikego.android.rom.sdk.bean.PostUserOrderBody;
import com.yikego.android.rom.sdk.bean.UserOrderListInfo;
import com.yikego.market.R;
import com.yikego.market.adapter.UserOrderAdapter;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-9-15.
 */
public class UserOrderActivity extends Activity{

    private static final String TAG = "UserOrderActivity";
    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;
    private ImageView mDeleteView;
    private ListView mListView;

    private Request mCurrentRequest;
    private ThemeService mThemeService;
    private Handler mHandler ;
    private static final int ACTION_NETWORK_ERROR = 0;
    private static final int ACTION_USER_ORDER_INFO = 1;
    private PostUserOrderBody mPostUserOrderBody = new PostUserOrderBody();
    private UserOrderListInfo mUserOrderListInfo;
    private int userId;

    private UserOrderAdapter mUserOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        if (!GlobalUtil.isLogin(this)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mThemeService = ThemeService.getServiceInstance(this);
        mUserOrderAdapter = new UserOrderAdapter(this);
        initHandler();
        initActionBar();
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.user_order_list);

    }

    private void initActionBar() {
        mSearchView = (ImageView) findViewById(R.id.market_detail_search);
        mSearchText = (TextView) findViewById(R.id.market_search);
        mSearchText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        mDeleteView = (ImageView) findViewById(R.id.market_detail_delete);
        mDeleteView.setVisibility(View.VISIBLE);
        actionBarText = (TextView) findViewById(R.id.actionbar_title);
        actionBarText.setText(R.string.user_order);
        actionBack = (ImageView) findViewById(R.id.market_detail_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setPostData();

        postUserOrderInfo();
    }

    private void postUserOrderInfo() {
        Request request = new Request(0, Constant.TYPE_GET_USER_ORDER);
        // Object[] params = new Object[2];
        request.setData(mPostUserOrderBody);
        request.addObserver(new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                // TODO Auto-generated method stub
                if (data != null) {
                    Message msg = Message.obtain(mHandler,
                            ACTION_USER_ORDER_INFO, data);
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

    private void setPostData() {
        userId = GlobalUtil.getUserId(UserOrderActivity.this);
        if (userId < 0)
            return;
        mPostUserOrderBody.setNowPage(1);
        mPostUserOrderBody.setUserId(userId);
        mPostUserOrderBody.setPageCount(25);
    }

    private void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ACTION_USER_ORDER_INFO:
                        UserOrderListInfo userOrderListInfo = (UserOrderListInfo) msg.obj;
                        Log.d(TAG, "getUerOrder resultCode : " + userOrderListInfo.getResultCode());
                        if (userOrderListInfo.getResultCode() == 0){
                            mUserOrderListInfo = userOrderListInfo;
                            if (userOrderListInfo != null){
                                List<OrderList> orderLists = mUserOrderListInfo.getOrderlist();
                                if (orderLists!=null && orderLists.size() > 0){
                                    Log.d(TAG, "getUserOrder Lists size : " + orderLists.size());
                                    mUserOrderAdapter.setOrderLists(orderLists);
                                    mUserOrderAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

}
