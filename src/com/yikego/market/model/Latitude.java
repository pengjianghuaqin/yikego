package com.yikego.market.model;

import java.io.Serializable;

public class Latitude implements Serializable{
    public float lat;
    public float lng;

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {

        return lat;
    }

    public float getLng() {
        return lng;
    }
}
