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
	
	public abstract void getThemePreviews(Request request);
	public abstract void postMessageRecord(Request request);
    public abstract void postUserRegister(Request request);

    public abstract void postUserLogin(Request request);
}