package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikego.market.R;

/**
 * Created by wll on 14-9-15.
 */
public class UserScoreActivity extends Activity{

    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;
    private TextView mScoreExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        initActionBar();
    }

    private void initActionBar() {
        mSearchView = (ImageView) findViewById(R.id.market_detail_search);
        mSearchText = (TextView) findViewById(R.id.market_search);
        mSearchText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        mScoreExchange = (TextView) findViewById(R.id.score_exchange);
        mScoreExchange.setVisibility(View.VISIBLE);
        actionBarText = (TextView) findViewById(R.id.actionbar_title);
        actionBarText.setText(R.string.main_left_shop_score);
        actionBack = (ImageView) findViewById(R.id.market_detail_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
