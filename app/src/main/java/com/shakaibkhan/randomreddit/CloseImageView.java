package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by shakaibkhan on 2017-06-04.
 */

public class CloseImageView extends Activity {

    private WebView zoomableImage;
    private Button backButton;

    @Override

    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_post_zoomable);

        zoomableImage = (WebView) findViewById(R.id.post_webview);
        backButton = (Button) findViewById(R.id.back_button);

        Intent intent = getIntent();
        String imageURL = intent.getStringExtra(PostSlidingFragment.IMAGE_URL);
        zoomableImage.loadUrl(imageURL);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity(1);
            }
        });

    }



}
