package squarerock.hoot.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.adapters.HootFragmentPagerAdapter;
import squarerock.hoot.fragments.MentionsFragment;
import squarerock.hoot.fragments.TimelineFragment;
import squarerock.hoot.models.ProfileModel;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TweetListActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabsStrip;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_toolbar_title) TextView tvToolbarTitle;
    @BindView(R.id.iv_user_profile_image) CircleImageView civUserProfile;

    private static final String TAG = "TweetListActivity";
    private HootFragmentPagerAdapter adapter;
    private TwitterClient twitterClient;
    private String userScreenName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_tweetlist);
        ButterKnife.bind(this);

        twitterClient = Hoot.getRestClient();

        adapter = new HootFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TimelineFragment.getInstance(), "Tweets");
        adapter.addFragment(MentionsFragment.getInstance(), "Mentions");
        viewPager.setAdapter(adapter);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("Home");
        tvToolbarTitle.setTextColor(Color.BLACK);
        toolbar.setBackgroundColor(Color.WHITE);

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setDisplayUseLogoEnabled(true);
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        ProfileModel userProfile = ProfileModel.getUserProfile();
        if (userProfile == null) {
            if(Utility.isNetworkAvailable(this)) {
                Log.d(TAG, "loadUserProfile: Network is available. Getting profile");
                twitterClient.getUserProfile(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            loadImage(ProfileModel.parseJSON(new String(responseBody, "UTF-8")));
                            Log.d(TAG, "onSuccess: ");
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "onSuccess: ", e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, "onFailure: ", error.getCause());
                    }
                });
            } else {
                Log.d(TAG, "loadUserProfile: Network not available");
            }
        } else {
            Log.d(TAG, "loadUserProfile: Profile found. Loading it");
            loadImage(userProfile);
        }
    }

    void loadImage(ProfileModel model){
        userScreenName = model.screen_name;
        Utility.loadImage(this, model.profile_image_url_https, civUserProfile, false);
        civUserProfile.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        final MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
//                fetchBooks(query);
                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();

                Intent intent = new Intent(TweetListActivity.this, SearchActivity.class);
                intent.putExtra("search_item", query);
                startActivity(intent);

                // Set activity title to search query
//                BookListActivity.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_user_profile_image:
                Log.d(TAG, "onClick: User profile clicked. Loading it");
                Intent intent = new Intent(this, UserTimelineActivity.class);
                intent.putExtra("screen_name", userScreenName);
                startActivity(intent);
                break;
        }
    }
}
