package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.*;
import com.yikego.market.R;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wll on 14-9-13.
 */
public class QuickLoginActivity extends Activity{
    private static final String TAG = "QuickLoginActivity";

    private ThemeService mThemeService;

    //login type
    private static final int TYPE_LOGIN_QUICk = 2;
    private static final String LOGIN_RESULT_CODE_OK = "0";
    private static final String LOGIN_RESULT_CODE_PWD_ERROR = "-1";
    private static final String LOGIN_RESULT_CODE_AUTHCODE_ERROR = "-2";
    private static final String LOGIN_RESULT_CODE_ERROR = "-3";
    private static final int ACTION_NETWORK_ERROR = 0;
    private static final int ACTION_GET_AUTH_CODE = 1;
    private static final int ACTION_USER_LOGIN = 2;

    private UserLoginInfo userLoginInfo = new UserLoginInfo();

    private Handler mHandler;
    private int time = 60;
    private TextView actionBarText;
    private ImageView mSearchView;
    private TextView mSearchText;
    private ImageView actionBack;

    private EditText mUserNameEdit;
    private EditText mAuthCodeEdit;
    private Button mGetAuthButton;
    private Button mLoginButton;

    private UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
    private UserRegisterInfo.InnerUser user = userRegisterInfo.user;
    private AuthCodeInfo authCodeInfo = new AuthCodeInfo();

    private SharedPreferences mSharePreferences;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_login);
        mThemeService = ThemeService.getServiceInstance(this);

        mSharePreferences = this.getSharedPreferences("userInfo", MODE_PRIVATE);

        initActionBar();
        initView();
        initHandler();
    }

    private void initActionBar() {
        actionBarText = (TextView) findViewById(R.id.actionbar_title);
        actionBarText.setText(R.string.quick_login);
        mSearchView = (ImageView) findViewById(R.id.market_detail_search);
        mSearchText = (TextView) findViewById(R.id.market_search);
        mSearchText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        actionBack = (ImageView) findViewById(R.id.market_detail_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        mUserNameEdit = (EditText) findViewById(R.id.tel_editText);
        mAuthCodeEdit = (EditText) findViewById(R.id.authCode_editText);
        mGetAuthButton = (Button) findViewById(R.id.authCode_button);
        mLoginButton = (Button) findViewById(R.id.loginOn_Button);

        mGetAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authCodeInfo.userPhone = mUserNameEdit.getText().toString().trim();
                authCodeInfo.messageRecordType = String.valueOf(Constant.TYPE_AUTH_CODE_QUICK_LOGIN);
                if (GlobalUtil.isValidPhone(authCodeInfo.userPhone)){
                    getAuthCode(authCodeInfo);
                    mGetAuthButton.setEnabled(false);
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mGetAuthButton!=null){
                                        if (time <= 0) {
                                            mGetAuthButton.setEnabled(true);
                                            mGetAuthButton.setText(R.string.authCode_button_text);
                                            timerTask.cancel();
                                        } else {
                                            mGetAuthButton.setText("" + time);
                                        }
                                        time--;
                                    }
                                }
                            });
                        }
                    };

                    timer.schedule(timerTask,0,1000);
                }else {
                    GlobalUtil.showToastString(QuickLoginActivity.this, R.string.phone_invalid);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLoginInfo.loginType = String.valueOf(TYPE_LOGIN_QUICk);
                userLoginInfo.userPhone = mUserNameEdit.getText().toString().trim();
                userLoginInfo.matchContent = mAuthCodeEdit.getText().toString().trim();
                if (!GlobalUtil.isValidPhone(userLoginInfo.userPhone)){
                    GlobalUtil.showToastString(QuickLoginActivity.this, R.string.phone_invalid);
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

    private void getAuthCode(AuthCodeInfo authCodeInfo) {
        Request request = new Request(0, Constant.TYPE_GET_AUTH_CODE);
        request.setData(authCodeInfo);
        request.addObserver(new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                // TODO Auto-generated method stub
                if (data != null) {
                    Message msg = Message.obtain(mHandler, ACTION_GET_AUTH_CODE,
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
        mThemeService.getThemeList(request);
    }

    private void initHandler() {
        // TODO Auto-generated method stub
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case ACTION_GET_AUTH_CODE:
                        MessageRecord messageRecord = (MessageRecord) msg.obj;
                        Log.d(TAG, "messageRecorde : " + messageRecord.messageRecordId);
                        break;
                    case ACTION_USER_LOGIN:
                        Intent intent = new Intent();
                        UserInfo userInfo = (UserInfo) msg.obj;

                        Log.d(TAG, "userLogin update : " + userInfo.resultCode);
                        if (userInfo.resultCode.equals(LOGIN_RESULT_CODE_OK)){
                            intent.setAction(Constant.LOGIN_OK);

                            SharedPreferences.Editor editor = mSharePreferences.edit();
                            editor.putString("userId", userInfo.user.userId);
                            editor.putString("userPhone", userInfo.user.userPhone);
                            editor.putString("passWord", userInfo.user.passWord);
                            editor.putString("userAddress", userInfo.user.userAddress);
                            editor.putString("userStatus", userInfo.user.userStatus);
                            editor.putString("createTime", userInfo.user.createTime);
                            editor.commit();

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            GlobalUtil.showToastString(QuickLoginActivity.this, R.string.login_ok);
                            sendBroadcast(intent);
                            finish();
                        }else if (userInfo.resultCode.equals(LOGIN_RESULT_CODE_PWD_ERROR)){
                            GlobalUtil.showToastString(QuickLoginActivity.this, R.string.login_password_error);
                        }else if (userInfo.resultCode.equals(LOGIN_RESULT_CODE_AUTHCODE_ERROR)){
                            GlobalUtil.showToastString(QuickLoginActivity.this, R.string.auth_code_error);
                        }else {
                            GlobalUtil.showToastString(QuickLoginActivity.this, R.string.login_error);
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }
}
