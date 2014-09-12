package com.yikego.market.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yikego.market.R;

/**
 * Created by wll on 14-9-11.
 *
 * Sliding menu list
 */
public class SlidingMenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.left_menu, null);
    }
}
