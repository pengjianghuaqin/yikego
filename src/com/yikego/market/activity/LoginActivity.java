package com.yikego.market.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.yikego.android.rom.sdk.bean.UserInfo;
import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.android.rom.sdk.bean.UserRegisterInfo;
import com.yikego.market.R;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-9-13.
 */
public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	private ThemeService mThemeService;
	public static final String ACTION_USER_REGISTER = "android.intent.action.USER_REGISTER";
	// login type
	private static final int TYPE_LOGIN_USERNAME = 1;
	private static final String LOGIN_RESULT_CODE_OK = "0";
	private static final String LOGIN_RESULT_CODE_PWD_ERROR = "-1";
	private static final String LOGIN_RESULT_CODE_AUTHCODE_ERROR = "-2";
	private static final String LOGIN_RESULT_CODE_ERROR = "-3";

	private static final int ACTION_NETWORK_ERROR = 0;
	private static final int ACTION_USER_LOGIN = 1;

	private UserLoginInfo userLoginInfo;

	private ImageView mSearchView;
	private TextView mSearchText;
	private TextView actionBarText;
	private ImageView actionBack;

	private EditText mUserNameEdit;
	private EditText mPassWordEdit;
	private Button mLoginButton;

	private TextView quickLoginText;
	private TextView registerText;
	private Handler mHandler;
	SharedPreferences mSharePreferences;
	private final BroadcastReceiver mUserRegisterReceiver;

	public LoginActivity() {
		userLoginInfo = new UserLoginInfo();
		mUserRegisterReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(ACTION_USER_REGISTER)) {
					UserLoginInfo userRegisterInfo = (UserLoginInfo) intent
							.getSerializableExtra("userRegisterInfo");
					userLoginInfo.userPhone = userRegisterInfo.userPhone;
					userLoginInfo.passWord = userRegisterInfo.passWord;
					userLoginInfo.loginType = String
							.valueOf(TYPE_LOGIN_USERNAME);
					userLogin(userLoginInfo);
				}
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mThemeService = ThemeService.getServiceInstance(this);

		mSharePreferences = this.getSharedPreferences("userInfo", MODE_PRIVATE);
		registerIntentReceivers();
		initActionBar();
		initView();
		initHandler();
	}

	private void registerIntentReceivers() {
		// TODO Auto-generated method stub
		IntentFilter intentFilter = new IntentFilter(ACTION_USER_REGISTER);
		registerReceiver(mUserRegisterReceiver, intentFilter);

	}

	private void unregisterIntentReceivers() {
		// TODO Auto-generated method stub
		unregisterReceiver(mUserRegisterReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterIntentReceivers();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ACTION_USER_LOGIN:
					Intent intent = new Intent();
					UserInfo userInfo = (UserInfo) msg.obj;

					Log.d(TAG, "userLogin update : " + userInfo.resultCode);
					if (userInfo.resultCode.equals(LOGIN_RESULT_CODE_OK)) {
						intent.setAction(Constant.LOGIN_OK);

						SharedPreferences.Editor editor = mSharePreferences
								.edit();
						editor.putString("userId", userInfo.user.userId);
						editor.putString("userPhone", userInfo.user.userPhone);
						editor.putString("passWord", userInfo.user.passWord);
						editor.putString("userAddress",
								userInfo.user.userAddress);
						editor.putString("userStatus", userInfo.user.userStatus);
						editor.putString("createTime", userInfo.user.createTime);
						editor.commit();

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						GlobalUtil.showToastString(LoginActivity.this,
								R.string.login_ok);
						sendBroadcast(intent);
						finish();
					} else if (userInfo.resultCode
							.equals(LOGIN_RESULT_CODE_PWD_ERROR)) {
						GlobalUtil.showToastString(LoginActivity.this,
								R.string.login_password_error);
					} else if (userInfo.resultCode
							.equals(LOGIN_RESULT_CODE_AUTHCODE_ERROR)) {
						GlobalUtil.showToastString(LoginActivity.this,
								R.string.auth_code_error);
					} else {
						GlobalUtil.showToastString(LoginActivity.this,
								R.string.login_error);
					}

					break;
				default:
					break;
				}
			}
		};
	}

	private void initActionBar() {
		mSearchView = (ImageView) findViewById(R.id.market_detail_search);
		mSearchText = (TextView) findViewById(R.id.market_search);
		mSearchText.setVisibility(View.GONE);
		mSearchView.setVisibility(View.GONE);
		actionBarText = (TextView) findViewById(R.id.actionbar_title);
		actionBarText.setText(R.string.user_login);
		actionBack = (ImageView) findViewById(R.id.market_detail_back);
		actionBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private void initView() {

		quickLoginText = (TextView) findViewById(R.id.quick_login_text);
		registerText = (TextView) findViewById(R.id.register_text);

		quickLoginText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this,
						QuickLoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		registerText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this, Register.class);
				startActivity(intent);
			}
		});

		mUserNameEdit = (EditText) findViewById(R.id.user_name_editText);
		mPassWordEdit = (EditText) findViewById(R.id.password_editText);
		mLoginButton = (Button) findViewById(R.id.loginOn_Button);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				userLoginInfo.loginType = String.valueOf(TYPE_LOGIN_USERNAME);
				userLoginInfo.userPhone = mUserNameEdit.getText().toString()
						.trim();
				userLoginInfo.passWord = mPassWordEdit.getText().toString()
						.trim();
				if (!GlobalUtil.isValidPhone(userLoginInfo.userPhone)) {
					GlobalUtil.showToastString(LoginActivity.this,
							R.string.phone_invalid);
					return;
				}
				if (!GlobalUtil.isPassword(userLoginInfo.passWord)
						|| !GlobalUtil.isPasswLength(userLoginInfo.passWord)) {
					GlobalUtil.showToastString(LoginActivity.this,
							R.string.password_invalid);
					return;
				}
				userLogin(userLoginInfo);
			}
		});
	}

	private void userLogin(UserLoginInfo userLoginInfo) {
		Request request = new Request(0, Constant.TYPE_POST_USER_LOGIN);
		request.setData(userLoginInfo);
		request.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (data != null) {
					Message msg = Message.obtain(mHandler, ACTION_USER_LOGIN,
							data);
					mHandler.sendMessage(msg);
				} else {
					Request request = (Request) observable;
					if (request.getStatus() == Constant.STATUS_ERROR) {
						mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
					}
				}
			}
		});
		mThemeService.postUserLogin(request);
	}
}
