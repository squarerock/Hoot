package squarerock.hoot.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import squarerock.hoot.adapters.HootFragmentPagerAdapter;
import squarerock.hoot.fragments.TimelineFragment;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TweetListActivity extends AppCompatActivity {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabsStrip;

    private static final String TAG = "TweetListActivity";
    private HootFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_tweetlist);
        ButterKnife.bind(this);

        adapter = new HootFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TimelineFragment.getInstance(), "Tweets");

        viewPager.setAdapter(adapter);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }
}
