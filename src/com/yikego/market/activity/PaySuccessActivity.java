package com.yikego.market.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.yikego.android.rom.sdk.bean.OrderResult;
import com.yikego.market.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.yikego.market.yikegoApplication;

public class PaySuccessActivity extends Activity {

	private TextView actionBar_title;
    private ImageView actionBar_img;
    private TextView actionBar_backHome;
    private OrderResult orderResult;
    private int mOrderType;
    private TextView mOrderTypeTextView;
    private TextView mOrderNoTv;
    private TextView mOrderMoneyTv;
    private Button phone1;
    private Button phone2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_pay_success);
        yikegoApplication.getInstance().addActivity(this);

        if (getIntent()!=null){
            this.orderResult = (OrderResult) getIntent().getSerializableExtra("orderResult");
            this.mOrderType = getIntent().getIntExtra("orderType", -1);
        }
		actionBar_title = (TextView) findViewById(R.id.actionbar_title);
		actionBar_title.setText(R.string.text_commit_order_success);
        actionBar_img = (ImageView) findViewById(R.id.market_detail_back);
        actionBar_img.setVisibility(View.INVISIBLE);
        actionBar_backHome = (TextView) findViewById(R.id.back_home);
        actionBar_backHome.setText(getString(R.string.back_home));
        actionBar_backHome.setVisibility(View.VISIBLE);
        actionBar_backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PaySuccessActivity.this, MarketBrowser.class);
//                startActivity(intent);
                yikegoApplication.getInstance().exit();
            }
        });

        initView();
	}

    private void initView() {
        mOrderTypeTextView = (TextView) findViewById(R.id.order_playway);
        mOrderMoneyTv = (TextView) findViewById(R.id.order_amount);
        mOrderNoTv = (TextView) findViewById(R.id.order_num);

        phone1 = (Button) findViewById(R.id.phone_num2);
        phone2 = (Button) findViewById(R.id.phone_num1);

        if (orderResult!=null)
            phone1.setText(String.valueOf(orderResult.storePhone));
            phone2.setText(orderResult.storeTel);
            mOrderMoneyTv.setText(String.valueOf(orderResult.totalFee));
            mOrderNoTv.setText(String.valueOf(orderResult.orderNo));
    }

/*    public void onBackButton(View v) {
		finish();
	}*/

    @Override
    public void onBackPressed() {
        return;
    }
}
