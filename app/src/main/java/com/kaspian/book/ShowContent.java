	package com.kaspian.book;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.angmarch.views.NiceSpinner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



public class ShowContent extends Activity {

    public SharedPreferences shared;
    public SharedPreferences.Editor editor;
    public ImageView iv_favorites;
    public ImageView quicksettings;
    LinearLayout quicksettslayout;
    LinearLayout contentlinear;
    LinearLayout sl;
    NiceSpinner fontspin;
    TextView title;
    TextViewPlus textt;
    DiscreteSeekBar fontseek;
    Button done;
    int which_season;
    int which_tuts;
    Intent intentsss;
    Typeface sans;
    Typeface sansb;
    Typeface roya;
    Typeface yekan;
    Typeface arabic;
    Typeface koodak;
    Typeface MAINTF;
    ScrollView sc;
    int size;
    ImageView share;
    DataBase db;
    int api;
    LinearLayout.LayoutParams params;
    LinearLayout.LayoutParams paramsb;
    LinearLayout.LayoutParams paramslink;
    LinearLayout.LayoutParams paramsvideo;
    public String text_for_share = "";
    ArrayList<TextView> tvs;
    ArrayList<Button> btns;
    public static ArrayList<VoicePlayer> vps;
    LinearLayout night;
    ToggleButton nightmode;
    boolean intenthappen = false;
    boolean ress = false;
    static boolean showContentIsAlive = false;

    View nonVideoLayout;
    ViewGroup videoLayout;
    View loadingView;

