package com.yikego.market.webservice;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.yikego.android.rom.sdk.bean.*;

import org.w3c.dom.Comment;

import com.yikego.market.model.Image2;
import com.yikego.market.utils.Constant;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.util.Log;

public class RequestHandler extends Thread {

	private static final String THREAD_NAME = "RequestHandler";

	public boolean bIsRunning;
	private int nThreadId;
	private ThemeService mService;
	private ThemeServiceAgent mAgent;

	public RequestHandler(ThemeService service, int threadId) {
		// set thread name according to thread id
		super((THREAD_NAME + threadId));

		this.nThreadId = threadId;
		this.mService = service;
		this.bIsRunning = true;
		this.mAgent = mService.getAgentInstance();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			if (!bIsRunning) {
				continue;
			}
			Request request = null;
			Object[] params = null;
			Object param = null;
            Object data = null;
			try {
				request = (Request) mService.popRequest(nThreadId);
				if (request == null) {
					continue;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			int startIndex;
			int type;
			int page;
			String userPhone;
			String messageRecordType;
			String phoneModel;
			String osVersion;
			String content;
			switch (request.getType()) {
			case Constant.TYPE_GET_AUTH_CODE:
				if (request.getData() != null) {

                    AuthCodeInfo authCodeInfo = (AuthCodeInfo) request.getData();
                    messageRecordType = authCodeInfo.messageRecordType;
                    userPhone = authCodeInfo.userPhone;

//                    Log.d("wll", "userlogin info : " + authCodeInfo.userPhone);
					try {
						data = mAgent.postMessageRecord(messageRecordType, userPhone);
						request.setStatus(Constant.STATUS_SUCCESS);
						request.notifyObservers(data);
					} catch (SocketException e) {
						request.setStatus(Constant.STATUS_ERROR);
						request.notifyObservers(null);
					}
				}
				break;
                case Constant.TYPE_POST_USER_REGISTER:
                    if (request.getData() != null){
                        UserRegisterInfo userRegisterInfo = (UserRegisterInfo) request.getData();
                        try {
                            data = mAgent.postUserRegister(userRegisterInfo);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_POST_USER_LOCAL_INFO:
                    if (request.getData() != null){
                    	PostUserLocationInfo postUserLocationInfo = (PostUserLocationInfo) request.getData();
                        try {
                            data = mAgent.postUserLocationInfo(postUserLocationInfo);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_POST_USER_LOGIN:
                    if (request.getData() != null){
                        UserLoginInfo userLoginInfo = (UserLoginInfo) request.getData();
                        try {
                            data = mAgent.postUserLogin(userLoginInfo);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_GET_GOODS_TYPE_INFO:
                    if (request.getData() != null){
                    	StoreId storeId = (StoreId) request.getData();
                    	Log.v("TYPE_GET_GOODS_TYPE_INFO", "storeId ="+storeId.storeId);
                        try {
                            data = mAgent.getMarketGoodsInfoListData(storeId);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_GET_GOODS_LIST_INFO:
                    if (request.getData() != null){
                    	PostProductType postProductType = (PostProductType) request.getData();
                    	Log.v("TYPE_GET_GOODS_LIST_INFO","postProductType nowPage="+postProductType.nowPage);
                    	Log.v("TYPE_GET_GOODS_LIST_INFO","postProductType pageCount="+postProductType.pageCount);
                    	Log.v("TYPE_GET_GOODS_LIST_INFO","postProductType productTypeId="+postProductType.productTypeId);
                        try {
                            data = mAgent.getMarketGoodsInfoList(postProductType);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_POST_SUBMIT_ORDER:
                    if (request.getData() != null){
                    	CommitOrder commitOrder = (CommitOrder) request.getData();
                    	try {
                            data = mAgent.PostOrder(commitOrder);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        }catch (SocketException e){
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                case Constant.TYPE_APP_ICON:
    				if (request.getData() != null) {
    					params = (Object[]) request.getData();
    					String imgUrl = (String) params[1];
    					Image2 img = new Image2();
    					
    					try {
    						img._id = ((Integer) params[0]).intValue();
    						img.mAppIcon = mAgent.getAppIcon(imgUrl);
    						request.setStatus(Constant.STATUS_SUCCESS);
    						request.notifyObservers(img);
    					} catch (SocketException e) {
    						// TODO Auto-generated catch block
    						request.setStatus(Constant.STATUS_ERROR);
    						request.notifyObservers(null);
    					}
    				}
    				break;
                case Constant.TYPE_GET_USER_ORDER:
                    if (request.getData() != null){
                        PostUserOrderBody postUserOrderBody = (PostUserOrderBody) request.getData();
                        Log.d(THREAD_NAME, "TYPE_GET_USER_ORDRE userId : " + postUserOrderBody.getUserId());
                        try {
                            data = mAgent.getUserOrder(postUserOrderBody);
                            request.setStatus(Constant.STATUS_SUCCESS);
                            request.notifyObservers(data);
                        } catch (SocketException e) {
                            request.setStatus(Constant.STATUS_ERROR);
                            request.notifyObservers(null);
                        }
                    }
                    break;
                    case Constant.TYPE_GET_USER_POINT:
                        if (request.getData() != null){
                            PostUserOrderBody postUserOrderBody = (PostUserOrderBody) request.getData();
                            Log.d(THREAD_NAME, "TYPE_GET_USER_POINT userId : " + postUserOrderBody.getUserId());
                            try {
                                data = mAgent.getUserPoint(postUserOrderBody);
                                request.setStatus(Constant.STATUS_SUCCESS);
                                request.notifyObservers(data);
                            } catch (SocketException e) {
                                request.setStatus(Constant.STATUS_ERROR);
                                request.notifyObservers(null);
                            }
                        }
                        break;
			default:
				break;
			}
		}
	} // end of run
}