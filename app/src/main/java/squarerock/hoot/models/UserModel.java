package squarerock.hoot.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import squarerock.hoot.databases.TweetsDb;

/**
 * Created by pranavkonduru on 10/28/16.
 */

@Table(database = TweetsDb.class)
@Parcel(analyze={UserModel.class})
public class UserModel extends BaseModel {

    @Column(name = "id")
    @PrimaryKey
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "screen_name")
    public String screen_name;

    @Column(name = "profile_image_url_https")
    public String profile_image_url_https;

    @Column(name = "verified")
    public boolean verified;

    @Column(name = "description")
    public String description;

    @Column(name = "followers_count")
    public long followers_count;

    @Column(name = "friends_count")
    public long friends_count;

    @Column(name = "favourites_count")
    public long favourites_count;

    @Column(name = "statuses_count")
    public long statuses_count;

    @Column(name = "profile_banner_url")
    public String profile_banner_url;

    @Column(name = "following")
    public boolean following;
    
    UserModel(){}

    public UserModel(long remoteId, String name, String screenName, String profileImageUrl, boolean verified) {
        this.id = remoteId;
        this.name = name;
        this.screen_name = screenName;
        this.profile_image_url_https = profileImageUrl;
        this.verified = verified;
    }

    public static UserModel getUser(String screenName){
        return new Select().from(UserModel.class)
                .where(UserModel_Table.screen_name.eq(screenName))
                .querySingle();
    }

    public static UserModel parseJSON(String jsonString){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonString, UserModel.class);
    }

    public static UserModel fromJSON(JSONObject json) {
        UserModel user = new UserModel();
        try {
            user.name = json.getString("name");
            user.id = json.getLong("id");
            user.screen_name = json.getString("screen_name");
            user.profile_image_url_https = json.getString("profile_image_url");
            user.verified = json.getBoolean("verified");
            user.description = json.getString("description");
            user.followers_count = json.getInt("followers_count");
            user.friends_count = json.getInt("friends_count");
            user.favourites_count = json.getInt("favourites_count");
            user.profile_banner_url = json.getString("profile_banner_url");
            user.following = json.getBoolean("following");

            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

}

