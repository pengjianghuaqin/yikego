package com.yikego.android.rom.sdk.bean;

import java.util.List;


public class CommitOrder {

    public int storeId;
    public int userId;
    public int orderType;
    public String subject;
    public String body;
    public int orderStatus;
    public List<OrderDetail> orderDetailList;
}
