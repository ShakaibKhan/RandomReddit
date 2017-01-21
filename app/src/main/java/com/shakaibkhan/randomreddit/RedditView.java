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
import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-05.
 */

public class RedditView extends FragmentActivity {
    ImageView mImageDisplay;
    private static final int NUM_PAGES = 100;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private RedditParser redditParser;
    private SubredditManager srm;
    private Hashtable currentLinks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_parser);
        this.mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new RedditView.ScreenSlidePagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(mPagerAdapter);
        redditParser = new RedditParser("nsfw_gifs");
        redditParser.execute("");

        //Try to get the initially loaded image urls
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Serializable subredditLinks = bundle.getSerializable("InitialLinks");
        this.currentLinks = (Hashtable)subredditLinks;

        // Load another table of links
        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        //this.srm = new SubredditManager(subreddits,false,);
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
            slidingFragment.setUrl(redditParser.getImageLink());
            if(redditParser.position == redditParser.final_position){
                redditParser = new RedditParser(redditParser.subreddit, redditParser.after);
                redditParser.execute("");
            }
            slidingFragment.setFragmentPostion(position);
            return slidingFragment;
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }

    }

}
