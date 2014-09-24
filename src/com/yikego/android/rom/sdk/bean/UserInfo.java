package com.yikego.android.rom.sdk.bean;

/**
 * Created by wanglinglong on 14-9-24.
 */
public class UserInfo {
    public String resultCode;
    public class InnerUserClass{
        public String userId;
        public String userPhone;
        public String passWord;
        public String userAddress;
        public String userStatus;
        public String createTime;
    }

    public InnerUserClass user = new InnerUserClass();
}
