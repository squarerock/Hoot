package squarerock.hoot.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import squarerock.hoot.databases.TweetsDb;

/**
 * Created by pranavkonduru on 10/30/16.
 */

@Table(database = TweetsDb.class)
@Parcel(analyze={EntitiesModel.class})
public class EntitiesModel extends BaseModel {

    @Column (name = "id")
    @PrimaryKey (autoincrement = true)
    Long id;

    /*@ForeignKey(references = {
            @ForeignKeyReference(
                    columnType = long.class,
                    columnName = "media",
                    foreignKeyColumnName = "id"
            )
    })
    public List<MediaModel> media;*/
}
