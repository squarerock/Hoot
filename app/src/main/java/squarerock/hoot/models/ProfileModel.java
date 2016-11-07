package squarerock.hoot.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import squarerock.hoot.databases.TweetsDb;

/**
 * Created by pranavkonduru on 11/5/16.
 */
@Table(database = TweetsDb.class)
@Parcel(analyze={ProfileModel.class})
public class ProfileModel extends BaseModel {

    public ProfileModel(){}

    @PrimaryKey
    @Column
    public Long id;

    @Column(name = "friends_count")
    public long friends_count;

    @Column(name = "followers_count")
    public long followers_count;

    @Column(name = "description")
    public String description;

    @Column(name = "name")
    public String name;

    @Column(name = "profile_background_image_url_https")
    public String profile_background_image_url_https;

    @Column(name = "profile_image_url_https")
    public String profile_image_url_https;

    @Column(name = "screen_name")
    public String screen_name;

    public static ProfileModel getUserProfile(){
        return new Select().from(ProfileModel.class).querySingle();
    }

    public static ProfileModel parseJSON(String jsonString){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonString, ProfileModel.class);
    }
}
