package gcm.play.android.samples.com.gcmquickstart;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dk on 6/29/2015.
 */
public class Contacts extends AppCompatActivity {

    ListView contacts;
    private final String DEBUG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        contacts = (ListView) findViewById(R.id.lvContacts);

        new getUsers().execute("gg");
    }

    private class getUsers extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;
//        List = null;
        ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Contacts.this);
            progressDialog.setMessage("Fetching Users ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();

            //fetch data
            JSONObject jsonObject = jsonParser.getJSONFromUrl(Configs.usersURL);

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
//            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            try{
                //get JsonArray
                JSONArray usersArray = jsonObject.getJSONArray("users");
                Log.d(DEBUG_TAG,"json array "+usersArray.toString());
                for (int i=0; i<usersArray.length();i++){
                    //get individual data
                    JSONObject jo = usersArray.getJSONObject(i);
                    String num = jo.getString("names");
                    String gcmId = jo.getString("gcmId");
                    // Add retrieved data to hashmap
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("num", num);
                    map.put("gcmId", gcmId);

                    //save returned data in DB
                    DbHandler dbHandler = new DbHandler(getApplicationContext());
                    User user = new User();
                    Boolean dbresponse = dbHandler.addEntry(user);
                    if(!dbresponse){
                        Toast.makeText(getApplicationContext(),"Failed to save",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                    }

                    arrayList.add(map);
//                    Listview_Adapter listAdapter = new Listview_Adapter(getApplicationContext(),arrayList);
//                    Log.d(DEBUG_TAG,"listview adapter: "+listAdapter);
//                    Log.d(DEBUG_TAG, "listview: " + contacts);
//                    contacts.setAdapter(listAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
