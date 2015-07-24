package gcm.play.android.samples.com.gcmquickstart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Friends extends Fragment {

    ListView lvContacts;
    static final String TAG = "Friends Fragment";
    DbHandler dbHandler;
    Cursor contacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.friends,container,false);
        setHasOptionsMenu(true);

        lvContacts = (ListView) rootView.findViewById(R.id.lvContacts);
        dbHandler = new DbHandler(getActivity());
        contacts = dbHandler.getContacts();
        if (contacts.getCount() == 0){
            Log.d(TAG,"no contacts "+contacts.getCount());

            fetchContacts();

        }else{
            Log.d(TAG,"contacts returned "+contacts.getCount());
            //define adapter
//            ContactsCursorAdapter contactsAdapter = new ContactsCursorAdapter(getActivity(),contacts);
//            lvContacts.setAdapter(contactsAdapter);

            ArrayList<User> results = new ArrayList<>();
            ArrayList<HashMap<String,String>> uuu = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();

            for (contacts.moveToFirst();!contacts.isAfterLast();contacts.moveToNext()){
                map.put("num",contacts.getString(contacts.getColumnIndex("phone")));
                map.put("gcdId", contacts.getString(contacts.getColumnIndex("regId")));

                User user = new User();
                user.setProfilePic("");
                user.setGcmId(contacts.getString(contacts.getColumnIndex("regId")));
                user.setPhone(contacts.getString(contacts.getColumnIndex("phone")));

                results.add(user);
                uuu.add(map);
                ListAdapter listAdapter = new List_Adapter(getActivity(),results);
                lvContacts.setAdapter(listAdapter);
            }
            Log.d(TAG, "results " + uuu.toString());
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
                fetchContacts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchContacts(){
        Log.d(TAG,"fetch contacts called");
        contacts = dbHandler.getContacts();
        if(contacts.getCount()!=0){
            dbHandler.deleteTable();
        }
        new AsyncTask<Void,Void,JSONObject>(){

            private ProgressDialog progressDialog;
            ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();
            ArrayList<User> results = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching Users ...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(true);
                progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JsonParser jsonParser = new JsonParser();

                //fetch data
                JSONObject jsonObject = jsonParser.getJSONFromUrl(Configs.usersURL);

                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                progressDialog.dismiss();
                JSONObject jo;
                HashMap<String, String> map = new HashMap<String, String>();
                DbHandler dbHandler = new DbHandler(getActivity());
                try{
                    //get JsonArray
                    JSONArray usersArray = jsonObject.getJSONArray("users");
                    Log.d(TAG,"json array "+usersArray.toString());
                    for (int i=0; i<usersArray.length();i++){
                        //get individual data
                        jo = usersArray.getJSONObject(i);
                        String num = jo.getString("names");
                        String gcmId = jo.getString("gcmId");
                        // Add retrieved data to hashmap
                        map.put("num", num);
                        map.put("gcmId", gcmId);
                        User user = new User();
                        user.setPhone(num);
                        user.setGcmId(gcmId);

                        //save returned data in DB
                        Boolean dbresponse = dbHandler.addEntry(user);
                        if(!dbresponse){
                            Toast.makeText(getActivity(), "Failed to save", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Saved",Toast.LENGTH_SHORT).show();
                        }

                        arrayList.add(map);
                        results.add(user);
                        Log.d(TAG,"ArrayList "+arrayList.toString());
                            List_Adapter listAdapter = new List_Adapter(getActivity(),results);
//                            Log.d(TAG,"listview adapter: "+listAdapter);
//                            Log.d(TAG, "listview: " + lvContacts);
                            lvContacts.setAdapter(listAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

}
