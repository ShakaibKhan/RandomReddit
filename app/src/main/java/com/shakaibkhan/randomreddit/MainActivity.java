package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.Hashtable;


public class MainActivity extends Activity {

    public Button mStartButton;
    public ProgressBar mSpinner;
    public SubredditManager subredditManager;
    private Context mContext;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Opening  screen video
        mVideoView = (VideoView) findViewById(R.id.openingVideo);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        String path = "android.resource://" + getPackageName() + "/" + R.raw.redditclash;
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.start();

        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        mStartButton = (Button)findViewById(R.id.btn_login);
        mContext = getApplicationContext();

        mStartButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext,"Start Browsing",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });


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

}


