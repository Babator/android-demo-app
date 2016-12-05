package android_demo_app.babator.com.androiddemoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.interfaces.OnBabatorAds;

import android_demo_app.babator.com.androiddemoapp.ads.BBIMAManager;


public class PlayerActivity extends Activity {
    private static String TAG = "PlayerActivity";
    private BabatorViewHandler mBabatorViewHandler = null;
    private VideoView mPlayer = null;
    private MediaController mediaControls = null;
    private String API_KEY;

    private boolean hasAds = false;
    private BBIMAManager mAdManager;
    private Uri initialUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
            hasAds = intent.getBooleanExtra("Ads", true);
        }

        preparePlayer();
    }

    private void preparePlayer() {
        mPlayer = (VideoView) findViewById(R.id.video_view);
        if (!hasAds) {
            initialUri = Uri.parse(getString(R.string.content_url));
            mPlayer.setVideoURI(initialUri);
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
                            mediaControls = new MediaController(PlayerActivity.this);
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
            }
        });
    }

    private void loadAds(String url) {
        if (hasAds) {
            mAdManager = new BBIMAManager(getApplicationContext(), url);
            mAdManager.setListener(mBabatorViewHandler.getBabator());
            ViewGroup adContainer = (ViewGroup) findViewById(R.id.adContainer);
            mAdManager.requestAds(mPlayer, getString(R.string.ad_tag_url), adContainer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //region BabatorViewHandler object
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
        mBabatorViewHandler = new BabatorViewHandler(this, mPlayer, this.getClass(), initialUri);
        mBabatorViewHandler.initialize(API_KEY);
        mBabatorViewHandler.setListener(new BabatorViewHandler.Listener() {
            @Override
            public void onVideoSelected(BabatorViewHandler handler, BBVideoParams videoParams) {

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
        //endregion
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
