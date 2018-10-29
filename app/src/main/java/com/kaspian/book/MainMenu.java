package com.kaspian.book;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;

public class MainMenu extends Activity {

    private int DEFAULT_POSITION = 1;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    LinearLayout ratel;

    ImageView header;
    DataBase db;
    public static int tabnum = 0;
    Typeface sansb;
    Typeface sans;
    boolean intenthappen = false;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.mothercolor_darker));
        }

        if(!Settings_Activity.loadNightMode(this)){
            setContentView(R.layout.main_menu);
        }else{
            setContentView(R.layout.main_menu_dark);
        }

        db = new DataBase(this);

        tabnum = db.countTables();

        if(App.SecureDatabase){
            if(tabnum != App.SeasonCount){
                if(Build.VERSION.SDK_INT>=16){
                    Toast.makeText(MainMenu.this,"تنظیمات دیتابیس انجام نشده",Toast.LENGTH_LONG).show();
                    finishAffinity();
                    stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                }else{
                    Toast.makeText(MainMenu.this,"تنظیمات دیتابیس انجام نشده",Toast.LENGTH_LONG).show();
                    finish();
                    stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                }
            }
        }


        sansb = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");
        header = (ImageView) findViewById(R.id.header);
        ratel = (LinearLayout) findViewById(R.id.ratel);
        Button start = (Button) findViewById(R.id.enterbtn);

        if(App.Header){
            if(!Settings_Activity.loadNightMode(this)){
                header.setImageResource(R.mipmap.header);
            }else{
                header.setImageResource(R.mipmap.header_dark);
            }
        }


        if(!App.Rate_App){
            ratel.setVisibility(View.GONE);
        }

        start.setTypeface(sansb);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabnum==1){
                    intenthappen =true;
                    Intent intent = new Intent(MainMenu.this,ContentMenu.class);
                    startActivity(intent);
                    finish();
                }else{
                    intenthappen =true;
                    Intent intent = new Intent(MainMenu.this,SeasonMenu.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        ImageView bookmarks = (ImageView) findViewById(R.id.fav);
        bookmarks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intenthappen = true;
                Intent intent = new Intent(MainMenu.this,Favorites.class);
                startActivity(intent);
                finish();
            }

        });


        ImageView about = (ImageView) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intenthappen = true;
                Intent i5 = new Intent(MainMenu.this, AboutUs.class);
                startActivity(i5);
                finish();

            }
        });


        ImageView settings = (ImageView) findViewById(R.id.set);
        settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intenthappen = true;
                Intent intent = new Intent(MainMenu.this, Settings_Activity.class);
                startActivity(intent);
                finish();

            }
        });

        ImageView rate = (ImageView) findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try{


                    intenthappen = true;
                    Uri uri = Uri.parse("bazaar://details?id=" + BuildConfig.APPLICATION_ID);
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_EDIT, uri);
                    startActivity(myAppLinkToMarket);



                }catch(Exception e){
                    Toast.makeText(MainMenu.this, "بازار را نصب ندارید؟" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        NavConstructor.Build(MainMenu.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

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
    }//end of onCreate



    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if(App.DoubleTapToExit){

            if (doubleBackToExitPressedOnce) {
                if(Build.VERSION.SDK_INT>=16){
                    finishAffinity();
                    stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                }else{
                    finish();
                    stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                }
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.exittoast) , Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        }else{
            doubleBackToExitPressedOnce = false;
            AlertDialog.Builder alert;

            if(!Settings_Activity.loadNightMode(this)){
                alert = new AlertDialog.Builder(this,5);
            }else{
                alert = new AlertDialog.Builder(this, 4);
            }

            alert.setMessage("تایید خروج؟");
            alert.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(Build.VERSION.SDK_INT>=16){
                        finishAffinity();
                        stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                    }else{
                        finish();
                        stopService(new Intent(MainMenu.this,BackgroundSoundService.class));
                    }


                }

            });
            alert.setNegativeButton("خیر", null);
            alert.show();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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
