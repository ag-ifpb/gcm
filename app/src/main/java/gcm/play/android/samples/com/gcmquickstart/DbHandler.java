package gcm.play.android.samples.com.gcmquickstart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by dk on 5/28/2015.
 */
public class DbHandler {

    //define database helper constants
    private SQLiteDatabase liteDatabase;
    private DbHelper dbHelper;
    private String[] allcolumns = {dbHelper.KEY_ID,dbHelper.KEY_NAME,dbHelper.KEY_REG};

    final String TAG = "DB Handler";

    public DbHandler (Context context){
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        liteDatabase = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Boolean addEntry(User user){
        String response = null;
        Boolean toReturn = false;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(DbHelper.KEY_NAME,user.getPhone());
            values.put(DbHelper.KEY_REG, user.getGcmId());
            Log.d(TAG,"values "+values.toString());

            liteDatabase.insert(DbHelper.DB_TABLE, null, values);
            response = "Entries added to db";
            Log.d(TAG,response);
            toReturn = true;
        } catch (SQLException e) {
            e.printStackTrace();
            response = "exception: " + e.toString();
            Log.d(TAG, response);

        }

        close();
        return toReturn;
    }

    public Cursor getContacts(){

        try {
            open();
            String[] columns = {"phone","regId"};
            Cursor contacts = liteDatabase.query("contacts",columns,null,null,null,null,null);
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG,"Error retrieving data "+e.toString());
        }

        return null;
    }
}
