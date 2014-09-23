package com.yikego.android.rom.sdk;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import com.yikego.android.rom.sdk.bean.MessageRecord;
import com.yikego.android.rom.sdk.bean.UserId;
import com.yikego.android.rom.sdk.bean.UserRegisterInfo;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.android.rom.sdk.rest.BaseResource;


public class ServiceProvider extends BaseResource {
    private static final String TAG = "ServiceProvicer";
	 /**
     * 获取图片
     * @param url
     * @return
     */
    public static byte[] getImage(String url)throws IOException,HttpException{
        return getRawData(url, "image/*");
    }
	/**用户评论
     */
	public static void postUserComment(String userId, String loginId, String appId,
			String phoneModel, String stars, String content,String channel) throws IOException, HttpException{	
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
	      formParams.add(new BasicNameValuePair("userId", userId));
	      formParams.add(new BasicNameValuePair("loginId", loginId));
	      formParams.add(new BasicNameValuePair("appId", appId));
	      formParams.add(new BasicNameValuePair("stars", stars));
	      formParams.add(new BasicNameValuePair("phoneModel", phoneModel));
	      formParams.add(new BasicNameValuePair("content", content));
	      formParams.add(new BasicNameValuePair("channel", channel));
	     post(ClientInfo.RESOURCE_ROOT_URL + "/service/usercomment",formParams);
	}
	/**用户注册
     */
	public static void postUserLogin(String loginType, String userPhone, String passWord,
			String matchContent) throws IOException, HttpException{	
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
	      formParams.add(new BasicNameValuePair("loginType", loginType));
	      formParams.add(new BasicNameValuePair("userPhone", userPhone));
	      formParams.add(new BasicNameValuePair("passWord", passWord));
	      formParams.add(new BasicNameValuePair("matchContent", matchContent));
	     post(ClientInfo.RESOURCE_ROOT_URL + "/user/userLogin",formParams);
	}

    public static Object postMessageRecord(String messageRecordType, String userPhone) throws IOException, HttpException{
        UserLoginInfo userInfo = new UserLoginInfo();
        userInfo.messageRecordType = messageRecordType;
        userInfo.userPhone = userPhone;
//	     post(ClientInfo.RESOURCE_ROOT_URL + "/messageRecord/authCode",userInfo);
        MessageRecord messageRecord =  post(ClientInfo.RESOURCE_ROOT_URL + "/messageRecord/authCode", userInfo,
                MessageRecord.class);
        Log.d(TAG, "postMessageRecord messageRecorde : " + messageRecord.messageRecordId);
        return messageRecord;
    }

    /*
    * user register
    * */
    public static Object postUserRegister(UserRegisterInfo userRegisterInfo)
        throws IOException, HttpException{
        Log.d(TAG, "postUserRegister userRegisterInfo ; " + userRegisterInfo.matchContent);
        UserId userId = post(ClientInfo.RESOURCE_ROOT_URL + "/user/register", userRegisterInfo, UserId.class);
        return userId;
    }

 }
