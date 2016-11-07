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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.utils.PatternEditableBuilder;
import squarerock.hoot.utils.Utility;

import static com.raizlabs.android.dbflow.config.FlowLog.TAG;

/**
 * Created by pranavkonduru on 11/4/16.
 */

public class MentionsAdapter extends RecyclerView.Adapter<MentionsAdapter.MentionsViewHolder> implements PatternEditableBuilder.SpannableClickedListener {

    private List<TweetModel> mentions;
    private Context context;
    private TweetAdapter.TweetClickCallback callback;

    public MentionsAdapter(List<TweetModel> mentions, TweetAdapter.TweetClickCallback callback) {
        this.mentions = mentions;
        this.callback = callback;
    }

    @Override
    public MentionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mention, parent, false);
        return new MentionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MentionsViewHolder holder, int position) {
        TweetModel tweet = mentions.get(position);
        holder.tvName.setText(tweet.user.name);
        holder.tvHandle.setText(String.format(Locale.ENGLISH, "@%s",tweet.user.screen_name));

        holder.tvTweet.setText(tweet.text);
        Utility.buildSpannableText(holder.tvTweet, Utility.HASHTAG_PATTERN, this);
        Utility.buildSpannableText(holder.tvTweet, Utility.USER_HANDLE_PATTERN, this);

        holder.tvTime.setText(Utility.getRelativeTimeAgo(tweet.created_at));

        Utility.loadImage(context, tweet.user.profile_image_url_https, holder.ivProfilePic);
    }

    @Override
    public int getItemCount() {
        return mentions.size();
    }

    @Override
    public void onSpanClicked(String text) {
        if(callback != null){
            Log.d(TAG, "onSpanClicked: "+text);
            if(text.contains("#")){
                Log.d(TAG, "onSpanClicked: hashtag");
                callback.onHashTagClicked(text);
            } else if (text.contains("@")) {
                Log.d(TAG, "onSpanClicked: handle");
                callback.onUserProfileClicked(text.substring(text.indexOf("@")));
            }
        }
    }

    public class MentionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_reply) ImageView ivReply;
        @BindView(R.id.iv_star) ImageView ivStar;
        @BindView(R.id.iv_retweet) ImageView ivRetweet;
        @BindView(R.id.iv_profile_pic) ImageView ivProfilePic;

        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_handle) TextView tvHandle;
        @BindView(R.id.tv_tweet) TextView tvTweet;
        @BindView(R.id.tv_time) TextView tvTime;

        public MentionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            ivProfilePic.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            TweetModel tweetClicked = mentions.get(0);

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                tweetClicked = mentions.get(position);
            }

            switch (view.getId()){
                case R.id.iv_profile_pic:
                    if (callback != null) {
                        Log.d(TAG, "onClick: user profile clicked");
                        callback.onUserProfileClicked(tweetClicked.user.screen_name);
                    }
                    break;
                default:
                    if (callback != null) {
                        Log.d(TAG, "onClick: Tweet Clicked at position: "+position);
                        callback.onTweetClicked(tweetClicked);
                    }
            }
        }
    }

    public void update(List<TweetModel> newTweets){
        Log.d(TAG, "update: ");
        int currSize = getItemCount();
        mentions.addAll(currSize, newTweets);
        notifyItemRangeInserted(currSize, newTweets.size());
    }

    public void add(TweetModel newTweet){
        mentions.add(0, newTweet);
        notifyItemInserted(0);
    }

    public void clearAll(){
        Log.d(TAG, "clearAll: ");
        int currSize = getItemCount();
        mentions.clear();
        notifyItemRangeRemoved(0, currSize);
    }
}
