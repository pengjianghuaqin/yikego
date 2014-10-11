package com.yikego.android.rom.sdk.bean;

/**
 * Created by wanglinglong on 14-10-11.
 */
public class PostUserOrderBody {
    private int pageCount;
    private int userId;
    private int nowPage;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }
}
