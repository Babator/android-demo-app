package android_demo_app.babator.com.androiddemoapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.interfaces.OnBabatorAds;

import java.lang.reflect.Field;

import android_demo_app.babator.com.androiddemoapp.ads.BBIMAManager;

public class PlayerActivityInline extends AppCompatActivity {
    private static String TAG = "PlayerActivityInline";

    private VideoView mPlayer = null;
    private MediaController mediaControls = null;
    private String API_KEY;
    private BabatorViewHandler mBabatorViewHandler = null;

    private Uri currentURI;
    private static String KEY_SAVED_VIDEO_URI = "SAVED_VIDEO_URI";
    private static String KEY_SAVED_VIDEO_POSITION = "SAVED_VIDEO_POSITION";
    private static String KEY_SAVED_CUSTOMERS = "SAVED_CUSTOMERS";
    private int mVideoPosition = -1;

    protected boolean hasAds = false;
    BBIMAManager mAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inline_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        if(savedInstanceState == null){ //1st creation of activity

        } else { // we already have something. not 1st time of this activity
            currentURI = Uri.parse(savedInstanceState.getString(KEY_SAVED_VIDEO_URI));
            mVideoPosition = savedInstanceState.getInt(KEY_SAVED_VIDEO_POSITION);

        }

        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
        }

        preparePlayer();

    }

    private void preparePlayer() {
        mPlayer = (VideoView) findViewById(R.id.video_view);
        if (!hasAds) {
            Uri video = Uri.parse(getString(R.string.content_url));
            mPlayer.setVideoURI(video);
            mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d("MediaPlayer info", "isPlaying -" + mp.isPlaying());
                    return false;
                }
            });
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        if (mediaControls == null) {
                            mediaControls = new MediaController(PlayerActivityInline.this);
                        }

                        try {
                            mPlayer.setMediaController(mediaControls);
                            mediaControls.setAnchorView(mPlayer);

                        } catch (Exception e) {
                            Log.e(getClass().getSimpleName(), "Error" + e.getMessage());
                        }
                    }
                });
                mPlayer.start();
                if(mVideoPosition != -1){
                    mPlayer.seekTo(mVideoPosition);
                }
            }
        });
    }

    protected void loadAds(String url) {
        if (hasAds) {
            mAdManager = new BBIMAManager(getApplicationContext(), url);
            mAdManager.setListener(mBabatorViewHandler.getBabator());
            ViewGroup adContainer = (ViewGroup) findViewById(R.id.adContainer);
            mAdManager.requestAds(mPlayer, getString(R.string.ad_tag_url), adContainer);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SAVED_VIDEO_URI, currentURI.toString());
        outState.putInt(KEY_SAVED_VIDEO_POSITION, mVideoPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
        mBabatorViewHandler = new BabatorViewHandler(this, mPlayer, this.getClass());
        mBabatorViewHandler.initialize(API_KEY);
        mBabatorViewHandler.setListener(new BabatorViewHandler.Listener() {
            @Override
            public void onVideoSelected(BabatorViewHandler handler, BBVideoParams videoParams) {
                Uri video = Uri.parse(videoParams.getVideoId());
                mPlayer.setVideoURI(video);
            }
        });
        mBabatorViewHandler.getBabator().setOnBabatorAds(new OnBabatorAds() {
            @Override
            public boolean shouldLoadAd(String recommendationUrl) {
                loadAds(recommendationUrl);
                return true;
            }
        });
        loadAds(getString(R.string.content_url));

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mVideoPosition = mPlayer.getCurrentPosition();
            if(mBabatorViewHandler != null){
                mBabatorViewHandler.dispose();
            }
        }
        catch (Exception e){

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
    }

    private Object fetchFieldByName(String name) {
        Object fetched = null;
        try {
            Field field = VideoView.class.getDeclaredField(name);
            field.setAccessible(true);
            fetched = field.get(mPlayer);
        } catch(Exception e) {}
        return fetched;
    }
}
