package squarerock.hoot.models;

import android.support.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

import squarerock.hoot.databases.TweetsDb;

@Table(database = TweetsDb.class)
@Parcel(analyze={TweetModel.class})
public class TweetModel extends BaseModel {

	@PrimaryKey
	@Column
	public Long id;

	@Column(name = "created_at")
	public String created_at;

	@Column(name = "text")
	public String text;

	@Column(name = "retweet_count")
	public int retweet_count;

	@Column(name = "favorite_count")
	public int favorite_count;

	@Column(name = "favorited")
	public boolean favorited;

	@Column(name = "retweeted")
	public boolean retweeted;

    @ForeignKey(references = {
            @ForeignKeyReference(
                    columnType = long.class,
                    columnName = "user",
                    foreignKeyColumnName = "id"
            )
    })
	@Column
	public UserModel user;

    /*@Column
    public EntitiesModel media;*/

    /*@ForeignKey(references = {
            @ForeignKeyReference(
                    columnType = long.class,
                    columnName = "extended_entities",
                    foreignKeyColumnName = "id"
            )
    })
    public EntitiesModel extended_entities;*/

	TweetModel() {
		super();
	}

	public TweetModel(Long id, String created_at,
					  String text, int retweet_count,
					  int favorite_count, boolean favorited,
					  boolean retweeted, UserModel userModel) {
		this.id = id;
		this.created_at = created_at;
		this.text = text;
		this.retweet_count = retweet_count;
		this.favorite_count = favorite_count;
		this.favorited = favorited;
		this.retweeted = retweeted;
		this.user = userModel;
	}

	public static List<TweetModel> getTweets(){
		return new Select().from(TweetModel.class)
                .orderBy(TweetModel_Table.created_at, false)
				.queryList();
	}

    public static TweetModel parseJSON(String jsonString){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonString, TweetModel.class);
    }

    public static long getMaxId(){
        List<TweetModel> remoteIdList = getTweetModelIds(false);
        return remoteIdList.get(0).id;
    }

    public static long getSinceId(){
        List<TweetModel> remoteIdList = getTweetModelIds(true);
        return remoteIdList.get(0).id;
    }

    @NonNull
    private static List<TweetModel> getTweetModelIds(boolean sortAsc) {
        return new Select().from(TweetModel.class)
                .orderBy(TweetModel_Table.created_at, sortAsc)
                .queryList();
    }

    public static List<TweetModel> getTweetsByScreenName(String screenName){
        return new Select().from(TweetModel.class)
                .innerJoin(UserModel.class)
                .on()
                .where(UserModel_Table.screen_name.eq(screenName))
                .orderBy(TweetModel_Table.created_at, false)
                .queryList();

    }

    /*public static TweetModel fromJSON(JSONObject jsonobject) throws JSONException {
        TweetModel tweet = new TweetModel();

        try {
            tweet.id = jsonobject.getLong("id");
            tweet.created_at = jsonobject.getString("created_at");
            tweet.text = jsonobject.getString("text");
            tweet.retweet_count = jsonobject.getInt("retweet_count");
            tweet.favorite_count = jsonobject.getInt("favorite_count");
            tweet.favorited = jsonobject.getBoolean("favorited");
            tweet.retweeted = jsonobject.getBoolean("retweeted");
            tweet.user = UserModel.fromJSON(jsonobject.getJSONObject("user"));
//            tweet.media = EntitiesModel.fromJSON(jsonobject);
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return the tweet object
        return tweet;
    }

    public static ArrayList<TweetModel> fromJSONArray(JSONArray jsonarray) {
        ArrayList<TweetModel> tweets = new ArrayList<>();
        //Iterate the json array and create tweets
        for (int i = 0; i < jsonarray.length(); i++) {
            try {
                JSONObject tweetjson = jsonarray.getJSONObject(i);
                TweetModel tweet = TweetModel.fromJSON((tweetjson));
                if (tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //return finished list
        return tweets;
    }*/
}
