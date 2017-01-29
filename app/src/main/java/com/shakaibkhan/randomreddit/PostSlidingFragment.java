package com.shakaibkhan.randomreddit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

/**
 * Created by shakaibkhan on 2016-12-31.
 */

public class PostSlidingFragment extends Fragment {
    private ImageView mImageDisplay;
    private ProgressBar mProgressBar;
    private TextView mPostTitle;
    private int fragmentPostion = 0;

    private String url = null;
    private String title = null;

    public LinkManager linkManager;
    public PostCalculator postCalculator;
    private ViewPager mViewPager;
    public void setUrl(String urlImage){
        this.url = urlImage;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setManagersAndExecute(PostCalculator pc, LinkManager lm){
        this.postCalculator = pc;
        this.linkManager = lm;
        executeNewPost();
    }

    public void executeNewPost(){
        String postSubreddit = postCalculator.getNextSubreddit();

        //Set the title, image url, and age restriction of the post
        this.setUrl(linkManager.getUrl(postSubreddit));
        this.setTitle(linkManager.getTitle(postSubreddit));
    }

    int index = 1;

    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_sliding,container,false);
        mImageDisplay = (ImageView) rootView.findViewById(R.id.image_displayed);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mPostTitle = (TextView) rootView.findViewById(R.id.title_displayed);

        setImage(url);
        mPostTitle.setText(this.title);
        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }else{
            //mViewPager.getAdapter().notifyDataSetChanged();
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
 }
