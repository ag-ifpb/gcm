package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dk on 5/27/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    //Database constants
    public static final String DB_NAME = "ZunghaDb";
    static final int DB_VERSION = 1;
    static final String DB_TABLE = "contacts";

    //Table Columns
    static final String KEY_NAME = "phone";
    public static final String KEY_ID = "_id";
    static final String KEY_TITLE = "DbAdapter";
    static final String KEY_REG = "regId";

    //Table Columns for messages table
    static final String MSG_TABLE = "messages";
    public static final String MSG_ID = "id";
    static final String MSG_TO = "messageTo";
    static final String MSG_FROM = "messageFrom";
    static final String MSG_TEXT = "messageText";
    static final String MSG_TIME = "messageTime";

    final String DEBUG_TAG = this.getClass().getSimpleName();

    //Database creation string
    static final String DATABASE_CREATE =
            "create table " + DB_TABLE + " ("
            + KEY_ID + " integer primary key autoincrement,"
            + KEY_NAME + " text not null,"
            + KEY_REG + " text not null"
            + ")";

    static final String MESSAGES_TABLE =
            "create table " + MSG_TABLE + " ("
                    + MSG_ID + " integer primary key autoincrement,"
                    + MSG_TO + " text not null,"
                    + MSG_FROM + " text not null,"
                    + MSG_TEXT + " text not null,"
                    + MSG_TIME + " text not null"
                    + ")";


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.execSQL(MESSAGES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(DEBUG_TAG, "Upgrading database from version" + i + " to version " + i1 + " which will destroy old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(sqLiteDatabase);

    }

}
