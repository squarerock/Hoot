package squarerock.hoot.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class Utility {

    private static final String TAG = "Utility";

    public static String getRelativeTimeAgo(String rawJsonDate) {
        Log.d(TAG, "getRelativeTimeAgo: "+rawJsonDate);
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
                    .toString()
                    .replace(" seconds", "s")
                    .replace(" minutes", "m")
                    .replace(" minute", "m")
                    .replace(" hours", "h")
                    .replace(" hour", "h")
                    .replace("ago","");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getDetailViewTime(String rawJsonDate){

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        Log.d(TAG, "getDetailViewTime: "+rawJsonDate);
        String detailTimeFormat = "hh:mm a . dd MMM yy";
        SimpleDateFormat sdf = new SimpleDateFormat(detailTimeFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String detailDate = "";
        try {
            detailDate = sdf.format(sf.parse(rawJsonDate));
            Log.d(TAG, "getDetailViewTime: "+detailDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return detailDate;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /*public static TweetModel saveTweetToDb(Tweet tweet){
        Log.d(TAG, "saveTweetToDb: "+tweet.getText());
        User user = tweet.getUserDetails();
        UserModel userModel = new UserModel(user.getId(),
                user.getName(),
                user.getScreenName(),
                user.getProfileImageUrl()
        );
        userModel.save();

        *//*EntitiesModel entitiesModel = null;
        if(tweet.getEntities() != null) {
            List<Media> mediasList = tweet.getEntities().getMedias();
            if (mediasList != null) {
                for (Media media : mediasList) {
                    MediaModel mediaModel = new MediaModel(media.getId(), media.getMediaUrl());
                    mediaModel.save();
                    entitiesModel = new EntitiesModel(mediaModel);
                    entitiesModel.save();

                }
            }
        }*//*


        TweetModel tweetModel = new TweetModel(tweet.getId(),
                tweet.getCreated_at(),
                tweet.getText(),
                tweet.getRetweet_count(),
                tweet.getFavoriteCount(),
                tweet.isRetweeted(),
                tweet.isFavorite(),
                userModel);
        tweetModel.save();

        return tweetModel;
    }

    public static List<Tweet> getTweetsList(List<TweetModel> tweetModels){
        List<Tweet> tweetList = new ArrayList<>();
        for(TweetModel tweetModel : tweetModels){
            Tweet tweet = new Tweet();
            tweet.id = tweetModel.id;
            tweet.created_at = tweetModel.created_at;
            tweet.text = tweetModel.text;
            tweet.retweet_count = tweetModel.retweet_count;
            tweet.favoriteCount = tweetModel.favorite_count;
            tweet.isFavorite = tweetModel.favorited;
            tweet.isRetweeted = tweetModel.retweeted;

            tweet.userDetails.id = tweetModel.user.id;
            tweet.userDetails.name = tweetModel.user.name;
            tweet.userDetails.screenName = tweetModel.user.screen_name;
            tweet.userDetails.profileImageUrl = tweetModel.user.profile_image_url_https;

            tweetList.add(tweet);
        }
        return tweetList;
    }*/
}
