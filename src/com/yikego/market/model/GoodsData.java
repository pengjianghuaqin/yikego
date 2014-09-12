package com.yikego.market.model;

public class GoodsData{
	private String goodsName = "九九女儿红";
	private String goodsDetail = "500ml，1998年陈酿";
	private int goodsPrice = 68;
	private String goodsIconUrl = "";
	public GoodsData(){
		
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
	public int getGoodsPrice(){
		return goodsPrice;
	}
	
	public void setGoodsIconUrl(String goodsIconUrl){
		this.goodsIconUrl = goodsIconUrl;
	}
	public String getGoodsIconUrl(){
		return goodsIconUrl;
	}
}