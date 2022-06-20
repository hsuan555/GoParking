package com.example.goparking;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public InfoWindow (Activity context){
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        View view = context.getLayoutInflater().inflate(R.layout.info_window, null);
//
//        TextView tv_Name=view.findViewById(R.id.tv_name);
//        TextView tv_remain=view.findViewById(R.id.tv_remain);
//        TextView tv_remain_amount=view.findViewById(R.id.tv_remain_amount);
//        TextView tv_all=view.findViewById(R.id.tv_all);
//        TextView tv_all_amount=view.findViewById(R.id.tv_all_amount);
//
//        String[] datas = marker.getSnippet().split(",");
//
//        tv_Name.setText("停車場名稱");
//        tv_remain.setText("剩餘車位");
//        tv_remain_amount.setText(datas[0]);
//        tv_all.setText("全部車位");
//        tv_all_amount.setText(datas[1]);
//
//        return view;

        View view = context.getLayoutInflater().inflate(R.layout.info_with_gernal, null);

        TextView txv_parkName=view.findViewById(R.id.txv_parkName);
        TextView txv_carNum=view.findViewById(R.id.txv_carNum);

        String num = marker.getSnippet();
        String name=marker.getTitle();

        txv_parkName.setText(name);
        txv_carNum.setText(num);

        return view;
    }
}
