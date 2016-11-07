package squarerock.hoot.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.adapters.FollowAdapter;
import squarerock.hoot.models.UserModel;

/**
 * Created by pranavkonduru on 11/6/16.
 */

public class FriendsAndFollowingActivity extends AppCompatActivity {

    @BindView(R.id.rv_follow) RecyclerView rvFollow;
    @BindView(R.id.tv_follow_title) TextView tvToolbarTitle;

    private String usedFor;
    private String screenName;

    private FollowAdapter adapter;
    private TwitterClient twitterClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);

        usedFor = getIntent().getStringExtra("used_for");
        screenName = getIntent().getStringExtra("screen_name");

        tvToolbarTitle.setText(usedFor);

        twitterClient = Hoot.getRestClient();

        initRecyclerView();

        fetchFollowing(usedFor);
    }

    private void fetchFollowing(String usedFor) {
        if("followers".equalsIgnoreCase(usedFor)){
            twitterClient.getFollowers(screenName, -1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } else{
            twitterClient.getFriends(screenName, -1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }
    }

    private void initRecyclerView() {
        adapter = new FollowAdapter(new ArrayList<UserModel>(), usedFor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvFollow.setLayoutManager(layoutManager);
        rvFollow.setAdapter(adapter);
    }



}
