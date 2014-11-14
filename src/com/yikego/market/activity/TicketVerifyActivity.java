package com.yikego.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yikego.android.rom.sdk.bean.CouponCheckInfo;
import com.yikego.android.rom.sdk.bean.CouponInfo;
import com.yikego.market.R;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.Observable;
import java.util.Observer;

import android.os.Handler;

/**
 * Created by pj on 14-11-8.
 */
public class TicketVerifyActivity extends Activity {
	private static final String TAG = "TicketVerifyActivity";

	// login type
	private static final String COUPON_RESULT_CODE_OK = "0";
	private static final String COUPON_RESULT_CODE_NOT_EXIST = "-1";
	private static final String COUPON_RESULT_CODE_CHECKED = "-2";
	private static final String COUPON_RESULT_CODE_USED = "-3";
	private static final String COUPON_RESULT_CODE_CANCELED = "-4";
	private static final String COUPON_RESULT_CODE_OVERDUE = "-5";
	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_COUPON_RESULT = 1;

	private CouponCheckInfo mCouponCheckInfo = new CouponCheckInfo();
	private ThemeService mThemeService;
	private ImageView mSearchView;
	private TextView mSearchText;
	private TextView actionBarText;
	private ImageView actionBack;
	private TextView mScoreExchange;
	private EditText mVerify_edit;
	private TextView mBtn_vefiry;
	private Handler mHandler;
	private Context mContext;
	public static final int REQUSET = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_ticket);
		mThemeService = ThemeService.getServiceInstance(this);
		mContext = this;
		initActionBar();
		initView();
		initHandler();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent();
		intent.setAction(MarketCouponListActivity.ACTION_COUPON_VERIFY);
		sendBroadcast(intent);
	}
	private void initView() {

		mVerify_edit = (EditText) findViewById(R.id.verify_edit);
		mBtn_vefiry = (TextView) findViewById(R.id.btn_vefiry);
		mBtn_vefiry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mVerify_edit.getText() != null) {
					mCouponCheckInfo.userId = GlobalUtil
							.getUserId(TicketVerifyActivity.this);
					mCouponCheckInfo.couponNo = Integer.parseInt(mVerify_edit
							.getText().toString());
					ckeckCoupon(mCouponCheckInfo);
				}

			}
		});

	}

	private void initActionBar() {
		mSearchView = (ImageView) findViewById(R.id.market_detail_search);
		mSearchText = (TextView) findViewById(R.id.market_search);
		mSearchText.setVisibility(View.GONE);
		mSearchView.setVisibility(View.GONE);
		actionBarText = (TextView) findViewById(R.id.actionbar_title);
		actionBarText.setText(R.string.main_left_shop_quan_verify);
		mScoreExchange = (TextView) findViewById(R.id.score_exchange);
		mScoreExchange.setText("二维码");
		mScoreExchange.setVisibility(View.VISIBLE);
		mScoreExchange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CaptureActivity.class);
				startActivityForResult(intent, REQUSET);
			}

		});

		actionBack = (ImageView) findViewById(R.id.market_detail_back);
		actionBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		// requestCode标示请求的标示 resultCode表示有数据
		if (requestCode == REQUSET && resultCode == RESULT_OK) {
			String tmpNo = intent.getStringExtra("couponNo");
			boolean mat = tmpNo.matches("\\d+");
			if (mat) {
				mVerify_edit.setText(intent.getStringExtra("couponNo"));
			} else {
				GlobalUtil.showToastString(TicketVerifyActivity.this,
						R.string.coupon_check_null);
			}

		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ACTION_COUPON_RESULT:
					CouponInfo couponInfo = (CouponInfo) msg.obj;

					Log.d(TAG, "userLogin update : " + couponInfo.resultCode);
					if (couponInfo.resultCode.equals(COUPON_RESULT_CODE_OK)) {

						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_check_ok);

					} else if (couponInfo.resultCode
							.equals(COUPON_RESULT_CODE_NOT_EXIST)) {
						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_no_exist);
					} else if (couponInfo.resultCode
							.equals(COUPON_RESULT_CODE_CHECKED)) {
						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_checked);
					} else if (couponInfo.resultCode
							.equals(COUPON_RESULT_CODE_CANCELED)) {
						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_cancel);
					} else if (couponInfo.resultCode
							.equals(COUPON_RESULT_CODE_OVERDUE)) {
						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_overdue);
					} else if (couponInfo.resultCode
							.equals(COUPON_RESULT_CODE_USED)) {
						GlobalUtil.showToastString(TicketVerifyActivity.this,
								R.string.coupon_used);
					}

					break;
				default:
					break;
				}
			}
		};
	}

	private void ckeckCoupon(CouponCheckInfo couponCheckInfo) {
		Request request = new Request(0, Constant.TYPE_POST_COUPON_CHECK);
		request.setData(mCouponCheckInfo);
		request.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (data != null) {
					Message msg = Message.obtain(mHandler,
							ACTION_COUPON_RESULT, data);
					mHandler.sendMessage(msg);
				} else {
					Request request = (Request) observable;
					if (request.getStatus() == Constant.STATUS_ERROR) {
						mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
					}
				}
			}
		});
		mThemeService.postCouponCheck(request);
	}
}
