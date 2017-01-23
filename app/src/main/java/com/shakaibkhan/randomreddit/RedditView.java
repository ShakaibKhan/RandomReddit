package com.shakaibkhan.randomreddit;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by shakaibkhan on 2017-01-05.
 */

public class RedditView extends FragmentActivity {
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    public static LinkManager linkManager;
    private Hashtable currentLinks;
    private Hashtable currentTitles;
    private Hashtable currentAfters;
    private Hashtable currentOver_18s;

    public final int numberOfPages = 3;

    public static PostCalculator postCalculator;



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
            @Override
            public void onPageSelected(int position){
                if (mViewPager.getAdapter() != null) {
                    ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) mViewPager.getAdapter();
                    PostSlidingFragment fragment = (PostSlidingFragment) adapter.getItem(position);
                    if (fragment != null) {
                        fragment.executeNewPost();
                    }
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int arg0) {
                //mViewPager.getAdapter().notifyDataSetChanged();
            }
        });

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
        private Fragment[] fragments = new Fragment[3];
        private int index = 0;
        int oldPosition = 1000;

        public String oldSubreddit;
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){

            PostSlidingFragment slidingFragment = new PostSlidingFragment();
            slidingFragment.setManagersAndExecute(postCalculator,linkManager);
            slidingFragment.setFragmentPostion(position);

            return slidingFragment;
            //mViewPager.getAdapter().notifyDataSetChanged();
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
