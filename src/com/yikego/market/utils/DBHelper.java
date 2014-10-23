package com.yikego.market.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.yikego.market.contentProvider.LoacationHistory;

public class DBHelper {
    /* - insertUnique
 *  - arguments:
 *  * cr : ContentResolver instance to use
 *  * uri : target w/ Uri format to insert
 *  * cv : ContentValues to insert
 *  * uid : unique column to replace
 *
 *  - return:null
 */

    public static void updateDB(ContentResolver cr, ContentValues values) {
        insertUnique(cr, LoacationHistory.LoacationHistoryColumns.CONTENT_URI
                , values, LoacationHistory.LoacationHistoryColumns.STREETNAME);
    }
    public static void insertUnique(ContentResolver cr,
                                    Uri uri, ContentValues cv, String uid){

        String scase = (String)cv.get(uid);
        String selection = "street_name = '"+scase+"'";

        Cursor c = cr.query(uri, null, selection, null, null);

        if(c.getCount() > 0){
            cr.update(uri, cv, selection, null);
        }
        else {
            cr.insert(uri, cv);
        }
    }
}
