package squarerock.hoot.models;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

import squarerock.hoot.databases.TweetsDb;

/**
 * Created by pranavkonduru on 11/4/16.
 */

@Table(database = TweetsDb.class)
@Parcel(analyze={MentionsModel.class})
public class MentionsModel extends BaseModel {

    public MentionsModel(){}

    @PrimaryKey
    @Column
    public Long id;

    public MentionsModel(Long remote_id) {
        this.id = remote_id;
    }

    public static List<TweetModel> getAllMentions(){
        return new Select().from(TweetModel.class)
                .where(MentionsModel_Table.id.eq(TweetModel_Table.id))
                .orderBy(TweetModel_Table.created_at, false)
                .queryList();
    }

    public static long getMaxId(){
        List<MentionsModel> remoteIdList = getMentionsModels(true);
        return remoteIdList.get(0).id;
    }

    public static long getSinceId(){
        List<MentionsModel> remoteIdList = getMentionsModels(false);
        return remoteIdList.get(0).id;
    }

    @NonNull
    private static List<MentionsModel> getMentionsModels(boolean sortAsc) {
        return new Select().from(MentionsModel.class)
                    .orderBy(MentionsModel_Table.id, sortAsc)
                    .queryList();
    }

}
