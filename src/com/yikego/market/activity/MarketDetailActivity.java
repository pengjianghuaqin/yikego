package com.yikego.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.yikego.market.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by wll on 14-8-16.
 */
public class MarketDetailActivity extends Activity
	implements OnItemClickListener{

    private GridView mGridView;
    private ImageView mBack;
    private TextView mSearch;
    private GridAdapter mGridAdapter;
    private List<Map<String,String>> mapList;
    private String[] title = new String[]{"特惠", "团购", "厨房", "卫生", "饮品", ""};
    private String[] detail = new String[]{"", "", "米/油/调料/用具", "纸巾/洗涤/洁具", "啤酒/果汁/可乐", ""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
//        initActionBar();
    }

    private void initActionBar() {

/*        ActionBar actionBar = getActionBar();
//		actionBar.setDisplayUseLogoEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.getCustomView().findViewById(R.id.actionbar_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        actionBar.getCustomView().findViewById(R.id.actionbar_search)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSearchRequested();
                    }
                });*/
    }

    private void initViews() {
        mGridView = (GridView) findViewById(R.id.market_detail_grid);

        mapList= new ArrayList<Map<String, String>>();
        for (int i = 0; i< title.length; i++){
            Map<String,String> item = new HashMap<String, String>();
            item.put("title", title[i]);
            item.put("detail", detail[i]);
            mapList.add(item);
        }

        mGridAdapter = new GridAdapter(mapList);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(this);  
        mBack = (ImageView) findViewById(R.id.market_detail_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearch = (TextView) findViewById(R.id.market_search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MarketDetailActivity.this, SearchGoodActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
	 public void onItemClick(AdapterView<?> adapter,//The AdapterView where the click happened    
	                                   View view,//The view within the AdapterView that was clicked   
	                                   int position,//The position of the view in the adapter   
	                                   long arg3//The row id of the item that was clicked   
	                                   ) {  
//		HashMap<String, Object> item=(HashMap<String, Object>) adapter.getItemAtPosition(position);  

	     if(position < mGridAdapter.getCount()) {
	    		Intent intent = new Intent(this, MarketGoodsListActivity.class);
//				intent.putExtra("type",1);
				startActivity(intent);
			}
	     
	 }  

    private class GridAdapter extends BaseAdapter{
        private List<Map<String, String>> list ;

        private GridAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.market_detail_grid_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.grid_item_title);
            TextView detail = (TextView) convertView.findViewById(R.id.grid_item_detail);
            title.setText(list.get(position).get("title"));
            detail.setText(list.get(position).get("detail"));

            return convertView;
        }
    }
}
