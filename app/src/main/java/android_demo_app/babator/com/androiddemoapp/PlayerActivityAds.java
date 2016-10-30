package android_demo_app.babator.com.androiddemoapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import android_demo_app.babator.com.androiddemoapp.ads.VideoPlayer;

public class PlayerActivityAds extends ActionBarActivity {
    private static String TAG = "PlayerActivityAds";

    private MediaController mediaControls = null;
    public static String API_KEY;

    // The video player.
    private static VideoPlayer mVideoPlayer;
    // The container for the ad's UI.
    private static ViewGroup mAdUIContainer;
    // The play button to trigger the ad request.
    private static View mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_ads);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
        }

    }

    private void orientVideoDescriptionFragment(int orientation) {
        // Hide the extra content when in landscape so the video is as large as possible.
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment extraContentFragment = fragmentManager.findFragmentById(R.id.videoDescription);

        if (extraContentFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fragmentTransaction.hide(extraContentFragment);
            } else {
                fragmentTransaction.show(extraContentFragment);
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientVideoDescriptionFragment(newConfig.orientation);
    }

    /**
     * The main fragment for displaying video content.
     */
    public static class VideoFragment extends Fragment {

        protected VideoPlayerController mVideoPlayerController;

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            mVideoPlayerController = new VideoPlayerController(this.getActivity(), mVideoPlayer,
                    mAdUIContainer, API_KEY);
            mVideoPlayerController.setContentVideo(getString(R.string.content_url));

            // When Play is clicked, request ads and hide the button.
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVideoPlayerController.play();
                    view.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video, container, false);

            mVideoPlayer = (VideoPlayer) rootView.findViewById(R.id.sampleVideoPlayer);
            mAdUIContainer = (ViewGroup) rootView.findViewById(R.id.videoPlayerWithAdPlayback);
            mPlayButton = rootView.findViewById(R.id.playButton);

            return rootView;
        }

        @Override
        public void onResume() {
            if (mVideoPlayerController != null) {
                mVideoPlayerController.resume();
            }
            super.onResume();
        }
        @Override
        public void onPause() {
            if (mVideoPlayerController != null) {
                mVideoPlayerController.pause();
            }
            super.onPause();
        }
    }

    /**
     * The fragment for displaying any video title or other non-video content.
     */
    public static class VideoDescriptionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_video_description, container, false);
        }
    }
}
