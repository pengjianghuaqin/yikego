package com.yikego.android.rom.sdk.bean;

import java.util.List;

/**
 * Created by wanglinglong on 14-10-11.
 */
public class UserOrderListInfo {
    private int pageCount;
    private int totalCount;
    private int resultCode;
    private int nowPage;
    private List<OrderList> orderlist;

    public List<OrderList> getOrderlist() {
        return orderlist;
    }

    public void setOrderlist(List<OrderList> orderlist) {
        this.orderlist = orderlist;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
