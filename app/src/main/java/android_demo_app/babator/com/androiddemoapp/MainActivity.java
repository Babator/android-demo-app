package android_demo_app.babator.com.androiddemoapp;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.babator.babatorui.BabatorViewHandler;
import com.babator.babatorui.babatorcore.BBVideoParams;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "PlayerActivity";

    public static String API_KEY = "d035223d-8bba-40d2-bb13-5a22298250c6";
    private VideoView mPlayer = null;
    private MediaController mediaControls = null;

    private BabatorViewHandler mBabatorViewHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        mPlayer = (VideoView) findViewById(R.id.video_view);
        Uri video = Uri.parse("http://download.itcuties.com/teaser/itcuties-teaser-480.mp4");
        mPlayer.setVideoURI(video);

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        if (mediaControls == null) {
                            mediaControls = new MediaController(MainActivity.this);
                        }

                        try {
                            mPlayer.setMediaController(mediaControls);
                            mediaControls.setAnchorView(mPlayer);

                        }
                        catch (Exception e){
                            Log.e(TAG, "Error" + e.getMessage());
                        }
                    }
                });
                mPlayer.start();
            }
        });

        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d("MediaPlayer info", "isPlaying -" + mp.isPlaying());
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //region BabatorViewHandler object
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
        mBabatorViewHandler = new BabatorViewHandler(this, mPlayer);
        mBabatorViewHandler.initialize(API_KEY);
        mBabatorViewHandler.setListener(new BabatorViewHandler.BababtorViewHandlerListener() {
            @Override
            public void onVideoSelected(BabatorViewHandler handler, BBVideoParams videoParams) {
                Uri video = Uri.parse(videoParams.getVideoId());
                mPlayer.setVideoURI(video);
            }
        });
        //endregion
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}