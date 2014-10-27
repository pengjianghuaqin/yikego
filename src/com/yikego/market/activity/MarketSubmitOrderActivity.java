package com.yikego.market.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yikego.android.rom.sdk.bean.CommitOrder;
import com.yikego.android.rom.sdk.bean.OrderDetail;
import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.android.rom.sdk.bean.OrderResult;
import com.yikego.market.R;
import com.yikego.market.adapter.OrderListAdapter;
import com.yikego.market.utils.CachedThumbnails;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.yikego.market.yikegoApplication;

public class MarketSubmitOrderActivity extends ListActivity implements
		OnItemClickListener {
	private static final int ACTIVITY_RESULT_COD = 1000;
	private static final int ACTIVITY_RESULT_DETAIL = 1001;
	private Context mContext;
	private ListView mListView;
    private TextView productCount;
    private TextView priceCount;
	private OrderListAdapter mAdapter;
	private Request mCurrentRequest;
	private ThemeService mThemeService;
	private Handler mHandler;
	private TextView mPlayment_way;
	private TextView mAddress;
    private TextView mPayMoney;
    private int mOrderType = -1;
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_SUBMIT_ORDER = 1;
	public MarketSubmitOrderActivity() {
		mContext = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submitorder);
        yikegoApplication.getInstance().addActivity(this);
		mThemeService = ThemeService.getServiceInstance(mContext);
		initView();
		initHandler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAddress = (TextView)findViewById(R.id.addressee);
		mAddress.setText(GlobalUtil.getUserAddress(this));
		mPlayment_way = (TextView)findViewById(R.id.playment_way);
		int playment_way = GlobalUtil.getPlaymentWay(this);
		if(playment_way == GlobalUtil.PLAYMENT_COD) {
			mPlayment_way.setText(R.string.text_playment_cod);	
		} else {
			mPlayment_way.setText(R.string.text_playment_online);	
		}

	}
	private void initView() {
		Button settlement = (Button) findViewById(R.id.btn_submit_order);
		settlement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostSubmitOrder();
			}
		});
        findViewById(R.id.market_detail_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
		mListView = getListView();
		mAdapter = new OrderListAdapter(mContext,
				MarketDetailActivity.orderDetailList);
		mListView.setAdapter(mAdapter);

        productCount = (TextView) findViewById(R.id.product_count);
        priceCount = (TextView) findViewById(R.id.price_mount);
        mPayMoney = (TextView) findViewById(R.id.order_pay_money);

        float coutPrice = 0;
        int count = 0;
        for (int i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
            if (MarketDetailActivity.orderDetailList.get(i).selectFlag) {
                count += MarketDetailActivity.orderDetailList.get(i).count;
                coutPrice += (MarketDetailActivity.orderDetailList.get(i).price)
                        * MarketDetailActivity.orderDetailList.get(i).count;

             }
        }
        BigDecimal b = new BigDecimal(coutPrice);
        coutPrice = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

        setPriceCount(coutPrice);
        setProductCount(count);
    }

    private void setProductCount(int count) {
        productCount.setText(count + getResources().getString(R.string.orderProductCount));
    }

    private void setPriceCount(float coutPrice) {
        priceCount.setText(getResources().getString(R.string.yuan)+String.valueOf(coutPrice));
        mPayMoney.setText(getResources().getString(R.string.pay_for) +
            String.valueOf(coutPrice));
    }

    private void PostSubmitOrder() {
		// TODO Auto-generated method stub

		Request request = new Request(0, Constant.TYPE_POST_SUBMIT_ORDER);
		// Object[] params = new Object[2];
		CommitOrder commitOrder = new CommitOrder();
		commitOrder.storeId = MarketDetailActivity.storeID;
        commitOrder.orderType = 1;
        mOrderType = commitOrder.orderType;
        if (GlobalUtil.isLogin(this))
            commitOrder.userId = GlobalUtil.getUserId(this);
        else
            commitOrder.userId = 1;
		commitOrder.orderStatus = 0;
		commitOrder.orderDetailList = new ArrayList<OrderDetail>();
        StringBuilder subjectSb = new StringBuilder();
        StringBuilder summarySb = new StringBuilder();
		for(int i=0;i<MarketDetailActivity.orderDetailList.size();i++){
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.productId = MarketDetailActivity.orderDetailList.get(i).productId;
			orderDetail.count = MarketDetailActivity.orderDetailList.get(i).count;
			commitOrder.orderDetailList.add(orderDetail);

            if (subjectSb.length()>256){
                continue;
            }else {
                subjectSb = subjectSb.append(MarketDetailActivity.orderDetailList.get(i).name);
                if (i!=MarketDetailActivity.orderDetailList.size()-1)
                    subjectSb = subjectSb.append("，");
                    summarySb = summarySb.append("；");
            }
            if (summarySb.length()>400){
                continue;
            }else {
                summarySb = summarySb.append(MarketDetailActivity.orderDetailList.get(i).summary);
                if (i!=MarketDetailActivity.orderDetailList.size()-1)
                    summarySb = summarySb.append("；");
            }
		}
        commitOrder.subject = subjectSb.toString();
        commitOrder.body = summarySb.toString();
		request.setData(commitOrder);
		request.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Message msg = Message.obtain(mHandler,
							ACTION_SUBMIT_ORDER, data);
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
				case ACTION_SUBMIT_ORDER:
					OrderResult orderResult = (OrderResult) msg.obj;
                    if (orderResult!=null && orderResult.resultCode == 0){
//                        Toast toast = Toast.makeText(mContext,"orderId="+
//                                orderResult.orderNo+"/ resultCode="+orderResult.resultCode+
//                                "/ totalFee="+orderResult.totalFee, Toast.LENGTH_SHORT);
//                        toast.show();
                        showOrderSuccess(orderResult);
                    }
					break;

				default:
					break;
				}
			}
		};
	}
	
	private void showOrderSuccess(OrderResult orderResult) {
		Intent intent = new Intent();
		intent.setClass(this, PaySuccessActivity.class);
        intent.putExtra("orderResult", orderResult);
        intent.putExtra("orderType", mOrderType);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case ACTIVITY_RESULT_COD:
			
			break;

		case ACTIVITY_RESULT_DETAIL:
			
			break;
			
		default:
			break;
		}
	}
	
	public void layout_cod_onclick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PaymentWayActivity.class);
		startActivityForResult(intent, ACTIVITY_RESULT_COD);
	}
	
	public void layout_detail_onclick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, ConsigneeEditActivity.class);
		startActivityForResult(intent, ACTIVITY_RESULT_DETAIL);
	}
}