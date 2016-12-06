package android_demo_app.babator.com.androiddemoapp;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;
import com.babator.babatorui.babatorcore.interfaces.OnBabatorAds;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;


public class JWPlayerActivity extends BasePlayerActivity {
    private static String TAG = "JWPlayerActivity";
    private JWPlayerView mJWPlayerView;
    private FrameLayout mPlayerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jw_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        mPlayerContainer = (FrameLayout) findViewById(R.id.player_container);
        preparePlayer();
    }

    private void preparePlayer() {
        // Initialize a new JW Player.
        mJWPlayerView = new JWPlayerView(this, new PlayerConfig.Builder()
                .file(getString(R.string.content_url))
                .build());

        // Add the View to the View Hierarchy.
        mPlayerContainer.addView(mJWPlayerView);
        setInitialLayoutParams();

        // Play the content
        mJWPlayerView.play(true);

    }

    /**
     * Sets the initial layout parameters for the {@link JWPlayerView}.
     */
    private void setInitialLayoutParams() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPlayerContainer.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (displayMetrics.widthPixels / 16) * 9)); // 16:9
        } else {
            // We need to use height to calculate a 16:9 ratio since we're in landscape mode.
            mPlayerContainer.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (displayMetrics.heightPixels / 16) * 9)); // 16:9
            // Toggle fullscreen, since we're in landscape mode.
            mJWPlayerView.setFullscreen(true, true);
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        if(mJWPlayerView != null) {
            mJWPlayerView.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
            if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setInitialLayoutParams();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        mJWPlayerView.onResume();

        //region BabatorViewHandler object
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
        mBabatorViewHandler = new BabatorViewHandler(this, mJWPlayerView, this.getClass(), initialUri);
        mBabatorViewHandler.initialize(API_KEY);
        mBabatorViewHandler.setListener(new BabatorViewHandler.Listener() {
            @Override
            public void onVideoSelected(BabatorViewHandler handler, BBVideoParams videoParams) {

            }
        });
        mBabatorViewHandler.getBabator().setOnBabatorAds(new OnBabatorAds() {
            @Override
            public boolean shouldLoadAd(String recommendationUrl) {
                //loadAds(recommendationUrl);
                return true;
            }
        });

        //endregion

        super.onResume();
    }

    @Override
    protected void onPause() {
        mJWPlayerView.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mJWPlayerView.onDestroy();
        super.onDestroy();
    }

}
