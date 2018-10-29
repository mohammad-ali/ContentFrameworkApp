package com.kaspian.book;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ContentMenu extends ListActivity {
    public ListView lv;
    BaseAdapter adapternav;

    private int number_of_keys;
    TextViewPlus header;
    List<String> list;
    int which_season;
    Intent intent;
    int rows;

    DataBase db;

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
            setContentView(R.layout.content_menu);
        }else{
            setContentView(R.layout.content_menu_dark);
        }
        header = (TextViewPlus) findViewById(R.id.header);

        images = new ArrayList<Integer>();
        intenthappen = false;

        intent = getIntent();
        which_season = intent.getIntExtra("s",1);
        list = new ArrayList<String>();
        db = new DataBase(this);
        String sa = "s"+which_season;
        rows = db.countRows(sa);
        //Log.e("s",rows + " " + which_season);
        for(int i =1 ; i<=rows;i++){
            String titles = db.getTitle(which_season,i);
            list.add(titles);
            images.add(Favorites.GetTitleImage(ContentMenu.this,which_season,i));
        }

        if(which_season==1){
            if(App.SecureDatabase){
                if(rows != App.FirstSeasonCount){
                    if(Build.VERSION.SDK_INT>=16){
                        Toast.makeText(ContentMenu.this,"تنظیمات دیتابیس انجام نشده",Toast.LENGTH_LONG).show();
                        finishAffinity();
                        stopService(new Intent(ContentMenu.this,BackgroundSoundService.class));
                    }else{
                        Toast.makeText(ContentMenu.this,"تنظیمات دیتابیس انجام نشده",Toast.LENGTH_LONG).show();
                        finish();
                        stopService(new Intent(ContentMenu.this,BackgroundSoundService.class));
                    }
                }
            }
        }



        if(MainMenu.tabnum != 1){
            String stringName = "s" + which_season;
            int string_res_ID = getResources().getIdentifier(stringName, "string", getPackageName());
            if(string_res_ID!=0){
                String desc = getResources().getString(string_res_ID);
                header.setText(desc);
            }else{
                header.setText("فصل"+" " + which_season);
            }
        }

        db.close();

        final RelativeLayout guid = (RelativeLayout) findViewById(R.id.guid_layout);
        final Button guidb = (Button) findViewById(R.id.guidb);
        guidb.setTypeface(App.sansb);
        if (SeasonMenu.isFirstTimeinmenu(ContentMenu.this)) {
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

        adapternav = new kaspianAdapter(ContentMenu.this);
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
                if(rows>7){
                    if(position>4){
                        saveScroll(position-3);
                    }else {
                        saveScroll(0);
                    }

                }else{
                    saveScroll(0);
                }
                Intent i = new Intent(getApplicationContext(), ShowContent.class);
                number_of_keys = position + 1;
                i.putExtra("w", number_of_keys);
                i.putExtra("s", which_season);
                i.putExtra("from", "list");
                startActivity(i);
                finish();
            }

        });


        final int WhereWasScroll = loadSavedPreferencesScroll();
        lv.setSelection(WhereWasScroll);

        ImageView s = (ImageView) findViewById(R.id.sreachfor);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intenthappen =true;
                Intent ii = new Intent(ContentMenu.this,Search_all.class);
                ii.putExtra("s",which_season);
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
                        intenthappen =true;
                        Intent i = new Intent(ContentMenu.this,ContentMenu.class);
                        int position = lv.getFirstVisiblePosition();
                        i.putExtra("s", which_season);
                        saveScroll(position);
                        startActivity(i);
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        finish();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(rows!=0){
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

        NavConstructor.Build(ContentMenu.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

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

    private int loadSavedPreferencesScroll() {
        SharedPreferences sp = getSharedPreferences("S"+which_season, Activity.MODE_PRIVATE);
        return sp.getInt("S"+which_season, 0);
    }

    private void saveScroll(int value) {
        SharedPreferences sp = getSharedPreferences("S"+which_season, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("S"+which_season, value);
        editor.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed() {
        intenthappen =true;
        if(MainMenu.tabnum==1){
            startActivity(new Intent(ContentMenu.this,MainMenu.class));
            finish();
        }else{
            startActivity(new Intent(ContentMenu.this,SeasonMenu.class));
            finish();
        }

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
            View row = inflater.inflate(theme, parent, false);



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
