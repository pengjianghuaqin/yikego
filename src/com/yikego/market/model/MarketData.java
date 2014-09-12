package com.yikego.market.model;

public class MarketData{
	private String marketName = "比邻便利超市";
	private String marketPath = "230";
	private String businessTime = "10:00-22:00";
	private int takeawayPrice = 30;
	
	public MarketData(){
		
	}
	
	public void setMarketName(String marketName){
		this.marketName = marketName;
	}
	public String getMarketName(){
		return marketName;
	}
	
	public void setMarketPath(String marketPath){
		this.marketPath = marketPath;
	}
	public String getMarketPath(){
		return marketPath;
	}
	
	public void setBusinessTime(String businessTime){
		this.businessTime = businessTime;
	}
	public String getBusinessTime(){
		return businessTime;
	}
	
	public void setTakeawayPrice(int takeawayPrice){
		this.takeawayPrice = takeawayPrice;
	}
	public int getTakeawayPrice(){
		return takeawayPrice;
	}
}