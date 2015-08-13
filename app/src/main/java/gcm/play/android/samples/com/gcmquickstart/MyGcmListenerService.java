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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        //save in db
        DbHandler dbHandler = new DbHandler(getApplicationContext());
        String[] sunText = message.split("~");
        Messages messages = new Messages(sunText[1]);
        messages.setMessageFrom(sunText[0]);
        Utils utils = new Utils(getApplicationContext());
        messages.setMessageTo(utils.getPref(Configs.userPref));
        messages.setMessageTime(new Date().toString());
        dbHandler.saveMessage(messages);

        updateUI(message,from);

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message,sunText[0]);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message,String sender) {
        Utils utils = new Utils(getApplicationContext());
        int num = Integer.parseInt(utils.getPref(Configs.notificationNumber));
        int numm = num + 1;
        final String GROUP_KEY_MESSAGES = "group_key_messages";
        Intent intent = new Intent(this, Chat_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Configs.phone, sender);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_stat_ic_notification);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setLargeIcon(largeIcon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("New message " + message)
                        .setBigContentTitle(getString(R.string.app_name))
                        .setSummaryText(numm + " new messages"))
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_MESSAGES)
                .setGroupSummary(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());

        utils.savePref(Configs.notificationNumber,String.valueOf(numm));
    }

    private void updateUI (String message,String from){
        //send broadcast to ui
        Intent broadcastOut = new Intent(Configs.Broadcast_Action);
        broadcastOut.putExtra(Configs.sender,from);
        broadcastOut.putExtra(Configs.chatText,message);
        sendBroadcast(broadcastOut);
    }
}
