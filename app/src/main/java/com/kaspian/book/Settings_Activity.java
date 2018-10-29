package com.kaspian.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Settings_Activity extends Activity {

    Typeface sans;
    Typeface roya;
    Typeface yekan;
    Typeface arabic;
    Typeface koodak;
    Typeface sansb;
    TextView sample;
    TextView s2;
    TextView s4;
    int size = 0;
    int api;
    LinearLayout keepon;
    LinearLayout night;
    LinearLayout music;
    ToggleButton nightmode;
    ToggleButton screenon;
    ToggleButton musict;

    public static boolean isitnight;
    boolean intenthappen = false;

    private int DEFAULT_POSITION = 5;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

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
        isitnight = loadNightMode(this);
        if(isitnight){
            setContentView(R.layout.settings_layout_dark);
        }else{
            setContentView(R.layout.settings_layout);
        }

        sansb = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        roya = Typeface.createFromAsset(getAssets(), "fonts/naskh.ttf");
        yekan = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        arabic = Typeface.createFromAsset(getAssets(), "fonts/arabic.ttf");
        koodak = Typeface.createFromAsset(getAssets(), "fonts/koodak.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");

        s2 = (TextView) findViewById(R.id.s2);
        s4 = (TextView) findViewById(R.id.size);
        sample = (TextView) findViewById(R.id.sample);

        keepon = (LinearLayout) findViewById(R.id.keepon);
        night = (LinearLayout) findViewById(R.id.night);
        music = (LinearLayout) findViewById(R.id.music);

        nightmode = (ToggleButton) findViewById(R.id.nightmode);
        screenon = (ToggleButton) findViewById(R.id.screenon);
        musict = (ToggleButton) findViewById(R.id.musict);

        api = Build.VERSION.SDK_INT;

        if(App.Music){
            music.setVisibility(View.VISIBLE);
        }

        if(loadKeepOn(Settings_Activity.this)){
            screenon.setToggleOn();
        }else{
            screenon.setToggleOff();
        }
        if(loadNightMode(Settings_Activity.this)){
            nightmode.setToggleOn();
        }else{
            nightmode.setToggleOff();
        }
        if(loadMusic(Settings_Activity.this)){
            musict.setToggleOn();
        }else{
            musict.setToggleOff();
        }

        keepon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadKeepOn(Settings_Activity.this)){
                    screenon.setToggleOff();
                    saveKeepOn(false,Settings_Activity.this);
                }else{
                    screenon.setToggleOn();
                    saveKeepOn(true,Settings_Activity.this);
                }
            }
        });

        screenon.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(!on){
                    saveKeepOn(false,Settings_Activity.this);
                }else{
                    saveKeepOn(true,Settings_Activity.this);
                }
            }
        });

        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadNightMode(Settings_Activity.this)){
                    nightmode.setToggleOff();
                    saveNightMode(false,Settings_Activity.this);
                    restart();

                }else{
                    nightmode.setToggleOn();
                    saveNightMode(true,Settings_Activity.this);
                    restart();
                }
            }
        });

        nightmode.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(!on){
                    saveNightMode(false,Settings_Activity.this);
                    restart();
                }else{
                    saveNightMode(true,Settings_Activity.this);
                    restart();
                }
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadMusic(Settings_Activity.this)){
                    musict.setToggleOff();
                    saveMusic(false,Settings_Activity.this);
                    stopService(new Intent(Settings_Activity.this,BackgroundSoundService.class));

                }else{
                    musict.setToggleOn();
                    saveMusic(true,Settings_Activity.this);
                    startService(new Intent(Settings_Activity.this,BackgroundSoundService.class));
                }
            }
        });

        musict.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(!on){
                    saveMusic(false,Settings_Activity.this);
                    stopService(new Intent(Settings_Activity.this,BackgroundSoundService.class));
                }else{
                    saveMusic(true,Settings_Activity.this);
                    startService(new Intent(Settings_Activity.this,BackgroundSoundService.class));
                }
            }
        });


        NiceSpinner s = (NiceSpinner) findViewById(R.id.spinner_font);
        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.font_selector)));
        s.attachDataSource(dataset);


        size = loadFontSize(Settings_Activity.this);
        sample.setTextSize(size);
        s4.setText("سایز فعلی: " + size);

        switch (loadFontType(Settings_Activity.this)) {
            case 0:
                sample.setTypeface(sans);
                s2.setText("فونت فعلی: " + "سانس");
                break;
            case 1:
                sample.setTypeface(roya);
                s2.setText("فونت فعلی: " + "دروید نسخ");
                break;
            case 2:
                sample.setTypeface(yekan);
                s2.setText("فونت فعلی: " + "یکان");
                break;
            case 3:
                sample.setTypeface(arabic);
                s2.setText("فونت فعلی: " + "عربیک");
                break;
            case 4:
                sample.setTypeface(koodak);
                s2.setText("فونت فعلی: " + "کودک");
                break;
            case 5:
                sample.setTypeface(Typeface.DEFAULT);
                s2.setText("فونت فعلی: " + "پیشفرض گوشی");
                break;
        }

        if(!Settings_Activity.loadNightMode(this)){
            s.setTextColor(Color.parseColor("#202020"));
        }else{
            s.setTextColor(Color.parseColor("#ffffff"));
        }


        s.setSelectedIndex(loadFontType(Settings_Activity.this));

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveFontType(position,Settings_Activity.this);
                switch (position){
                    case 0: sample.setTypeface(sans); s2.setText("فونت فعلی: " + "سانس"); break;
                    case 1: sample.setTypeface(roya); s2.setText("فونت فعلی: " + "دروید نسخ"); break;
                    case 2: sample.setTypeface(yekan); s2.setText("فونت فعلی: " + "یکان"); break;
                    case 3: sample.setTypeface(arabic); s2.setText("فونت فعلی: " + "عربیک"); break;
                    case 4: sample.setTypeface(koodak); s2.setText("فونت فعلی: " + "کودک"); break;
                    case 5: sample.setTypeface(Typeface.DEFAULT); s2.setText("فونت فعلی: " + "پیشفرض گوشی"); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DiscreteSeekBar seek = (DiscreteSeekBar) findViewById(R.id.seekBar);

        seek.setMin(App.Min_Font_Size);
        seek.setMax(App.Max_Font_Size);

        seek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                size = value;
                saveFontSize(size,Settings_Activity.this);
                sample.setTextSize(size);
                s4.setText("سایز فعلی: " + size);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });


        size = loadFontSize(Settings_Activity.this);
        sample.setTextSize(size);
        s4.setText("سایز فعلی: " + size);
        seek.setProgress(size);

        LinearLayout mail = (LinearLayout) findViewById(R.id.email);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{App.DeveloperEmail});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                try {
                    startActivity(Intent.createChooser(i, "Send Email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Settings_Activity.this, "برنامه جیمیل را نصب ندارید؟!", Toast.LENGTH_SHORT).show();
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

        NavConstructor.Build(Settings_Activity.this,mDrawerList,mDrawerLayout,DEFAULT_POSITION);

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
        Intent i = new Intent(Settings_Activity.this,MainMenu.class);

        startActivity(i);
        finish();
    }



    public static void saveFontType(int value,Context c) {
        SharedPreferences sp = c.getSharedPreferences("font", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("font", value);
        editor.commit();
    }
    public static int loadFontType(Context c) {
        SharedPreferences sp = c.getSharedPreferences("font", Activity.MODE_PRIVATE);
        int check = sp.getInt("font", 100);
        if(check == 100){
            Log.e("s","test");
            switch (App.Defualt_Font){
                case 1:
                    return sp.getInt("font", 0);
                case 2:
                    return sp.getInt("font", 1);
                case 3:
                    return sp.getInt("font", 2);
                case 4:
                    return sp.getInt("font", 3);
                case 5:
                    return sp.getInt("font", 4);
                case 6:
                    return sp.getInt("font", 5);
                default:
                    return sp.getInt("font", 0);

            }
        }else{
            return sp.getInt("font", 0);
        }
    }


    public static void saveFontSize(int value,Context c) {
        SharedPreferences sp = c.getSharedPreferences("size", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("size", value);
        editor.commit();
    }
    public static int loadFontSize(Context c) {
        SharedPreferences sp = c.getSharedPreferences("size", Activity.MODE_PRIVATE);
        return sp.getInt("size",App.Defualt_Font_Size);
    }


    public static void saveKeepOn(boolean value,Context c) {
        SharedPreferences sp = c.getSharedPreferences("keep", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("keep", value);
        editor.commit();
        //Log.e("s","screen on="+value);
    }
    public static boolean loadKeepOn(Context c) {
        SharedPreferences sp = c.getSharedPreferences("keep", Activity.MODE_PRIVATE);
        return sp.getBoolean("keep", true);
    }


    public static void saveNightMode(boolean value,Context c) {
        SharedPreferences sp = c.getSharedPreferences("nm", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("nm", value);
        editor.commit();
        //Log.e("s","nightmode="+value);
    }
    public static boolean loadNightMode(Context c) {
        SharedPreferences sp = c.getSharedPreferences("nm", Activity.MODE_PRIVATE);
        return sp.getBoolean("nm", false);
    }


    public static void saveMusic(boolean value,Context c) {
        SharedPreferences sp = c.getSharedPreferences("mus", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("mus", value);
        editor.commit();
    }
    public static boolean loadMusic(Context c) {
        SharedPreferences sp = c.getSharedPreferences("mus", Activity.MODE_PRIVATE);
        return sp.getBoolean("mus", App.FirstRunMusic);
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

    public void restart(){
        intenthappen = true;
        Intent res = new Intent(Settings_Activity.this, Settings_Activity.class);

        startActivity(res);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
        if (App.Music && Settings_Activity.loadMusic(this)) {
            if (!BackgroundSoundService.player.isPlaying()) {
                BackgroundSoundService.player.start();
            }
        }
    }
}

