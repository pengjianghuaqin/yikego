package com.yikego.market.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.yikego.android.rom.sdk.bean.ProductInfo;
public class GoodsData implements Serializable{
	private static final long serialVersionUID = -2387824253271681301L;
	private String goodsName;
	private String goodsDetail;
	private float goodsPrice;
	private List<String> goodsIconUrl;
	public GoodsData(ProductInfo productInfo){
		goodsIconUrl = new ArrayList<String>();
		this.goodsName = productInfo.name;
		this.goodsDetail = productInfo.sku;
		this.goodsPrice = productInfo.price;
		
		for(int i=0;i<productInfo.pictures.size();i++){
			this.goodsIconUrl.add(productInfo.pictures.get(i).picPath);
		}
	}
	
	public void setGoodsName(String goodsName){
		this.goodsName = goodsName;
	}
	public String getGoodsName(){
		return goodsName;
	}
	
	public void setGoodsDetail(String goodsDetail){
		this.goodsDetail = goodsDetail;
	}
	public String getGoodsDetail(){
		return goodsDetail;
	}
	
	public void setGoodsPrice(int goodsPrice){
		this.goodsPrice = goodsPrice;
	}
	public float getGoodsPrice(){
		return goodsPrice;
	}
	
	public void setGoodsIconUrl(List<String> goodsIconUrl){
		this.goodsIconUrl = goodsIconUrl;
	}
	public List<String> getGoodsIconUrl(){
		return goodsIconUrl;
	}
}