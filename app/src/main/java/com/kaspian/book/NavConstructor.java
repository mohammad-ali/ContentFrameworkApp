package com.kaspian.book;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class NavConstructor {

    public static int which_act;
    public static Context cc;
    public static Activity ac;
    public static ListView _mDrawerList;
    public static DrawerLayout _mDrawerLayout;
    public static int defpos;
    public static String[] _navMenuTitles;
    public static TypedArray _navMenuIcons;
    public static ArrayList<NavDrawerItem> _navDrawerItems;
    public static NavDrawerListAdapter _adapter;
    public static boolean intenthappennav = false;

    @SuppressWarnings("ResourceType")
    public static void Build(Context context,ListView mDrawerList,
                             DrawerLayout mDrawerLayout,int DEFAULT_POSITION){

        _mDrawerList = mDrawerList;
        _mDrawerLayout = mDrawerLayout;
        cc = context;
        ac = (Activity) context;
        defpos = DEFAULT_POSITION;
        intenthappennav = false;
        _navMenuTitles = context.getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        _navMenuIcons = context.getResources()
                .obtainTypedArray(R.array.nav_drawer_icons_dark);

        _navDrawerItems = new ArrayList<NavDrawerItem>();

        _navDrawerItems.add(new NavDrawerItem());

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[1], _navMenuIcons.getResourceId(1, -1)));

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[2], _navMenuIcons.getResourceId(2, -1)));

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[3], _navMenuIcons.getResourceId(3, -1)));

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[4], _navMenuIcons.getResourceId(4, -1)));

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[5], _navMenuIcons.getResourceId(5, -1)));

        _navDrawerItems.add(new NavDrawerItem(_navMenuTitles[6], _navMenuIcons.getResourceId(6, -1)));

        // Recycle the typed array

        _navMenuIcons.recycle();

        // setting the nav drawer list adapter
        _adapter = new NavDrawerListAdapter(context.getApplicationContext(),
                _navDrawerItems);
        mDrawerList.setAdapter(_adapter);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }



    public static class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            displayView(position);
        }
    }

    public static void displayView(int position) {
        switch (position) {
            case 1:
                which_act =1;
                break;
            case 2:
                which_act =2;
                break;
            case 3:
                which_act =3;
                break;
            case 4:
                which_act =4;
                break;
            case 5:
                which_act =5;
                break;
            case 6:
                which_act =6;
                break;

            default:
                break;
        }

        if(position!=0){

            _mDrawerList.setItemChecked(position, true);
            _mDrawerList.setSelection(position);
            _mDrawerLayout.closeDrawer(_mDrawerList);
        }else{
            _mDrawerList.setItemChecked(defpos, true);
            _mDrawerList.setSelection(defpos);
        }

        NavigationClosed();
    }

    public static void NavigationClosed(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (which_act){
                    case 1: //main menu
                        if(defpos!=1){
                            intenthappennav = true;
                            Intent i1 = new Intent(cc, MainMenu.class);
                            ac.startActivity(i1);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            //ac.finish();
                        }
                        break;
                    case 2: //content
                        if(defpos!=2) {
                            intenthappennav = true;
                            Intent i2;
                            if (MainMenu.tabnum == 1) {
                                i2 = new Intent(cc, ContentMenu.class);
                            } else {
                                i2 = new Intent(cc, SeasonMenu.class);
                            }
                            ac.startActivity(i2);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            //ac.finish();
                        }
                        break;
                    case 3: // favorite
                        if(defpos!=3){
                            intenthappennav = true;
                            Intent i3 = new Intent(cc, Favorites.class);
                            ac.startActivity(i3);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            //ac.finish();
                        }

                        break;
                    case 4: // search
                        if(defpos!=4){
                            intenthappennav = true;
                            Intent i4 = new Intent(cc, Search_all.class);
                            i4.putExtra("nav",true);
                            i4.putExtra("defpos",defpos);
                            ac.startActivity(i4);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }

                        break;
                    case 5: //settings
                        if(defpos!=5){
                            intenthappennav = true;
                            Intent i5 = new Intent(cc, Settings_Activity.class);
                            ac.startActivity(i5);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            //ac.finish();
                        }

                        break;
                    case 6:  //aboutus
                        if(defpos!=6){
                            intenthappennav = true;
                            Intent i6 = new Intent(cc, AboutUs.class);
                            ac.startActivity(i6);
                            ac.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            //ac.finish();
                        }
                        break;

                }

            }
        }, 250);


    }
}

