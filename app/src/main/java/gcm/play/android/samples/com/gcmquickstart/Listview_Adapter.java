package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dk on 5/15/2015.
 */
public class Listview_Adapter extends ArrayAdapter {

    //Declare Variables
    Context context;
    LayoutInflater inflater;
    List data = new ArrayList();
    //ImageLoader imageLoader;
    HashMap<String,String> resultp = new HashMap<String, String>();

    public Listview_Adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void add(Messages chatObject) {
        data.add(chatObject);
        super.add(chatObject);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Messages getItem(int i) {
        return (Messages)data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder mViewholder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.chat_item,null);
            mViewholder = new ViewHolder();
            mViewholder.mChat = (TextView) convertView.findViewById(R.id.tvChatItem);
            mViewholder.rlayout = (LinearLayout) convertView.findViewById(R.id.chatLine);
            convertView.setTag(mViewholder);
//            mViewholder.mName.setText(data.get(i));
        }else {
            mViewholder = (ViewHolder) convertView.getTag();
        }

//        mViewholder.mName.setText("ada");

        final Messages chatdata = getItem(position);
//        String[] from = chatdata.getMessageText().split("~");
        Utils utils = new Utils(context);
        if (chatdata.getMessageFrom().equalsIgnoreCase(utils.getPref(Configs.userPref))) {
            mViewholder.rlayout.setGravity(Gravity.END);
            mViewholder.mChat.setText(chatdata.getMessageText());
        } else {
            mViewholder.rlayout.setGravity(Gravity.START);
            mViewholder.mChat.setText(chatdata.getMessageText());
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView mChat;
        LinearLayout rlayout;
    }

}
