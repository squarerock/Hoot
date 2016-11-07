package squarerock.hoot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
import squarerock.hoot.activities.SearchActivity;
import squarerock.hoot.activities.TweetDetailActivity;
import squarerock.hoot.activities.UserTimelineActivity;
import squarerock.hoot.adapters.TweetAdapter;
import squarerock.hoot.listeners.EndlessRecyclerViewScrollListener;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.models.TwitterTimeline;
import squarerock.hoot.utils.Utility;


/**
 * Created by pranavkonduru on 11/4/16.
 */

public class UserTweetsFragment extends Fragment implements TweetAdapter.TweetClickCallback {

    @BindView(R.id.rv_user_tweets) RecyclerView rvUserTweets;

    private Snackbar snackbar;
    private TwitterClient twitterClient;
    private TweetAdapter adapter;
    private String screenName, screenType;
    private EndlessRecyclerViewScrollListener scrollListener;

    private static final String TAG = "UserTweetsFragment";

    public UserTweetsFragment() {}

    public static UserTweetsFragment getInstance(String screenName, String screenType){
        Bundle args = new Bundle();
        args.putString(Utility.EXTRA_USER_SCREEN_NAME, screenName);
        args.putString(Utility.EXTRA_USER_SCREEN_TYPE, screenType);
        UserTweetsFragment fragment = new UserTweetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        screenName = getArguments().getString(Utility.EXTRA_USER_SCREEN_NAME, "");
        screenType = getArguments().getString(Utility.EXTRA_USER_SCREEN_TYPE, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_user_tweets, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        twitterClient = Hoot.getRestClient();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");

        snackbar = Snackbar.make(rvUserTweets, "Network not available. Try again later", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK",null);

        fetchUserTweets(-1, false);
    }

    private void fetchUserTweets(long since_id, boolean refreshing) {
        Log.d(TAG, "fetchUserTweets: "+refreshing);

        if(Utility.isNetworkAvailable(getContext())){
            Log.d(TAG, "fetchUserTweets: Network available");
            if(snackbar.isShown()) snackbar.dismiss();

            twitterClient.getUserTimeline(screenName, since_id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "onSuccess: ");
                    try {
                        List<TweetModel> tweets = TwitterTimeline.parseJSON(new String(responseBody, "UTF-8")).getTweets();
                        for(int i = 0; i < tweets.size(); i++){
                            tweets.get(i).user.save();
                            tweets.get(i).save();
                        }
                        Log.d(TAG, "onSuccess: updating adapter");
                        adapter.update(tweets);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "onSuccess: ",e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "onFailure: ", error.getCause());
                    loadOfflineTweets();
                }
            });
        } else {
            Log.d(TAG, "fetchUserTweets: Network unavailable");
            loadOfflineTweets();
            snackbar.show();
        }
    }

    private void loadOfflineTweets(){
        adapter.clearAll();
        List<TweetModel> offlineTweets = TweetModel.getTweetsByScreenName(screenName);
        adapter.update(offlineTweets);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");
        adapter = new TweetAdapter(new ArrayList<TweetModel>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: loading more tweets");
                fetchUserTweets(TweetModel.getSinceId(), false);
            }
        };
        rvUserTweets.setAdapter(adapter);
        rvUserTweets.setLayoutManager(layoutManager);
        rvUserTweets.addOnScrollListener(scrollListener);
    }

    @Override
    public void onTweetClicked(TweetModel tweet) {
        Log.d(TAG, "onTweetClicked: "+tweet.user.screen_name);
        Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
        intent.putExtra("Tweet", Parcels.wrap(TweetModel.class, tweet));
        getActivity().startActivity(intent);
    }

    @Override
    public void onUserProfileClicked(String screenName) {
        Log.d(TAG, "onUserProfileClicked: opening user profile for ScreenName: "+screenName);
        Intent intent = new Intent(getContext(), UserTimelineActivity.class);
        intent.putExtra("screen_name", screenName);
        getActivity().startActivity(intent);
    }

    @Override
    public void onHashTagClicked(String hashtag) {
        Log.d(TAG, "onHashTagClicked: "+hashtag);
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("search_item", hashtag);
        getActivity().startActivity(intent);
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

                adapter.updateItem(positionInAdapter, updatedTweet);
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

                adapter.updateItem(positionInAdapter, updatedTweet);
            }
        });
    }
}
