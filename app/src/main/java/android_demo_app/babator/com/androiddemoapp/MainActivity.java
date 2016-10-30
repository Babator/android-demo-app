package android_demo_app.babator.com.androiddemoapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mPlayersView = null;
    private VideoListAdapter mAdapter = null;


    private Class<?>[] activities = {};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        //region Players RecyclerView and adapter
        mPlayersView = (RecyclerView) findViewById(R.id.gridView);
        if (mPlayersView != null) {
            mPlayersView.setHasFixedSize(true);
            mPlayersView.setLayoutManager(new GridLayoutManager(this, 1));
            mAdapter = new VideoListAdapter(this, DemoAppUtils.loadArray(this, R.array.players));
            mPlayersView.addItemDecoration(new DividerItemDecoration(this));

            mPlayersView.setAdapter(mAdapter);
        }

        //endregion

    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    //region VideoListAdapter
    public class VideoListAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        private String [][] itemsList;
        private Context mContext;

        public VideoListAdapter(Context context, String [][] items) {
            this.itemsList = items;
            this.mContext = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, null);


            CustomViewHolder viewHolder = new CustomViewHolder(view, new CustomViewHolder.IHolderClicks() {
                @Override
                public void onItemClick(CustomViewHolder caller, int position) {
                    try {
                        Class<?> cls = Class.forName("android_demo_app.babator.com.androiddemoapp." + itemsList[position][1]);
                        mContext.startActivity(new Intent(mContext, cls).putExtra("api_key", getString(R.string.api_key)));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            String[] feedItem = itemsList[i];
            customViewHolder.tvName.setText(feedItem[0]);
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.length : 0);
        }

    }
    //endregion
}
