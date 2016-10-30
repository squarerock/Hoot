package squarerock.hoot.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TweetDetailActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    @BindView(R.id.iv_detail_pic) ImageView ivDetailPic;
    @BindView(R.id.et_detail_reply) EditText etDetailReply;
    @BindView(R.id.tv_detail_handle) TextView tvDetailHandle;
    @BindView(R.id.tv_detail_name) TextView tvDetailName;
    @BindView(R.id.tv_detail_text) TextView tvDetailText;
    @BindView(R.id.tv_detail_time) TextView tvDetailTime;
    @BindView(R.id.iv_verified) ImageView ivVerified;
    @BindView(R.id.tv_detail_likes_number) TextView tvDetailLikesNumber;
    @BindView(R.id.tv_detail_retweets_number) TextView tvDetailRetweetsNumber;

    private static final String TAG = "TweetDetailActivity";
    private TweetModel tweet;
    private TwitterClient twitterClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweetdetail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tweet");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        Parcelable parcelable = getIntent().getParcelableExtra("Tweet");
        if(parcelable != null){
            Object parcelObject = Parcels.unwrap(parcelable);
            if(parcelObject instanceof TweetModel) {
                twitterClient = Hoot.getRestClient();

                Log.d(TAG, "onCreate: Got the extra");
                tweet = (TweetModel) parcelObject;

                Picasso.with(this)
                        .load(tweet.user.profile_image_url_https)
                        .into(ivDetailPic);

                tvDetailHandle.setText(String.format("@%s",tweet.user.screen_name));
                tvDetailName.setText(tweet.user.name);
                tvDetailText.setText(tweet.text);
                tvDetailTime.setText(Utility.getDetailViewTime(tweet.created_at));
                tvDetailLikesNumber.setText(String.valueOf(tweet.favorite_count));
                tvDetailRetweetsNumber.setText(String.valueOf(tweet.retweet_count));

                if(!tweet.user.verified) ivVerified.setVisibility(View.GONE);

                etDetailReply.setHint(String.format("Reply to %s", tweet.user.name));
                etDetailReply.setOnEditorActionListener(this);
                etDetailReply.setOnClickListener(this);
            } else {
                Log.d(TAG, "onCreate: parcel object instance of some other thing");
            }
        } else {
            Log.d(TAG, "onCreate: finishing activity");
            finish();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if(actionId == EditorInfo.IME_ACTION_SEND && twitterClient != null){
            Log.d(TAG, "onEditorAction: posting tweet");
            String replyTweet = textView.getText().toString();
            if(!replyTweet.contains(tweet.user.screen_name)){
                Snackbar.make(textView.getRootView(), "Cannot post tweet without mentioning user", Snackbar.LENGTH_LONG).show();
            }

            /*twitterClient.postTweet(textView.getText().toString(), tweet.id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });*/
            handled = true;
        }
        return handled;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_detail_reply:
                String replyTo = String.format(Locale.ENGLISH, "@%s ",tweet.user.screen_name);

                if(!etDetailReply.getText().toString().startsWith(replyTo)){
                    etDetailReply.append(String.format(Locale.ENGLISH, "%s",replyTo));
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }
}
