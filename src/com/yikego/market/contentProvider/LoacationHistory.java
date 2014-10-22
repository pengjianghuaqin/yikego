package com.yikego.market.contentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LoacationHistory {
	  
    /**
     * 开发组织署名，Android就是通过这个名字来寻找对应的provider
     */
    public static final String AUTHORITY="com.yikego.market";
  
    private LoacationHistory(){}
  
    /**
     *
     * 记录Contact表相关的信息，在这边implements BaseColumns接口是因为，
     * BaseColumns有提供静态字段，_id和_count，这2个都是默认在Cursor可读取的属性
     *
     */
    public static final class LoacationHistoryColumns implements BaseColumns {
  
        private LoacationHistoryColumns(){}
        /**
         * 访问它的uri
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/locationHistory");
  
        /**
         * 数据库字段影射
         */
        public static final String STREETNAME="street_name";	//街道名称
        public static final String LONGITUDE="longitude";	//经度
        public static final String LATITUDE="Latitude";	//维度

        public static final String[] QUERY = {_ID,STREETNAME,LONGITUDE};
    }
  
}