package squarerock.hoot.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TwitterTimeline {

    private List<TweetModel> tweets;

    public TwitterTimeline(List<TweetModel> tweets) {
        this.tweets = tweets;
    }

    public List<TweetModel> getTweets() {
        return tweets;
    }

    public void setTweets(List<TweetModel> tweets) {
        this.tweets = tweets;
    }

    public static TwitterTimeline parseJSON(String response){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});
        Gson gson = gsonBuilder.create();

        return gson.fromJson("{\"tweets\":"+response+"}", TwitterTimeline.class);
    }
}
