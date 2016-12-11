package babator.com.sdkdemo.ads;

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

public class BBIMAManager implements AdErrorEvent.AdErrorListener, AdsLoader.AdsLoadedListener, AdEvent.AdEventListener {
    // The AdsLoader instance exposes the requestAds method.
    private final AdsLoader mAdsLoader;

    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager mAdsManager;

    // Factory class for creating SDK objects.
    private final ImaSdkFactory mSdkFactory;

    private BBAdsHandler mListener;

    private BBAdVideoPlayerFactory.PlayerAdsWrapper mPlayerHandler;

    private final String mContentUrl;

    private final Context mContext;

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
        mPlayerHandler = new BBAdVideoPlayerFactory().getPlayerAdsWrapper(mContext, player, mContentUrl, adTagUrl);
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

    public void dispose(){
        try {
            if(mPlayerHandler != null){
                mPlayerHandler.dismissAdHandling();
                mPlayerHandler = null;
            }
        }
        catch (Exception e){
            Log.d("BBIMAManager", "BBIMAManager.dispose(): " + e.getMessage());
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.d("BBIMAManager", adErrorEvent.getError().getMessage());
        mListener.onAdEventChanged(BBAdsHandler.AdEvent.error, mPlayerHandler.getAdUrl());
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
        Log.d("BBIMAManager", "BBIMAManager.onAdEvent(): " + adEvent.getType().toString());
        switch (adEvent.getType()) {
            case LOADED:
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.loaded, mPlayerHandler.getAdUrl());
                mAdsManager.start();
                break;
            case STARTED:
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.started, mPlayerHandler.getAdUrl());
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
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.ended, mPlayerHandler.getAdUrl());
                break;
            case SKIPPED:
                mListener.onAdEventChanged(BBAdsHandler.AdEvent.skipped, mPlayerHandler.getAdUrl());
                break;
            default:
                break;
        }
    }
}
