package squarerock.hoot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.TextHttpResponseHandler;

import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.adapters.TweetAdapter;
import squarerock.hoot.adapters.UserSearchAdapter;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.models.UserModel;

/**
 * Created by pranavkonduru on 11/6/16.
 */

public class SearchActivity extends AppCompatActivity implements UserSearchAdapter.UserSearchCallback, TweetAdapter.TweetClickCallback {


    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.rv_search_users) RecyclerView rvSearchUsers;
    @BindView(R.id.rv_search_tweets) RecyclerView rvSearchTweets;
    @BindView(R.id.viewSeparator) View separator;

    private UserSearchAdapter userSearchAdapter;
    private TweetAdapter tweetAdapter;
    private String searchItem;
    private TwitterClient twitterClient;

    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        searchItem = getIntent().getStringExtra("search_item");

        twitterClient = Hoot.getRestClient();

        initToolbar();

        initRecyclerViews();

        try {
            searchItems();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "onCreate: Exception occurred fetching tweets. Finishing", e);
            e.printStackTrace();
            finish();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                finish();
            }
        });

        getSupportActionBar().setTitle(searchItem);
    }

    private void searchItems() throws UnsupportedEncodingException {
        Log.d(TAG, "searchItems: ");
        if(!searchItem.contains("#")){
            searchUsers();
            searchTweets();
        } else {
            rvSearchUsers.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
            searchTweets();
        }
    }

    private void searchTweets() throws UnsupportedEncodingException {
        Log.d(TAG, "searchTweets: ");
        twitterClient.searchTweets(searchItem, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: ",throwable );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: ");
                ArrayList<TweetModel> tweets = new ArrayList<>();

                JsonObject jsonObject = new JsonParser().parse(responseString).getAsJsonObject();
                JsonArray statusArray = jsonObject.getAsJsonArray("statuses");
                for(JsonElement element : statusArray){
                    TweetModel tweet = TweetModel.parseJSON(element.toString());
                    tweet.user.save();
                    tweet.save();
                    tweets.add(tweet);
                }
                tweetAdapter.update(tweets);
            }
        });
    }

    private void searchUsers() throws UnsupportedEncodingException {
        Log.d(TAG, "searchUsers: ");
        twitterClient.searchUsers(searchItem, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: ",throwable );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: searchUsers"+responseString);
                List<UserModel> users = new ArrayList<>();

                JsonArray jsonUserArray = new JsonParser().parse(responseString).getAsJsonArray();
                for(JsonElement userElement : jsonUserArray){
                    Log.d(TAG, "onSuccess: userArray: "+userElement);
                    UserModel user = UserModel.parseJSON(userElement.toString());
                    user.save();
                    users.add(user);
                }
                userSearchAdapter.update(users);
            }
        });
    }

    private void initRecyclerViews() {
        initUserRecyclerView();
        initTweetRecyclerView();
    }

    private void initTweetRecyclerView() {
        userSearchAdapter = new UserSearchAdapter(new ArrayList<UserModel>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSearchUsers.setHasFixedSize(true);
        rvSearchUsers.setAdapter(userSearchAdapter);
        rvSearchUsers.setLayoutManager(layoutManager);
    }

    private void initUserRecyclerView() {
        tweetAdapter = new TweetAdapter(new ArrayList<TweetModel>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSearchTweets.setAdapter(tweetAdapter);
        rvSearchTweets.setLayoutManager(layoutManager);
    }

    @Override
    public void onTweetClicked(TweetModel tweet) {
        Log.d(TAG, "onTweetClicked: "+tweet.text);
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra("Tweet", Parcels.wrap(TweetModel.class, tweet));
        startActivity(intent);
    }

    @Override
    public void onUserProfileClicked(String screenName) {
        Log.d(TAG, "onUserProfileClicked: "+screenName);
        Intent intent = new Intent(this, UserTimelineActivity.class);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }

    @Override
    public void onHashTagClicked(String hashtag) {
        Log.d(TAG, "onHashTagClicked: "+hashtag);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("search_item", hashtag);
        startActivity(intent);
    }

    @Override
    public void onRetweet(final int positionInAdapter, long tweetId, boolean isRetweeted) {
        twitterClient.retweet(tweetId, isRetweeted, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: ",throwable );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: ");
                TweetModel updatedTweet = TweetModel.parseJSON(responseString);
                updatedTweet.user.save();
                updatedTweet.save();

                tweetAdapter.updateItem(positionInAdapter, updatedTweet);
            }
        });
    }

    @Override
    public void onFavorite(final int positionInAdapter, long tweetId, boolean isFavorited) {
        twitterClient.favorite(tweetId, isFavorited, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "onFailure: ",throwable );
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: ");
                TweetModel updatedTweet = TweetModel.parseJSON(responseString);
                updatedTweet.user.save();
                updatedTweet.save();

                tweetAdapter.updateItem(positionInAdapter, updatedTweet);
            }
        });
    }

}
