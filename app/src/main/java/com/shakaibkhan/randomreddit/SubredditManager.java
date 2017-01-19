package com.shakaibkhan.randomreddit;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-13.
 */

public class SubredditManager {

    public String[] subredditNames = new String[10];
    private Hashtable subredditFeed = new Hashtable();
    private Enumeration subs ;
    private Handler handler ;
    public int numberOfFinishedSubreddits = 0;
    public ProgressBar mSpinner;
    public Button mStartButton;
    public boolean startPage;

    SubredditManager(String[] srs, ProgressBar spin, Button btn, boolean start){
        this.startPage = start;
        subredditNames = srs;
        this.mSpinner = spin;
        this.mStartButton = btn;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                switch(message.what){
                    case 0:
                        if(startPage){
                            numberOfFinishedSubreddits++;
                            if(numberOfFinishedSubreddits == subredditNames.length){
                                mSpinner.setVisibility(View.GONE);
                                mStartButton.setVisibility(View.VISIBLE);
                            }
                        }
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler));
        }

        subs = subredditFeed.keys();
    }

    public void getAllSubredditStarted(){
        while(subs.hasMoreElements()){
            RedditParser rp = (RedditParser) subredditFeed.get(subs.nextElement());
            rp.execute("");
        }
    }

}

