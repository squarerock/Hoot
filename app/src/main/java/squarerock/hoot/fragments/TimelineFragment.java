package squarerock.hoot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.activities.TweetDetailActivity;
import squarerock.hoot.adapters.TweetAdapter;
import squarerock.hoot.listeners.EndlessRecyclerViewScrollListener;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.models.TwitterTimeline;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TimelineFragment extends Fragment implements TweetAdapter.TweetClickCallback, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvTimeline) RecyclerView rvTimeline;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabTweet) FloatingActionButton fabTweet;

    private TweetAdapter adapter;
    private ArrayList<TweetModel> tweets = new ArrayList<>();
    private TwitterClient twitterClient;
    private EndlessRecyclerViewScrollListener scrollListener;
    private static final String TAG = "TimelineFragment";
    private final int REQUEST_CODE = 1984;

    public TimelineFragment() {
    }

    public static TimelineFragment getInstance(){
        return new TimelineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
        twitterClient = Hoot.getRestClient();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        fetchTweets(0, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    void initRecyclerView(){
        adapter = new TweetAdapter(tweets, this);
        rvTimeline.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTimeline.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: loading for page: " + page);
                fetchTweets(page, false);
            }
        };
        rvTimeline.addOnScrollListener(scrollListener);
    }

    void addPostedTweet(TweetModel addedTweet){
        adapter.add(addedTweet);
        rvTimeline.scrollToPosition(0);
    }

    @Override
    public void onTweetClicked(TweetModel tweet) {
        Log.d(TAG, "onTweetClicked: "+tweet.user.name);
        Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
        intent.putExtra("Tweet", Parcels.wrap(TweetModel.class, tweet));
        getActivity().startActivity(intent);
    }

    private void fetchTweets(int page, final boolean isRefreshing) {
        if (Utility.isNetworkAvailable(getContext())) {
            Log.d(TAG, "fetchTweets: Network available. Fetching tweets");
            if(!swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(true);
            }

            twitterClient.getHomeTimeline(page, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        List<TweetModel> tweets = TwitterTimeline.parseJSON(new String(responseBody, "UTF-8")).getTweets();
                        for (int i = 0; i < tweets.size(); i++) {
                            tweets.get(i).user.save();
                            tweets.get(i).save();
                        }

                        if(isRefreshing){
                            adapter.clearAll();
                        }

                        adapter.update(tweets);

                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "onSuccess: ", e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "onFailure: ", error);
                }
            });
        } else {
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            Log.d(TAG, "fetchTweets: Network unavailable. Fetching from database");
            adapter.clearAll();
            adapter.update(TweetModel.getTweets());
            Snackbar snackbar = Snackbar.make(rvTimeline, "Network not available. Try again later", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK",null)
            .show();
        }
    }

    @Override
    public void onRefresh() {
        scrollListener.resetState();
        fetchTweets(0, true);
    }

    @OnClick(R.id.fabTweet)
    public void fabClicked(View view){
        Log.d(TAG, "fabClicked: ");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment tweetComposeFragment = TweetComposeFragment.getInstance();
        tweetComposeFragment.setTargetFragment(this, REQUEST_CODE);
        ((TweetComposeFragment)tweetComposeFragment).show(fm, "compose_tweet");
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE){
            switch (resultCode){
                case 0:
                    Log.d(TAG, "onActivityResult: adding tweet to adapter");
                    TweetModel addedTweet = Parcels.unwrap(data.getParcelableExtra("addedTweet"));
                    addPostedTweet(addedTweet);
                    break;
                default:
                    Log.e(TAG, "onActivityResult: error occurred");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
