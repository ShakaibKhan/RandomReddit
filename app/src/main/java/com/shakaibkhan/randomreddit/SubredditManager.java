package com.shakaibkhan.randomreddit;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by shakaibkhan on 2017-01-13.
 */

public class SubredditManager {

    public String[] subredditNames = new String[10];
    private Enumeration subs ;
    private Handler handler ;
    public int numberOfFinishedSubreddits = 0;
    public ProgressBar mSpinner;
    public Button mStartButton;
    public boolean startPage;

    //
    public Hashtable subredditLinks = new Hashtable();
    public Hashtable subredditAfter = new Hashtable();
    public Hashtable subredditTitles = new Hashtable();
    public Hashtable subredditOver_18 = new Hashtable();
    private Hashtable subredditFeed = new Hashtable();

    public LinkManager linkManager;

    SubredditManager(String[] srs, ProgressBar spin, Button btn, boolean start){
        this.startPage = start;
        subredditNames = srs;
        this.mSpinner = spin;
        this.mStartButton = btn;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.obj != null){
                    if(startPage){
                        numberOfFinishedSubreddits++;
                        if(numberOfFinishedSubreddits == subredditNames.length){
                            mSpinner.setVisibility(View.GONE);
                            mStartButton.setVisibility(View.VISIBLE);
                        }
                    }
                    //Get the links returned by the parser
                    RedditParser rp = (RedditParser) subredditFeed.get(message.obj);
                    subredditLinks.put(message.obj,getRidOfNulls(rp.redditLinks));
                    subredditAfter.put(message.obj,rp.after);
                    subredditTitles.put(message.obj,getRidOfNulls(rp.redditTitles));
                    subredditOver_18.put(message.obj,getRidOfNulls(rp.redditOver_18));
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler));
        }

        subs = subredditFeed.keys();
    }

    SubredditManager(String[] srs, LinkManager lm){
        subredditNames = srs;
        this.linkManager = lm;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.obj != null){

                    //Get the links returned by the parser
                    RedditParser rp = (RedditParser) subredditFeed.get(message.obj);
                    linkManager.newLinks.put(message.obj,getRidOfNulls(rp.redditLinks));
                    linkManager.newAfter.put(message.obj,rp.after);
                    linkManager.newTitles.put(message.obj,getRidOfNulls(rp.redditTitles));
                    linkManager.newOver_18s.put(message.obj,getRidOfNulls(rp.redditOver_18));
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler,(String)lm.currentAfter.get(srn)));
        }

        subs = subredditFeed.keys();
    }

    public void loadMoreSubredditContent(String srn, LinkManager lm){
        this.linkManager = lm;
        RedditParser rp = (RedditParser) subredditFeed.get(srn);
        rp.execute("");
    }


    public void getAllSubredditStarted(){
        while(subs.hasMoreElements()){
            RedditParser rp = (RedditParser) subredditFeed.get(subs.nextElement());
            rp.execute("");
        }
    }

    public String[] getRidOfNulls(String[] array){
        List<String> arrayWithoutNulls = new ArrayList<String>();
        for(String s: array){
            if(s != null && s.length() > 0){
                arrayWithoutNulls.add(s);
            }
        }
        return (String[])arrayWithoutNulls.toArray(new String[arrayWithoutNulls.size()]);
    }


    public Hashtable getSubredditLinks(){
        return this.subredditLinks;
    }

}

