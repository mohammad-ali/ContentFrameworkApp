package com.kaspian.book;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SeasonMenu extends ListActivity {
    public ListView lv;
    BaseAdapter adapternav;

    private int total_num;
    List<String> list;
    private int DEFAULT_POSITION = 2;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    List<Integer> images;
    boolean intenthappen = false;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.mothercolor_darker));
        }

        if(!Settings_Activity.loadNightMode(this)){
            setContentView(R.layout.season_menu);
        }else{
            setContentView(R.layout.season_menu_dark);
        }
        images = new ArrayList<Integer>();

        total_num = MainMenu.tabnum;
        list = new ArrayList<String>();

        for(int i=0 ; i<total_num ; i++){
            String stringName = "s" + String.valueOf(i + 1);
            int string_res_ID = getResources().getIdentifier(stringName, "string", getPackageName());
            if(string_res_ID!=0){
                String my_string = getResources().getString(string_res_ID);
                list.add(my_string);
            }else{
                int print = i+1;
                list.add("فصل"+" " + print);
            }
            images.add(Favorites.GetTitleImage(SeasonMenu.this,i+1,0));
        }
        intenthappen = false;
        final RelativeLayout guid = (RelativeLayout) findViewById(R.id.guid_layout);
        final Button guidb = (Button) findViewById(R.id.guidb);
        guidb.setTypeface(App.sansb);
        if (isFirstTimeinmenu(SeasonMenu.this)) {
            if(App.User_Help_Screen){
                guid.setVisibility(View.VISIBLE);
            }
        }
        guid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guid.setVisibility(View.GONE);
            }
        });
        guidb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guid.setVisibility(View.GONE);
                guidb.setVisibility(View.GONE);
            }
        });

        adapternav = new kaspianAdapter(SeasonMenu.this);
        setListAdapter(adapternav);
        lv = getListView();
        if(App.Divider){
            ColorDrawable sage;
            if(Settings_Activity.loadNightMode(this)){
                sage = new ColorDrawable(this.getResources().getColor(R.color.night_back_lighter));
            }else{
                sage = new ColorDrawable(this.getResources().getColor(R.color.divider));
            }

            lv.setDivider(sage);
            lv.setDividerHeight(2);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                intenthappen =true;
                Intent i = new Intent(getApplicationContext(), ContentMenu.class);
                int s = position + 1;
                i.putExtra("s", s);

                startActivity(i);
                finish();
            }

        });



        ImageView s = (ImageView) findViewById(R.id.sreachfor);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intenthappen =true;
                Intent ii = new Intent(SeasonMenu.this,Search_all.class);
                ii.putExtra("season",true);

                startActivity(ii);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Log.e("a", "scrolling stopped...");
                    long m = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    int mb = (int) m/1000000 ;
                    Log.e("kaspian", "used: " + mb);
                    if(mb>175){
                        Intent i = new Intent(SeasonMenu.this,SeasonMenu.class);
                        intenthappen =true;
                        startActivity(i);
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        finish();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(MainMenu.tabnum!=0){
                    if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1
                            && lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getHeight()) {
                        Log.e("kaspian","end of list");
                    }
                }

            }

        });




        ////////////////////////////////////////////////////////////////////////////
        ImageView nav = (ImageView) findViewById(R.id.opener);
        nav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
                    mDrawerLayout.closeDrawer(Gravity.END);
                }
                else {
                    mDrawerLayout.openDrawer(Gravity.END);
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        NavConstructor.Build(SeasonMenu.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {

                //change_activity_from_nav =false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item != null && item.getItemId() == android.R.id.home) {
                    if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
                        mDrawerLayout.closeDrawer(Gravity.END);
                    }
                    else {
                        mDrawerLayout.openDrawer(Gravity.END);
                    }
                }
                return false;
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            NavConstructor.displayView(DEFAULT_POSITION);
        }

    }




    public static boolean isFirstTimeinmenu(Context c) {
        SharedPreferences sp = c.getSharedPreferences("f", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean runedbefore = sp.getBoolean("f", false);
        if (!runedbefore){
            editor.putBoolean("f", true);
            editor.commit();
        }
        return !runedbefore;
    }


    @Override
    public void onBackPressed() {
        intenthappen =true;
        Intent i = new Intent(SeasonMenu.this,MainMenu.class);

        startActivity(i);
        finish();
    }





    // NAVIGATION METHODS - NAVIGATION METHODS - NAVIGATION METHODS - NAVIGATION METHODS
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class kaspianAdapter extends BaseAdapter {

        Context context;
        private LayoutInflater inflater = null;

        public kaspianAdapter(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int theme = 0;
            if(!Settings_Activity.loadNightMode(context)){
                theme = R.layout.list_item;
            }else{
                theme = R.layout.list_item_dark;
            }
            View row = inflater.inflate(theme , parent, false);



            TextViewPlus tv = (TextViewPlus) row.findViewById(R.id.Title_of_lv_items);
            tv.setText(list.get(position));

            if(images.get(position) != 0){
                ImageView t = (ImageView) row.findViewById(R.id.titleimage);
                t.setImageResource(images.get(position));
            }

            return row;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!intenthappen && !NavConstructor.intenthappennav && App.Music && Settings_Activity.loadMusic(this)){
            BackgroundSoundService.player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.Music && Settings_Activity.loadMusic(this)) {
            if (!BackgroundSoundService.player.isPlaying()) {
                BackgroundSoundService.player.start();
            }
        }

    }
}

