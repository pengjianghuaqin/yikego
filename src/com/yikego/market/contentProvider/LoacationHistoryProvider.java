package com.yikego.market.contentProvider;

import com.yikego.market.contentProvider.LoacationHistory.LoacationHistoryColumns;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class LoacationHistoryProvider extends ContentProvider {
    private static final String DATABASE_NAME = "locationHistory.db";
    public static final String TABLE_NAME = "locationHistory";
    private static final int DATABASE_VERSION = 6;
    
    DatabaseHelper dbhelper;
    
    private static final int ITEM = 1;
    private static final int ITEM_ID = 2;
    private static final UriMatcher sUriMatcher; 
    
    static{
    	  
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(LoacationHistory.AUTHORITY, "locationHistory", ITEM);
        sUriMatcher.addURI(LoacationHistory.AUTHORITY, "locationHistory/#", ITEM_ID);
  
    }  
    
    
    @Override
    public boolean onCreate() {
  
        dbhelper = new DatabaseHelper(getContext());
        return true;
    }
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
        SQLiteDatabase db = dbhelper.getWritableDatabase();
       //执行删除，得到删除的行数
        int count = db.delete(TABLE_NAME, selection, selectionArgs);
        return count;
    }

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        if(sUriMatcher.match(uri) != ITEM){
            throw new IllegalArgumentException("Unknow Uri: " + uri);
        }
  
        SQLiteDatabase db = dbhelper.getWritableDatabase();
  
        long rowId = db.insert(TABLE_NAME, null , values);
  
        if(rowId > 0){
  
            Uri contactUri = ContentUris.withAppendedId(LoacationHistoryColumns.CONTENT_URI, rowId);
            //更新数据
            getContext().getContentResolver().notifyChange(contactUri, null);
  
            return contactUri;
        }
  
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
  
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
  
        switch(sUriMatcher.match(uri)){
  
        case ITEM :
            //设置要查询的表的表名
            qb.setTables(TABLE_NAME);
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
  
        case ITEM_ID :   
  
            String locationHistoryId = uri.getPathSegments().get(1);
            qb.setTables(TABLE_NAME);
            //添加查询条件, 因为是查询单条所以添加 判断ID的条件
            qb.appendWhere(LoacationHistoryColumns._ID + "=" + locationHistoryId);
            return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
  
        default :
            throw new IllegalArgumentException("unknow Uri: " + uri);
        }
  
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int count = 0;
        int code = sUriMatcher.match(uri);
        switch(code){
        case ITEM:
        	count = db.update(TABLE_NAME, values, selection, selectionArgs);
        	break;
        case ITEM_ID :
            String locationHistoryId = uri.getPathSegments().get(1);
            count = db.update(TABLE_NAME, values, LoacationHistoryColumns._ID + "=?", new String[]{locationHistoryId});
            break;
        }
        //更新数据
        getContext().getContentResolver().notifyChange(uri, null);
  
        return count;
	}

	
    private static class DatabaseHelper extends SQLiteOpenHelper{
    	  
        public DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
  
        @Override
        public void onCreate(SQLiteDatabase db) {
  
            db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                        + LoacationHistoryColumns._ID + " INTEGER PRIMARY KEY, "
                        + LoacationHistoryColumns.STREETNAME + " TEXT, "
                        + LoacationHistoryColumns.LONGITUDE + " TEXT, "
                        + LoacationHistoryColumns.LATITUDE + " TEXT);"
  
            );
  
        }
  
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Log.d("DatabaseHelper", "oldVersion = " + oldVersion +
        			", newVersion = " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
  
    }
}
