package com.yikego.market.webservice;

public abstract interface IThemeService {

	public abstract void postUserInfo(Request request);
	
	public abstract void getThemeList(Request request);
	public abstract void getThemeIcon(Request request);

	// Clear thumb request
	public abstract void clearThumbRequest(int threadId);
	
	public abstract void getIconList(Request request);
	public abstract void getIconImg(Request request);
	public abstract void getRingtoneList(Request request);
	
	public abstract void postMessageRecord(Request request);
    public abstract void postUserRegister(Request request);

    public abstract void getStoreList(Request request);

    public abstract void getStoreInfo(Request request);

    public abstract void postUserLogin(Request request);

    public abstract void getUserOrder(Request request);

    public abstract void getAppIcon(Request request);

    public abstract void postCouponCheck(Request request);

}