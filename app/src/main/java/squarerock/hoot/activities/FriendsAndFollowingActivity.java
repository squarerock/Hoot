package squarerock.hoot.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.adapters.FollowAdapter;
import squarerock.hoot.listeners.EndlessRecyclerViewScrollListener;
import squarerock.hoot.models.UserModel;
import squarerock.hoot.models.Users;

/**
 * Created by pranavkonduru on 11/6/16.
 */

public class FriendsAndFollowingActivity extends AppCompatActivity {

    @BindView(R.id.rv_follow) RecyclerView rvFollow;
    @BindView(R.id.tv_follow_title) TextView tvToolbarTitle;
    @BindView(R.id.toolbar_follow) Toolbar toolbar;

    private String usedFor;
    private String screenName;
    private EndlessRecyclerViewScrollListener scrollListener;

    private FollowAdapter adapter;
    private TwitterClient twitterClient;
    boolean isFriends;
    long nextCursor;

    private static final String TAG = "FriendsAndFollowingActi";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);

        isFriends = getIntent().getBooleanExtra("is_friends", false);
        screenName = getIntent().getStringExtra("screen_name");

        usedFor = isFriends ? "Friends" : "Followers";

        initToolbar();

        twitterClient = Hoot.getRestClient();

        initRecyclerView();

        fetchFollowing(isFriends, -1);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                finish();
            }
        });

        tvToolbarTitle.setText(usedFor);
    }

    private void fetchFollowing(boolean isFriends, long cursor) {
        if(!isFriends){
            twitterClient.getFollowers(screenName, cursor, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: ", throwable.getCause());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, "onSuccess: response for followers: "+responseString);
                    Users users = Users.parseJSON(responseString);
                    List<UserModel> userModels = users.getUsers();
                    nextCursor = users.getNextCursor();

                    /*for(UserModel userModel : userModels){
                        userModel.save();
                    }*/

                    adapter.update(userModels);

                }
            });
        } else{
            twitterClient.getFriends(screenName, cursor, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: ", throwable.getCause());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, "onSuccess: response for friends: "+responseString);
                    Users users = Users.parseJSON(responseString);
                    List<UserModel> userModels = users.getUsers();
                    nextCursor = users.getNextCursor();

                    /*for(UserModel userModel : userModels){
                        userModel.save();
                    }*/

                    adapter.update(userModels);
                }
            });
        }
    }

    private void initRecyclerView() {
        adapter = new FollowAdapter(new ArrayList<UserModel>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchFollowing(isFriends, nextCursor);
            }
        };
        rvFollow.setLayoutManager(layoutManager);
        rvFollow.addOnScrollListener(scrollListener);
        rvFollow.setAdapter(adapter);
    }

}
