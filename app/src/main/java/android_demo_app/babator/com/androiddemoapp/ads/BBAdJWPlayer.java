package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.util.Log;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;

import java.lang.reflect.Field;

public class BBAdJWPlayer extends BBAdVideoViewPlayer {
    private static final String TAG = "BBAdJWPlayer";
    private final JWPlayerView mPlayer;
    private VideoPlayerEvents.OnPlayListener mOnPlayListener;
    private VideoPlayerEvents.OnPauseListener mOnPauseListener;
    private VideoPlayerEvents.OnCompleteListener mOnCompleteListener;
    private VideoPlayerEvents.OnSeekedListener mOnSeekedListener;
    private VideoPlayerEvents.OnSeekListener mOnSeekListener;
    private VideoPlayerEvents.OnIdleListener mOnIdleListener;
    private VideoPlayerEvents.OnFirstFrameListener mOnFirstFrameListener;

    /**
     * Subscribe player listeners
     */
    private void addListeners(){
        mPlayer.addOnPlayListener(this.mOnPlayListener);
        mPlayer.addOnPauseListener(this.mOnPauseListener);
        mPlayer.addOnCompleteListener(mOnCompleteListener);
        mPlayer.addOnSeekedListener(this.mOnSeekedListener);
        mPlayer.addOnSeekListener(this.mOnSeekListener);
        mPlayer.addOnIdleListener(this.mOnIdleListener);
        mPlayer.addOnFirstFrameListener(this.mOnFirstFrameListener);
    }

    /**
     * Remove player listeners
     */
    private void removeListeners(){
        mPlayer.removeOnPlayListener(this.mOnPlayListener) ;
        mPlayer.removeOnPauseListener(this.mOnPauseListener);
        mPlayer.removeOnCompleteListener(this.mOnCompleteListener);
        mPlayer.removeOnSeekedListener(this.mOnSeekedListener);
        mPlayer.removeOnSeekListener(this.mOnSeekListener);
        mPlayer.removeOnIdleListener(this.mOnIdleListener);
        mPlayer.removeOnFirstFrameListener(this.mOnFirstFrameListener);
    }

    public BBAdJWPlayer(Context context, Object player, String contentUrl, String adUrl) {
        mContext = context;
        mPlayer = (JWPlayerView) player;
        mContentUrl = contentUrl;
        mAdUrl = adUrl;
        addListeners();
        initListeners();
    }

    /**
     * Init player listeners
     */
    private void initListeners(){
        this.mOnFirstFrameListener = new VideoPlayerEvents.OnFirstFrameListener() {
            @Override
            public void onFirstFrame(int i) {
                Log.d(TAG, "onFirstFrame: " +   (mPlayer.getPosition() / 1000));
            }
        };

        this.mOnPlayListener = new VideoPlayerEvents.OnPlayListener() {
            @Override
            public void onPlay(com.longtailvideo.jwplayer.core.PlayerState playerState) {

                if(playerState == PlayerState.PAUSED) {    //this means now it is playing (paused was the previous state)
                    for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                        if (mIsAdDisplayed) {
                            adCallback.onPlay();
                        }
                    }
                }
                Log.d(TAG, "onPlay: " +   (mPlayer.getPosition() / 1000) + ". playerState:" + playerState.toString());
            }

        };

        this.mOnPauseListener = new VideoPlayerEvents.OnPauseListener() {
            @Override
            public void onPause(PlayerState playerState) {
                if((playerState == com.longtailvideo.jwplayer.core.PlayerState.PLAYING)) {    //this means now it is paused (playing was the previous state)
                    for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                        if (mIsAdDisplayed) {
                            adCallback.onPause();
                        }
                    }
                }
                Log.d(TAG, "onPause: " +   (mPlayer.getPosition() / 1000) + ". playerState:" + playerState.toString());
            }
        };

        this.mOnCompleteListener = new VideoPlayerEvents.OnCompleteListener() {
            @Override
            public void onComplete() {
                if (mIsAdDisplayed) {
                    for (VideoAdPlayer.VideoAdPlayerCallback adCallback : mAdCallbacks) {
                        adCallback.onEnded();
                    }
                    mIsAdDisplayed = false;
                    Log.d(TAG, mContentUrl);
                }
                Log.d(TAG, "onComplete: " +   (mPlayer.getPosition() / 1000));
            }
        };

        mOnSeekedListener = new VideoPlayerEvents.OnSeekedListener() {
            @Override
            public void onSeeked() {
                Log.d(TAG, "onSeeked: " +   (mPlayer.getPosition() / 1000));

            }
        };

        mOnSeekListener = new VideoPlayerEvents.OnSeekListener() {
            @Override
            public void onSeek(int i, int i1) {
                Log.d(TAG, "onSeek: " +   (mPlayer.getPosition() / 1000) + " from: " + i  + ", to: " + i1 );
            }
        };

        mOnIdleListener = new VideoPlayerEvents.OnIdleListener() {
            @Override
            public void onIdle(com.longtailvideo.jwplayer.core.PlayerState playerState) {
                Log.d(TAG, "onIdle: " +  (mPlayer.getPosition() / 1000) + ". playerState:" + playerState.toString());
            }
        };

    }

    public void restorePlayerContent(final Context context) {
        mPlayer.getConfig().setFile(mContentUrl);
        if (mCurrentPosition > 0) {
            mPlayer.seek(mCurrentPosition);
        }
        mPlayer.play(true);
    }

    public void storeContentPosition() {
        mCurrentPosition = mPlayer.getPosition();
    }

    public void setCurrentPosition(long currentPosition) {
        mPlayer.seek((long)0);
    }

    public void dismissAdHandling() {
        removeListeners();
    }

    @Override
    public void playAd() {
        mIsAdDisplayed = true;
        mPlayer.play(true);
    }

    @Override
    public void loadAd(String s) {
        mIsAdDisplayed = true;
        mPlayer.getConfig().setFile(mContentUrl);
    }

    @Override
    public void stopAd() {
        mPlayer.stop();
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
    public void addCallback(VideoAdPlayer.VideoAdPlayerCallback videoAdPlayerCallback) {
        mAdCallbacks.add(videoAdPlayerCallback);
    }

    @Override
    public void removeCallback(VideoAdPlayer.VideoAdPlayerCallback videoAdPlayerCallback) {
        mAdCallbacks.remove(videoAdPlayerCallback);
    }

    @Override
    public VideoProgressUpdate getAdProgress() {
        if (!mIsAdDisplayed || mPlayer.getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(mPlayer.getPosition(), mPlayer.getDuration());
    }

    @Override
    public VideoProgressUpdate getContentProgress() {
        if (mIsAdDisplayed || mPlayer.getDuration() <= 0) {
            return  VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        return new VideoProgressUpdate(mPlayer.getPosition(), mPlayer.getDuration());
    }

}
