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

import static com.raizlabs.android.dbflow.config.FlowLog.TAG;

/**
 * Created by pranavkonduru on 11/6/16.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder>{

    private List<UserModel> userModels;
    private Context context;

    public FollowAdapter(List<UserModel> userModels) {
        this.userModels = userModels;
    }

    @Override
    public FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowViewHolder holder, int position) {
        UserModel user = userModels.get(position);
        holder.tvFollowDescription.setText(user.description);
        holder.tvFollowHandle.setText(user.screen_name);
        holder.tvFollowName.setText(user.name);
        Utility.loadImage(context, user.profile_image_url_https, holder.ivFollow);
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_follow_description) TextView tvFollowDescription;
        @BindView(R.id.tv_follow_handle) TextView tvFollowHandle;
        @BindView(R.id.tv_follow_name) TextView tvFollowName;

        @BindView(R.id.iv_follow) ImageView ivFollow;

        public FollowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void update(List<UserModel> users){
        Log.d(TAG, "update: ");
        int currSize = getItemCount();
        userModels.addAll(currSize, users);
        notifyItemRangeInserted(currSize, users.size());
    }

    public void add(UserModel user){
        userModels.add(0, user);
        notifyItemInserted(0);
    }

    public void clearAll(){
        Log.d(TAG, "clearAll: ");
        int currSize = getItemCount();
        userModels.clear();
        notifyItemRangeRemoved(0, currSize);
    }
}
