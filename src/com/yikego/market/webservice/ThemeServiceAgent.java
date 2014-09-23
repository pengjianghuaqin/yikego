package com.yikego.market.webservice;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.w3c.dom.Comment;

import com.yikego.android.rom.sdk.ServiceProvider;
import com.yikego.market.utils.GlobalUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ThemeServiceAgent {

	// 测试服务器
	public static String RESOURCE_ROOT_URL="http://121.40.182.230:8080/ykcore/";

	private static ThemeServiceAgent mInstance;
	private int nPageSize;
	private Context mContext;
	private boolean bIsLogin;
	// private String mClientId;
	private String mDeviceId;
	private String mSubsId;

	public ThemeServiceAgent(Context context) {

		this.mContext = context;
		// nRecommandAppListPageSize = Constant.RECOMMANDLIST_COUNT_PER_TIME;
		bIsLogin = false;

	}

	public static ThemeServiceAgent getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ThemeServiceAgent(context);
		}
		return mInstance;
	}
	
	public String postUserLogin(String[] verifyData)
			throws SocketException {
		String verifyType = "0";
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				ServiceProvider.postUserLogin(verifyData[0],verifyData[1],verifyData[2],verifyData[3]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return verifyType;
		}
	}
	public String postMessageRecord(String messageRecordType,String userPhone)
			throws SocketException {
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				ServiceProvider.postMessageRecord(messageRecordType,userPhone);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
}