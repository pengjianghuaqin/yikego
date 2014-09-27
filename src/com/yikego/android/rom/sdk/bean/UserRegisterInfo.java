package com.yikego.android.rom.sdk.bean;

/**
 * Created by wanglinglong on 14-9-23.
 */
public class UserRegisterInfo {
    public class InnerUser{
        public String userPhone;
        public String passWord;
        public String userAddress;
    };
    public String matchContent;

    public InnerUser user = new InnerUser();
}
