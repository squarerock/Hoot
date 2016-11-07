package squarerock.hoot;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "p8MMfKwZnwfqakhTcxNGhGgR7";       // Change this
	public static final String REST_CONSUMER_SECRET = "JhmisB1sB5xuADtMZ7QsBYedWOwwqbvbMpm5RIBJP4ofw98cWx"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://hoot"; // Change this (here and in manifest)

	private static final String TAG = "TwitterClient";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(int page, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
        if(sinceId != -1){
            params.put("since_id", sinceId);
        }
        if(maxId != -1){
            params.put("max_id", maxId);
        }
        if(page != -1) {
            params.put("page", String.valueOf(page));
        }
		client.get(apiUrl, params, handler);
	}

    public void postTweet(String tweet, long replyId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("/statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        if(replyId != -1){
            params.put("in_reply_to_status_id", replyId);
        }

        client.post(url,params,handler);
    }

    public void getMentionTweets(int page, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if(sinceId != -1){
            params.put("since_id", String.valueOf(sinceId));
        }
        if(maxId != -1){
            params.put("max_id", String.valueOf(maxId));
        }
        params.put("page", String.valueOf(page));
        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, long since_id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if(since_id != -1){
            params.put("since_id", since_id);
        }
        params.put("screen_name", screenName);

        // Get everything in one shot. Filter them out in UI
        params.put("exclude_replies", false);
        params.put("include_rts", true);
        client.get(apiUrl, params, handler);
    }

    public void getUserProfile(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/verify_credentials.json");
        client.get(url,handler);
    }

    public void getFollowers(String screenName, long cursor, AsyncHttpResponseHandler handler){
        String url = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        if(cursor != -1){
            params.put("cursor", cursor);
        }
        client.get(url, params, handler);
    }

    public void getFriends(String screenName, long cursor, AsyncHttpResponseHandler handler){
        String url = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        if(cursor != -1){
            params.put("cursor", cursor);
        }
        client.get(url, params, handler);
    }


    public void retweet(long retweetId, boolean retweeted, AsyncHttpResponseHandler handler) {
        String apiUrl;
        if(!retweeted)
            apiUrl= getApiUrl("statuses/retweet/") + retweetId + ".json";
        else{
            apiUrl = getApiUrl("statuses/unretweet/") + retweetId + ".json";
        }
        client.post(apiUrl, handler);
    }

    public void favorite(long favoriteId, boolean favorited, AsyncHttpResponseHandler handler){
        String apiUrl;
        if(!favorited){
            apiUrl = getApiUrl("favorites/create.json");
        } else{
            apiUrl = getApiUrl("favorites/destroy.json");
        }

        RequestParams params = new RequestParams();
        params.put("id", favoriteId);
        client.post(apiUrl, params, handler);
    }

    public void getUser(String screenName,AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        client.get(apiUrl,params,handler);
    }

    public void searchTweets(String query, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        params.put("q", query);
        client.get(apiUrl, params, handler);
    }

    public void searchUsers(String query, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        String apiUrl = getApiUrl("users/search.json");
        RequestParams params = new RequestParams();
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        params.put("q", query);
        params.put("count", 5);
        client.get(apiUrl, params, handler);
    }
}