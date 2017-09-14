package com.stephen.curry.memesou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stephen.curry.memesou.R;
import com.stephen.curry.memesou.SearchActivity;
import com.stephen.curry.memesou.bean.RegionInfo;

import java.util.List;

/**
 * Created by Curry on 2017/9/13.
 */

public class MyGridViewAdapter extends MyBaseAdapter<RegionInfo, GridView> {
    private LayoutInflater inflater;

    public MyGridViewAdapter(Context ct, List<RegionInfo> list) {
        super(ct, list);
        inflater = LayoutInflater.from(ct);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyGridViewAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new MyGridViewAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_remen_city, null);
            holder.id_tv_cityname = (TextView) convertView.findViewById(R.id.id_tv_cityname);
            holder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_logo);
            convertView.setTag(holder);
        } else {
            holder = (MyGridViewAdapter.ViewHolder) convertView.getTag();
        }
        RegionInfo info =(RegionInfo) getItem(position);
        holder.id_tv_cityname.setText(info.getName());
        holder.iv_logo.setImageResource(info.getSrcId());
        return convertView;
    }

    class ViewHolder {
        TextView id_tv_cityname;
        ImageView iv_logo;
    }
}
