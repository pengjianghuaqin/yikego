package com.yikego.market.model;

import com.yikego.market.R;
import com.yikego.market.activity.MarketShoppingCarActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterShoppingCarDialog extends AlertDialog {
        private Context mContext;

        public EnterShoppingCarDialog(Context context) {
                super(context);
                mContext = context;
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) { 
                super.onCreate(savedInstanceState);
                setContentView(R.layout.enter_shopping_car_dialog);
                Button enterBtn = (Button) findViewById(R.id.enter);
                enterBtn.setOnClickListener(clickListener);
        }
        
        private View.OnClickListener clickListener = new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                	Intent intent = new Intent(mContext, MarketShoppingCarActivity.class);
                	mContext.startActivity(intent);
                }
        };

}