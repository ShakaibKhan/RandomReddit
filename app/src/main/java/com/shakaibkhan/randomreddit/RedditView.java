package com.shakaibkhan.randomreddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-05.
 */

public class RedditView extends FragmentActivity implements PostSlidingFragment.OnInvisibleListener {
    public ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    public static LinkManager linkManager;
    private Hashtable currentLinks;
    private Hashtable currentTitles;
    private Hashtable currentAfters;
    private Hashtable currentOver_18s;
    public static PostCalculator postCalculator;
    public PostSlidingFragment slidingFragment;
    public String oldSubreddit = "";

    public void refreshPost(PostSlidingFragment psf){
        psf.refreshGonePost();
    }

    public void setOldSubreddit(String subreddit){
        oldSubreddit = subreddit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_parser);
        this.mViewPager = (ViewPager) findViewById(R.id.pager);
        this.mViewPager.setOffscreenPageLimit(1);
        mPagerAdapter = new RedditView.ScreenSlidePagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.setCurrentItem(1000);
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            int oldPosition = mViewPager.getCurrentItem();
            @Override
            public void onPageSelected(int position){

                //Change the subtotals of the subreddit depending on which way the user scrolls


                if(oldSubreddit != ""){
                    //Scroll right
                    if(position > oldPosition){
                        postCalculator.changeSubtotalBy(oldSubreddit, 1);
                    }
                    //Scroll left
                    else if(position < oldPosition){
                        postCalculator.changeSubtotalBy(oldSubreddit, -1);
                    }
                }

                oldPosition = position;
            }
            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        })
        ;

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

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            slidingFragment = new PostSlidingFragment();
            slidingFragment.setManagersAndExecute(postCalculator,linkManager);
            slidingFragment.setFragmentPostion(position);
            return slidingFragment;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        //Infinite Scrolling
        @Override
        public int getCount(){
            return Integer.MAX_VALUE;
        }

    }

}
