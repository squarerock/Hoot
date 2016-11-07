package squarerock.hoot.utils;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class Utility {

    private static final String TAG = "Utility";
    public final static String EXTRA_USER_SCREEN_NAME = "screen_name";
    public final static String EXTRA_USER_SCREEN_TYPE = "screen_type";
    public static final String USER_HANDLE_PATTERN = "\\@(\\w+)";
    public static final String HASHTAG_PATTERN = "\\#(\\w+)";


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
        Log.d(TAG, "isNetworkAvailable: ");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void buildSpannableText(TextView textView, String pattern, PatternEditableBuilder.SpannableClickedListener listener){
        new PatternEditableBuilder()
                .addPattern(Pattern.compile(pattern), Color.BLUE, listener)
                .into(textView);
    }
    public static void loadImage(Context context, String imagePath, ImageView imageView){
        loadImage(context, imagePath, imageView, true);
    }

    public static void loadImage(Context context, String imagePath, ImageView imageView, boolean transform){
        if(transform) {
            Picasso.with(context)
                    .load(imagePath)
                    .transform(new RoundedCornersTransformation(4, 2))
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(imagePath)
                    .into(imageView);
        }
    }

}
