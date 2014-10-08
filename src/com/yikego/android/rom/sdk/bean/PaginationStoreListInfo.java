package com.yikego.android.rom.sdk.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class PaginationStoreListInfo implements Serializable{
    public int pageCount;
    public int resultCode;
    public int totalCount;
    public int nowPage;
    public ArrayList<StoreInfo> storelist;
}
