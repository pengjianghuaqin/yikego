package com.yikego.android.rom.sdk.bean;

import java.io.Serializable;

/**
 * Created by wll on 14-10-20.
 */
public class UserOrderDetail implements Serializable{
    private UserOrderDetailList order;
    private ProductSubTotal productSubtotal;
    private int resultCode;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public UserOrderDetailList getOrder() {
        return order;
    }

    public void setOrder(UserOrderDetailList order) {
        this.order = order;
    }

    public ProductSubTotal getProductSubtotal() {
        return productSubtotal;
    }

    public void setProductSubtotal(ProductSubTotal productSubtotal) {
        this.productSubtotal = productSubtotal;
    }

    public class ProductSubTotal{
        private String totalFee;
        private int count;
        public String getTotalFee() {
            return totalFee;
        }

        public void setTotalFee(String totalFee) {
            this.totalFee = totalFee;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
