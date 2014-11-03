package com.yikego.market.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.*;
import com.yikego.market.R;
import com.yikego.market.adapter.OrderListAdapter;
import com.yikego.market.adapter.UserOrderDetailListAdapter;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-10-20.
 */
public class UserOrderDetailActivity extends ListActivity {
    private static final String TAG = "UserOrderDetailActivity";
    private TextView mOrderMoneyTv;
    private TextView mOrderNoTv;
    private TextView mOrderTimeTv;
    private TextView mOrderStatusTv;
    private ImageView mBack;
    private TextView mOrderType;
    private TextView mUserTelTv;
    private TextView mUserAddTv;

    private OrderList orderList = null;
    private int orderId;
    private int storeId;
    private int userId;
    private String orderNo;
    private int orderType;
    private String subject;
    private String body;
    private float totalFee;
    private int orderStatus;
    private String createTime;
    private List<OrderDetail> orderDetailList;
    private ListView mListView;
    private UserOrderDetailListAdapter mAdapter;
    private TextView productCount;
    private TextView priceCount;
    private TextView mPayMoney;
    private Handler mHandler;
    private static final int ACTION_GET_ORDER_DETAIL = 1;
    private static final int ACTION_NETWORK_ERROR=2;
    private ThemeService mThemeService;
    private PostOrderNo mPostOrderNo = new PostOrderNo();
    private UserOrderDetail mUserOrderDetail=null;
    private UserOrderDetailList mUserOrderDetailList = null;
    private List<UserOrderDetailInfo> mUserDetailLists;
    private UserOrderDetail.ProductSubTotal mProductSubTotal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        mThemeService = ThemeService.getServiceInstance(this);
        if (getIntent() != null) {
            Intent intent = getIntent();
            orderList = (OrderList) intent.getSerializableExtra("orderList");
            this.orderNo = orderList.getOrderNo();
            mPostOrderNo.setOrderNo(orderNo);
        }

        initHandler();
        getOrderDetail();
        initView();
    }

    private void getOrderDetail() {
        if (mPostOrderNo.getOrderNo()==null|mPostOrderNo.getOrderNo().equals("")){
            return;
        }

        Request request = new Request(0, Constant.TYPE_GET_ORDER_DETAIL);
        request.setData(mPostOrderNo);
        request.addObserver(new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                // TODO Auto-generated method stub
                if (data != null) {
                    Message msg = Message.obtain(mHandler,
                            ACTION_GET_ORDER_DETAIL, data);
                    mHandler.sendMessage(msg);
                } else {
                    Request request = (Request) observable;
                    if (request.getStatus() == Constant.STATUS_ERROR) {
                        mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
                    }
                }
            }
        });
        mThemeService.getUserOrder(request);
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.market_detail_back);
        mOrderMoneyTv = (TextView) findViewById(R.id.user_order_item_money);
        mOrderNoTv = (TextView) findViewById(R.id.user_order_item_id);
        mOrderStatusTv = (TextView) findViewById(R.id.user_order_item_status);
        mOrderTimeTv = (TextView) findViewById(R.id.user_order_item_time);
        mOrderType = (TextView) findViewById(R.id.playment_way);
        mUserTelTv = (TextView) findViewById(R.id.tel);
        mUserAddTv = (TextView) findViewById(R.id.addressee);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mOrderMoneyTv.setText(String.valueOf(orderList.getTotalFee()));
        mOrderTimeTv.setText(orderList.getCreateTime());
        mOrderNoTv.setText(String.valueOf(orderList.getOrderNo()));
        mUserTelTv.setText(GlobalUtil.getUserPhone(this));
        mUserAddTv.setText(GlobalUtil.getUserAddress(this));

        String status  = "";
        orderStatus = orderList.getOrderStatus();
        if (orderStatus == 0){
            status = getString(R.string.order_status_create);
        }else if (orderStatus == 1){
            status = getString(R.string.order_status_costed);
        }else if (orderStatus == 2){
            status = getString(R.string.order_status_sending);
        }else if (orderStatus == 3){
            status = getString(R.string.order_status_ok);
        }
        mOrderStatusTv.setText(status);

        if (orderList.getOrderType()==1){
            mOrderType.setText(R.string.text_playment_cod);
        }else if (orderList.getOrderType()==2){
            mOrderType.setText(R.string.text_playment_online);
        }else {
            mOrderType.setText("error");
        }

        mListView = getListView();

        productCount = (TextView) findViewById(R.id.product_count);
        priceCount = (TextView) findViewById(R.id.price_mount);
        mPayMoney = (TextView) findViewById(R.id.order_pay_money);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserDetailLists = null;
        mUserOrderDetail = null;
        mUserOrderDetailList = null;
        mHandler = null;
    }

    private void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ACTION_GET_ORDER_DETAIL:
                        UserOrderDetail userOrderDetail = (UserOrderDetail) msg.obj;
                        Log.d(TAG, "ACTION_GET_ORDER_DETAIL resultCode : " +
                                userOrderDetail.getResultCode());
                        if (userOrderDetail.getResultCode()==0){
                            mUserOrderDetail = userOrderDetail;
                            mUserOrderDetailList = mUserOrderDetail.getOrder();
                            mUserDetailLists = mUserOrderDetailList.getOrderDetailList();
                            mProductSubTotal = mUserOrderDetail.getProductSubtotal();
                            updateView();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void updateView() {
        if (mAdapter==null){
            mAdapter = new UserOrderDetailListAdapter(this, mUserDetailLists);
            mListView.setAdapter(mAdapter);
        }

        priceCount.setText("￥: "+mProductSubTotal.getTotalFee());
        productCount.setText(" X " + String.valueOf(mProductSubTotal.getCount()));

    }

}

