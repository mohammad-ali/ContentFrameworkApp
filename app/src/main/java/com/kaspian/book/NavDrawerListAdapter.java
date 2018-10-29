package com.kaspian.book;

import com.kaspian.book.NavDrawerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if(position==0){
                convertView = mInflater.inflate(R.layout.drawer_list_item_head, null);
            }else{
                if(!Settings_Activity.loadNightMode(context)){
                    convertView = mInflater.inflate(R.layout.drawer_list_item, null);
                }else{
                    convertView = mInflater.inflate(R.layout.drawer_list_item_dark, null);
                }

            }
        }

        if(position!=0){
            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            TextViewPlus txtTitle = (TextViewPlus) convertView.findViewById(R.id.drawertitle);

            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
            txtTitle.setText(navDrawerItems.get(position).getTitle());
        }


        return convertView;
    }

}

