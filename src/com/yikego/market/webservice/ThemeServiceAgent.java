package com.yikego.market.webservice;

import java.io.IOException;
import java.net.SocketException;

import com.yikego.android.rom.sdk.bean.CommitOrder;
import com.yikego.android.rom.sdk.bean.MarketGoodsInfoListData;
import com.yikego.android.rom.sdk.bean.OrderResult;
import com.yikego.android.rom.sdk.bean.PaginationStoreListInfo;
import com.yikego.android.rom.sdk.bean.PostProductType;
import com.yikego.android.rom.sdk.bean.PostUserLocationInfo;
import com.yikego.android.rom.sdk.bean.ProductListInfo;
import com.yikego.android.rom.sdk.bean.StoreId;
import com.yikego.android.rom.sdk.bean.UserLoginInfo;
import com.yikego.android.rom.sdk.bean.UserRegisterInfo;

import org.apache.http.HttpException;

import com.yikego.android.rom.sdk.ServiceProvider;
import com.yikego.market.utils.GlobalUtil;

import android.content.Context;
import android.util.Log;

public class ThemeServiceAgent {

	// 测试服务器
	public static String RESOURCE_ROOT_URL="http://121.40.182.230:8080/ykcore/";

	private static ThemeServiceAgent mInstance;
	private int nPageSize;
	private Context mContext;
	private boolean bIsLogin;
	// private String mClientId;
	private String mDeviceId;
	private String mSubsId;

	public ThemeServiceAgent(Context context) {

		this.mContext = context;
		// nRecommandAppListPageSize = Constant.RECOMMANDLIST_COUNT_PER_TIME;
		bIsLogin = false;

	}

	public static ThemeServiceAgent getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ThemeServiceAgent(context);
		}
		return mInstance;
	}
	public Object postMessageRecord(String messageRecordType,String userPhone)
			throws SocketException {
        Object data = null;
		if (!GlobalUtil.checkNetworkState(mContext)) {
			throw new SocketException();
		} else {
			try {
				data =  ServiceProvider.postMessageRecord(messageRecordType,userPhone);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
		}
	}

    public Object postUserRegister(UserRegisterInfo userRegisterInfo)
            throws  SocketException{
        Object data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data =ServiceProvider.postUserRegister(userRegisterInfo);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
    public Object postUserLogin(UserLoginInfo userLoginInfo)
            throws SocketException{
        Object data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data = ServiceProvider.postUserLogin(userLoginInfo);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
    public PaginationStoreListInfo postUserLocationInfo(PostUserLocationInfo postUserLocationInfo)
            throws  SocketException{
    	PaginationStoreListInfo data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data =ServiceProvider.getStoreList(postUserLocationInfo);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
    
    public MarketGoodsInfoListData getMarketGoodsInfoListData(StoreId storeId)
            throws  SocketException{
    	MarketGoodsInfoListData data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data =ServiceProvider.getProductTypeList(storeId);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
    
    public ProductListInfo getMarketGoodsInfoList(PostProductType postProductType)
            throws  SocketException{
    	ProductListInfo data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data =ServiceProvider.getProductList(postProductType);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
    
    public OrderResult PostOrder(CommitOrder commitOrder)
            throws  SocketException{
    	OrderResult data = null;
        if (!GlobalUtil.checkNetworkState(mContext)){
            throw new SocketException();
        }else {
            try {
                data =(OrderResult) ServiceProvider.postOrder(commitOrder);
            }catch (IOException e){
                e.printStackTrace();
            }catch (HttpException e){
                e.printStackTrace();
            }
        }
        return data;
    }
}