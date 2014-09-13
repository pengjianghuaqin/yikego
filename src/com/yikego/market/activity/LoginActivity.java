package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.market.R;

/**
 * Created by wll on 14-9-13.
 */
public class LoginActivity extends Activity{

    private static final String TAG = "LoginActivity";

    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;

    private TextView quickLoginText;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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

    }
}
