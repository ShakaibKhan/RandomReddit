package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by shakaibkhan on 2017-01-13.
 */

public class SubredditManager {

    public static final int NUMBER_OF_SUBREDDITS = 10;
    public String[] subredditNames = new String[NUMBER_OF_SUBREDDITS];
    private Enumeration subs ;
    private Handler handler ;
    public int numberOfFinishedSubreddits = 0;
    public ProgressBar mSpinner;
    public Button mStartButton;
    public boolean startPage;
    public Button retryButton;
    public ViewPager viewPager;
    public TextView retryText;

    //
    public Hashtable subredditLinks = new Hashtable();
    public Hashtable subredditAfter = new Hashtable();
    public Hashtable subredditTitles = new Hashtable();
    public Hashtable subredditOver_18 = new Hashtable();
    private Hashtable subredditFeed = new Hashtable();

    public LinkManager linkManager;

    //Used at the main activity
    SubredditManager(String[] srs, ProgressBar spin, Button btn, boolean start,Button rtbt, Context context){
        this.startPage = start;
        subredditNames = srs;
        this.mSpinner = spin;
        this.mStartButton = btn;
        this.retryButton = rtbt;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.obj != null){
                    if(startPage){
                        numberOfFinishedSubreddits++;
                        if((numberOfFinishedSubreddits == subredditNames.length) && (!message.obj.equals("NOCONNECTION"))){
                            mSpinner.setVisibility(View.GONE);
                            mStartButton.setVisibility(View.VISIBLE);
                        }
                        if(message.obj.equals("NOCONNECTION")){
                            mSpinner.setVisibility(View.GONE);
                            retryButton.setVisibility(View.VISIBLE);
                            return;
                            //numberOfFinishedSubreddits = 0;
                        }
                    }
                    //Get the links returned by the parser
                    RedditParser rp = (RedditParser) subredditFeed.get(message.obj);
                    int[] indexes = getNullIndexes(rp.redditLinks);
                    //clears all reddit links that are null while leaving the null titles, over 18's, and anyother content intact


                    subredditLinks.put(message.obj,cleanArray(rp.redditLinks,indexes));
                    subredditAfter.put(message.obj,rp.after);
                    subredditTitles.put(message.obj,cleanArray(rp.redditTitles,indexes));
                    subredditOver_18.put(message.obj,cleanArray(rp.redditOver_18,indexes));
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler,context));
        }

        subs = subredditFeed.keys();
    }

    //Only used by viewpager class
    SubredditManager(String[] srs, LinkManager lm, Button retryB, Context context, ViewPager vp, TextView rtt){
        subredditNames = srs;
        this.linkManager = lm;
        this.viewPager = vp;
        this.retryButton = retryB;
        this.retryText =rtt;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.obj != null){
                    if(message.obj.equals("NOCONNECTION")){
                        viewPager.setVisibility(View.GONE);
                        retryButton.setVisibility(View.VISIBLE);
                        retryText.setVisibility(View.VISIBLE);
                        return;
                    }

                    //Get the links returned by the parser
                    RedditParser rp = (RedditParser) subredditFeed.get(message.obj);
                    int[] indexes = getNullIndexes(rp.redditLinks);

                    linkManager.newLinks.put(message.obj,cleanArray(rp.redditLinks,indexes));
                    linkManager.newAfter.put(message.obj,rp.after);
                    linkManager.newTitles.put(message.obj,cleanArray(rp.redditTitles,indexes));
                    linkManager.newOver_18s.put(message.obj,cleanArray(rp.redditOver_18,indexes));
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler,(String)lm.currentAfter.get(srn),context));
        }

        subs = subredditFeed.keys();
    }

    SubredditManager(String[] srs, ProgressBar spin, Button btn, boolean start, Hashtable afters, Button retryB, Context context){
        this.startPage = start;
        subredditNames = srs;
        this.mSpinner = spin;
        this.mStartButton = btn;
        this.retryButton = retryB;
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.obj != null){
                    if(startPage){
                        numberOfFinishedSubreddits++;
                        if((numberOfFinishedSubreddits == subredditNames.length) && (!message.obj.equals("NOCONNECTION"))){
                            mSpinner.setVisibility(View.GONE);
                            mStartButton.setVisibility(View.VISIBLE);
                        }
                        if(message.obj.equals("NOCONNECTION")){
                            mSpinner.setVisibility(View.GONE);
                            retryButton.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    //Get the links returned by the parser
                    RedditParser rp = (RedditParser) subredditFeed.get(message.obj);
                    int[] indexes = getNullIndexes(rp.redditLinks);
                    //clears all reddit links that are null while leaving the null titles, over 18's, and anyother content intact


                    subredditLinks.put(message.obj,cleanArray(rp.redditLinks,indexes));
                    subredditAfter.put(message.obj,rp.after);
                    subredditTitles.put(message.obj,cleanArray(rp.redditTitles,indexes));
                    subredditOver_18.put(message.obj,cleanArray(rp.redditOver_18,indexes));
                }
            }
        };
        for (String srn: subredditNames) {
            subredditFeed.put(srn,new RedditParser(srn,this.handler,(String)afters.get(srn),context));
        }

        subs = subredditFeed.keys();
    }

    public String[] cleanArray(String[] array, int[] indexes){
        String[] returnArray = new String[array.length-indexes.length];
        int arrayIndex = 0;
        int addinArrayIndex = 0;

        for(String item: array){
            boolean notNull =!(hasInt(indexes,arrayIndex));
            if(notNull){
                returnArray[addinArrayIndex] = array[arrayIndex];
                addinArrayIndex++;
            }
            arrayIndex++;
        }
        return returnArray;
    }

    public boolean hasInt(int[] arr, int targetValue) {
        for(int s: arr){
            if(s == targetValue)
                return true;
        }
        return false;
    }

    public void loadMoreSubredditContent(String srn, LinkManager lm,Context context){
        this.linkManager = lm;
        subredditFeed.put(srn,new RedditParser(srn,this.handler,(String)lm.currentAfter.get(srn),context));
        ((RedditParser)subredditFeed.get(srn)).execute("");
    }


    public void getAllSubredditStarted(){
        while(subs.hasMoreElements()){
            RedditParser rp = (RedditParser) subredditFeed.get(subs.nextElement());
            rp.execute("");
        }
    }

    public int[] getNullIndexes(String[] array){
        List<Integer> arrayWithoutNulls = new ArrayList<Integer>();
        int index = 0;
        for(String s: array){
            if(s == null || s.length() <= 0){
                arrayWithoutNulls.add(index);
            }
            index++;
        }
        return convertIntegers(arrayWithoutNulls);
    }

    public int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public Hashtable getSubredditLinks(){
        return this.subredditLinks;
    }

}

