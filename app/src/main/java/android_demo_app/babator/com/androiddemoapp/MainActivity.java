package android_demo_app.babator.com.androiddemoapp;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.babator.babatorui.BabatorViewController;
import com.babator.babatorui.babatorcore.BBVideoParams;

public class MainActivity extends AppCompatActivity implements BabatorViewController.OnFragmentInteractionListener{
    private static String TAG = "PlayerActivity";

    public static String API_KEY = "d035223d-8bba-40d2-bb13-5a22298250c6";
    private VideoView mPlayer = null;
    private BabatorViewController mBabatorViewController = null;
    private MediaController mediaControls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        mPlayer = (VideoView) findViewById(R.id.video_view);
        Uri video = Uri.parse("http://techslides.com/demos/sample-videos/small.3gp");
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

        //region Players BabatorViewController fragment
        mBabatorViewController = BabatorViewController.init(API_KEY);
        mBabatorViewController.setRecommendationSize(10);
        mBabatorViewController.setBabatorViewConrollerListener(new BabatorViewController.BabatorViewControllerListener() {
            @Override
            public void onVideoSelected(BabatorViewController controller, BBVideoParams videoParams) {
                //video selected on list
            }
        });
        mBabatorViewController.setPlayer(mPlayer);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, mBabatorViewController);
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        //endregion

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
