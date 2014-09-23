package com.yikego.market.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {
	
	public static Bitmap zoomAndRotateBitmap(Bitmap oldbmp, int w, int h, int degree) {

		Matrix matrix = new Matrix();
		Bitmap resize = Bitmap.createScaledBitmap(oldbmp, w, h, true);
		int width = resize.getWidth();
		int height = resize.getHeight();
		matrix.setRotate(degree);
		Bitmap newbmp = Bitmap.createBitmap(resize, 0, 0, width, height,
				matrix, true);
		return newbmp;
    }
	public static Bitmap zoomAndRotateBitmap(Bitmap oldbmp, double scalex, double scaley ,int degree){
	
		Matrix matrix = new Matrix(); 
		int width = oldbmp.getWidth();//获取资源位图的宽 
		int height = oldbmp.getHeight();//获取资源位图的高 
		float sx = (float) scalex ;
		float sy = (float) scaley ;
		matrix.postScale(sx,sy);//获取缩放比例 
		matrix.setRotate(degree);
		Bitmap dstbmp = Bitmap.createBitmap(oldbmp, 0, 0,width, height, matrix, true); //根据缩放比例获取新的位图 
        
		return dstbmp;
	}
  
}
