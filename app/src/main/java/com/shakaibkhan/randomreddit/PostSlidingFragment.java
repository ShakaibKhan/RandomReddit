package com.shakaibkhan.randomreddit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    ImageView mImageDisplay;
    ProgressBar mProgressBar;
    int fragmentPostion = 0;
    String url = null;

    public void setUrl(String urlImage){
        this.url = urlImage;
    }

    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_sliding,container,false);
        mImageDisplay = (ImageView) rootView.findViewById(R.id.image_displayed);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        setImage(url);
        return rootView;
    }

    public void setImage(String url){
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mProgressBar.setVisibility(View.GONE);
                return false;
            }
        })
        .into(mImageDisplay);
    }

    public void setFragmentPostion(int postion){
        fragmentPostion = postion;
    }
 }
