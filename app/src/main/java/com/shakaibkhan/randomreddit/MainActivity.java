package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Hashtable;


public class MainActivity extends Activity {

    public Button mStartButton;
    public ProgressBar mSpinner;
    SubredditManager subredditManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        mStartButton = (Button)findViewById(R.id.btn_start_browsing);
        mSpinner = (ProgressBar) findViewById(R.id.spinner);
        this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true);
        subredditManager.getAllSubredditStarted();

    }

    public void openReddit(View view){
        Intent openBrowser = new Intent(this, RedditView.class);
        Hashtable ht = this.subredditManager.getSubredditLinks();
        Bundle bundle = new Bundle();
        bundle.putString("InitialLinks",ht.toString());
        openBrowser.putExtras(bundle);
        startActivity(openBrowser);
    }

//        mImageDisplay = (ImageView) findViewById(R.id.image_displayed);
//
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mImageDisplay);
//        Glide.with(this).load("http://i.imgur.com/GXx2yPW.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImageDisplay);

}


