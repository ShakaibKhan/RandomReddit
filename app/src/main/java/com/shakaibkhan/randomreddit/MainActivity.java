package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


public class MainActivity extends Activity {

    public Button mStartButton;
    public ProgressBar mSpinner;
    public SubredditManager subredditManager;

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Opening  screen video

        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        mStartButton = (Button)findViewById(R.id.btn_start_browsing);
        mSpinner = (ProgressBar) findViewById(R.id.spinner);
        this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true);
        subredditManager.getAllSubredditStarted();

    }

    public void openReddit(View view){
        Intent openBrowser = new Intent(this, RedditView.class);
        //Get all of the initally loaded reddit info
        Hashtable ht = this.subredditManager.getSubredditLinks();
        Hashtable av = this.subredditManager.subredditAfter;
        Hashtable rt = this.subredditManager.subredditTitles;
        Hashtable oe = this.subredditManager.subredditOver_18;

        Bundle bundle = new Bundle();
        bundle.putSerializable("InitialLinks",ht);
        bundle.putSerializable("Afters",av);
        bundle.putSerializable("Titles",rt);
        bundle.putSerializable("Over_18",oe);
        openBrowser.putExtras(bundle);
        startActivity(openBrowser);
    }

//        mImageDisplay = (ImageView) findViewById(R.id.image_displayed);
//
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mImageDisplay);
//        Glide.with(this).load("http://i.imgur.com/GXx2yPW.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImageDisplay);

}


