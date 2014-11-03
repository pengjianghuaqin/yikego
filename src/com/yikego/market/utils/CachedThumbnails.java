package com.yikego.market.utils;

import java.util.ArrayList;
import java.util.Hashtable;





import com.yikego.market.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class CachedThumbnails {

	public static boolean bAllowBufferIcon;
	
	private static final int MAX_SIZE = 50;
	private static final int FREE_COUNT = 10;
	
	private static Hashtable<String, Drawable> cachedDrawables = 
			new Hashtable<String, Drawable>();
	private static ArrayList<String> keys = new ArrayList<String>();
	private static Drawable defaultIcon;
	private static Drawable goodsDefaultIcon;

	/*
	 * get default icon displayed for applications
	 * when application icon not cached or under loading procedure
	 */
	public static Drawable getDefaultIcon(Context context) {
		// TODO Auto-generated method stub
		if (defaultIcon == null) {
			defaultIcon = context.getResources().getDrawable(R.drawable.default_icon);
		}
		return defaultIcon;
	}
	public static Drawable getGoodsDefaultIcon(Context context) {
		// TODO Auto-generated method stub
		if (goodsDefaultIcon == null) {
			Log.v("defaultIcon", "getGoodsDefaultIcon ");
			goodsDefaultIcon = context.getResources().getDrawable(R.drawable.img_bg_goods_icon);
			Log.v("defaultIcon", "getGoodsDefaultIcon defaultIcon"+defaultIcon);
		}
		return goodsDefaultIcon;
	}
	
	/*
	 * push to local map collection, then save to file system
	 */
	public static void cacheThumbnail(Context context, String Md5Path, Drawable drawable) {
		// TODO Auto-generated method stub
		pushToCache(Md5.encode(Md5Path), drawable);
		
//		if (bAllowBufferIcon) {
			FileManager.writeAppIconToFile(context, Md5.encode(Md5Path), drawable);
//		}
	}
	
	public static void cacheGoodsIconThumbnail(Context context,String Md5Path, Drawable drawable) {
		// TODO Auto-generated method stub
		pushToCache(Md5.encode(Md5Path), drawable);
		
//		if (bAllowBufferIcon) {
			FileManager.writeGoodsIconToFile(context, Md5.encode(Md5Path), drawable);
//		}
	}
	/*
	 * Find in local map collection
	 * If not found, then try to read from file system with appId
	 */
	@SuppressWarnings("unused")
	public static Drawable getThumbnail(Context context, String id) {
		// TODO Auto-generated method stub
		String mId = Md5.encode(id);
		if (cachedDrawables.containsKey(mId)) {
			return cachedDrawables.get(mId);
		} else {
			Drawable drawable = null;

				if(context != null)
				{
//					if(drawable != null)
//					{
//						drawable.setCallback(null);
//					}
					drawable = FileManager.readAppIconFromFile(context, mId);
				}
				if (drawable != null) {
					pushToCache(mId, drawable);
				}
			return drawable;
		}
	}
	@SuppressWarnings("unused")
	public static Drawable getGoodsThumbnail(Context context, String id) {
		// TODO Auto-generated method stub
		String mId = Md5.encode(id);
		if (cachedDrawables.containsKey(mId)) {
			return cachedDrawables.get(mId);
		} else {
			Drawable drawable = null;

				if(context != null)
				{
//					if(drawable != null)
//					{
//						drawable.setCallback(null);
//					}
					drawable = FileManager.readGoodsIconFromFile(context, mId);
				}
				if (drawable != null) {
					pushToCache(mId, drawable);
				}
			return drawable;
		}
	}
	
	/*
	 * Push drawable resource to local cache map with appId
	 */
	public static void pushToCache(String path, Drawable drawable) {
		// TODO Auto-generated method stub
		if (cachedDrawables.size() >= MAX_SIZE) {
			// pop the oldest FREE_COUNT items in cachedDrawables
			for (int i = 0; i < FREE_COUNT; i++) {
				cachedDrawables.remove(keys.remove(i));
			}
		}
		cachedDrawables.put(path, drawable);
		// add id to keys collection
		keys.add(path);
	}
}