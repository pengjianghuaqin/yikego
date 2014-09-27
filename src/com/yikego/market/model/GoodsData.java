package com.yikego.market.model;

import java.util.List;

import com.yikego.android.rom.sdk.bean.ProductInfo;

public class GoodsData{
	private String goodsName = "九九女儿红";
	private String goodsDetail = "500ml，1998年陈酿";
	private float goodsPrice = 68;
	private List<String> goodsIconUrl;
	public GoodsData(ProductInfo productInfo){
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