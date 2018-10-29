package com.kaspian.book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class Favorites extends Activity {


    public List<Integer> Fav_seasons;
    public List<Integer> Fav_contents;
    // List view
    private ListView lv;
    public boolean Empty = true;
    // Listview Adapter
    BaseAdapter adapter;
    Typeface sans;
    Typeface sansb;
    int rows;

    List<String> list;
    List<String> list_desc;
    List<Integer> images;
    boolean intenthappen = false;

    private int DEFAULT_POSITION = 3;
    private int which_activity = 0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    DataBase db;

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
        intenthappen = false;
        if(!Settings_Activity.loadNightMode(this)){
            setContentView(R.layout.favorites);
        }else{
            setContentView(R.layout.favorites_dark);
        }

        db = new DataBase(this);

        sansb = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");

        // Listview Data
        list = new ArrayList<String>();
        list_desc = new ArrayList<String>();
        images = new ArrayList<Integer>();
        Fav_seasons = new ArrayList<Integer>();
        Fav_contents = new ArrayList<Integer>();

        final SharedPreferences shared = getSharedPreferences("Prefs", MODE_PRIVATE);

        if (MainMenu.tabnum == 1){


            rows = db.countRows("s1");
            for(int i =1 ; i<=rows ; i++){

                String tag = "1_" + i;
                Boolean b = shared.getBoolean(tag, false);
                if(b) {

                    // add titles
                    String title = db.getTitle(1,i);
                    list.add(title);
                    Fav_contents.add(i);
                    images.add(GetTitleImage(Favorites.this,1,i));
                    Empty = false;
                }

            }



        }else{

            for(int x = 1; x <= MainMenu.tabnum; x++) {

                String sa = "s"+x;
                rows = db.countRows(sa);
                Log.e("s","there are " + rows + " post in season " +x);
                for(int i =1 ; i<=rows ; i++){

                    String tag = x + "_" + i;
                    Log.e("s", "our tag is" +tag);
                    Boolean b = shared.getBoolean(tag, false);
                    if(b) {

                        Log.e("s","content " + i + " from season " + x + " loaded");

                        Fav_contents.add(i);
                        Fav_seasons.add(x);
                        // add titles
                        String title = db.getTitle(x,i);
                        list.add(title);
                        images.add(GetTitleImage(Favorites.this,x,i));
                        // add descs
                        String stringName = "s" + x;
                        int string_res_ID = getResources().getIdentifier(stringName, "string", getPackageName());
                        if(string_res_ID!=0){
                            String desc = getResources().getString(string_res_ID);
                            list_desc.add(desc);
                        }else{
                            list_desc.add("فصل"+" " + x);
                        }


                        Empty = false;
                    }

                }

            }

        }




        ImageView emptytext = (ImageView) findViewById(R.id.empty);
        if(Empty){
            emptytext.setVisibility(View.VISIBLE);
        }
        lv = (ListView) findViewById(R.id.list_view);
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

        // Adding items to listview
        adapter = new kaspianAdapter(Favorites.this);
        lv.setAdapter(adapter);

        final int WhereWasScroll = loadSavedPreferencesScroll();
        lv.setSelection(WhereWasScroll);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                intenthappen =true;
                Intent i = new Intent(getApplicationContext(), ShowContent.class);
                int content = Fav_contents.get(position);
                int season;
                if (MainMenu.tabnum == 1){
                    season = 1;
                }else{
                    season = Fav_seasons.get(position);
                }
                i.putExtra("w", content);
                i.putExtra("s", season);
                i.putExtra("from", "Fav");
                startActivity(i);
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
                        Intent i = new Intent(Favorites.this,Favorites.class);
                        int position = lv.getFirstVisiblePosition();
                        saveScroll(position);
                        intenthappen =true;
                        startActivity(i);
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        finish();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(!Empty){
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

        NavConstructor.Build(Favorites.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

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


    @Override
    public void onBackPressed() {
        intenthappen =true;
        startActivity(new Intent(Favorites.this,MainMenu.class));
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
            return Fav_contents.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return Fav_contents.get(position);
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
                theme = R.layout.fav_list_item;
            }else{
                theme = R.layout.fav_list_item_dark;
            }
            View row = inflater.inflate(theme, parent, false);



            TextViewPlus tv = (TextViewPlus) row.findViewById(R.id.Title_of_lv_items);
            TextViewPlus tvdesc = (TextViewPlus) row.findViewById(R.id.dec_of_lv_items);
            tv.setText(list.get(position));
            
            if (MainMenu.tabnum ==1){
                tvdesc.setVisibility(View.GONE);
            }else{
				tvdesc.setText(list_desc.get(position));
			}
            if(images.get(position) != 0){
                ImageView t = (ImageView) row.findViewById(R.id.titleimage);
                t.setImageResource(images.get(position));
            }

            return row;
        }

    }


    private int loadSavedPreferencesScroll() {
        SharedPreferences sp = getSharedPreferences("Scroll3", Activity.MODE_PRIVATE);
        return sp.getInt("Scroll3", 0);
    }

    private void saveScroll(int value) {
        SharedPreferences sp = getSharedPreferences("Scroll3", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Scroll3", value);
        editor.commit();
    }

    public static int GetTitleImage (Context c , int s, int m){

        String name = null ;
        if(m==0){ // image for season
            name = "t" + s;
        }else{  // image for content
            name = "t" + s + "_" + m;
        }
        return c.getResources().getIdentifier(name, "mipmap", c.getPackageName());
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


