package com.kaspian.book;

import android.app.Activity;
import android.content.Context;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by kaspian on 8/31/2016.
 */
public class VoicePlayer extends LinearLayout {

    private Thread prepare;
    private MediaPlayer mPlayer = null;
    private ProgressBar load;
    private SeekBar seekBar;
    private TextView totaltime;
    private TextView error;
    private ImageView stopstart;
    private ImageView firstlunch;
    private boolean internet;
    private boolean isOnlineMode = false;
    private boolean mCancel = false;
    private Context cc;
    private String TAG2 = "VoicePlayer";
    HttpProxyCacheServer proxy;
    private int rawid;
    private int wseason;
    private int wtut;
    private int wv;
    private String code_1;

    public VoicePlayer(Context context) {
        super(context);
        cc = context;
        //init();
    }

    public VoicePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        cc = context;
        //init();
    }

    public VoicePlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        cc = context;
        //init();
    }



    public void Initialize(boolean online,String code, int raw, int s , int t, int v ){
        isOnlineMode = online;
        rawid = raw;
        code_1 = code;
        wseason = s;
        wtut = t;
        wv = v;
        init();
    }


    private void init() {
        proxy = App.getProxy(cc);
        inflate(getContext(), R.layout.voice, this);
        mPlayer = new MediaPlayer();
        load = (ProgressBar) this.findViewById(R.id.loading);
        seekBar = (SeekBar) this.findViewById(R.id.voiceseek);
        totaltime = (TextView) this.findViewById(R.id.totaltime);
        error = (TextView) this.findViewById(R.id.warn);
        stopstart = (ImageView) this.findViewById(R.id.pp);
        firstlunch = (ImageView) this.findViewById(R.id.fl);
        stopstart.setVisibility(INVISIBLE);
        firstlunch.setVisibility(VISIBLE);
        load.setVisibility(INVISIBLE);

        error.setText("");
        if (mPlayer.isPlaying()) {
            stopstart.setImageResource(R.drawable.pause);
        } else {
            stopstart.setImageResource(R.drawable.play);
        }
        seekBar.setEnabled(false);

        if(isOnlineMode){
            if(proxy.isCached(code_1)){
                setDataSource(code_1,wseason,wtut,wv,false);
            }
        }else{
            create(cc,rawid,wseason,wtut,wv,false);
        }

        firstlunch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying()) {
                    stopstart.setImageResource(R.drawable.play);
                    mPlayer.pause();


                } else {
                    if(App.Music && Settings_Activity.loadMusic(cc)){
                        try{
                            BackgroundSoundService.player.pause();
                        }catch (Exception e){}
                    }

                    if(isOnlineMode){
                        setDataSource(code_1,wseason,wtut,wv,true);
                    }else{
                        create(cc,rawid,wseason,wtut,wv,true);
                    }
                    load.setVisibility(VISIBLE);
                    stopstart.setVisibility(INVISIBLE);
                    firstlunch.setVisibility(INVISIBLE);


                }
            }
        });

        stopstart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying()) {
                    stopstart.setImageResource(R.drawable.play);
                    mPlayer.pause();

                } else {
                    if(App.Music && Settings_Activity.loadMusic(cc)){
                        try{
                            BackgroundSoundService.player.pause();
                        }catch (Exception e){}
                    }

                    start();

                }
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopstart.setImageResource(R.drawable.play);
            }
        });

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                switch (i) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.e(TAG2, "unknown media playback error");
                        error.setText("خطای ناشناخته");
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        error.setText("خطا در اتصال به سرور");
                        Log.e(TAG2, "server connection died");
                    default:
                        Log.e(TAG2, "generic audio playback error");
                        error.setText("خطا در اجرای فایل");
                        break;
                }

                switch (i1) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        Log.e(TAG2, "IO media error");
                        error.setText("خطای ورودی خروجی");
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        Log.e(TAG2, "media error, malformed");
                        error.setText("خطا، فایل معیوب");
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        Log.e(TAG2, "unsupported media content");
                        error.setText("عدم پشتیبانی از فرمت");
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        Log.e(TAG2, "media timeout error");
                        error.setText("خطای زمان انتظار");
                        break;
                    default:
                        Log.e(TAG2, "unknown playback error");
                        error.setText("خطای ناشناخته");
                        break;
                }


                return true;
            }
        });


        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                error.setText("بارگیری اطلاعات...");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        error.setText("");
                    }
                }, 2500);
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    int min = progress / 60;
                    int sec = progress % 60;
                    String realmin;
                    String realsec;
                    if (min < 10) {
                        realmin = "0" + min;
                    } else {
                        realmin = "" + min;
                    }
                    if (sec < 10) {
                        realsec = "0" + sec;
                    } else {
                        realsec = "" + sec;
                    }
                    totaltime.setText(realmin + ":" + realsec);
                    mPlayer.seekTo(progress * 1000);
                }
                if (!fromUser && mPlayer != null) {
                    if (progress != 0) {
                        int min = progress / 60;
                        int sec = progress % 60;
                        String realmin;
                        String realsec;
                        if (min < 10) {
                            realmin = "0" + min;
                        } else {
                            realmin = "" + min;
                        }
                        if (sec < 10) {
                            realsec = "0" + sec;
                        } else {
                            realsec = "" + sec;
                        }
                        totaltime.setText(realmin + ":" + realsec);
                    }

                }
            }
        });



    }

    public void forceStop() {
        try {
            mPlayer.stop();
            mPlayer.release();
        } catch (Exception e) {
            Log.e(TAG2, "cant release and stop");
        }
    }

    public void start() {
        for (int i = 0; i < ShowContent.vps.size(); i++) {
            if(ShowContent.vps.get(i).isPlaying()){
                ShowContent.vps.get(i).pause();
            }
        }

        stopstart.setImageResource(R.drawable.pause);
        LoadSeekBarRefreshSystem();
        mPlayer.start();

    }



    public void stop() {
        mPlayer.stop();
    }

    public void release() {
        mPlayer.release();
    }

    public void pause() {
        mPlayer.pause();
        stopstart.setImageResource(R.drawable.play);
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    public void SeekTo(int i) {
        mPlayer.seekTo(i);
    }




    public void setDataSource(String data, final int season, final int post , final int number, final boolean playit) {
        internet = isNetworkAvailable();

        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String proxyUrl = proxy.getProxyUrl(data);
            //Log.e(TAG2,"CHECK THIS OUT kaspian: "+proxyUrl);
            if(proxy.isCached(data)){
                load.setVisibility(GONE);
            }else{
                load.setVisibility(VISIBLE);
                if(internet){
                    error.setText(cc.getResources().getString(R.string.voice_cache));
                }else{
                    error.setText("عدم دسترسی به اینترنت");
                }
            }
            mPlayer.setDataSource(proxyUrl);


            prepare = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPlayer.prepare();
                    } catch (Exception e) {
                        Log.e("VoicePlayer", "error 1");
                    }
                }
            });
            prepare.start();
        } catch (Exception e) {
            Log.e("VoicePlayer", "error 2");
        }


        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG2, "prepare done!");
                load.setVisibility(GONE);
                stopstart.setVisibility(VISIBLE);
                firstlunch.setVisibility(GONE);
                seekBar.setEnabled(true);
                mPlayer.setVolume(100, 100);
                int duration = mPlayer.getDuration();
                int timeissec = duration / 1000;
                seekBar.setMax(timeissec);

                if(App.SaveVoiceSeekPosition){
                    int progress = ShowContent.loadVoice(season,post,number,cc);
                    Log.e(TAG2,"loaded voice season:"+season + " tut:"+post+" number of voice:"+number+" pos:"+progress);
                    if(progress!=0){
                        mPlayer.seekTo(progress);
                        int progresssecond = progress/1000;
                        seekBar.setProgress(progresssecond);
                        int min = progresssecond / 60;
                        int sec = progresssecond % 60;

                        String realmin;
                        String realsec;
                        if (min < 10) {
                            realmin = "0" + min;
                        } else {
                            realmin = "" + min;
                        }
                        if (sec < 10) {
                            realsec = "0" + sec;
                        } else {
                            realsec = "" + sec;
                        }
                        totaltime.setText(realmin + ":" + realsec);
                    }else{
                        int min = timeissec / 60;
                        int sec = timeissec % 60;
                        String realmin;
                        String realsec;
                        if (min < 10) {
                            realmin = "0" + min;
                        } else {
                            realmin = "" + min;
                        }
                        if (sec < 10) {
                            realsec = "0" + sec;
                        } else {
                            realsec = "" + sec;
                        }
                        Log.e(TAG2, "time: " + min + " : " + sec);
                        totaltime.setText(realmin + ":" + realsec);
                    }
                }else{
                    int min = timeissec / 60;
                    int sec = timeissec % 60;
                    String realmin;
                    String realsec;
                    if (min < 10) {
                        realmin = "0" + min;
                    } else {
                        realmin = "" + min;
                    }
                    if (sec < 10) {
                        realsec = "0" + sec;
                    } else {
                        realsec = "" + sec;
                    }
                    Log.e(TAG2, "time: " + min + " : " + sec);
                    totaltime.setText(realmin + ":" + realsec);
                }


                error.setText("");

                if(playit && ShowContent.showContentIsAlive){
                    start();
                }




            }
        });
    }


    public void create(Context c, int raw, final int season, final int post , final int number,final boolean playit) {
        Log.e(TAG2, "create offline");
        mPlayer = MediaPlayer.create(c, raw);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPlayer.prepare();
                } catch (Exception e) {
                    Log.e("VoicePlayer", "error 1");
                }
            }
        }).start();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                load.setVisibility(GONE);
                firstlunch.setVisibility(GONE);
                stopstart.setVisibility(VISIBLE);
                seekBar.setEnabled(true);
                mPlayer.setVolume(100, 100);
                int duration = mPlayer.getDuration();
                int timeissec = duration / 1000;
                seekBar.setMax(timeissec);

                if(App.SaveVoiceSeekPosition){
                    int progress = ShowContent.loadVoice(season,post,number,cc);
                    Log.e(TAG2,"loaded voice season:"+season + " tut:"+post+" number of voice:"+number+" pos:"+progress);
                    if(progress!=0){
                        mPlayer.seekTo(progress);
                        int progresssecond = progress/1000;
                        seekBar.setProgress(progresssecond);
                        int min = progresssecond / 60;
                        int sec = progresssecond % 60;
                        String realmin;
                        String realsec;
                        if (min < 10) {
                            realmin = "0" + min;
                        } else {
                            realmin = "" + min;
                        }
                        if (sec < 10) {
                            realsec = "0" + sec;
                        } else {
                            realsec = "" + sec;
                        }
                        totaltime.setText(realmin + ":" + realsec);
                    }else{
                        int min = timeissec / 60;
                        int sec = timeissec % 60;
                        String realmin;
                        String realsec;
                        if (min < 10) {
                            realmin = "0" + min;
                        } else {
                            realmin = "" + min;
                        }
                        if (sec < 10) {
                            realsec = "0" + sec;
                        } else {
                            realsec = "" + sec;
                        }
                        Log.e(TAG2, "time: " + min + " : " + sec);
                        totaltime.setText(realmin + ":" + realsec);
                    }
                }else{
                    int min = timeissec / 60;
                    int sec = timeissec % 60;
                    String realmin;
                    String realsec;
                    if (min < 10) {
                        realmin = "0" + min;
                    } else {
                        realmin = "" + min;
                    }
                    if (sec < 10) {
                        realsec = "0" + sec;
                    } else {
                        realsec = "" + sec;
                    }
                    Log.e(TAG2, "time: " + min + " : " + sec);
                    totaltime.setText(realmin + ":" + realsec);
                }

                error.setText("");
                if(playit){
                    start();
                }


            }
        });

    }




    private Handler mHandler = new Handler();
    private void LoadSeekBarRefreshSystem(){
        Activity activity = (Activity) cc;
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mPlayer != null){
                    int mCurrentPosition;
                    try {
                        mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    }catch (Exception e){
                        mCurrentPosition = 0;
                    }

                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) cc.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
