package android_demo_app.babator.com.androiddemoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;

import com.babator.babatorui.BabatorViewHandler;

import android_demo_app.babator.com.androiddemoapp.ads.BBIMAManager;

/**
 * Created by danshneider on 06/12/2016.
 */

public class BasePlayerActivity extends AppCompatActivity {

    protected BabatorViewHandler mBabatorViewHandler = null;
    protected MediaController mMediaController = null;
    protected String API_KEY;
    protected boolean hasAds = false;
    protected BBIMAManager mAdManager;
    protected Uri initialUri;

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
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
