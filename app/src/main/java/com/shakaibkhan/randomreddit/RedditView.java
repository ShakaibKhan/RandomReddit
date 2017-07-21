package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by shakaibkhan on 2017-01-05.
 */

public class RedditView extends FragmentActivity implements PostSlidingFragment.OnInvisibleListener {

    public ViewPager mViewPager;
    public ImageView mVoteDisplay;
    private PagerAdapter mPagerAdapter;
    public static LinkManager linkManager;
    private Hashtable currentLinks;
    private Hashtable currentTitles;
    private Hashtable currentAfters;
    private Hashtable currentOver_18s;
    public static PostCalculator postCalculator;
    public PostSlidingFragment slidingFragment;
    public String oldSubreddit = "";
    public Animation animationFadeOut;
    public Context mContext;

    public FloatingActionButton mSkipButton;
    public Switch mNsfwSwitch;
    public Button retryConnection;
    public TextView retryText;

    private boolean skippedPost = false;


    public void refreshPost(PostSlidingFragment psf){
        psf.refreshGonePost();
    }

    public void setOldSubreddit(String subreddit){
        oldSubreddit = subreddit;
    }

    @Override
    protected void onDestroy(){
        SharedPreferences pref;
        pref = getSharedPreferences("last_afters", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Afters",linkManager.getAfter());

        //Get the current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String writeDate = df.format(c.getTime());

        editor.putString("Date", writeDate);
        editor.commit();

        super.onDestroy();
    }

    @Override
    protected void onStop(){
        SharedPreferences pref;
        pref = getSharedPreferences("last_afters", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Afters",linkManager.getAfter());

        //Get the current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String writeDate = df.format(c.getTime());

        editor.putString("Date", writeDate);
        editor.commit();

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_parser);

        this.mVoteDisplay = (ImageView) findViewById(R.id.vote_display);

        animationFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animationFadeOut.setRepeatCount(Animation.INFINITE);
        animationFadeOut.setRepeatMode(Animation.RESTART);

        mNsfwSwitch = (Switch)findViewById(R.id.nsfw_switch);
        mNsfwSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNsfwSwitch.isChecked()) {
                    mNsfwSwitch.setText("NSFW ON");
                }else{
                    mNsfwSwitch.setText("NSFW OFF");
                }
            }
        });

        this.retryConnection = (Button) findViewById(R.id.retry_fragment);

        this.mViewPager = (ViewPager) findViewById(R.id.pager);
        this.mViewPager.setOffscreenPageLimit(1);
        mPagerAdapter = new RedditView.ScreenSlidePagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.setCurrentItem(1000);
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            int oldPosition = mViewPager.getCurrentItem();
            @Override
            public void onPageSelected(int position){

                if(!MainActivity.isNetworkAvailable(getApplicationContext())){
                    mViewPager.setVisibility(View.GONE);
                    retryConnection.setVisibility(View.VISIBLE);
                    retryText.setVisibility(View.VISIBLE);
                }

                //Change the subtotals of the subreddit depending on which way the user scrolls
                if(oldSubreddit != ""){
                    if(!skippedPost){
                        //Scroll right
                        if(position < oldPosition){
                            postCalculator.changeSubtotalBy(oldSubreddit, 1);
                            mVoteDisplay.setImageResource(R.drawable.upvote);
                        }
                        //Scroll left
                        else if(position > oldPosition){
                            postCalculator.changeSubtotalBy(oldSubreddit, -1);
                            mVoteDisplay.setImageResource(R.drawable.downvote);
                        }
                        mVoteDisplay.setVisibility(View.GONE);
                        mVoteDisplay.startAnimation(animationFadeOut);
                    }
                    skippedPost = false;
                }

                oldPosition = position;

            }
            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        //Try to get the initially loaded image urls
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        this.currentLinks = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("InitialLinks"));
        this.currentAfters = new Hashtable<String,String>((HashMap<String,String>)bundle.getSerializable("Afters"));
        this.currentTitles = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("Titles"));
        this.currentOver_18s = new Hashtable<String,String[]>((HashMap<String,String[]>)bundle.getSerializable("Over_18"));

        this.retryText = (TextView) findViewById(R.id.retrytext_fragment);


        // Load another table of links
        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        this.linkManager= new LinkManager(currentLinks,currentAfters,currentTitles,currentOver_18s,subreddits,getApplicationContext(),retryConnection,mViewPager,retryText);
        this.postCalculator = new PostCalculator(subreddits);

        mContext = this;

        mSkipButton = (FloatingActionButton) findViewById(R.id.skip_button);
        mSkipButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                skippedPost = true;
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });


    }

    public void restartBrowser(View view){
        retryConnection.setVisibility(View.GONE);
        retryText.setVisibility(View.GONE);
        Intent openBrowser = new Intent(this, RedditView.class);
        //Get all of the initally loaded reddit info
        Hashtable ht = this.currentLinks;
        Hashtable av = this.currentAfters;
        Hashtable rt = this.currentTitles;
        Hashtable oe = this.currentOver_18s;

        //send reddit view the links we loaded
        Bundle bundle = new Bundle();
        bundle.putSerializable("InitialLinks",ht);
        bundle.putSerializable("Afters",av);
        bundle.putSerializable("Titles",rt);
        bundle.putSerializable("Over_18",oe);
        openBrowser.putExtras(bundle);
        startActivity(openBrowser);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent startHome = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startHome);
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
            slidingFragment.setContext(mContext);
            slidingFragment.setViewPagerElements(mNsfwSwitch);
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
