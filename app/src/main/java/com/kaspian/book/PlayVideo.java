package com.kaspian.book;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

public class PlayVideo extends Activity implements UniversalVideoView.VideoViewCallback {

    String code;
    String code2;
    Intent intentsss;
    int which_season;
    int which_tuts;
    int whichvideo;
    private static final String TAG = "PlayVideo";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;

    View mVideoLayout;

    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;
    private HttpProxyCacheServer proxy;
    private boolean internet;
    boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Settings_Activity.loadKeepOn(PlayVideo.this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.palyvideo);

//        if( savedInstanceState != null ) {
//            mSeekPosition = savedInstanceState.getInt(SEEK_POSITION_KEY);
//            Log.e(TAG,"seek pos loaded:" +mSeekPosition);
//        }
        proxy = App.getProxy(PlayVideo.this);

        intentsss = getIntent();
        code = intentsss.getStringExtra("code");
        code2 = intentsss.getStringExtra("code2");
        which_tuts = intentsss.getIntExtra("w",1);
        which_season = intentsss.getIntExtra("s",1);
        whichvideo = intentsss.getIntExtra("whichvid",0);


        Log.e("kaspian","wich wiv = " + whichvideo);
        //Log.w(TAG,"kaspian code is " + code + " and code 2 " + code2);


        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);

        internet = isNetworkAvailable();

        mMediaController.hideLoading();
        if(!internet && code2.equals("")){
            if(proxy.isCached(code)){
                mMediaController.hideLoading();
            }else{
                mMediaController.showLoading();
                Toast.makeText(PlayVideo.this,getResources().getString(R.string.video_cache),Toast.LENGTH_LONG).show();

            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoView.setMediaController(mMediaController);
            }
        }, 100);

        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);

        final int RecentSeekPos = loadVideoRecent(which_season,which_tuts,whichvideo,PlayVideo.this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if(App.SaveVideoSeekPosition){
                    Log.e(TAG,"from recent: " + App.fromrecent);
                    if (App.fromrecent) {
                        if (RecentSeekPos > 0) {
                            mVideoView.seekTo(RecentSeekPos);
                        }
                    }else{
                        int seek = loadVideo(which_season,which_tuts,whichvideo,PlayVideo.this);
                        if(seek>0){
                            mVideoView.seekTo(seek);
                            Log.d(TAG, "seeking to = " + seek);
                        }
                    }

                }else{
                    if (mSeekPosition > 0) {
                        mVideoView.seekTo(mSeekPosition);
                    }
                }
                mVideoView.start();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer media, int what, int extra) {
                if (what == 100)
                {
                    mVideoView.stopPlayback();
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mVideoView.start();
                        }
                    });

                }
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion ");
                completed = true;

            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.seekTo(mSeekPosition);
            mVideoView.start();
        }
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                if(code2.equals("")){//means onlnie request
                    Log.w(TAG,"kaspian it try online");
                    if(proxy.isCached(code)){
                        mMediaController.hideLoading();
                    }else{
                        mMediaController.showLoading();
                        if(internet){
                            //Toast.makeText(PlayVideo.this,getResources().getString(R.string.caching),Toast.LENGTH_LONG).show();
                        }
                    }
                    String proxyUrl = proxy.getProxyUrl(code);
                    Log.e(TAG,"CHECK THIS OUT kaspian: "+ proxyUrl);
                    mVideoView.setVideoPath(proxyUrl);
                }else{ //means offline request
                    Log.w(TAG,"kaspian it try oFFline");
                    int resID = getResources().getIdentifier(code2, "raw", getPackageName());
                    String path = "android.resource://" + getPackageName() + "/" + resID;
                    mVideoView.setVideoURI(Uri.parse(path));
                }

                mVideoView.requestFocus();
            }
        });
    }



//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
//        mSeekPosition = mVideoView.getCurrentPosition();
//        mVideoView.pause();
//        fromrecent = true;
//        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle outState) {
//        super.onRestoreInstanceState(outState);
//        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
//        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
//    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSeekPosition = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "XXXXXXXXXXXX - Distroyed playvideo activity - XXXXXXXXXXXXX  fromrecent = false");
        super.onDestroy();
        App.fromrecent = false;
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPause.");
        mSeekPosition = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on Stop.");
        mSeekPosition = mVideoView.getCurrentPosition();
        App.fromrecent = true;
        saveVideoRecent(which_season,which_tuts,whichvideo,mSeekPosition,PlayVideo.this);
        Log.e(TAG, "XX - Pauseed playvideo activity - XX  fromrecent = true   And saved pos is:" + mSeekPosition );
        mVideoView.pause();
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
    }


    @Override
    public void onBackPressed() {
        if(completed){
            saveVideo(which_season,which_tuts,whichvideo,0,PlayVideo.this);
        }else{
            saveVideo(which_season,which_tuts,whichvideo,mVideoView.getCurrentPosition(),PlayVideo.this);
        }

        Log.d(TAG, "seek saved = " + mVideoView.getCurrentPosition());
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            Intent i = new Intent(PlayVideo.this,ShowContent.class);
            i.putExtra("w", which_tuts);
            i.putExtra("s", which_season);
            try{
                if(intentsss.getStringExtra("from")!=null){
                    if(intentsss.getStringExtra("from").equalsIgnoreCase("Fav")){
                        i.putExtra("from","Fav");
                    }
                }
            }catch (Exception e){
            }

            startActivity(i);
            finish();
        }
    }


    public static void saveVideo(int Season,int Post,int Number,int value,Context c) {
        String TAG = "video"+Season+""+Post+""+Number;
        Log.e("saved tag >>>>>>>>",TAG);
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(TAG, value);
        editor.commit();
    }

    public static int loadVideo(int Season,int Post,int Number, Context c) {
        String TAG = "video"+Season+""+Post+""+Number;
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        return sp.getInt(TAG,0);
    }

    public static void saveVideoRecent(int Season,int Post,int Number,int value,Context c) {
        String TAG = "rec"+Season+""+Post+""+Number;
        Log.e("saved tag >>>>>>>>",TAG);
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(TAG, value);
        editor.commit();
    }

    public static int loadVideoRecent(int Season,int Post,int Number, Context c) {
        String TAG = "rec"+Season+""+Post+""+Number;
        SharedPreferences sp = c.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        return sp.getInt(TAG,0);
    }


}
