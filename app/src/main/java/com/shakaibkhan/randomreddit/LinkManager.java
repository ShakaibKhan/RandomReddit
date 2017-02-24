package com.shakaibkhan.randomreddit;

import android.util.Log;

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

    LinkManager(Hashtable<String,String[]>cl, Hashtable<String,String> ca,
                Hashtable<String,String[]> ct, Hashtable<String,String[]> co18
                ,String[] subredditList
    ){
        this.currentLinks = cl;
        this.currentAfter = ca;
        this.currentTitles = ct;
        this.currentOver_18s = co18;

        this.subredditManager = new SubredditManager(subredditList,this);
        this.subredditManager.getAllSubredditStarted();

        for(String sr: subredditList){
            positions.put(sr,0);
        }
    }

    public String getUrl(String subreddit){
        String[] url = (String[])currentLinks.get(subreddit);
        if((int)this.positions.get(subreddit) >= url.length){
            this.positions.put(subreddit,0);
            getMoreContent(subreddit);
        }
        //updating the position of the particular subreddit
        this.positions.put(subreddit,(int)this.positions.get(subreddit)+1);
        if(((int)this.positions.get(subreddit)-1) == 0){
            if(url.length == 0){
                Log.e("a","a");
            }
        }
        return url[(int)this.positions.get(subreddit)-1];
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
        currentLinks.put(subreddit,newLinks.get(subreddit));
        currentTitles.put(subreddit,newTitles.get(subreddit));
        currentOver_18s.put(subreddit,newOver_18s.get(subreddit));
        currentAfter.put(subreddit,newAfter.get(subreddit));
        this.subredditManager.loadMoreSubredditContent(subreddit,this);
    }

}
