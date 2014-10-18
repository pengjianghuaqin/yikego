package com.yikego.android.rom.sdk.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoreInfo implements Serializable{
	private static final long serialVersionUID = -2387824253271681301L;
    public int storeId;
    public String name;
    public String nickname;
    public int sendPrice;
    public String sendDescription;
    public String storeTel;
    public String storePhone;
    public float lng;
    public float lat;
    public String address;
    public int openHour;
    public int openMinute;
    public int closeHour;
    public int closeMinute;
    public int storeStatus;
    public ArrayList<StoreInfoImg> pictures;
    public String createTime;
    public int aboutDistance;
} 
