package com.yikego.market.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.yikego.android.rom.sdk.bean.AuthCodeInfo;
import com.yikego.android.rom.sdk.bean.MessageRecord;
import com.yikego.android.rom.sdk.bean.UserId;
import com.yikego.android.rom.sdk.bean.UserRegisterInfo;
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
    private Button authCodeButton;

    private Handler mHandler;
    private Request mCurrentRequest;
    private static final int ACTION_NETWORK_ERROR = 0;
    private static final int ACTION_GET_AUTH_CODE = 1;
    private static final int ACTION_USER_REGISTER = 2;

    private int time = 60;
    //
    private static final int REGISTER_RESULT_CODE_OK = 0;
    private static final int REGISTER_RESULT_CODE_EXIST = -1;
    private static final int REGISTER_RESULT_CODE_AUTHCODE_ERROR = -2;
    private static final int REGISTER_RESULT_CODE_ERROR = -3;

    private UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
    private UserRegisterInfo.InnerUser user = userRegisterInfo.user;
    private AuthCodeInfo authCodeInfo = new AuthCodeInfo();

    private Timer timer = new Timer();
    private TimerTask timerTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mThemeService = ThemeService.getServiceInstance(this);

        initActionBar();
        initView();
        initHandler();
    }

    private void initView() {
        telephoneEdit = (EditText) findViewById(R.id.tel_editText);
        passwordEdit = (EditText) findViewById(R.id.pwd_editText);
        authCodeEdit = (EditText) findViewById(R.id.authCode_editText);
        addressEdit = (EditText) findViewById(R.id.address_editText);
        authCodeButton = (Button) findViewById(R.id.acthCode_button);
        registerButton = (Button) findViewById(R.id.register_Button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userRegisterInfo.matchContent = authCodeEdit.getText().toString();
                userRegisterInfo.matchContent = "11111";

                user.userPhone = telephoneEdit.getText().toString().trim();
                user.userAddress = addressEdit.getText().toString().trim();
                user.passWord = passwordEdit.getText().toString().trim();

                if (!GlobalUtil.isValidPhone(user.userPhone)){
                    GlobalUtil.showToastString(Register.this, R.string.phone_invalid);
                    return;
                }
                if (!GlobalUtil.isPassword(user.passWord)|| !GlobalUtil.isPasswLength(user.passWord)){
                    GlobalUtil.showToastString(Register.this, R.string.password_invalid);
                    return;
                }
                userRegister(userRegisterInfo);
            }
        });

        authCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeInfo.userPhone = telephoneEdit.getText().toString().trim();
                authCodeInfo.messageRecordType = String.valueOf(Constant.TYPE_AUTH_CODE_REGISTER);
                if (GlobalUtil.isValidPhone(authCodeInfo.userPhone)){
                    getAuthCode(authCodeInfo);
                    authCodeButton.setEnabled(false);
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (authCodeButton!=null){
                                        if (time <= 0) {
                                            authCodeButton.setEnabled(true);
                                            authCodeButton.setText(R.string.authCode_button_text);
                                            timerTask.cancel();
                                        } else {
                                            authCodeButton.setText("" + time);
                                        }
                                        time--;
                                    }
                                }
                            });
                        }
                    };

                    timer.schedule(timerTask,0,1000);
                }else {
                    GlobalUtil.showToastString(Register.this, R.string.phone_invalid);
                }
            }
        });

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
        mCurrentRequest = request;
        mThemeService.getThemeList(request);
    }

    private void userRegister(UserRegisterInfo userRegisterInfo) {
        Request request = new Request(0, Constant.TYPE_POST_USER_REGISTER);
        request.setData(userRegisterInfo);
        request.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    Log.d(TAG, "userRegister update : " + data.toString());
                    Message msg = Message.obtain(mHandler, ACTION_USER_REGISTER,
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
        mThemeService.postUserRegister(request);
    }

    private void initActionBar() {
        mSearchView = (ImageView) findViewById(R.id.market_detail_search);
        mSearchText = (TextView) findViewById(R.id.market_search);
        mSearchText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        actionBarText = (TextView) findViewById(R.id.actionbar_title);
        actionBarText.setText(R.string.user_register);
        actionBack = (ImageView) findViewById(R.id.market_detail_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                    case ACTION_USER_REGISTER:
                        UserId userId = (UserId) msg.obj;
                        Log.d(TAG, "UserId : " + userId.userId);
                        if (userId.resultCode.equals(String.valueOf(REGISTER_RESULT_CODE_OK))){
                            Toast.makeText(Register.this, getString(R.string.register_ok), Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (userId.equals(String.valueOf(REGISTER_RESULT_CODE_EXIST))){
                            Toast.makeText(Register.this, getString(R.string.register_exist), Toast.LENGTH_SHORT).show();
                        }else if (userId.equals(String.valueOf(REGISTER_RESULT_CODE_AUTHCODE_ERROR))){
                            Toast.makeText(Register.this, getString(R.string.auth_code_error), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Register.this, getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }
}
