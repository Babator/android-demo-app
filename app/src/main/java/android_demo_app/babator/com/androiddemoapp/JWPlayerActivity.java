package android_demo_app.babator.com.androiddemoapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.longtailvideo.jwplayer.JWPlayerFragment;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;


public class JWPlayerActivity extends AppCompatActivity {
    private static String TAG = "JWPlayerActivity";
    private Uri initialUri;
    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jw_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
        }

        initialUri = Uri.parse(getString(R.string.content_url));
        preparePlayer();
    }

    private void preparePlayer() {
        JWPlayerFragment fragment = (JWPlayerFragment) getFragmentManager().findFragmentById(R.id.playerFragment);

        // Get a handle to the JWPlayerView
        JWPlayerView playerView = fragment.getPlayer();

        // Create a PlaylistItem
        PlaylistItem video = new PlaylistItem(getString(R.string.content_url));

        // Load a stream into the player

        playerView.load(video);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

}
