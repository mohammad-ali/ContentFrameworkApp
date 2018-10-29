package com.kaspian.book;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;

public class AboutUs extends Activity {

    private int DEFAULT_POSITION = 6;
    private int which_activity = 0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;



    TextViewPlus verr;
    TextViewPlus dn;
    TextViewPlus rs;
    TextViewPlus cm;
    Button site;
    Button productb;
    LinearLayout resley1;
    LinearLayout resley2;
    LinearLayout cmley1;
    LinearLayout cmley2;
    boolean intenthappen = false;

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
            setContentView(R.layout.about_us);
        }else{
            setContentView(R.layout.about_us_dark);
        }
        String versionName = BuildConfig.VERSION_NAME;
        intenthappen = false;
        verr = (TextViewPlus) findViewById(R.id.verr);
        dn = (TextViewPlus) findViewById(R.id.dn);
        rs = (TextViewPlus) findViewById(R.id.rs);
        cm = (TextViewPlus) findViewById(R.id.cm);

        site = (Button) findViewById(R.id.siteb);
        productb = (Button) findViewById(R.id.productb);

        resley1 = (LinearLayout) findViewById(R.id.resley1);
        resley2 = (LinearLayout) findViewById(R.id.resley2);
        cmley1 = (LinearLayout) findViewById(R.id.cmley1);
        cmley2 = (LinearLayout) findViewById(R.id.cmley2);

        site.setTypeface(App.sansb);
        productb.setTypeface(App.sansb);

        verr.setText(versionName);

        dn.setText(""  + "\n" +
                "محمدعلی کاظمی نژاد");

        if(App.DeveloperSite.equals("")){
            site.setVisibility(View.GONE);
        }else{
            site.setVisibility(View.VISIBLE);
        }

        if(App.DeveloperbazzarID.equals("")){
            productb.setVisibility(View.GONE);
        }else{
            productb.setVisibility(View.VISIBLE);
        }

        if(getResources().getString(R.string.resources).equals("")){
            resley1.setVisibility(View.GONE);
            resley2.setVisibility(View.GONE);
        }else{
            resley1.setVisibility(View.VISIBLE);
            resley2.setVisibility(View.VISIBLE);
        }

        if(getResources().getString(R.string.comment).equals("")){
            cmley1.setVisibility(View.GONE);
            cmley2.setVisibility(View.GONE);
        }else{
            cmley1.setVisibility(View.VISIBLE);
            cmley2.setVisibility(View.VISIBLE);
        }


        productb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid="
                            + App.DeveloperbazzarID));
                    intent.setPackage("com.farsitel.bazaar");

                    startActivity(intent);

                }catch (Exception e){

                    String url = "http://cafebazaar.ir/developer/"
                            + App.DeveloperbazzarID;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }

            }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = App.DeveloperSite;

                if (!url.startsWith("http://") && !url.startsWith("https://")){
                    url = "http://" + url;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

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

        NavConstructor.Build(AboutUs.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

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
    public void onBackPressed() {
        intenthappen =true;
        Intent intent = new Intent(AboutUs.this,MainMenu.class);

        startActivity(intent);
        finish();
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
        if (App.Music && Settings_Activity.loadMusic(this)){if(!BackgroundSoundService.player.isPlaying()){
                BackgroundSoundService.player.start();
            }
        }

    }

}
