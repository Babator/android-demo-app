package babator.com.sdkdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBRecommendation;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.Babator;
import com.babator.babatorui.babatorcore.interfaces.BBVideoMetaData;
import com.babator.babatorui.monetization.BabatorPlayers.BabatorMonetizationPlayer;
import com.babator.babatorui.monetization.IMA.BBIMAManager;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class MonetizationActivity extends BasePlayerActivity implements BBVideoMetaData {
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer mPlayer;
    private Uri currentPlayingUrl;
    private Context mContext;
    private boolean hasAds = false;
    private boolean babatorMonetization = true;
    private BabatorViewHandler mBabatorViewHandler = null;
    private BBIMAManager mAdManager;
    private BabatorMonetizationPlayer mBabatorMonetizationPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer_monetize);
        mContext = this;
        Intent intent = getIntent();
        if(intent != null){
            hasAds = intent.getBooleanExtra("Ads", false) && (!babatorMonetization);
        }
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
    }

    @Override
    protected void onResume() {

        if(mBabatorMonetizationPlayer != null){
            mBabatorMonetizationPlayer.resumeBBAdsManager();
        }

        if(mPlayer == null) {
            //Handler mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl();

            mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(mPlayer);
            //region Prepare ExoPlayer MediaSource
            DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "BabatorDemoApp"), bandwidthMeterA);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource videoSource = new ExtractorMediaSource(initialUri,
                    dataSourceFactory, extractorsFactory, null, null);
            mPlayer.prepare(videoSource);
            //endregion
            if (!hasAds) {
                mPlayer.setPlayWhenReady(true);
                currentPlayingUrl = initialUri;

            }
        }


        //region BabatorViewHandler object
        if(mBabatorViewHandler == null) {
            mBabatorViewHandler = new BabatorViewHandler(this, simpleExoPlayerView, this.getClass(), initialUri, true);
            mBabatorViewHandler.initialize(API_KEY);
            mBabatorViewHandler.setListener(new BabatorViewHandler.Listener() {
                @Override
                public void onVideoSelected(BabatorViewHandler handler, BBVideoParams videoParams) {


                }

                @Override
                public void onVideoAutoPlayed(BabatorViewHandler handler, BBVideoParams videoParams) {

                }
            });

            if (babatorMonetization) {
                mBabatorViewHandler.getBabator().setBabatorMonetizationPlayerListener(new Babator.BabatorMonetizationPlayerListener() {
                    @Override
                    public void onBabatorMonetizationPlayerReady(BabatorMonetizationPlayer player) {
                        try {
                            if (player != null) {
                                mPlayer.release();  //release current player
                                mPlayer = null; //and null it

                                mBabatorMonetizationPlayer = player;
                                ViewGroup parentInActivity = (ViewGroup) simpleExoPlayerView.getParent();    //get view parent of current player
                                parentInActivity.removeView(simpleExoPlayerView);   //remove current player view

                                parentInActivity.addView( mBabatorMonetizationPlayer.getBBMonetizationView());

                                simpleExoPlayerView = mBabatorMonetizationPlayer.getSimpleExoPlayerView();
                                mPlayer = simpleExoPlayerView.getPlayer();

                                Toast.makeText(mContext, "Babator Monetization is in the house :-)", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onPlayerPaused(long position) {

                    }

                    @Override
                    public void onPlayerPlayed() {

                    }

                    @Override
                    public void onPlayerSeeked(long position) {

                    }

                    @Override
                    public void onRecommendationsFetched(BBRecommendation recommendations) {

                    }
                });
            }
        }
        loadAds(initialUri.toString());
        //endregion
        super.onResume();
    }

    protected void loadAds(String url) {
        if (hasAds) {
            currentPlayingUrl = Uri.parse(url);
            mAdManager = new BBIMAManager(getApplicationContext(), url);
            mAdManager.setListener(mBabatorViewHandler.getBabator());
            ViewGroup adContainer = (ViewGroup) findViewById(R.id.adContainer);
            mAdManager.requestAds(mPlayer, getString(R.string.ad_tag_url), adContainer, url);
        }
    }

    private MediaSource setMediaSource(Uri videoUri){
        MediaSource videoSource = null;
        try {
            DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "BabatorDemoApp"), bandwidthMeterA);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            videoSource = new ExtractorMediaSource(videoUri,
                    dataSourceFactory, extractorsFactory, null, null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return videoSource;

    }

    @Override
    protected void onPause() {

        if(mBabatorMonetizationPlayer != null){
            mBabatorMonetizationPlayer.pauseBBAdsManager();
        }

        if(mPlayer != null){
            mPlayer.setPlayWhenReady(false);
        }
        super.onPause();

        //mPlayer = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
            mBabatorViewHandler = null;
        }
        if(mAdManager != null){
            mAdManager.dispose();
            mAdManager = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
            mBabatorViewHandler = null;
        }
        if(mAdManager != null){
            mAdManager.dispose();
            mAdManager = null;
        }
    }

    @Override
    public Uri getVideoUrl() {
        return currentPlayingUrl;
    }
}
