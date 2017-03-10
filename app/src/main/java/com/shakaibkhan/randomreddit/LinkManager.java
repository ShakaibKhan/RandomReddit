package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-21.
 */

public class LinkManager {
    public Hashtable currentLinks = new Hashtable();
    public Hashtable currentAfter = new Hashtable();
    public Hashtable currentTitles = new Hashtable();
    public Hashtable currentOver_18s = new Hashtable();

    public Hashtable newLinks = new Hashtable();
    public Hashtable newAfter = new Hashtable();
    public Hashtable newTitles = new Hashtable();
    public Hashtable newOver_18s = new Hashtable();

    public SubredditManager subredditManager;


    public Hashtable positions = new Hashtable();

    public Button retryButton;
    public ViewPager viewPager;
    private Context context;
    public TextView retryText;

    public String getAfter(){
        String Afters = currentAfter.toString();
        return Afters;
    }

    LinkManager(Hashtable<String,String[]>cl, Hashtable<String,String> ca,
                Hashtable<String,String[]> ct, Hashtable<String,String[]> co18
                ,String[] subredditList, Context mcontext, Button rtb, ViewPager vp, TextView rtt
    ){
        this.currentLinks = cl;
        this.currentAfter = ca;
        this.currentTitles = ct;
        this.currentOver_18s = co18;
        this.context = mcontext;
        this.retryButton = rtb;
        this.retryText = rtt;

        this.subredditManager = new SubredditManager(subredditList,this,retryButton,context,vp,rtt);
        this.subredditManager.getAllSubredditStarted();

        for(String sr: subredditList){
            positions.put(sr,0);
        }
    }

    public String getUrl(String subreddit, boolean NSFWON){
        String[] url = (String[])currentLinks.get(subreddit);
        String[] nsfw = (String[])currentOver_18s.get(subreddit);
        if((int)this.positions.get(subreddit) >= (url.length)){
            this.positions.put(subreddit,0);
            getMoreContent(subreddit);
            return getUrl(subreddit,NSFWON);
        }
        //updating the position of the particular subreddit
        this.positions.put(subreddit,(int)this.positions.get(subreddit)+1);

        if((!NSFWON) && (Boolean.parseBoolean(nsfw[(int)this.positions.get(subreddit)-1]) == true)){
            return "NSFW";
        }else{
            return url[(int)this.positions.get(subreddit)-1];
        }

    }

    public String getUrl(String subreddit){
        return getUrl(subreddit, false);
    }


    public String getTitle(String subreddit){
        String[] titles = (String[])currentTitles.get(subreddit);
        if((int)this.positions.get(subreddit) >= titles.length){
            return "";
        }
        return titles[(int)this.positions.get(subreddit)-1]
                + "\n r/" + subreddit;
    }

    public void getMoreContent(String subreddit){
        //Load more links here
        String[] urls = (String[])newLinks.get("pics");

        //if(urls != null){
            currentLinks.put(subreddit,newLinks.get(subreddit));
            currentTitles.put(subreddit,newTitles.get(subreddit));
            currentOver_18s.put(subreddit,newOver_18s.get(subreddit));
            currentAfter.put(subreddit,newAfter.get(subreddit));
        //}
        this.subredditManager.loadMoreSubredditContent(subreddit,this,context);
    }

}
