package com.yikego.market.utils;

import java.io.File;

import android.os.Environment;

public class Constant {
	
	public static final int THEME_LIST_TYPE = 0;
	public static final int THEME_LOCKSCREEN_TYPE = 1;
	public static final int THEME_DYNAMIC_WALLPAPER_TYPE = 2;
	public static final int THEME_FONT_TYPE = 3;
	public static final int THEME_FONT_ICON = 4;
	
	
	public static final int THEME_WALLPAPER_TYPE = 0;
	public static final int THEME_LOCKSCREEN_WALLPAPER_TYPE = 1;
	/** Request */
	/** Request����״̬ */
	public static final int STATUS_PENDING = 0x30000;
	public static final int STATUS_FAIL = 0x30001;
	public static final int STATUS_SUCCESS = 0x30002;
	public static final int STATUS_CANCELED = 0x30003;
	public static final int STATUS_ERROR = 0x30004;

	public static final int THEME_LIST_COUNT_PER_TIME = 15;
	public static final int WALLPAPER_LIST_COUNT_PER_TIME = 10;

    //request type for connect server
	
	public static final int TYPE_APP_ICON = 268500000;
	public static final int TYPE_GET_AUTH_CODE = 268501006;
    public static final int TYPE_POST_USER_REGISTER = 268501007;
    public static final int TYPE_POST_USER_LOGIN = 268501008;
    public static final int TYPE_POST_USER_LOCAL_INFO = 268501009;
    public static final int TYPE_GET_GOODS_TYPE_INFO = 268501010;
    public static final int TYPE_GET_GOODS_LIST_INFO = 268501011;
    public static final int TYPE_POST_SUBMIT_ORDER = 268501012;
    public static final int TYPE_GET_USER_ORDER = 268501013;
    public static final int TYPE_GET_USER_POINT = 268501014;
    //get auth code type 1:register  2:quick login
    public static final int TYPE_AUTH_CODE_REGISTER = 1;
    public static final int TYPE_AUTH_CODE_QUICK_LOGIN = 2;
    //login status
    public static final String LOGIN_OK = "com.yikego.market.loginok";
}