package com.yikego.android.rom.sdk;

import org.apache.http.protocol.HTTP;

import com.yikego.android.rom.sdk.rest.BaseResource;

public class ClientInfo {
    
    public static String RESOURCE_ROOT_URL="http://121.40.182.230:8080/ykcore";
//    public static String RESOURCE_ROOT_URL="http://www.yikego.com:8080/ykcore";
    
    public static boolean GZIP_ENCODING=true;
    
    public static final String USER_AGENT=HTTP.USER_AGENT;
    
    public static final String API_Version="0.1b";
    
    public static final String USER_ID="User-ID";
    
    public static final String CLIENT_ID="Client-ID";
    
  	public static final String DEVICE_ID="Device-ID";
    
    public static final String SUBSCRIBER_ID="Subscriber-ID";
    
    public static final String ACCESS_TOKEN="Access_Token";
    
//    static{
//        setHeader(USER_AGENT, "App-Rom/"+API_Version);
//        setHeader("API-Version", API_Version);
//    }
    
    public static void setHeader(String name,String value){
        BaseResource.customHeaders.put(name, value);
    }
    
    public static String getApiVersion(){
        return API_Version;
    }
    
    public static String getHeaderValue(String name){
        return BaseResource.customHeaders.get(name);
    }
    
   public static void setClientId(String value){
        setHeader(CLIENT_ID, value);
    }
    
    public static void setDeviceId(String value){
        setHeader(DEVICE_ID, value);
    }
    
    public static void setSubscriberId(String value){
        setHeader(SUBSCRIBER_ID, value);
    }
    
    public static void setUserId(String value){
        setHeader(USER_ID, value);
    }
    
    public static void setAccessToken(String value){
        setHeader(ACCESS_TOKEN, value);
    }
    
    public static void setRootResourceUrl(String rootResourceUrl){
        RESOURCE_ROOT_URL=rootResourceUrl;
    }
    
    public static void setGzipEncoding(boolean gzip){
        GZIP_ENCODING=gzip;
    }

}
