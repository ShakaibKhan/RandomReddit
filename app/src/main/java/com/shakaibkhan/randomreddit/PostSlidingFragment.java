package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by shakaibkhan on 2016-12-31.
 */

public class PostSlidingFragment extends Fragment {
    private ImageView mImageDisplay;
    private ProgressBar mProgressBar;
    public TextView mPostTitle;
    private int fragmentPostion = 0;

    public String url = null;
    public String title = null;

    public LinkManager linkManager;
    public PostCalculator postCalculator;
    public OnInvisibleListener mCallback;
    public boolean isAttached = false;

    public String postSubreddit;

    private ImageView mVoteDisplay;

    public void setUrl(String urlImage){
        this.url = urlImage;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Animation animationFadeOut;

    public void setAnimation(Context mContext){
        animationFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        animationFadeOut.setRepeatCount(Animation.INFINITE);
        animationFadeOut.setRepeatMode(Animation.RESTART);
    }

    public void setManagersAndExecute(PostCalculator pc, LinkManager lm){
        this.postCalculator = pc;
        this.linkManager = lm;
        executeNewPost();
    }

    public void executeNewPost(){
        postSubreddit = postCalculator.getNextSubreddit();
        //Set the title, image url, and age restriction of the post
        this.setUrl(linkManager.getUrl(postSubreddit));
        this.setTitle(linkManager.getTitle(postSubreddit));
    }

    public void refreshGonePost(){
        this.mPostTitle.setText(title);
        this.setImage(url);
        mVoteDisplay.setVisibility(View.VISIBLE);
        mVoteDisplay.setImageResource(R.drawable.upvote);
    }

    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_sliding,container,false);
        mImageDisplay = (ImageView) rootView.findViewById(R.id.image_displayed);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mPostTitle = (TextView) rootView.findViewById(R.id.title_displayed);
        mVoteDisplay = (ImageView) rootView.findViewById(R.id.vote_display);
        this.mPostTitle.setText(title);
        this.setImage(url);

        return rootView;
    }
    int it = 0;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(isAttached){
                mCallback.setOldSubreddit(postSubreddit);
            }
        }else{
            if(isAttached){
                executeNewPost();
                mCallback.refreshPost(this);
            }
        }
    }

    public void setImage(String url){
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mProgressBar.setVisibility(View.GONE);
                mPostTitle.setVisibility(View.VISIBLE);
                mImageDisplay.setVisibility(View.GONE);
                mImageDisplay.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mProgressBar.setVisibility(View.GONE);
                mPostTitle.setVisibility(View.VISIBLE);
                mImageDisplay.setVisibility(View.GONE);
                mImageDisplay.setVisibility(View.VISIBLE);
                return false;
            }
        })
        .into(mImageDisplay);
    }

    public void setFragmentPostion(int postion){
        fragmentPostion = postion;
    }

    @Override
    public void onPause(){
        super.onPause();
        return;
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        isAttached = true;

        try{
            mCallback = (OnInvisibleListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()+ " did not implement OnInvisibleListener");
        }

    }

    //Fades out the upvote symbol using anim and it's interporlator
    public void setUpVote(){
        mVoteDisplay.setImageResource(R.drawable.upvote);
        mVoteDisplay.setVisibility(View.GONE);
        mVoteDisplay.startAnimation(animationFadeOut);
    }

    public void setDownVote(){
        mVoteDisplay.setImageResource(R.drawable.downvote);
        mVoteDisplay.setVisibility(View.GONE);
        mVoteDisplay.startAnimation(animationFadeOut);
        //mVoteDisplay.animation
    }

    public interface OnInvisibleListener{
        public void refreshPost(PostSlidingFragment psf);

        public void setOldSubreddit(String subreddit);
    }
 }


