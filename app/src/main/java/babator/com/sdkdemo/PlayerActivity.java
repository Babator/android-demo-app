package babator.com.sdkdemo;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.interfaces.OnBabatorAds;

import babator.com.sdkdemo.ads.BBIMAManager;

public class PlayerActivity extends BasePlayerActivity {
    private VideoView mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        preparePlayer();
    }

    private void preparePlayer() {
        mPlayer = (VideoView) findViewById(R.id.video_view);
        if (!hasAds) {
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
                        if (mMediaController == null) {
                            mMediaController = new MediaController(PlayerActivity.this);
                        }

                        try {
                            mPlayer.setMediaController(mMediaController);
                            mMediaController.setAnchorView(mPlayer);

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

}
