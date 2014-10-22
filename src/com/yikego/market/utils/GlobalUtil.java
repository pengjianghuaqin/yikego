package com.yikego.market.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;
import com.yikego.market.R;

public class GlobalUtil {

	public static boolean bAutoCheckUpdate;
	public static boolean bAutoCreateShortcut;

    //phone is valid
    private static final String PHONE_PATTERN = "[1]{1}[3,4,5,8]{1}[0-9]{9}";
    public static final int PLAYMENT_ONLINE = 0;
    public static final int PLAYMENT_COD = 1;
	/*
	 * Check system setting for whether allow to install 3rd party applications
	 */
	public static boolean allowInstallNonMarketApps(Context context) {
		// TODO Auto-generated method stub
		ContentResolver cr = context.getContentResolver();
		
		if (Secure.getInt(cr, Secure.INSTALL_NON_MARKET_APPS, 0) == 0) {
			return false;
		} else {
			return true;
		}
	}
	/*
	 * Get Date string with raw Date by specified format
	 */
	public static String getDateByFormat(Date date, String format) {
		// TODO Auto-generated method stub
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		
		return simpleDateFormat.format(date);
	}
	
	public static boolean checkNetworkState(Context context) {
		// TODO Auto-generated method stub
		ConnectivityManager connectMgr =
			(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectMgr == null) {
			return false;
		}
		
		NetworkInfo nwInfo = connectMgr.getActiveNetworkInfo();
		
		if (nwInfo == null || !nwInfo.isAvailable()) {
			return false;
		}
		return true;
	}

   /*
   * show toast
   * */
    public static void showToastString(Context context,int resId){
        Toast toast =  Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
   /*
   * is phone valid
   * */
   public static boolean isValidPhone(String phone) {
       if (TextUtils.isEmpty(phone))
           return false;
       return phone.matches(PHONE_PATTERN);
   }

    /**
     * 验证输入密码条件(字符与数据同时出现)
     *
     * @param 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isPassword(String str) {
        String regex = "[A-Za-z0-9]+";
        return str.matches(regex);
    }

    /**
     * 验证输入密码长度 (6-24位)
     *
     * @param 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isPasswLength(String str) {
        String regex = "^\\d{6,24}$";
        return str.matches(regex);
    }

    private static SharedPreferences mSharePreferences;
    private static String userId;
    private static String userPhone;
    private static String address;
    private static String curStreet;
    private static int playmentWay;
    public static boolean isLogin(Context context) {
        mSharePreferences = context.getSharedPreferences("userInfo" ,Context.MODE_PRIVATE);
        userId = mSharePreferences.getString("userId", "");
        userPhone = mSharePreferences.getString("userPhone", "");
        if ((null!=userPhone&&!userPhone.equals("")) &&
                (null!=userId && !userId.equals(""))){
            return true;
        }else {
            return false;
        }
    }

    public static int getUserId(Context context){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = mSharePreferences.getString("userId", "");
        if (userId!=null&&!userId.equals("")){
            return Integer.valueOf(userId);
        }else {
            return -1;
        }
    }

    public static String getUserPhone(Context context){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userPhone = mSharePreferences.getString("userPhone", "");
        return userPhone;
    }
    
    public static String getUserAddress(Context context){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        address = mSharePreferences.getString("address", "");
        return address;
    }
    
    public static void setUserAddress(Context context, String address){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putString("address", address);
        editor.commit();
    }

    public static String getCurrentStreet(Context context){
        mSharePreferences = context.getSharedPreferences("curStreet", Context.MODE_PRIVATE);
        curStreet = mSharePreferences.getString("curStreet", "");
        return curStreet;
    }
    
    public static void setCurrentStreet(Context context, String street){
        mSharePreferences = context.getSharedPreferences("curStreet", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putString("curStreet", street);
        editor.commit();
    }
    
    public static int getPlaymentWay(Context context){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        playmentWay = mSharePreferences.getInt("playmentWay", PLAYMENT_ONLINE);
        return playmentWay;
    }
    
    public static void setPlaymentWay(Context context, int playmentWay){
        mSharePreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharePreferences.edit();
        editor.putInt("playmentWay", playmentWay);
        editor.commit();
    }
 }