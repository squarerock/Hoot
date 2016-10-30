package squarerock.hoot.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

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

    UserModel(){}

    public UserModel(long remoteId, String name, String screenName, String profileImageUrl, boolean verified) {
        this.id = remoteId;
        this.name = name;
        this.screen_name = screenName;
        this.profile_image_url_https = profileImageUrl;
        this.verified = verified;
    }

}

