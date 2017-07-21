package com.shakaibkhan.randomreddit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;


public class MainActivity extends Activity {

    private Button mStartButton;
    private ProgressBar mSpinner;
    public SubredditManager subredditManager;
    private Context mContext;
    private VideoView mVideoView;
    private Button retryConnection;
    private ImageView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String writeDate = df.format(c.getTime());

        //Get the last afters of the current date
        SharedPreferences sharedPreferences = getSharedPreferences("last_afters",MODE_PRIVATE);
        String past_afters = sharedPreferences.getString("Afters","NA");
        String past_date = sharedPreferences.getString("Date","NA");


        //Opening  screen video
        mVideoView = (VideoView) findViewById(R.id.openingVideo);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        String path = "android.resource://" + getPackageName() + "/" + R.raw.redditclash;
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.start();


        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);
        mStartButton = (Button)findViewById(R.id.btn_login);
        mContext = getApplicationContext();
        mStartButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext,"Start Browsing",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });

        retryConnection = (Button) findViewById(R.id.retry_main);
        mSpinner = (ProgressBar) findViewById(R.id.spinner);
        if(writeDate.equals(past_date) && !past_afters.equals("NA")){
            Hashtable ht_afters = parseAfters(past_afters);
            this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true,ht_afters,retryConnection, mContext);
        }else{
            this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true,retryConnection, mContext);
        }



        title = (ImageView) findViewById(R.id.app_title);
        subredditManager.getAllSubredditStarted();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mVideoView.start();
    }

    public Hashtable parseAfters(String aft){
        Hashtable return_after =  new Hashtable();
        String[] afters_array = aft.split(",");
        String[] afters_name = new String[afters_array.length];
        for(int i =0;i<afters_array.length;i++){
            afters_array[i] = afters_array[i].replace("{","");
            afters_array[i] = afters_array[i].replace("}","");
            int after_start =afters_array[i].indexOf("=")+1;
            int string_end = afters_array[i].length();
            afters_name[i] = afters_array[i].substring(0,after_start-1);
            afters_array[i] = afters_array[i].substring(after_start,string_end);
            return_after.put(afters_name[i],afters_array[i]);
        }
        return return_after;
    }

    public void openReddit(View view){
        Intent openBrowser = new Intent(this, RedditView.class);
        //Get all of the initally loaded reddit info
        Hashtable ht = this.subredditManager.getSubredditLinks();
        Hashtable av = this.subredditManager.subredditAfter;
        Hashtable rt = this.subredditManager.subredditTitles;
        Hashtable oe = this.subredditManager.subredditOver_18;

        //send reddit view the links we loaded
        Bundle bundle = new Bundle();
        bundle.putSerializable("InitialLinks",ht);
        bundle.putSerializable("Afters",av);
        bundle.putSerializable("Titles",rt);
        bundle.putSerializable("Over_18",oe);
        openBrowser.putExtras(bundle);
        startActivity(openBrowser);
    }

    public void retrySubredditManager(View vew){
        //Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String writeDate = df.format(c.getTime());

        //Get the last afters of the current date
        SharedPreferences sharedPreferences = getSharedPreferences("last_afters",MODE_PRIVATE);
        String past_afters = sharedPreferences.getString("Afters","NA");
        String past_date = sharedPreferences.getString("Date","NA");

        String[] subreddits = getResources().getStringArray(R.array.subreddit_list);

        if(writeDate.equals(past_date) && !past_afters.equals("NA")){
            Hashtable ht_afters = parseAfters(past_afters);
            this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true,ht_afters,retryConnection,mContext);
        }else{
            this.subredditManager = new SubredditManager(subreddits,mSpinner,mStartButton,true,retryConnection,mContext);
        }
        retryConnection.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        subredditManager.getAllSubredditStarted();
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}


