package com.shakaibkhan.randomreddit;

import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by shakaibkhan on 2017-01-21.
 */

public class PostCalculator {
    public Hashtable subTotals = new Hashtable();
    private int total = 0;
    private String[] subreddits;

    PostCalculator(String[] sr){
        this.subreddits = sr;
        for(String subredditName: this.subreddits){
            subTotals.put(subredditName,1);
            total++;
        }
    }

    public String getNextSubreddit(){
        String[] subredditPool =  new String[total];
        Random random = new Random();
        int randomNumber = random.nextInt(total);
        int index = 0;

        for(String sr: subreddits){
            for(int i = 0; i< (int)subTotals.get(sr); i++){
                subredditPool[index] = sr;
                index++;
            }
        }
        return subredditPool[randomNumber];
    }
}
