package squarerock.hoot.databases;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by pranavkonduru on 10/27/16.
 */

@Database(name = TweetsDb.NAME, version = TweetsDb.VERSION)
public class TweetsDb {

    public static final String NAME = "AppDatabase"; // we will add the .db extension

    public static final int VERSION = 1;
}
