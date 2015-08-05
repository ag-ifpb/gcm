package gcm.play.android.samples.com.gcmquickstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import org.apache.commons.io.IOUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dk on 7/10/2015.
 */
public class Chat_Activity extends ActionBarActivity {

    ActionBar actionBar;
    ListView chatList;
    EditText chatText;
    Button chatSend;
    Listview_Adapter listview_adapter;
    Messages messages;
    Utils utils;
    String DEBUG_TAG = this.getClass().getSimpleName();
    IntentFilter filter;
    DbHandler dbHandler;

    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        utils = new Utils(getApplicationContext());
        dbHandler = new DbHandler(getApplicationContext());

        //get passed data from intent
        Intent myIntent = getIntent();
        final String phone = myIntent.getStringExtra(Configs.phone);
        regId = myIntent.getStringExtra(Configs.regId);
        try {
            if(regId.isEmpty() || regId.length()==0){
                //fetch regId corresponding to phone
                Cursor mregId = dbHandler.getRegId(phone);
                Log.d(DEBUG_TAG,"reg Id "+mregId.getCount());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "regId is null");
            Cursor mregId = dbHandler.getRegId(phone);
            Log.d(DEBUG_TAG, "reg Id " + mregId.getCount());
            for (mregId.moveToFirst();!mregId.isAfterLast();mregId.moveToNext()){

               regId = mregId.getString(mregId.getColumnIndex("regId"));
            }
        }
        actionBar.setTitle(phone);

        chatList = (ListView) findViewById(R.id.chatList);
        chatText = (EditText) findViewById(R.id.chatText);
        chatSend = (Button) findViewById(R.id.chatSend);
        listview_adapter= new Listview_Adapter(getApplicationContext(),R.layout.chat_item);
        chatList.setStackFromBottom(true);
        chatList.setAdapter(listview_adapter);

        Cursor msgs = dbHandler.getMessages(phone);
        Log.d(DEBUG_TAG, "messages returned " + msgs.getCount());
        if(msgs.getCount()!=0){
            //show messages in listview
            ArrayList<Messages> results = new ArrayList<>();
            for (msgs.moveToFirst();!msgs.isAfterLast();msgs.moveToNext()){

                Messages msgObject = new Messages(msgs.getString(msgs.getColumnIndex("messageText")));
                msgObject.setMessageTo(msgs.getString(msgs.getColumnIndex("messageTo")));
                msgObject.setMessageFrom(msgs.getString(msgs.getColumnIndex("messageFrom")));
                msgObject.setMessageTime(msgs.getString(msgs.getColumnIndex("messageTime")));
                listview_adapter.add(msgObject);

//                results.add(msgObject);
            }
        }

        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get data from edit text and populate listview
                if(!chatText.getText().toString().isEmpty()){
                    //add text to message object and call adapter

                    messages=new Messages(chatText.getText().toString());
                    messages.setMessageFrom(utils.getPref(Configs.userPref));
                    messages.setMessageTo(phone);
                    messages.setMessageTime(currentTime());
                    Log.i("message object", "" + messages);
                    listview_adapter.add(messages);
                    Log.i("list adapter", "" + listview_adapter);
                    sendMessage(regId, chatText.getText().toString());

                    //save in db
                    Boolean msgSave = dbHandler.saveMessage(messages);
                    if (msgSave){
                        Toast.makeText(getApplicationContext(),"Message Saved",Toast.LENGTH_SHORT).show();
                    }
//                    message_adapter.notifyDataSetChanged();
                    chatText.setText("");
                }else {
                    Toast.makeText(getApplicationContext(),"No text",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String sendMessage(final String to, String message){
        final String too = to;
//        final String sender = "~0703435435~";
        Utils utils = new Utils(getApplicationContext());
        final String sender = utils.getPref(Configs.userPref)+"~";
        final String msg = sender.concat(message);
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Prepare JSON containing the GCM message content. What to send and where to send.
                    JSONObject jGcmData = new JSONObject();
                    JSONObject jData = new JSONObject();
                    jData.put("message", msg);
                    // Where to send GCM message.
                    if (to.length() > 1) {
                        jGcmData.put("to", to);
                        Log.d(DEBUG_TAG, "recipient not null ");
                    } else {
                        jGcmData.put("to", "/topics/global");
                        Log.d(DEBUG_TAG, "recipient null ");
                    }
                    // What to send in GCM message.
                    jGcmData.put("data", jData);

                    // Create connection to send GCM Message request.
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key=" + Configs.API_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send GCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    Log.d(DEBUG_TAG,"output stream "+outputStream);
                    outputStream.write(jGcmData.toString().getBytes());

                    // Read GCM response.
                    InputStream inputStream = conn.getInputStream();
                    String resp = IOUtils.toString(inputStream);
//                    String resp = inputStream.toString();
                    System.out.println(resp);
                    Log.d(DEBUG_TAG,"GCM Response "+resp);
                    System.out.println("Check your device/emulator for notification or logcat for " +
                            "confirmation of the receipt of the GCM message.");
                } catch (IOException e) {
                    Log.d(DEBUG_TAG,"Unable to send GCM message.");
                    System.out.println("Unable to send GCM message.");
                    System.out.println("Please ensure that API_KEY has been replaced by the server " +
                            "API key, and that the device's registration token is correct (if specified).");
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(DEBUG_TAG,"JSON Error "+e.toString());
                }
                return null;
            }
        }.execute();

        return null;
    }

    public String currentTime(){
        //return current timestamp
        return new Date().toString();
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(),"Broadcast received",Toast.LENGTH_SHORT).show();
            Log.d(DEBUG_TAG, "Broadcast received");

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String msg =bundle.getString(Configs.chatText);
                Log.d(DEBUG_TAG,"Received message "+msg);
                String[] from = msg.split("~");
                messages = new Messages(from[1]);
                messages.setMessageFrom(from[0]);
                messages.setMessageTo(utils.getPref(Configs.userPref));
                messages.setMessageTime(currentTime());
                listview_adapter.add(messages);
                dbHandler.saveMessage(messages);
            } else {
                Toast.makeText(getApplicationContext(), "No Data received", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        filter = new IntentFilter(Configs.Broadcast_Action);
        getApplicationContext().registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(broadcastReceiver);
    }
}
