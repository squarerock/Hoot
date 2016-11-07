package squarerock.hoot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.adapters.HootFragmentPagerAdapter;
import squarerock.hoot.fragments.UserTweetsFragment;
import squarerock.hoot.models.UserModel;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 11/5/16.
 */

public class UserTimelineActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_banner_pic) ImageView ivBannerPic;
    @BindView(R.id.iv_user_pic) ImageView ivUserPic;
    @BindView(R.id.tv_user_name) TextView tvUserName;
    @BindView(R.id.tv_user_handle) TextView tvUserHandle;
    @BindView(R.id.tv_user_description) TextView tvUserDescription;
    @BindView(R.id.tv_followers) TextView tvFollowers;
    @BindView(R.id.tv_following) TextView tvFollowing;
    @BindView(R.id.toolbar_user) Toolbar toolbarUser;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.viewpager_user) ViewPager viewPager;
    @BindView(R.id.tabs_user) PagerSlidingTabStrip tabsStrip;

    private String screenName;
    private String name;
    private HootFragmentPagerAdapter adapter;
    private TwitterClient twitterClient;
    private UserModel user;

    private static final String TAG = "UserTimelineActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user1);
        ButterKnife.bind(this);

        screenName = getIntent().getStringExtra("screen_name");
        if(TextUtils.isEmpty(screenName)){
            Log.d(TAG, "onCreate: Screen name was empty. Finishing activity");
            finish();
        } else {
            user = UserModel.getUser(screenName);
            if(user == null || user.name == null){
                Log.d(TAG, "onCreate: screen name is null");
                twitterClient = Hoot.getRestClient();
                twitterClient.getUser(screenName, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, "onFailure: ", throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, "onSuccess: getting user");
                        user = UserModel.parseJSON(responseString);
                        user.save();
                        layoutActivity();
                    }
                });
            } else {
                layoutActivity();
            }
        }
    }

    private void layoutActivity() {
        populateFields();

        adapter = new HootFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(UserTweetsFragment.getInstance(screenName, "tweets"), "Tweets");
        adapter.addFragment(UserTweetsFragment.getInstance(screenName, "replies"), "Tweets & Replies");
        viewPager.setAdapter(adapter);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        setSupportActionBar(toolbarUser);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarUser.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_timeline, menu);
        return true;
    }

    private void populateFields() {
//        UserModel user = UserModel.getUser(screenName);
        Utility.loadImage(this, user.profile_banner_url, ivBannerPic);
        Utility.loadImage(this, user.profile_image_url_https, ivUserPic);

        name = user.name;

        tvUserName.setText(name);
        tvUserHandle.setText(String.format(Locale.ENGLISH, "@%s",user.screen_name));
        tvUserDescription.setText(user.description);
        tvFollowers.setText(String.valueOf(user.followers_count));
        tvFollowing.setText(String.valueOf(user.friends_count));

        tvFollowers.setOnClickListener(this);
        tvFollowing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_followers:
                startFriendsFollowingActivity(false);
                break;
            case R.id.tv_following:
                startFriendsFollowingActivity(true);
                break;
        }
    }

    private void startFriendsFollowingActivity(boolean isFriends) {
        Intent intent = new Intent(this, FriendsAndFollowingActivity.class);
        intent.putExtra("is_friends", isFriends);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }
}
