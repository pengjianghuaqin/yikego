package com.yikego.market.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.android.rom.sdk.bean.UserRegisterInfo;
import com.yikego.market.R;
import com.yikego.market.utils.Constant;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-9-13.
 */
public class Register extends Activity{

    private static final String TAG = "Register";

    private ThemeService mThemeService;

    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;

    private EditText telephoneEdit;
    private EditText passwordEdit;
    private EditText addressEdit;
    private EditText authCodeEdit;
    private Button registerButton;

    private UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
    private UserRegisterInfo.InnerUser user = userRegisterInfo.user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mThemeService = ThemeService.getServiceInstance(this);

        initActionBar();
        initView();
    }

    private void initView() {
        telephoneEdit = (EditText) findViewById(R.id.tel_editText);
        passwordEdit = (EditText) findViewById(R.id.pwd_editText);
        authCodeEdit = (EditText) findViewById(R.id.authCode_editText);
        addressEdit = (EditText) findViewById(R.id.address_editText);
        registerButton = (Button) findViewById(R.id.register_Button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userRegisterInfo.matchContent = authCodeEdit.getText().toString();
                userRegisterInfo.matchContent = "11111";

                user.userPhone = telephoneEdit.getText().toString();
                user.userAddress = addressEdit.getText().toString();
                user.passWord = passwordEdit.getText().toString();

                userRegister(userRegisterInfo);
            }
        });

    }

    private void userRegister(UserRegisterInfo userRegisterInfo) {
        Request request = new Request(0, Constant.TYPE_POST_USER_REGISTER);
        request.setData(userRegisterInfo);
        request.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Log.d(TAG, "userRegister update : " + data.toString());
            }
        });
        mThemeService.postUserRegister(request);
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
}
