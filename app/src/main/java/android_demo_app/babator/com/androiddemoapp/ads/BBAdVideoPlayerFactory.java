package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.util.Log;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.longtailvideo.jwplayer.JWPlayerView;

/**
 * this factory creates an object based on the player type. currently supports VideoView, SimpleExoPlayer, JWPlayerView
 */
public class BBAdVideoPlayerFactory {

    private static String TAG = "BBAdVideoPlayerFactory";

    /**
     * PlayerAdsWrapper interface
     */
    public interface PlayerAdsWrapper extends VideoAdPlayer, ContentProgressProvider {
        void restorePlayerContent(final Context context);
        void storeContentPosition();
        void setCurrentPosition(long currentPosition);
        void dismissAdHandling();
        void playAd();
        void loadAd(String s);
        void stopAd();
        void pauseAd();
        void resumeAd();
        String getAdUrl();
    }

    /**
     * Get a static PlayerAdsWrapper object
     * @param context
     * @param player
     * @param contentUrl
     * @param adUrl
     * @return
     */
    public PlayerAdsWrapper getPlayerAdsWrapper (Context context, Object player, String contentUrl, String adUrl){
        if(player == null){
            Log.d(TAG, "getPlayerAdsWrapper: player is null");
            return null;
        }
        if (player instanceof VideoView) {
            return new BBAdVideoViewPlayer(context, player, contentUrl, adUrl);
        } else if (player instanceof SimpleExoPlayer) {
            return new BBAdExoPlayer(context, player, contentUrl, adUrl);
        } else if (player instanceof JWPlayerView)
            return new BBAdJWPlayer(context, player, contentUrl, adUrl);
        else{
            Log.d(TAG, "getPlayerAdsWrapper: player is of unknown type");
            return null;
        }
    }

}
