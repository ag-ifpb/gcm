/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
//                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                String token = instanceID.getToken(Configs.SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);
//                get user Number
                String userNumber = intent.getStringExtra("userNumber");

                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(token,userNumber);

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                sharedPreferences.edit().putString(Configs.userPref, userNumber).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token,String userNumber) {
        // Add custom implementation, as needed.

        //custom code to send regId to server
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>(1);
        data.add(new BasicNameValuePair("regId",token));
        data.add(new BasicNameValuePair("user",userNumber));
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Configs.SERVER_URL);
            Log.d(TAG,"in upload "+ token);
            httpPost.setEntity(new UrlEncodedFormEntity(data));
            Log.i(TAG, "httppost" + httpPost);
            HttpResponse response = httpClient.execute(httpPost);
            Log.d(TAG, "Http response~ " + response);

            InputStream inputStream = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            Log.d(TAG, "string response " + stringBuilder.toString());
            inputStream.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
            Log.d(TAG, "topic" + topic);
            Log.d(TAG,"pubsub"+pubSub);
        }
    }
    // [END subscribe_topics]


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent contactsIntent = new Intent(getApplicationContext(),Contacts_Activity.class);
        contactsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(contactsIntent);
    }
}
