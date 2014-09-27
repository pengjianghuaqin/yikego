package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.UserInfo;
import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.market.R;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-9-13.
 */
public class LoginActivity extends Activity{

    private static final String TAG = "LoginActivity";

    private ThemeService mThemeService;

    //login type
    private static final int TYPE_LOGIN_USERNAME = 1;
    private static final String LOGIN_RESULT_CODE_OK = "1";
    private static final String LOGIN_RESULT_CODE_PWD_ERROR = "-1";
    private static final String LOGIN_RESULT_CODE_AUTHCODE_ERROR = "-2";
    private static final String LOGIN_RESULT_CODE_ERROR = "-3";

    private UserLoginInfo userLoginInfo = new UserLoginInfo();

    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;

    private EditText mUserNameEdit;
    private EditText mPassWordEdit;
    private Button mLoginButton;

    private TextView quickLoginText;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mThemeService = ThemeService.getServiceInstance(this);

        initActionBar();
        initView();
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
                Intent intent = new Intent(LoginActivity.this, QuickLoginActivity.class);
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
                userLoginInfo.userPhone = mUserNameEdit.getText().toString().trim();
                userLoginInfo.passWord = mPassWordEdit.getText().toString().trim();

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
                Intent intent = new Intent();
                if (data != null) {
                    UserInfo userInfo = (UserInfo) data;

                    Log.d(TAG, "userLogin update : " + userInfo.resultCode);
                    if (userInfo.resultCode.equals(LOGIN_RESULT_CODE_OK)){
                        intent.putExtra("userId", userInfo.user.userId);
                        intent.putExtra("userPhone", userInfo.user.userPhone);

                        setResult(Integer.valueOf(LOGIN_RESULT_CODE_OK), intent);
                    }
//                    Message msg = Message.obtain(mHandler, ACTION_USER_REGISTER,
//                            data);
//                    mHandler.sendMessage(msg);
                } else {
                    Request request = (Request) observable;
//                    if (request.getStatus() == Constant.STATUS_ERROR) {
//                        mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
//                    }
                }
            }
        });
        mThemeService.postUserLogin(request);
    }
}
