package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by dk on 5/12/2015.
 */
public class List_Adapter extends BaseAdapter {

    ArrayList<User> data;
    Context context;
    LayoutInflater inflater;
    private BitmapPool mPool;

    public List_Adapter(Context context,ArrayList<User> arrayList){
        this.data = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        mPool = Glide.get(context).getBitmapPool();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder mViewholder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.contact_items,viewGroup,false);
            mViewholder = new ViewHolder();
            mViewholder.mimage = (ImageView) convertView.findViewById(R.id.ivContact);
            mViewholder.mName = (TextView) convertView.findViewById(R.id.tvNum);
            convertView.setTag(mViewholder);
//            mViewholder.mName.setText(data.get(i));
        }else {
            mViewholder = (ViewHolder) convertView.getTag();
        }

//        mViewholder.mName.setText("ada");

        final User userdata = getItem(i);
        mViewholder.mName.setText(userdata.getPhone());
        //using glide to load image
        Glide.with(context)
                .load("http://104.236.45.161/andrew/photos/andrew.jpg")
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(mPool))
                .placeholder(R.drawable.ic_face_black_24dp)
                .into(mViewholder.mimage);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"Clicked "+userdata.getPhone(),Toast.LENGTH_SHORT).show();
                Intent chat = new Intent(context,Chat_Activity.class);
                chat.putExtra(Configs.phone,userdata.getPhone());
                chat.putExtra(Configs.regId,userdata.getGcmId());
                context.startActivity(chat);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView mimage;
        TextView mName;
    }
}
