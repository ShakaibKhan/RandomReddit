package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;


public class MainActivity extends Activity {

    public Button mStartButton;
    public ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        mStartButton = (Button)findViewById(R.id.btn_start_browsing);
        mSpinner = (ProgressBar) findViewById(R.id.spinner);
        SubredditManager subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true);
        subredditManager.getAllSubredditStarted();

    }

    public void openReddit(View view){
        Intent openBrowser = new Intent(this, RedditView.class);
        startActivity(openBrowser);
    }

//        mImageDisplay = (ImageView) findViewById(R.id.image_displayed);
//
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mImageDisplay);
//        Glide.with(this).load("http://i.imgur.com/GXx2yPW.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImageDisplay);

}


