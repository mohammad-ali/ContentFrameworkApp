package com.kaspian.book;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.Locale;

public class App extends Application {

    // Your Info:

    public static String DeveloperName = "" ;
    public static String DeveloperEmail = "" ;
    public static String DeveloperSite = "" ;
    public static String DeveloperbazzarID = "" ;



    // You Can Change This Settings:

    public static int DataBase_Version = 1 ;
    public static int Min_Font_Size = 10 ;
    public static int Max_Font_Size = 27 ;
    public static int Defualt_Font = 1 ;   // 1=sans  2=droid naskh  3=yekan  4=adobe arabic  5=kodaak  6=mobile
    public static int Defualt_Font_Size = 16 ;
    public static boolean User_Help_Screen = true ;
    public static boolean Rate_App = true ;
    public static int Welcome_Time = 2000 ; // 2000 = 2 s
    public static boolean Header = true ;
    public static boolean Share = true ;
    public static boolean Welcome_Screen = true ;
    public static boolean CopyText = true ;
    public static boolean DoubleTapToExit = true ;
    public static boolean Divider = false ;
    public static boolean SaveReadingPosition = true ;
    public static boolean Music = true ;
    public static boolean FullScreenWelcome = false ;
    public static boolean SaveVoiceSeekPosition = true ;
    public static boolean SaveVideoSeekPosition = true ;







    //Database Security
    public static boolean SecureDatabase = false ;
    public static int SeasonCount = 0 ;
    public static int FirstSeasonCount = 0 ;



    //Don't Change this:!

    public static Typeface sans;
    public static Typeface sansb;
    @Override
    public void onCreate() {
        super.onCreate();

        sansb = Typeface.createFromAsset(getAssets(), "fonts/sans_bold.ttf");
        sans = Typeface.createFromAsset(getAssets(), "fonts/sans.ttf");

        Configuration newConfig  = new Configuration();
        newConfig.locale = Locale.ENGLISH;
        super.onConfigurationChanged(newConfig);
        Locale.setDefault(newConfig.locale);
        getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());

    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }
	public static boolean fromrecent = false;
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
    
	
	
	public static boolean FirstRunMusic = true ;

}
