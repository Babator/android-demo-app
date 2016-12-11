package babator.com.sdkdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;

import com.babator.babatorui.BabatorViewHandler;

import babator.com.sdkdemo.ads.BBIMAManager;

public class BasePlayerActivity extends AppCompatActivity {

    BabatorViewHandler mBabatorViewHandler = null;
    MediaController mMediaController = null;
    String API_KEY;
    boolean hasAds = false;
    BBIMAManager mAdManager;
    Uri initialUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            API_KEY = intent.getStringExtra("api_key");
            hasAds = intent.getBooleanExtra("Ads", true);
        }
        initialUri = Uri.parse(getString(R.string.content_url));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
            mBabatorViewHandler = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mBabatorViewHandler != null){
            mBabatorViewHandler.dispose();
            mBabatorViewHandler = null;
        }
    }

}
