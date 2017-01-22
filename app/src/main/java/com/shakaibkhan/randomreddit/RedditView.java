package com.shakaibkhan.randomreddit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-05.
 */

public class RedditView extends FragmentActivity {
    ImageView mImageDisplay;
    private static final int NUM_PAGES = 100;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    public static LinkManager linkManager;

    private Hashtable currentLinks;
    private Hashtable currentTitles;
    private Hashtable currentAfters;
    private Hashtable currentOver_18s;

    public static PostCalculator postCalculator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_parser);
        this.mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new RedditView.ScreenSlidePagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(mPagerAdapter);
//        redditParser = new RedditParser("pics");
//        redditParser.execute("");

        //Try to get the initially loaded image urls
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        this.currentLinks = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("InitialLinks"));
        this.currentAfters = new Hashtable<String,String>((HashMap<String,String>)bundle.getSerializable("Afters"));
        this.currentTitles = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("Titles"));
        this.currentOver_18s = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("Over_18"));

        // Load another table of links
        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        this.linkManager= new LinkManager(currentLinks,currentAfters,currentTitles,currentOver_18s,subreddits);
        this.postCalculator = new PostCalculator(subreddits);
    }

    @Override
    public void onBackPressed(){
        if(mViewPager.getCurrentItem() == 0){
            super.onBackPressed();
        }else{
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() -1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            PostSlidingFragment slidingFragment = new PostSlidingFragment();
            String postSubreddit = postCalculator.getNextSubreddit();

            //Set the title, image url, and age restriction of the post
            slidingFragment.setUrl(linkManager.getUrl(postSubreddit));
            slidingFragment.setTitle(linkManager.getTitle(postSubreddit));
            slidingFragment.setFragmentPostion(position);
            return slidingFragment;
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }

    }

}
