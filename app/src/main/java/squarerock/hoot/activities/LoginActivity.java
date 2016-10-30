package squarerock.hoot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.models.TwitterTimeline;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	private TwitterClient twitterClient;
	private static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(this, TweetListActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		twitterClient = new TwitterClient(this);
//		fetchTweets();
	}

	private void fetchTweets(){
		twitterClient.getHomeTimeline(0, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Type collectionType = new TypeToken<List<TweetModel>>() {}.getType();
				try {
					Log.d(TAG, "onSuccess: response is: \n\n\n"+new String(responseBody, "UTF-8"));
					List<TweetModel> tweets = TwitterTimeline.parseJSON(new String(responseBody, "UTF-8")).getTweets();

//					List<TweetModel> tweets = (List<TweetModel>) gson.fromJson(new String(responseBody, "UTF-8"), collectionType);
					Log.d(TAG, "onSuccess: ");
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "onSuccess: ",e );
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Log.e(TAG, "onFailure: ",error);
			}
		});
	}
	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