    public static int whichvideo;


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
        if(Settings_Activity.loadKeepOn(ShowContent.this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        nonVideoLayout = findViewById(R.id.contentlinear);
        videoLayout = (ViewGroup)findViewById(R.id.fullscreen);
        loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);

        if(!Settings_Activity.loadNightMode(this)){
            setContentView(R.layout.show_content);
        }else{
            setContentView(R.layout.show_content_dark);
        }
        intenthappen = false;



        quicksettslayout = (LinearLayout) findViewById(R.id.quicklayout);
        sc = (ScrollView) findViewById(R.id.sc);
        night = (LinearLayout) findViewById(R.id.night);
        textt = (TextViewPlus) findViewById(R.id.textt);
        nightmode = (ToggleButton) findViewById(R.id.nightmode);

        intentsss = getIntent();
        which_tuts = intentsss.getIntExtra("w",1);
        which_season = intentsss.getIntExtra("s",1);
        ress = intentsss.getBooleanExtra("restart",false);
        final int WhereWasScroll = loadScrollc();
        int delay;
        if(Build.VERSION.SDK_INT>19){
            delay = 200;
        }else{
            delay = 300;
        }

        Log.e("saeeeed",WhereWasScroll+"");
        if(ress){
            quicksettslayout.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sc.scrollTo(0,WhereWasScroll);
                }
            }, delay);

        }else{
            quicksettslayout.setVisibility(View.GONE);
            if(App.SaveReadingPosition){
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofInt(sc, "scrollY",  WhereWasScroll).setDuration(500).start();
                    }
                }, delay);
            }
        }



        if(Settings_Activity.loadNightMode(ShowContent.this)){
            nightmode.setToggleOn();
        }else{
            nightmode.setToggleOff();
        }

        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Settings_Activity.loadNightMode(ShowContent.this)){
                    nightmode.setToggleOff();
                    Settings_Activity.saveNightMode(false,ShowContent.this);
                    restart(sc);

                }else{
                    nightmode.setToggleOn();
                    Settings_Activity.saveNightMode(true,ShowContent.this);
                    restart(sc);
                }
            }
        });

        nightmode.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(!on){
                    Settings_Activity.saveNightMode(false,ShowContent.this);
                    restart(sc);
                }else{
                    Settings_Activity.saveNightMode(true,ShowContent.this);
                    restart(sc);
                }
            }
        });


        title = (TextView) findViewById(R.id.title_bar);
        sl = (LinearLayout) findViewById(R.id.sl);
        if(!App.Share){
            sl.setVisibility(View.GONE);
        }
        api = Build.VERSION.SDK_INT;
        share = (ImageView) findViewById(R.id.share);
        contentlinear = (LinearLayout) findViewById(R.id.contentlinear);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 12, 0, 12);

        int valueInPixels = (int) getResources().getDimension(R.dimen.buttons_height);

        paramsb = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                valueInPixels);
        paramsb.setMargins(0, 12, 0, 12);

        paramslink = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                valueInPixels);
        paramslink.setMargins(1, 0, 1, 0);

        paramsvideo = new LinearLayout.LayoutParams(
                100,
                100);

        tvs = new ArrayList<TextView>();
        btns = new ArrayList<Button>();
        vps = new ArrayList<VoicePlayer>();

        size = Settings_Activity.loadFontSize(ShowContent.this);

        sansb = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");
        roya = Typeface.createFromAsset(getAssets(), "fonts/naskh.ttf");
        yekan = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        arabic = Typeface.createFromAsset(getAssets(), "fonts/arabic.ttf");
        koodak = Typeface.createFromAsset(getAssets(), "fonts/koodak.ttf");

        final RelativeLayout guid = (RelativeLayout) findViewById(R.id.guid_layout);
        final Button guidb = (Button) findViewById(R.id.guidb);
        guidb.setTypeface(sansb);
        if (isFirstTime()) {
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



        db = new DataBase(this);
        Log.e("s", " " + which_season + " " + which_tuts);
        String content = db.getContent(which_season,which_tuts);
        String[] contentarray = content.split("\\!\\#");

        switch (Settings_Activity.loadFontType(ShowContent.this)){
            case 0: MAINTF=sans;  break;
            case 1: MAINTF=roya;  break;
            case 2: MAINTF=yekan;  break;
            case 3: MAINTF=arabic;  break;
            case 4: MAINTF=koodak;  break;
            case 5: MAINTF=Typeface.DEFAULT; break;
        }



        int whichvoice = 0;
        whichvideo = 0;
        for (String part : contentarray) {

            if(part.contains("img==")){

                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                String code[] = part.split("==");
                int resID = getResources().getIdentifier(code[1], "mipmap", getPackageName());

                ImageView subImage = new ImageView(this);
                subImage.setImageResource(resID);
                subImage.setVisibility(View.VISIBLE);
                subImage.setLayoutParams(params);

                contentlinear.addView(subImage);

            }else if(part.contains("link==")){

                part = part.replace("\r\n", "").replace("\n","");
                final String code[] = part.split("==");

                Button blink = new Button(this);
                blink.setBackgroundResource(R.drawable.themainbutton);
                blink.setVisibility(View.VISIBLE);
                blink.setLayoutParams(paramsb);

                blink.setText(code[1]);
                blink.setTextColor(getResources().getColor(R.color.text_light));

                blink.setTextSize(size);
                blink.setTypeface(MAINTF);

                blink.setPadding(20,0,20,0);

                btns.add(blink);

                blink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = code[2];
                        if (!url.startsWith("http://") && !url.startsWith("https://")){
                            url = "http://" + url;
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                contentlinear.addView(blink);

            }else if(part.contains("gif==")){

                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                String code[] = part.split("==");
                int resID = getResources().getIdentifier(code[1], "mipmap", getPackageName());

                GifView gif = new GifView(this);
                gif.setMovieResource(resID);
                gif.setVisibility(View.VISIBLE);
                gif.setLayoutParams(params);

                contentlinear.addView(gif);

            }else if(part.contains("imgzoom==")){

                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                String code[] = part.split("==");
                int resID = getResources().getIdentifier(code[1], "mipmap", getPackageName());

                TouchImageView zoom = new TouchImageView(this);
                zoom.setImageResource(resID);
                zoom.setVisibility(View.VISIBLE);
                zoom.setLayoutParams(params);

                contentlinear.addView(zoom);

            }else if(part.contains("offlinevideo==")){


                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                final String code[] = part.split("==");
                final int vv = whichvideo;

                ImageView vidprev = new ImageView(this);
                vidprev.setImageResource(R.drawable.video_preview);
                vidprev.setVisibility(View.VISIBLE);
                vidprev.setLayoutParams(params);
                vidprev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tt = sc.getScrollY();
                        saveScrollc(tt);
                        Intent g = new Intent(ShowContent.this,PlayVideo.class);
                        g.putExtra("s", which_season);
                        g.putExtra("w",which_tuts);
                        g.putExtra("whichvid",vv);
                        g.putExtra("code2",code[1]);
                        g.putExtra("code","");
                        try {
                            if(intentsss.getStringExtra("from").equalsIgnoreCase("Fav")){
                                g.putExtra("from","Fav");
                            }
                        }catch (Exception e){}
                        startActivity(g);
                        finish();
                    }
                });
                contentlinear.addView(vidprev);
                whichvideo++;

            }else if(part.contains("video==")){

                final int vv = whichvideo;
                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                final String code[] = part.split("==");


                ImageView vidprev = new ImageView(this);
                vidprev.setImageResource(R.drawable.video_preview);
                vidprev.setVisibility(View.VISIBLE);
                vidprev.setLayoutParams(params);
                if (!code[1].startsWith("http://") && !code[1].startsWith("https://")){
                    code[1] = "http://" + code[1];
                }
                vidprev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tt = sc.getScrollY();
                        saveScrollc(tt);
                        Intent g = new Intent(ShowContent.this,PlayVideo.class);
                        g.putExtra("s", which_season);
                        g.putExtra("w",which_tuts);
                        g.putExtra("whichvid",vv);
                        g.putExtra("code",code[1]);
                        g.putExtra("code2","");
                        try {
                            if(intentsss.getStringExtra("from").equalsIgnoreCase("Fav")){
                                g.putExtra("from","Fav");
                            }
                        }catch (Exception e){}
                        startActivity(g);
                        finish();
                    }
                });
                contentlinear.addView(vidprev);
                whichvideo++;

            }else if(part.contains("offlinevoice==")){

                Log.w("show","offline voice section started");
                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                final String code[] = part.split("==");
                int resID = getResources().getIdentifier(code[1], "raw", getPackageName());
                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                VoicePlayer vp = new VoicePlayer(this);
                vp.Initialize(false,null,resID,which_season,which_tuts,whichvoice);

                contentlinear.addView(vp,para);
                vps.add(vp);
                whichvoice++;

            }else if(part.contains("voice==")){
                Log.w("show","online voice section started");

                part = part.replace("\r\n", "").replace("\n","").replace(" ","");
                final String code[] = part.split("==");

                if (!code[1].startsWith("http://") && !code[1].startsWith("https://")){
                    code[1] = "http://" + code[1];
                }

                Log.e("SSSkaspian>>>>>","Voice url is: "+ code[1]);

                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                VoicePlayer vp = new VoicePlayer(this);
                vp.Initialize(true,code[1],0,which_season,which_tuts,whichvoice);
                contentlinear.addView(vp,para);
                vps.add(vp);
                whichvoice++;

            } else {

                TextView tv = new TextView(this);

                if(App.CopyText){
                    tv.setTextIsSelectable(true);
                }else{
                    tv.setTextIsSelectable(false);
                }

                part = part.trim();
                if(!Settings_Activity.loadNightMode(this)){
                    tv.setTextColor(this.getResources().getColor(R.color.text_dark));
                }else{
                    tv.setTextColor(this.getResources().getColor(R.color.text_light));
                }

                for(int i=0;i<2;i++){
                    if(part.startsWith("[left]")){
                        part = part.replace("[left]","");
                        tv.setGravity(Gravity.LEFT);
                    }else if(part.startsWith("[center]")) {
                        part = part.replace("[center]", "");
                        tv.setGravity(Gravity.CENTER);
                    }else if(part.startsWith("[color:")) {
                        String colorcode = part.substring(0,14);
                        String color[] = colorcode.split(":");
                        String fina = color[1].replace("]","");
                        Log.e("kaspian",fina);
                        part = part.substring(14);
                        tv.setTextColor(Color.parseColor("#"+fina.trim()));
                    }else{
                        if(i!=1){
                            tv.setGravity(Gravity.RIGHT);
                        }
                    }
                }


                part = part.trim();
                tv.setText(part);
                text_for_share = text_for_share + part + "\n";
                tv.setLayoutParams(params);
                tv.setTextSize(size);
                tv.setTypeface(MAINTF);
                if(MAINTF==sans || MAINTF == roya){
                    tv.setLineSpacing(3,1);
                }else{
                    tv.setLineSpacing(15,1);
                }

                contentlinear.addView(tv);
                tvs.add(tv);

            }

        }

