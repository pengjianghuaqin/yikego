package com.yikego.android.rom.sdk.bean;

import java.util.List;

/**
 * Created by wll on 14-10-12.
 */
public class UserPointListInfo {
    private int pageCount;
    private int totalCount;
    private int resultCode;
    private int nowPage;
    private List<PointList> pointlist;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public List<PointList> getPointlist() {
        return pointlist;
    }

    public void setPointlist(List<PointList> pointlist) {
        this.pointlist = pointlist;
    }
}
