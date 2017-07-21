package com.shakaibkhan.randomreddit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView mPostTitle;
    private ImageButton mShareButton;
    private Switch mNsfwSwitch = null;

    private int fragmentPostion = 0;
    public static final String IMAGE_URL="image";

    public String url = null;
    public String title = null;

    public LinkManager linkManager;
    public PostCalculator postCalculator;
    public OnInvisibleListener mCallback;
    public boolean isAttached = false;

    public String postSubreddit;

    public void setUrl(String urlImage){
        this.url = urlImage;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Context mContext;

    public void setManagersAndExecute(PostCalculator pc, LinkManager lm){
        this.postCalculator = pc;
        this.linkManager = lm;
        executeNewPost();
    }

    public void executeNewPost(){
        postSubreddit = postCalculator.getNextSubreddit();
        //Set the title, image url, and age restriction of the post
        if(mNsfwSwitch != null) {
            boolean nsfwon = mNsfwSwitch.isChecked();
            String thisUrl = linkManager.getUrl(postSubreddit, nsfwon);
            while(thisUrl == "NSFW"){
                postSubreddit = postCalculator.getNextSubreddit();
                thisUrl = linkManager.getUrl(postSubreddit, nsfwon);
            }
            this.setUrl(thisUrl);

        }else{
            this.setUrl(linkManager.getUrl(postSubreddit));
        }

        if(this.url == "NSFW"){
            this.setTitle("NSFW POST");
        }else{
            this.setTitle(linkManager.getTitle(postSubreddit));
        }


    }

    public void refreshGonePost(){
        this.mPostTitle.setText(title);
        this.setImage(url);
    }

    public void setPostTitle(){
        this.mPostTitle.setText(title);
    }

    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post_sliding,container,false);


        final Context context = getContext();
        mImageDisplay = (ImageView) rootView.findViewById(R.id.image_displayed);
        mImageDisplay.setOnClickListener(
        new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(context,CloseImageView.class);
                intent.putExtra(IMAGE_URL,url);
                startActivityForResult(intent,1);
            }
        });


        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        this.setImage(url);
        mPostTitle = (TextView) rootView.findViewById(R.id.title_displayed);
        mPostTitle.setText(title);
        mShareButton = (ImageButton) rootView.findViewById(R.id.share_button);

        //Start action send intent to share image urls
        mShareButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent shareIntent = new Intent();
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setAction(Intent.ACTION_SEND);
                String message = url+" \n\nThis image was sent from Meme Clash";
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.removeExtra(Intent.EXTRA_KEY_EVENT);

                Intent chooser = Intent.createChooser(shareIntent, "Share This Image");
                try{
                    startActivity(chooser);
                }catch(Exception e){
                    Toast.makeText(mContext, "Please Download A Social Media App To Share Pics", Toast.LENGTH_LONG);
                }
            }
        });
        return rootView;
    }

    public void setViewPagerElements(Switch nsfws ){
        mNsfwSwitch = nsfws;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(isAttached) {
                mCallback.setOldSubreddit(postSubreddit);
            }
            //setPostTitle();
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

    public interface OnInvisibleListener{
        public void refreshPost(PostSlidingFragment psf);

        public void setOldSubreddit(String subreddit);
    }

    public void setContext(Context c){
        mContext = c;
    }
 }