//        if(App.SaveVoiceVideoSeekPosition){
//            for (int i = 0; i < vps.size(); i++) {
//                vps.get(i).SeekTo(loadVoice(i,ShowContent.this));
//            }
//        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text_for_share);
                startActivity(Intent.createChooser(sharingIntent,"اشتراک گذاری"));
            }
        });

        TextView title = (TextView) findViewById(R.id.title_bar);

        String stringtitle = db.getTitle(which_season,which_tuts);

        title.setText(stringtitle);

        shared = getSharedPreferences("Prefs", MODE_PRIVATE);
        editor = shared.edit();

        quicksettings = (ImageView) findViewById(R.id.quicksettings);

        fontspin = (NiceSpinner) findViewById(R.id.quickspinner);
        fontseek = (DiscreteSeekBar) findViewById(R.id.quickseek);
        done = (Button) findViewById(R.id.quickdone);

        fontseek.setMin(App.Min_Font_Size);
        fontseek.setMax(App.Max_Font_Size);


        quicksettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quicksettslayout.getVisibility() == View.VISIBLE) {
                    quicksettslayout.setVisibility(View.GONE);
                    try {
                        quicksettslayout.startAnimation(AnimationUtils.loadAnimation(ShowContent.this, android.R.anim.fade_out));
                    } catch (Exception e) {}
                }else{
                    quicksettslayout.setVisibility(View.VISIBLE);
                    try {
                        quicksettslayout.startAnimation(AnimationUtils.loadAnimation(ShowContent.this, android.R.anim.fade_in));
                    } catch (Exception e) {}
                }
            }
        });

        done.setTypeface(sansb);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quicksettslayout.setVisibility(View.GONE);
                try {
                    quicksettslayout.startAnimation(AnimationUtils.loadAnimation(ShowContent.this, android.R.anim.fade_out));
                } catch (Exception e) {}
            }
        });

        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.font_selector)));
        fontspin.attachDataSource(dataset);

        fontspin.setSelectedIndex(Settings_Activity.loadFontType(ShowContent.this));
        fontspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings_Activity.saveFontType(position, ShowContent.this);
                switch (position){
                    case 0: MAINTF=sans;  break;
                    case 1: MAINTF=roya;  break;
                    case 2: MAINTF=yekan;  break;
                    case 3: MAINTF=arabic;  break;
                    case 4: MAINTF=koodak;  break;
                    case 5: MAINTF=Typeface.DEFAULT; break;
                }

                for(int i=0 ; i < tvs.size() ; i++){
                    tvs.get(i).setTypeface(MAINTF);
                    if(MAINTF==sans || MAINTF == roya){
                        tvs.get(i).setLineSpacing(3,1);
                    }else{
                        tvs.get(i).setLineSpacing(15,1);
                    }
                }

                for(int i=0 ; i < btns.size() ; i++){
                    btns.get(i).setTypeface(MAINTF);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(!Settings_Activity.loadNightMode(this)){
            fontspin.setTextColor(Color.parseColor("#202020"));
        }else{
            fontspin.setTextColor(Color.parseColor("#ffffff"));
        }


        fontseek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                size = value;
                Settings_Activity.saveFontSize(size,ShowContent.this);
                for(int i=0 ; i < tvs.size() ; i++){
                    tvs.get(i).setTextSize(size);
                }
                for(int i=0 ; i < btns.size() ; i++){
                    btns.get(i).setTextSize(size);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });

        fontseek.setProgress(size);

        iv_favorites = (ImageView) findViewById(R.id.bookmark_bar);
        String tag = which_season + "_" + which_tuts;
        final Boolean b1 = shared.getBoolean(tag, false);
        if (b1){
            iv_favorites.setImageDrawable(getResources().getDrawable(R.drawable.bookmarked));
        }else{
            iv_favorites.setImageDrawable(getResources().getDrawable(R.drawable.not_bookmard));
        }

        iv_favorites.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String tag = which_season + "_" + which_tuts;
                Log.e("s", "our tag is" +tag);
                Boolean b2 = shared.getBoolean(tag, false);
                if (b2){
                    editor.putBoolean(tag, false);
                    editor.apply();
                    iv_favorites.setImageResource(R.drawable.not_bookmard);
                    // show message
                    String message = getResources().getString(R.string.remove_fav);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean(tag, true);
                    editor.apply();
                    iv_favorites.setImageResource(R.drawable.bookmarked);
                    // show message
                    String message = getResources().getString(R.string.addto_fav);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore2", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore2", true);
            editor.commit();
        }
        return !ranBefore;
    }


    @Override
    public void onBackPressed() {

        try {
            if(intentsss.getStringExtra("from").equalsIgnoreCase("Fav")){
                intenthappen =true;
                Intent i1 = new Intent(ShowContent.this,Favorites.class);
                startActivity(i1);
                finish();
            }else{
                intenthappen =true;
                Intent g = new Intent(ShowContent.this,ContentMenu.class);
                g.putExtra("s", which_season);
                startActivity(g);
                finish();
                //super.onBackPressed();
            }
        }catch (Exception e){
            intenthappen =true;
            Intent g = new Intent(ShowContent.this,ContentMenu.class);
            g.putExtra("s", which_season);
            startActivity(g);
            finish();
        }

    }

    int tt;
    public void restart(ScrollView sv){

        tt = sv.getScrollY();
        intenthappen =true;
        saveScrollc(tt);
        Log.e("kaspian",tt+"");
        Intent res = new Intent(ShowContent.this, ShowContent.class);
        res.putExtra("restart",true);
        res.putExtra("s", which_season);
        res.putExtra("w", which_tuts);
        res.putExtra("from", "list");
        startActivity(res);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }


    private int loadScrollc() {
        String tag = which_season+""+which_tuts;
        SharedPreferences sp = getSharedPreferences("C"+tag, Activity.MODE_PRIVATE);
        return sp.getInt("C"+tag, 0);
    }

    private void saveScrollc(int value) {
        String tag = which_season+""+which_tuts;
        SharedPreferences sp = getSharedPreferences("C"+tag, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("C"+tag, value);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tt = sc.getScrollY();
        saveScrollc(tt);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("kaspian","hey kaspian onPause Happened -  Good For Voices!!!!");
        if (!intenthappen && !NavConstructor.intenthappennav && App.Music && Settings_Activity.loadMusic(this)) {
            BackgroundSoundService.player.pause();
        }



        for (int i = 0; i < vps.size(); i++) {
            if(vps.get(i).isPlaying()){
                vps.get(i).pause();
            }
            if(App.SaveVoiceSeekPosition){
                saveVoice(which_season,which_tuts,i,vps.get(i).getCurrentPosition(),ShowContent.this);
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        showContentIsAlive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        showContentIsAlive = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void saveVoice(int Season,int Post,int Number,int value,Context c) {
        String TAG = "voice"+Season+""+Post+""+Number;
        Log.e("saved tag >>>>>>>>",TAG + "and pos:"+value);
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(TAG, value);
        editor.commit();
    }

    public static int loadVoice(int Season,int Post,int Number, Context c) {
        String TAG = "voice"+Season+""+Post+""+Number;
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        return sp.getInt(TAG,0);
    }


}

