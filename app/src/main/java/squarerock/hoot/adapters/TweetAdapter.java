package squarerock.hoot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.utils.PatternEditableBuilder;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/27/16.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> implements PatternEditableBuilder.SpannableClickedListener {

    private List<TweetModel> tweets;
    private TweetClickCallback tweetCallback;
    private static final String TAG = "TweetAdapter";
    private Context context;

    @Override
    public void onSpanClicked(String text) {
        if(tweetCallback != null){
            Log.d(TAG, "onSpanClicked: "+text);
            if(text.contains("#")){
                Log.d(TAG, "onSpanClicked: hashtag");
                tweetCallback.onHashTagClicked(text);
            } else if (text.contains("@")) {
                Log.d(TAG, "onSpanClicked: handle");
                tweetCallback.onUserProfileClicked(text.substring(text.indexOf("@")));
            }
        }
    }

    public interface TweetClickCallback{
        void onTweetClicked(TweetModel tweet);
        void onUserProfileClicked(String screenName);
        void onHashTagClicked(String hashtag);
        void onRetweet(int positionInAdapter, long tweetId, boolean isRetweeted);
        void onFavorite(int positionInAdapter, long tweetId, boolean isFavorited);
    }

    public TweetAdapter(List<TweetModel> tweets, TweetClickCallback callback) {
        this.tweets = tweets;
        this.tweetCallback = callback;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_item_rl, parent, false);
        this.context = parent.getContext();
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        TweetModel tweet = tweets.get(position);

        holder.tvTweetText.setText(tweet.text);

        Utility.buildSpannableText(holder.tvTweetText, Utility.USER_HANDLE_PATTERN, this);
        Utility.buildSpannableText(holder.tvTweetText, Utility.HASHTAG_PATTERN, this);

        holder.tvTweetTime.setText(Utility.getRelativeTimeAgo(tweet.created_at));
        holder.tvUserName.setText(tweet.user.name);
        holder.tvHandle.setText(String.format("@%s", tweet.user.screen_name));

        Utility.loadImage(context, tweet.user.profile_image_url_https, holder.ivProfilePic);

        holder.tvNumberOfReTweets.setText(String.valueOf(tweet.retweet_count));
        holder.tvNumberOfFavorites.setText(String.valueOf(tweet.favorite_count));

        if(tweet.favorited) holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
        if(tweet.retweeted) holder.ivRetweet.setImageResource(R.drawable.ic_retweeted);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_profile_pic) ImageView ivProfilePic;
        @BindView(R.id.tv_user_name) TextView tvUserName;
        @BindView(R.id.tv_handle) TextView tvHandle;
        @BindView(R.id.tv_tweet_time) TextView tvTweetTime;
        @BindView(R.id.tv_tweet_text) TextView tvTweetText;

        @BindView(R.id.iv_reply) ImageView ivReply;
        @BindView(R.id.iv_retweet) ImageView ivRetweet;
        @BindView(R.id.iv_favorites) ImageView ivFavorite;
        @BindView(R.id.iv_message) ImageView ivMessage;


        @BindView(R.id.tv_number_of_retweets) TextView tvNumberOfReTweets;
        @BindView(R.id.tv_number_of_favorites) TextView tvNumberOfFavorites;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            ivReply.setOnClickListener(this);
            ivRetweet.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
            ivMessage.setOnClickListener(this);
            ivProfilePic.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TweetModel tweetClicked = tweets.get(0);

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                tweetClicked = tweets.get(position);
            }

            switch (view.getId()){
                case R.id.iv_reply:
                    Log.d(TAG, "onClick: reply");
                    break;
                case R.id.iv_retweet:
                    Log.d(TAG, "onClick: retweet");
                    if(tweetCallback != null) {
                        tweetCallback.onRetweet(position, tweetClicked.id, tweetClicked.retweeted);
                    }
                    break;
                case R.id.iv_favorites:
                    Log.d(TAG, "onClick: favorite");
                    if(tweetCallback != null) {
                        tweetCallback.onFavorite(position, tweetClicked.id, tweetClicked.favorited);
                    }
                    break;
                case R.id.iv_message:
                    Log.d(TAG, "onClick: message");
                    break;
                case R.id.iv_profile_pic:
                    if(tweetCallback != null){
                        Log.d(TAG, "onClick: Loading profile upon tweet profile clicked");
                        tweetCallback.onUserProfileClicked(tweetClicked.user.screen_name);
                    }
                    break;
                default:
                    if (tweetCallback != null) {
                        Log.d(TAG, "onClick: Tweet Clicked at position: "+position);
                        tweetCallback.onTweetClicked(tweetClicked);
                    }
            }
        }
    }

    public void update(List<TweetModel> newTweets){
        Log.d(TAG, "update: ");
        int currSize = getItemCount();
        tweets.addAll(currSize, newTweets);
        notifyItemRangeInserted(currSize, newTweets.size());
    }

    public void add(TweetModel newTweet){
        tweets.add(0, newTweet);
        notifyItemInserted(0);
    }

    public void updateItem(int position, TweetModel tweetModel){
        tweets.set(position, tweetModel);
        notifyDataSetChanged();
    }

    public void clearAll(){
        Log.d(TAG, "clearAll: ");
        int currSize = getItemCount();
        tweets.clear();
        notifyItemRangeRemoved(0, currSize);
    }
}
