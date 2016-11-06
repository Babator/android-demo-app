package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nissimpardo on 26/10/2016.
 */

public class BBAdVideoPlayer implements VideoAdPlayer, ContentProgressProvider {
    private VideoView mPlayer;
    private boolean mIsAdDisplayed;
    private long mCurrentPosition;
    private final List<VideoAdPlayerCallback> mAdCallbacks =
            new ArrayList<VideoAdPlayerCallback>(1);
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private Handler stateHandler;
    private PlayerState mPlayerState = PlayerState.unknown;
    private String mContentUrl;
    private MediaController mediaControls;

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
                    if (mPlayer.isPlaying() && mPlayerState == PlayerState.paused) {
                        for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                            if (mIsAdDisplayed) {
                                adCallback.onPlay();
                            }
                        }
                    } else if (!mPlayer.isPlaying() && mPlayerState == PlayerState.playing) {
                        for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                            if (mIsAdDisplayed) {
                                adCallback.onPause();
                            }
                        }
                    }
                    mPlayerState = mPlayer.isPlaying() ? PlayerState.playing : PlayerState.paused;
                    Log.d("BBAdVideoPlayer", mPlayerState.toString());
                }
                updatePlayerState();
            }
        }, 200);
    }

    public BBAdVideoPlayer(Object player, String contentUrl) {
        mPlayer = (VideoView) player;
        mOnCompletionListener = (MediaPlayer.OnCompletionListener)fetchFieldByName("mOnCompletionListener");
//        mOnPreparedListener = (MediaPlayer.OnPreparedListener) fetchFieldByName("mOnPreparedListener");
//        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                if (mOnPreparedListener != null) {
//                    mOnPreparedListener.onPrepared(mp);
//                }
//            }
//        });
        mContentUrl = contentUrl; //fetchFieldByName("mUri").toString();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayerCallback adCallback : mAdCallbacks) {
                        adCallback.onEnded();
                    }
                    mIsAdDisplayed = false;
                    Log.d("BBAdVideoPlayer", mContentUrl);
                }
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        stateHandler = new Handler();
    }

    public void restorePlayerContent(final Context context) {
        mPlayer.setVideoPath(mContentUrl);
        if (mCurrentPosition > 0) {
            mPlayer.seekTo((int) mCurrentPosition);
        }
        mPlayer.start();
    }

    public void storeContentPosition() {
        mCurrentPosition = mPlayer.getCurrentPosition();
        if (mediaControls != null) {
            mediaControls.setVisibility(View.INVISIBLE);
        }
    }

    public void setCurrentPosition(long currentPosition) {
        mPlayer.seekTo(0);
    }

    public void dismissAdHandling() {
        stateHandler.removeMessages(0);
        stateHandler = null;
    }



    @Override
    public void playAd() {
        mIsAdDisplayed = true;
        updatePlayerState();
        mPlayer.start();
    }

    @Override
    public void loadAd(String s) {
        mIsAdDisplayed = true;
        mPlayer.setVideoPath(s);
    }

    @Override
    public void stopAd() {
        mPlayer.stopPlayback();
    }

    @Override
    public void pauseAd() {
        mPlayer.pause();
    }

    @Override
    public void resumeAd() {
        playAd();
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
        if (!mIsAdDisplayed || mPlayer.getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(mPlayer.getCurrentPosition(), mPlayer.getDuration());
    }

    @Override
    public VideoProgressUpdate getContentProgress() {
        if (mIsAdDisplayed || mPlayer.getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(mPlayer.getCurrentPosition(), mPlayer.getDuration());
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
