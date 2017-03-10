package com.shakaibkhan.randomreddit;


import android.content.Context;
import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.*;


/**
 * Created by shakaibkhan on 2017-01-03.
 */

public class RedditParser extends AsyncTask<String, String, String> {

    public String subreddit;
    final private String REDDIT_BASE_URL = "https://www.reddit.com/r/";
    public String redditURL ;
    public String after = null;
    public int position = 0;
    public int final_position = 0;
    public OkHttpClient webClient = new OkHttpClient();

    public String[] redditLinks = new String[25];
    public String[] redditTitles = new String[25];
    public String[] redditOver_18 = new String[25];

    public String redditResponse;
    private Handler handler = null;

    private Context mContext;


    RedditParser(String sr, Handler handle, Context context){
        this.subreddit = sr;
        this.handler = handle;
        this.mContext = context;
    }

    RedditParser(String sr, Handler handle, String af, Context context){
        subreddit = sr;
        this.after = af;
        this.handler = handle;
        this.mContext = context;
    }

    private void buildUrl(){
        this.redditURL = REDDIT_BASE_URL +this.subreddit+"/top/.json?limit=25";
        if(this.after != null ){
            this.redditURL += "&after="+this.after;
        }
    }


    public String getImageLink(){
        // End the thread and call a new one with after
        if(position>=final_position){
            //this.cancel(true);
        }
        position += 1;
        return redditLinks[position-1];
    }

    @Override
    protected String doInBackground(String... params){
        redditResponse = null;
        this.buildUrl();
        Request request = new Request.Builder().url(this.redditURL).build();
        if(MainActivity.isNetworkAvailable(mContext)){
            try{
                Response response = webClient.newCall(request).execute();
                redditResponse = response.body().string();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return  redditResponse;
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        JSONObject redditJson = null;
        JSONArray redditJsonArray = null;

                try{
            //NO NETWORK CONNECTION SO PLEASE TRY AGAIN
            if(redditResponse == null || redditResponse.isEmpty()){
                Message message = handler.obtainMessage();
                message.obj = "NOCONNECTION";
                this.handler.sendMessage(message);
                return;
            }

            redditJson = new JSONObject(redditResponse);
            redditJson = redditJson.getJSONObject("data");
            this.after = redditJson.getString("after");
            redditJsonArray = redditJson.getJSONArray("children");


            for(int i = 2; i< redditJsonArray.length();i++){
                redditJson = redditJsonArray.getJSONObject(i);
                redditJson = redditJson.getJSONObject("data");
                if(checkUrl(redditJson.getString("url"))){
                    if(redditJson.getString("url").contains(".gifv")){
                        redditLinks[final_position] = fixGifUrl(redditJson.getString("url").replace(";","&").replace("&amp&","&"));
                    }else{
                        redditLinks[final_position] = redditJson.getString("url").replace(";","&").replace("&amp&","&");
                    }

                    redditTitles[final_position] = redditJson.getString("title").replace(";","&").replace("&amp&","&");
                    redditOver_18[final_position] = redditJson.getString("over_18").replace(";","&").replace("&amp&","&");
                    final_position++;
                }
            }

        }catch(JSONException je){
            je.printStackTrace();
        }
        //Once we are finished we tell the handler we are finished and pass the links we found to
        if(this.handler != null){
            Message message = handler.obtainMessage();
            message.obj = this.subreddit;
            this.handler.sendMessage(message);
        }

    }

    private boolean checkUrl(String url){
        if(url.contains(".gif") ||url.contains(".gifv") || url.contains(".png") || url.contains(".jpg")
                || url.contains(".mp4") || url.contains(".jpeg") || url.contains("i.reddituploads.com")){
            return true;
        }
        return false;
    }

    private String fixGifUrl(String gifUrl){
        return gifUrl.replace(".gifv",".gif");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

}
