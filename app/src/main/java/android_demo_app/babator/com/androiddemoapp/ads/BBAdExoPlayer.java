package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class BBAdExoPlayer extends BBAdVideoViewPlayer {

    private PlayerState mCurrentState = PlayerState.unknown;
    private Handler stateHandler;

    private enum PlayerState {
        unknown,
        idle,
        paused,
        playing
    };

    private void updatePlayerState() {
        if (stateHandler == null){
            return;
        }
        stateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    final boolean playWhenReady = ((SimpleExoPlayer)mPlayer).getPlayWhenReady();
                    switch(((SimpleExoPlayer)mPlayer).getPlaybackState()){
                        case ExoPlayer.STATE_READY:
                            if((playWhenReady) && (mCurrentState != PlayerState.playing)){
                                mCurrentState = PlayerState.playing;//playing
                                for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                                    if (mIsAdDisplayed) {
                                        adCallback.onPlay();
                                    }
                                }
                            }
                            else if((!playWhenReady) && (mCurrentState != PlayerState.idle) && (mCurrentState != PlayerState.paused)) {
                                mCurrentState = PlayerState.paused;//paused
                                for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                                    if (mIsAdDisplayed) {
                                        adCallback.onPause();
                                    }
                                }
                            }
                            break;
                        case ExoPlayer.STATE_ENDED:
                            if (mIsAdDisplayed) {
                                for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                                    adCallback.onEnded();
                                }
                                mIsAdDisplayed = false;
                            }
                            break;
                    }
                    Log.d(TAG, mCurrentState.toString());
                }
                updatePlayerState();
            }
        }, 200);
    }

    public BBAdExoPlayer(Context context, Object player, String contentUrl, String adUrl) {
        mContext = context;
        mPlayer = player;
        mContentUrl = contentUrl;
        mAdUrl = adUrl;
        stateHandler = new Handler();
    }

    public MediaSource setNextVideo(String nextVideo) {
        MediaSource res = null;
        if (nextVideo != null) {
            mCurrentState = PlayerState.idle;
            ((SimpleExoPlayer)mPlayer).stop();
            DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, "BabatorUI"), bandwidthMeterA);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            res = new ExtractorMediaSource(Uri.parse(nextVideo),
                    dataSourceFactory, extractorsFactory, null, null);

        }
        return res;
    }

    public void restorePlayerContent(final Context context) {
        Log.d(TAG, "restorePlayerContent");
        ((SimpleExoPlayer)mPlayer).prepare(setNextVideo(mContentUrl));

        if (mCurrentPosition > 0) {
            ((SimpleExoPlayer)mPlayer).seekTo(mCurrentPosition);
        }
        ((SimpleExoPlayer)mPlayer).setPlayWhenReady(true);
    }

    public void storeContentPosition() {
        mCurrentPosition = ((SimpleExoPlayer)mPlayer).getCurrentPosition();
        Log.d(TAG, "storeContentPosition: " + mCurrentPosition);
    }

    public void setCurrentPosition(long currentPosition) {
        Log.d(TAG, "setCurrentPosition: " + currentPosition);
        ((SimpleExoPlayer)mPlayer).seekTo(0);
    }

    public void dismissAdHandling() {
        Log.d(TAG, "dismissAdHandling");
        if(stateHandler != null) {
            stateHandler.removeMessages(0);
            stateHandler = null;
        }
    }

    @Override
    public void playAd() {
        Log.d(TAG, "playAd");
        mIsAdDisplayed = true;
        updatePlayerState();
        ((SimpleExoPlayer)mPlayer).setPlayWhenReady(true);
    }

    @Override
    public void loadAd(String s) {
        Log.d(TAG, "loadAd");
        mIsAdDisplayed = true;
        ((SimpleExoPlayer)mPlayer).prepare(setNextVideo(s));
    }

    @Override
    public void stopAd() {
        Log.d(TAG, "stopAd");
        ((SimpleExoPlayer)mPlayer).stop();
    }

    @Override
    public void pauseAd() {
        Log.d(TAG, "pauseAd");
        ((SimpleExoPlayer)mPlayer).setPlayWhenReady(false) ;
    }

    @Override
    public void resumeAd() {
        Log.d(TAG, "resumeAd");
        playAd();
    }

    @Override
    public void addCallback(VideoAdPlayer.VideoAdPlayerCallback videoAdPlayerCallback) {
        Log.d(TAG, "addCallback");
        mAdCallbacks.add(videoAdPlayerCallback);
    }

    @Override
    public void removeCallback(VideoAdPlayer.VideoAdPlayerCallback videoAdPlayerCallback) {
        Log.d(TAG, "removeCallback");
        mAdCallbacks.remove(videoAdPlayerCallback);
    }

    @Override
    public VideoProgressUpdate getAdProgress() {
        if (!mIsAdDisplayed || ((SimpleExoPlayer)mPlayer).getDuration() <= 0) {
            Log.d(TAG, "getAdProgress: VIDEO_TIME_NOT_READY");
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        Log.d(TAG, "getAdProgress: position: " + ((SimpleExoPlayer)mPlayer).getCurrentPosition() + ". duration: " + ((SimpleExoPlayer)mPlayer).getDuration());
        return new VideoProgressUpdate(((SimpleExoPlayer)mPlayer).getCurrentPosition(), ((SimpleExoPlayer)mPlayer).getDuration());
    }

    @Override
    public VideoProgressUpdate getContentProgress() {
        if (mIsAdDisplayed || ((SimpleExoPlayer)mPlayer).getDuration() <= 0) {
            Log.d(TAG, "getContentProgress: VIDEO_TIME_NOT_READY");
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        Log.d(TAG, "getContentProgress: position: " + ((SimpleExoPlayer)mPlayer).getCurrentPosition() + ". duration: " + ((SimpleExoPlayer)mPlayer).getDuration());
        return new VideoProgressUpdate(((SimpleExoPlayer)mPlayer).getCurrentPosition(), ((SimpleExoPlayer)mPlayer).getDuration());
    }
}
