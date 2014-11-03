package com.yikego.android.rom.sdk.bean;

import java.io.Serializable;

/**
 * Created by wll on 14-10-20.
 */
public class UserOrderDetailInfo implements Serializable{

    private int orderDetialId;
    private int orderId;
    private int productId;
    private String sku;
    private String name;
    private String price;
    private int count;
    private String createTime;
    public int getOrderDetialId() {
        return orderDetialId;
    }

    public void setOrderDetialId(int orderDetialId) {
        this.orderDetialId = orderDetialId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
