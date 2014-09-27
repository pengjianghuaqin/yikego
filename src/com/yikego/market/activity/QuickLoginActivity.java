package com.yikego.market.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.market.R;

/**
 * Created by wll on 14-9-13.
 */
public class QuickLoginActivity extends Activity{
    private static final String TAG = "QuickLoginActivity";
    private TextView actionBarText;
    private ImageView mSearchView;
    private TextView mSearchText;
    private ImageView actionBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_login);

        initActionBar();
        initView();
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

    }
}
