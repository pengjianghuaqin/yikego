package com.yikego.market.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yikego.android.rom.sdk.bean.PointList;
import com.yikego.android.rom.sdk.bean.PostUserOrderBody;
import com.yikego.android.rom.sdk.bean.UserOrderListInfo;
import com.yikego.android.rom.sdk.bean.UserPointListInfo;
import com.yikego.market.R;
import com.yikego.market.adapter.UserPointAdapter;
import com.yikego.market.model.LoadingDialog;
import com.yikego.market.utils.Constant;
import com.yikego.market.utils.GlobalUtil;
import com.yikego.market.webservice.Request;
import com.yikego.market.webservice.ThemeService;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wll on 14-9-15.
 */
public class UserPointActivity extends Activity{

    private static final String TAG = "UserPointActivity";
    private ImageView mSearchView;
    private TextView mSearchText;
    private TextView actionBarText;
    private ImageView actionBack;
    private TextView mScoreExchange;
    private ListView mPointListView;
    private TextView mTotalPoint;

    private Request mCurrentRequest;
    private ThemeService mThemeService;
    private Handler mHandler ;
    private static final int ACTION_NETWORK_ERROR = 0;
    private static final int ACTION_USER_POINT_INFO = 1;
    private static final int ACTION_UPDATE_POINT_NUMBER = 2;

    private UserPointAdapter mUserPointAdapter = null;
    private PostUserOrderBody mPostUserOrderBody = new PostUserOrderBody();
    private UserPointListInfo mUserPointListInfo = null;
    private List<PointList> mPointLists;
    private int userId;
    private LoadingDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        if (!GlobalUtil.isLogin(this)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mThemeService = ThemeService.getServiceInstance(this);
        mUserPointAdapter = new UserPointAdapter(this);
        mProgressDialog = new LoadingDialog(this);
        initHandler();
        initActionBar();
        initView();
    }

    private void initView() {
        mPointListView = (ListView) findViewById(R.id.list_point);
        mPointListView.setAdapter(mUserPointAdapter);

        mTotalPoint = (TextView) findViewById(R.id.score_text);
        setNumberText(0);
        setPostData();

        getUserPointInfo();
    }

    private void initActionBar() {
        mSearchView = (ImageView) findViewById(R.id.market_detail_search);
        mSearchText = (TextView) findViewById(R.id.market_search);
        mSearchText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);
        mScoreExchange = (TextView) findViewById(R.id.score_exchange);
        mScoreExchange.setVisibility(View.VISIBLE);
        actionBarText = (TextView) findViewById(R.id.actionbar_title);
        actionBarText.setText(R.string.main_left_shop_score);
        actionBack = (ImageView) findViewById(R.id.market_detail_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    private void getUserPointInfo() {
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
        Request request = new Request(0, Constant.TYPE_GET_USER_POINT);
        // Object[] params = new Object[2];
        request.setData(mPostUserOrderBody);
        request.addObserver(new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                // TODO Auto-generated method stub
                if (data != null) {
                    Message msg = Message.obtain(mHandler,
                            ACTION_USER_POINT_INFO, data);
                    mHandler.sendMessage(msg);
                } else {
                    Request request = (Request) observable;
                    if (request.getStatus() == Constant.STATUS_ERROR) {
                        mHandler.sendEmptyMessage(ACTION_NETWORK_ERROR);
                    }
                }
            }
        });
        mCurrentRequest = request;
        mThemeService.getUserOrder(request);
    }

    private void setPostData() {
        userId = GlobalUtil.getUserId(this);
        if (userId < 0)
            return;
        mPostUserOrderBody.setNowPage(1);
        mPostUserOrderBody.setUserId(userId);
//        mPostUserOrderBody.setUserId(1);
        mPostUserOrderBody.setPageCount(25);
    }

    private void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case ACTION_USER_POINT_INFO:
                        UserPointListInfo userPointListInfo = (UserPointListInfo) msg.obj;
                        if (userPointListInfo!=null){
                            mUserPointListInfo = userPointListInfo;
                            if (mUserPointListInfo.getResultCode()==0){
                                List<PointList> pointLists = mUserPointListInfo.getPointlist();
                                mPointLists = pointLists;
                                if (pointLists!= null && pointLists.size()>0){
                                    Log.d(TAG, "getUserPoint pointList size : " + pointLists.size());
                                    mUserPointAdapter.setPointLists(pointLists);
                                    mUserPointAdapter.notifyDataSetChanged();

                                    mHandler.sendEmptyMessage(ACTION_UPDATE_POINT_NUMBER);
                                }
                            }
                        }
                        break;
                    case ACTION_UPDATE_POINT_NUMBER:
                        int total = getTotalNumber();
                        setNumberText(total);
                        mProgressDialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void setNumberText(int number) {
        mTotalPoint.setText(getResources().
                                getQuantityString(R.plurals.numberOfPoint, number, number));
    }

    private int getTotalNumber() {
        int total = 0;
        if (mPointLists!=null&&mPointLists.size()>0){
            for (int i=0; i<mPointLists.size();i++){
                PointList pointList = mPointLists.get(i);
                total = total + pointList.getNumber();
            }
        }
        return total;
    }
}
