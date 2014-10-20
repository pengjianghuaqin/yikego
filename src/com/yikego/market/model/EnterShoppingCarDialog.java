package com.yikego.market.model;

import com.yikego.android.rom.sdk.bean.OrderProductInfo;
import com.yikego.market.R;
import com.yikego.market.activity.MarketDetailActivity;
import com.yikego.market.activity.MarketShoppingCarActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterShoppingCarDialog extends AlertDialog {
	private Context mContext;
	private GoodsData mGoodsData;

	public EnterShoppingCarDialog(Context context, GoodsData goodsData) {
		super(context);
		mContext = context;
		mGoodsData = goodsData;
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
			OrderProductInfo orderProductInfo = new OrderProductInfo();
			orderProductInfo.productId = mGoodsData.goodsId;
			orderProductInfo.price = mGoodsData.getGoodsPrice();
			orderProductInfo.count = 1;
			orderProductInfo.name = mGoodsData.getGoodsName();
			if (MarketDetailActivity.orderDetailList != null
					&& MarketDetailActivity.orderDetailList.size() > 0) {
				int i = 0;
				for (i = 0; i < MarketDetailActivity.orderDetailList.size(); i++) {
					if (orderProductInfo.productId == MarketDetailActivity.orderDetailList
							.get(i).productId) {
						MarketDetailActivity.orderDetailList.get(i).count++;
						break;
					}
				}
				if (i == MarketDetailActivity.orderDetailList.size()) {
					MarketDetailActivity.orderDetailList.add(orderProductInfo);
				}
			} else {
				MarketDetailActivity.orderDetailList.add(orderProductInfo);
			}
			Intent intent = new Intent(mContext,
					MarketShoppingCarActivity.class);
			mContext.startActivity(intent);
		}
	};

}