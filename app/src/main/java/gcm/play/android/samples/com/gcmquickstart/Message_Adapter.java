package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dk on 7/10/2015.
 */
public class Message_Adapter extends BaseAdapter {

    ArrayList<Messages> message;
    Context context;
    LayoutInflater inflater;

    public Message_Adapter(Context context,ArrayList<Messages> arrayList){
        this.context = context;
        this.message = arrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Messages getItem(int i) {
        return message.get(i);
    }

    public void updateList(ArrayList<Messages> data) {
        message = data;
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder mViewholder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.chat_item,viewGroup,false);
            mViewholder = new ViewHolder();
            mViewholder.mChat = (TextView) convertView.findViewById(R.id.tvChatItem);
            convertView.setTag(mViewholder);
//            mViewholder.mName.setText(data.get(i));
        }else {
            mViewholder = (ViewHolder) convertView.getTag();
        }

//        mViewholder.mName.setText("ada");

        final Messages chatdata = getItem(i);
        mViewholder.mChat.setText(chatdata.getMessageText());

        return convertView;
    }

    public static class ViewHolder {
        TextView mChat;
    }

}
