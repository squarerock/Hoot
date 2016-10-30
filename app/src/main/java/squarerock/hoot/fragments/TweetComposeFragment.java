package squarerock.hoot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import squarerock.hoot.Hoot;
import squarerock.hoot.TwitterClient;
import squarerock.hoot.models.TweetModel;
import squarerock.hoot.utils.Utility;

/**
 * Created by pranavkonduru on 10/28/16.
 */

public class TweetComposeFragment extends DialogFragment implements View.OnClickListener, TextWatcher {

    @BindView(R.id.iv_cancel) ImageView ivCancel;
    @BindView(R.id.ib_profile) ImageButton ibProfile;
    @BindView(R.id.tv_tweet_button) TextView tvTweetButton;
    @BindView(R.id.tv_tweet_length) TextView tvTweetLength;
    @BindView(R.id.et_tweet) EditText etTweet;

    private static final String TAG = "TweetComposeFragment";
    private static final int TWEET_CHAR_LIMIT = 140;
    private TwitterClient twitterClient;

    public TweetComposeFragment() {
    }

    public static TweetComposeFragment getInstance(){
        return new TweetComposeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        twitterClient = Hoot.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweetcompose, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @OnClick({R.id.iv_cancel, R.id.tv_tweet_button})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_cancel:
                Log.d(TAG, "onClick: dismissing dialog");
                dismiss();
                break;
            case R.id.tv_tweet_button:
                Log.d(TAG, "onClick: processing tweet");
                processTweet();
                break;
            default:
                Log.d(TAG, "onClick: ");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        int tweetCnt = TWEET_CHAR_LIMIT - (start + count);
        if(tweetCnt < 0){
            tvTweetLength.setTextColor(getResources().getColor(R.color.red));
            tvTweetButton.setEnabled(false);
        }else{
            tvTweetLength.setTextColor(getResources().getColor(R.color.black));
            tvTweetButton.setEnabled(true);
        }
        tvTweetLength.setText(String.format(Locale.ENGLISH, "%d", tweetCnt));
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    void initViews(){
        ivCancel.setOnClickListener(this);
        etTweet.addTextChangedListener(this);
        tvTweetButton.setOnClickListener(this);
    }

    void processTweet(){
        if(Utility.isNetworkAvailable(getContext())){
            twitterClient.postTweet(etTweet.getText().toString(), -1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "onSuccess: posted Tweet");
                    try {
                        Log.d(TAG, "onSuccess: sending result to tweet compose fragment");
                        TweetModel postedTweet = TweetModel.parseJSON(new String(responseBody, "UTF-8"));
                        Intent intent = new Intent();
                        intent.putExtra("addedTweet", Parcels.wrap(TweetModel.class, postedTweet));
                        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "onFailure: ", error);
                    if (getView() != null) {
                        Snackbar.make(getView(), "Error occurred posting tweet", Snackbar.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "onFailure: getView is null");
                    }
                }
            });
        } else {
            if (getView() != null) {
                Snackbar.make(getView(), "No Internet available", Snackbar.LENGTH_LONG).show();
            } else  {
                Log.d(TAG, "processTweet: getView is null");
            }
        }

    }
}
