package com.yikego.android.rom.sdk.bean;

/**
 * Created by wll on 14-10-12.
 */
public class PointList {
    private int pointId;
    private int userId;
    private int pointType;
    private String pointDescription;
    private int number;
    private String createTime;
    private String pointTypeDes;

    public String getPointTypeDes() {
        return pointTypeDes;
    }

    public void setPointTypeDes(String pointTypeDes) {
        this.pointTypeDes = pointTypeDes;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPointDescription() {
        return pointDescription;
    }

    public void setPointDescription(String pointDescription) {
        this.pointDescription = pointDescription;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

}
