package squarerock.hoot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.TextHttpResponseHandler;

import org.parceler.Parcels;

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
import squarerock.hoot.adapters.MentionsAdapter;
import squarerock.hoot.adapters.TweetAdapter;
import squarerock.hoot.listeners.EndlessRecyclerViewScrollListener;
import squarerock.hoot.models.MentionsModel;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.models.TwitterTimeline;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 11/4/16.
 */

public class MentionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TweetAdapter.TweetClickCallback {

    @BindView(R.id.rv_mentions) RecyclerView rvMentions;
    @BindView(R.id.swipeMentions) SwipeRefreshLayout swipeRefreshLayout;

    private TwitterClient twitterClient;
    private MentionsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Snackbar snackbar;

    private static final String TAG = "MentionsFragment";

    public MentionsFragment() {}

    public static MentionsFragment getInstance(){
        return new MentionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentions, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
        twitterClient = Hoot.getRestClient();

        snackbar = Snackbar.make(rvMentions, "Network not available. Try again later", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK",null);

        return view;
    }

    private void initRecyclerView() {
        adapter = new MentionsAdapter(new ArrayList<TweetModel>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: "+page);
                fetchMentions(-1, MentionsModel.getSinceId(), MentionsModel.getMaxId(), false);
            }
        };

        rvMentions.setLayoutManager(layoutManager);
        rvMentions.addOnScrollListener(scrollListener);
        rvMentions.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        fetchMentions(0, -1, -1, false);
    }

    private void fetchMentions(int page, long sinceId, long maxId, final boolean isRefreshing) {
        if(Utility.isNetworkAvailable(getContext())) {
            Log.d(TAG, "fetchMentions: Network available. Fetching mentions");
            if(snackbar.isShown()) snackbar.dismiss();

            if(!swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(true);
            }

            twitterClient.getMentionTweets(page, sinceId, maxId, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: ",throwable.getCause());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    List<TweetModel> tweets = TwitterTimeline.parseJSON(responseString).getTweets();
                    for (int i = 0; i < tweets.size(); i++) {
                        tweets.get(i).user.save();
                        tweets.get(i).save();

                        // Save mentions
                        new MentionsModel(tweets.get(i).id).save();
                    }

                    if(isRefreshing){
                        adapter.clearAll();
                    }

                    adapter.update(tweets);

                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

        } else {
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            Log.d(TAG, "fetchTweets: Network unavailable. Fetching from database");
            adapter.clearAll();
            adapter.update(MentionsModel.getAllMentions());

            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onRefresh() {
        scrollListener.resetState();
        fetchMentions(0, -1, -1, true);
    }

    @Override
    public void onTweetClicked(TweetModel tweet) {
        Log.d(TAG, "onTweetClicked: "+tweet.text);
        Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
        intent.putExtra("Tweet", Parcels.wrap(TweetModel.class, tweet));
        startActivity(intent);
    }

    @Override
    public void onUserProfileClicked(String screenName) {
        Log.d(TAG, "onUserProfileClicked: "+screenName);
        Intent intent = new Intent(getActivity(), UserTimelineActivity.class);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }

    @Override
    public void onHashTagClicked(String hashtag) {
        Log.d(TAG, "onHashTagClicked: "+hashtag);
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("search_item", hashtag);
        startActivity(intent);
    }

    @Override
    public void onRetweet(int positionInAdapter, long tweetId, boolean isRetweeted) {
        Log.d(TAG, "onRetweet: ");
    }

    @Override
    public void onFavorite(int positionInAdapter, long tweetId, boolean isFavorited) {
        Log.d(TAG, "onFavorite: ");
    }
}
