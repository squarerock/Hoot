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
import squarerock.hoot.models.UserModel;
import squarerock.hoot.utils.Utility;


/**
 * Created by pranavkonduru on 11/6/16.
 */

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder> {

    private List<UserModel> userModel;
    private Context context;
    private UserSearchCallback callback;
    private String userScreenName;
    private static final String TAG = "UserSearchAdapter";

    public interface UserSearchCallback{
        void onUserProfileClicked(String screenName);
    }

    public UserSearchAdapter(List<UserModel> userModel, UserSearchCallback callback) {
        this.userModel = userModel;
        this.callback = callback;
    }

    @Override
    public UserSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false);
        return new UserSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserSearchViewHolder holder, int position) {
        UserModel user = userModel.get(position);
        Utility.loadImage(context, user.profile_image_url_https, holder.ivUser);
        if(!user.verified) holder.ivVerified.setVisibility(View.INVISIBLE);
        holder.tvName.setText(user.name);

        userScreenName = user.screen_name;
        holder.tvHandle.setText(userScreenName);
        holder.tvDescription.setText(user.description);
    }

    @Override
    public int getItemCount() {
        return userModel.size();
    }

    public class UserSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_search_item_user) ImageView ivUser;
        @BindView(R.id.iv_search_user_verified) ImageView ivVerified;
        @BindView(R.id.tv_search_user_name) TextView tvName;
        @BindView(R.id.tv_search_user_handle) TextView tvHandle;
        @BindView(R.id.tv_search_user_desc) TextView tvDescription;

        public UserSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(callback != null){
                Log.d(TAG, "onClick: User clickedL "+userScreenName);
                callback.onUserProfileClicked(userScreenName);
            }

        }
    }

    public void update(List<UserModel> users){
        Log.d(TAG, "update: ");
        int currSize = getItemCount();
        userModel.addAll(currSize, users);
        notifyItemRangeInserted(currSize, users.size());
    }

    public void add(UserModel user){
        userModel.add(0, user);
        notifyItemInserted(0);
    }

    public void clearAll(){
        Log.d(TAG, "clearAll: ");
        int currSize = getItemCount();
        userModel.clear();
        notifyItemRangeRemoved(0, currSize);
    }
}
