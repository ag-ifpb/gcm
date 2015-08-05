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

    Context context;

    final String TAG = "DB Handler";

    public DbHandler (Context context){
        dbHelper = new DbHelper(context);
        this.context = context;
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

    public Boolean saveMessage(Messages message){
        Boolean toReturn = false;
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(DbHelper.MSG_TO,message.getMessageTo());
            values.put(DbHelper.MSG_FROM, message.getMessageFrom());
            values.put(DbHelper.MSG_TEXT, message.getMessageText());
            values.put(DbHelper.MSG_TIME, message.getMessageTime());
            Log.d(TAG, "values " + values.toString());

            liteDatabase.insert(DbHelper.MSG_TABLE, null, values);
            Log.d(TAG,"Message saved");
            toReturn = true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG,"save Message "+e.toString());
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
        close();
        return null;
    }

    public Cursor getMessages(String number){
        try {
            open();
            String[] columns = {"phone","regId"};
//            Cursor contacts = liteDatabase.query("mesages",columns,null,null,null,null,null);
            Cursor contacts = liteDatabase.rawQuery("select * from messages where messageTo = '" + number + "' or messageFrom = '" + number + "' ", null);
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG,"Error retrieving data "+e.toString());
        }
        close();
        return null;
    }

    public void deleteTable(){
        //delete and recreate table on user refresh
        try {
            open();
            liteDatabase.execSQL("delete from "+DbHelper.DB_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error deleting data " + e.toString());
        }
        close();
    }

    public Cursor getRegId(String phone){
        Cursor regId = null;
        try {
            open();
            regId = liteDatabase.rawQuery("select * from contacts where phone = '"+phone+"' ",null);
            return regId;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG,"sql error "+e.toString());
        }
        close();
        return null;
    }
}
