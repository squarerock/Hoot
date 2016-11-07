package squarerock.hoot.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by pranavkonduru on 11/6/16.
 */

public class Users {

    private List<UserModel> users;
    private long next_cursor;

    public long getNextCursor() {
        return next_cursor;
    }

    public void setNextCursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public Users(List<UserModel> users, long next_cursor) {
        this.users = users;
        this.next_cursor = next_cursor;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public static Users parseJSON(String response){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()});
        Gson gson = gsonBuilder.create();

        return gson.fromJson(response, Users.class);
    }
}
