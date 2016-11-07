package squarerock.hoot.models;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import squarerock.hoot.databases.TweetsDb;

/**
 * Created by pranavkonduru on 10/30/16.
 */

@Table(database = TweetsDb.class)
@Parcel(analyze={EntitiesModel.class})
public class EntitiesModel extends BaseModel {

    public EntitiesModel() {
    }

    @PrimaryKey
    @Column
    Long id;

    @Column(name = "image_url")
    String image_url;

    @Column(name = "video_url")
    String video_url;

    public static EntitiesModel fromJSON(JSONObject json) {
        EntitiesModel entity = new EntitiesModel();

        extractImageInfo(json, entity);

        extractVideoInfo(json, entity);

        entity.save();
        return entity;
    }

    private static void extractVideoInfo(JSONObject json, EntitiesModel entity) {
        JSONObject extendedEntitiesJsonObject, jsonMediaObject;
        JSONArray mediaArray;

        try {
            extendedEntitiesJsonObject = json.getJSONObject("extended_entities");
            mediaArray = extendedEntitiesJsonObject.getJSONArray("media");

            for (int i = 0; i < mediaArray.length(); i++) {
                jsonMediaObject = mediaArray.getJSONObject(i);

                String mediaType = jsonMediaObject.getString("type");
                if ("video".equals(mediaType)) {
                    JSONArray videoVariants;
                    JSONObject jsonVideoInfo = jsonMediaObject.getJSONObject("video_info");
                    videoVariants = jsonVideoInfo.getJSONArray("variants");
                    for (int j = 0; j < videoVariants.length(); j++) {
                        JSONObject videoObject = videoVariants.getJSONObject(j);
                        if ("video/mp4".equals(videoObject.getString("content_type"))) {
                            entity.video_url = videoObject.getString("url");
                            if(!TextUtils.isEmpty(entity.video_url))
                                return;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void extractImageInfo(JSONObject json, EntitiesModel entities) {
        JSONObject entitiesJsonObject;
        JSONArray mediaArray;
        try {
            entitiesJsonObject = json.getJSONObject("entities");
            mediaArray = entitiesJsonObject.getJSONArray("media");
            for (int i = 0; i < mediaArray.length(); i++) {
                entitiesJsonObject = mediaArray.getJSONObject(i);
                entities.id = entitiesJsonObject.getLong("id");
                entities.image_url = entitiesJsonObject.getString("media_url");
                if (entities.image_url != null) {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
