package android_demo_app.babator.com.androiddemoapp;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.interfaces.OnBabatorAds;

import java.io.IOException;

import android_demo_app.babator.com.androiddemoapp.ads.BBIMAManager;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaController.MediaPlayerControl
{
    private static final String TAG = "MediaPlayerActivity";

    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;
    private MediaPlayer.OnSeekCompleteListener onSeekCompleteListener;
    private MediaPlayer.OnInfoListener onInfoListener;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private final BabatorViewHandler mBabatorViewHandler = null;

    private Uri initialUri;
    private boolean hasAds = false;
    private BBIMAManager mAdManager;
    private ViewGroup.LayoutParams initialParams;
    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
            hasAds = intent.getBooleanExtra("Ads", true);
        }

        if(initialUri == null){
            initialUri = Uri.parse(getString(R.string.content_url));
        }

        mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mMediaController.show();
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initialParams = mSurfaceView.getLayoutParams();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mMediaPlayer.setDataSource(getString(R.string.content_url));
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaController = new MediaController(this);
            mMediaController.setEnabled(true);
            mMediaController.show();

            //TODO Remove the toast and enable code below
            if(hasAds) {
                Toast.makeText(this, "Ads. are not supported yet with MediaPlayer.", Toast.LENGTH_LONG).show();
                hasAds = false;
            }

            //region BabatorViewHandler object
           /* if(mBabatorViewHandler != null){
                mBabatorViewHandler.dispose();
            }
            mBabatorViewHandler = new BabatorViewHandler(this, mMediaPlayer, mSurfaceView, this.getClass(), initialUri) ;
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
            });*/
            loadAds(getString(R.string.content_url));
            //endregion
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    private void handleAspectRatio() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mSurfaceView.setLayoutParams(lp);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        handleAspectRatio();
        mMediaPlayer.start();
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mSurfaceView);
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                // Clear listeners
                mMediaPlayer.setOnPreparedListener(null);
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.setOnVideoSizeChangedListener(null);
                mMediaPlayer.setOnBufferingUpdateListener(null);
                mMediaPlayer.setOnErrorListener(null);
                mMediaPlayer.setOnSeekCompleteListener(null);
                mMediaPlayer.setOnInfoListener(null);
                onSeekCompleteListener = null;
                onInfoListener = null;

                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "Error releasing media player", e);
            }
        }
    }

    private void loadAds(String url) {
        if (hasAds) {
            mAdManager = new BBIMAManager(getApplicationContext(), url);
            mAdManager.setListener(mBabatorViewHandler.getBabator());
            ViewGroup adContainer = (ViewGroup) findViewById(R.id.adContainer);
            mAdManager.requestAds(mMediaPlayer, getString(R.string.ad_tag_url), adContainer);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        handleAspectRatio();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
        releaseMediaPlayer();
        mSurfaceHolder.removeCallback(this);
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
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        if (onInfoListener == null) {
            return false;
        } else {
            return onInfoListener.onInfo(mediaPlayer, what, extra);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (onSeekCompleteListener != null) {
            onSeekCompleteListener.onSeekComplete(mediaPlayer);
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}

