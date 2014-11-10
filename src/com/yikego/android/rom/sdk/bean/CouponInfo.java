package com.yikego.android.rom.sdk.bean;

public class CouponInfo {
	public String resultCode;

    public class InnerCouponClass{
        public int userId;
        public int couponId;
        public String storeName;
        public String createTime;
        public String validTime;
        public String usedTime;
    }
    public InnerCouponClass coupon = new InnerCouponClass();
}
