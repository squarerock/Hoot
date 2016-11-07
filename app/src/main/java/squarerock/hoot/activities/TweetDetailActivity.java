package squarerock.hoot.activities;

import android.content.Intent;
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
import com.loopj.android.http.TextHttpResponseHandler;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.utils.PatternEditableBuilder;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TweetDetailActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener, PatternEditableBuilder.SpannableClickedListener {

    @BindView(R.id.iv_detail_pic) ImageView ivDetailPic;
    @BindView(R.id.et_detail_reply) EditText etDetailReply;
    @BindView(R.id.tv_detail_handle) TextView tvDetailHandle;
    @BindView(R.id.tv_detail_name) TextView tvDetailName;
    @BindView(R.id.tv_detail_text) TextView tvDetailText;
    @BindView(R.id.tv_detail_time) TextView tvDetailTime;
    @BindView(R.id.iv_verified) ImageView ivVerified;
    @BindView(R.id.iv_retweet) ImageView ivRetweets;
    @BindView(R.id.iv_favorites) ImageView ivFavorites;
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

                Utility.loadImage(this, tweet.user.profile_image_url_https, ivDetailPic);
                ivDetailPic.setOnClickListener(this);

                tvDetailHandle.setText(String.format("@%s",tweet.user.screen_name));
                tvDetailName.setText(tweet.user.name);
                tvDetailText.setText(tweet.text);

                Utility.buildSpannableText(tvDetailText, Utility.USER_HANDLE_PATTERN, this);
                Utility.buildSpannableText(tvDetailText, Utility.HASHTAG_PATTERN, this);

                tvDetailTime.setText(Utility.getDetailViewTime(tweet.created_at));
                tvDetailLikesNumber.setText(String.valueOf(tweet.favorite_count));
                tvDetailRetweetsNumber.setText(String.valueOf(tweet.retweet_count));

                if(!tweet.user.verified) ivVerified.setVisibility(View.GONE);

                if(tweet.retweeted) ivRetweets.setImageResource(R.drawable.ic_retweeted);
                if(tweet.favorited) ivFavorites.setImageResource(R.drawable.ic_favorited);

                etDetailReply.setHint(String.format("Reply to %s", tweet.user.name));
                etDetailReply.setOnEditorActionListener(this);
                etDetailReply.setOnClickListener(this);

                ivRetweets.setOnClickListener(this);
                ivFavorites.setOnClickListener(this);
            } else {
                Log.d(TAG, "onCreate: parcel object instance of some other thing");
            }
        } else {
            Log.d(TAG, "onCreate: finishing activity");
            finish();
        }
    }

    @Override
    public boolean onEditorAction(final TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if(actionId == EditorInfo.IME_ACTION_SEND && twitterClient != null){
            Log.d(TAG, "onEditorAction: posting tweet");
            String replyTweet = textView.getText().toString();
            if(!replyTweet.contains(tweet.user.screen_name)){
                Snackbar.make(textView.getRootView(), "Cannot post tweet without mentioning user", Snackbar.LENGTH_LONG).show();
            }

            twitterClient.postTweet(textView.getText().toString(), tweet.id, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: ", throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    TweetModel postedTweet = TweetModel.parseJSON(responseString);
                    postedTweet.user.save();
                    postedTweet.save();
                    Snackbar.make(textView.getRootView(), "Posted Tweet", Snackbar.LENGTH_LONG).show();
                }
            });
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
            case R.id.iv_detail_pic:
                Log.d(TAG, "onClick: starting user timeline");
                Intent intent = new Intent(this, UserTimelineActivity.class);
                intent.putExtra("screen_name", tweet.user.screen_name);
                startActivity(intent);
                break;
            case R.id.iv_retweet:
                Log.d(TAG, "onClick: retweet");
                twitterClient.retweet(tweet.id, tweet.retweeted, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, "onFailure: ",throwable.getCause());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, "onSuccess: retweet");
                        saveTweet(responseString);
                        if(tweet.retweeted) {
                            ivRetweets.setImageResource(R.drawable.ic_retweeted);
                        } else {
                            ivRetweets.setImageResource(R.drawable.ic_retweet);
                        }

                    }
                });
                break;
            case R.id.iv_favorites:
                Log.d(TAG, "onClick: favorite");
                twitterClient.favorite(tweet.id, tweet.favorited, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, "onFailure: ",throwable.getCause());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, "onSuccess: favorite");
                        saveTweet(responseString);
                        if(tweet.favorited) {
                            ivFavorites.setImageResource(R.drawable.ic_favorited);
                        } else {
                            ivFavorites.setImageResource(R.drawable.ic_favorite);
                        }
                    }
                });
                break;
        }
    }

    private void saveTweet(String responseString) {
        Log.d(TAG, "saveTweet: saving Tweet");
        tweet = TweetModel.parseJSON(responseString);
        Log.d(TAG, "saveTweet: retweet status: " +tweet.retweeted);
        Log.d(TAG, "saveTweet: favorite status: "+tweet.favorited);
        tweet.user.save();
        tweet.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }

    @Override
    public void onSpanClicked(String text) {
        Log.d(TAG, "onSpanClicked: "+text);
        if(text.contains("#")){
            Log.d(TAG, "onSpanClicked: hashtag");
        } else if (text.contains("@")){
            Log.d(TAG, "onSpanClicked: handle");
        }
    }
}
