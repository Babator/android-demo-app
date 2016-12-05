package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nissimpardo on 26/10/2016.
 */

public class BBAdVideoViewPlayer implements BBAdVideoPlayerFactory.PlayerAdsWrapper {
    static final String TAG = "BBAdVideoViewPlayer";
    protected static Context mContext;
    protected Object mPlayer;
    protected PlayerState mPlayerState = PlayerState.unknown;
    protected static String mContentUrl;
    protected static String mAdUrl;
    protected boolean mIsAdDisplayed;
    protected long mCurrentPosition;
    protected final List<VideoAdPlayerCallback> mAdCallbacks = new ArrayList<VideoAdPlayerCallback>(1);

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaController mediaControls;
    private Handler stateHandler;

    private enum PlayerState {
        unknown,
        playing,
        paused;
    }

    private void updatePlayerState() {
        if (stateHandler == null) {
            return;
        }
        stateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    if (((VideoView)mPlayer).isPlaying() && mPlayerState == PlayerState.paused) {
                        for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                            if (mIsAdDisplayed) {
                                adCallback.onPlay();
                            }
                        }
                    } else if (!((VideoView)mPlayer).isPlaying() && mPlayerState == PlayerState.playing) {
                        for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                            if (mIsAdDisplayed) {
                                adCallback.onPause();
                            }
                        }
                    }
                    mPlayerState = ((VideoView)mPlayer).isPlaying() ? PlayerState.playing : PlayerState.paused;
                    Log.d(TAG, mPlayerState.toString());
                }
                updatePlayerState();
            }
        }, 200);
    }

    public BBAdVideoViewPlayer(){

    }
    public BBAdVideoViewPlayer(Context context, Object player, String contentUrl, String adUrl) {
        mContext = context;
        mPlayer = player;
        mOnCompletionListener = (MediaPlayer.OnCompletionListener)fetchFieldByName("mOnCompletionListener");
        mContentUrl = contentUrl; //fetchFieldByName("mUri").toString();
        mAdUrl = adUrl;
        ((VideoView)mPlayer).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                        adCallback.onEnded();
                    }
                    mIsAdDisplayed = false;
                }
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        stateHandler = new Handler();
    }

    public void restorePlayerContent(final Context context) {
        ((VideoView)mPlayer).setVideoPath(mContentUrl);
        if (mCurrentPosition > 0) {
            ((VideoView)mPlayer).seekTo((int) mCurrentPosition);
        }
        ((VideoView)mPlayer).start();
    }

    public void storeContentPosition() {
        mCurrentPosition = ((VideoView)mPlayer).getCurrentPosition();
        if (mediaControls != null) {
            mediaControls.setVisibility(View.INVISIBLE);
        }
    }

    public void setCurrentPosition(long currentPosition) {
        ((VideoView)mPlayer).seekTo(0);
    }

    public void dismissAdHandling() {
        if(stateHandler != null) {
            stateHandler.removeMessages(0);
            stateHandler = null;
        }
    }

    @Override
    public void playAd() {
        mIsAdDisplayed = true;
        updatePlayerState();
        ((VideoView)mPlayer).start();
    }

    @Override
    public void loadAd(String s) {
        mIsAdDisplayed = true;
        ((VideoView)mPlayer).setVideoPath(s);
    }

    @Override
    public void stopAd() {
        ((VideoView)mPlayer).stopPlayback();
    }

    @Override
    public void pauseAd() {
        ((VideoView)mPlayer).pause();
    }

    @Override
    public void resumeAd() {
        playAd();
    }

    @Override
    public String getAdUrl() {
        return this.mAdUrl;
    }

    @Override
    public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
        mAdCallbacks.add(videoAdPlayerCallback);
    }

    @Override
    public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
        mAdCallbacks.remove(videoAdPlayerCallback);
    }

    @Override
    public VideoProgressUpdate getAdProgress() {
        if (!mIsAdDisplayed || ((VideoView)mPlayer).getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(((VideoView)mPlayer).getCurrentPosition(), ((VideoView)mPlayer).getDuration());
    }

    @Override
    public VideoProgressUpdate getContentProgress() {
        if (mIsAdDisplayed || ((VideoView)mPlayer).getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(((VideoView)mPlayer).getCurrentPosition(), ((VideoView)mPlayer).getDuration());
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
