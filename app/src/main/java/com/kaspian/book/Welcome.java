package com.kaspian.book;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class Welcome extends Activity {
    Intent intent;
    boolean intenthappen = false;
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

        if(App.FullScreenWelcome){
            setContentView(R.layout.welcome2);
        }else{
            setContentView(R.layout.welcome);
        }

        intent = new Intent(Welcome.this,MainMenu.class);

        if(App.Music && Settings_Activity.loadMusic(Welcome.this)){
            startService(new Intent(Welcome.this,BackgroundSoundService.class));
        }else{
            stopService(new Intent(Welcome.this,BackgroundSoundService.class));
        }

        intenthappen = false;
        if(App.Welcome_Screen){
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    intenthappen =true;
                    startActivity(intent);
                    finish();

                }
            }, App.Welcome_Time);
        }else{
            intenthappen =true;
            startActivity(intent);
            finish();
        }



    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!intenthappen && !NavConstructor.intenthappennav && App.Music && Settings_Activity.loadMusic(this)){
            try{
                BackgroundSoundService.player.pause();
            }catch (Exception e){

            }

        }
    }





}
