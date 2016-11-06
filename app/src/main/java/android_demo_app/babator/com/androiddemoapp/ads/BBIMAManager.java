package android_demo_app.babator.com.androiddemoapp.ads;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.babator.babatorui.babatorcore.interfaces.BBAdsHandler;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

/**
 * Created by nissimpardo on 25/10/2016.
 */

public class BBIMAManager implements AdErrorEvent.AdErrorListener, AdsLoader.AdsLoadedListener, AdEvent.AdEventListener {
    // The AdsLoader instance exposes the requestAds method.
    private AdsLoader mAdsLoader;

    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager mAdsManager;

    // Factory class for creating SDK objects.
    private ImaSdkFactory mSdkFactory;

    private BBAdsHandler mListener;

    private BBAdVideoPlayer mPlayerHandler;

    private String mContentUrl;

    private Context mContext;

    public BBIMAManager(Context context, String contentUrl) {
        // Create an AdsLoader.
        mContext = context;
        mContentUrl = contentUrl;
        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(context);
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(this);
    }

    public void requestAds(Object player, String adTagUrl, ViewGroup adContainer) {
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        mPlayerHandler = new BBAdVideoPlayer(player, mContentUrl);
        adDisplayContainer.setPlayer(mPlayerHandler);
        adDisplayContainer.setAdContainer(adContainer);
        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);
        request.setContentProgressProvider(mPlayerHandler);

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }



    public void setListener(BBAdsHandler listener) {
        mListener = listener;
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.d("BBAdVideoPlayer", adErrorEvent.getError().getMessage());
        mListener.onAdEventChanged(BBAdsHandler.AdEvent.error);
    }

    @Override
    public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
        // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
        // events for ad playback and errors.
        mAdsManager = adsManagerLoadedEvent.getAdsManager();

        // Attach event and error event listeners.
        mAdsManager.addAdErrorListener(this);
        mAdsManager.addAdEventListener(this);
        mAdsManager.init();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        switch (adEvent.getType()) {
            case LOADED:
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.started);
                mAdsManager.start();
                break;
            case STARTED:
                break;
            case CONTENT_RESUME_REQUESTED:
                mPlayerHandler.restorePlayerContent(mContext);
                break;
            case CONTENT_PAUSE_REQUESTED:
                mPlayerHandler.storeContentPosition();
                break;
            case ALL_ADS_COMPLETED:
                mPlayerHandler.setCurrentPosition(0);
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                if (mPlayerHandler != null) {
                    mPlayerHandler.dismissAdHandling();
                }
                break;
            case COMPLETED:
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.ended);
                break;
            default:
                break;
        }
    }
}
