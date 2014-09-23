package com.yikego.market.webservice;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;



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
			case Constant.TYPE_POST_USER_LOGIN:
				if (request.getData() != null) {

					params = (Object[]) request.getData();

					messageRecordType = (String) params[0];
					userPhone = (String) params[1];
					try {
						mAgent.postMessageRecord(messageRecordType, userPhone);
						request.setStatus(Constant.STATUS_SUCCESS);
						request.notifyObservers(null);
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