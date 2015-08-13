package gcm.play.android.samples.com.gcmquickstart;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.toolbox.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by dk on 5/15/2015.
 */
public class Profile_Settings extends ActionBarActivity {

    Intent intent;
    String extra;
    TextView result;
    ImageView profilePic;
    ImageButton profilePicBtn;
    ProgressBar progressBar;
    String mCurrentPhotoPath;
    Button upload;
    static final String DEBUG_TAG = Profile_Settings.class.getSimpleName();

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GET_PHOTO = 2;
    static final int REQEST_CROP_PHOTO = 3;
    static final String URL = Configs.picURL;
    private Uri picUri;
    Utils utils;

    public int orientation_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        profilePic = (ImageView) findViewById(R.id.icon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        utils = new Utils(getApplicationContext());

        String pic = utils.getPref(Configs.userPic);
        String userNumber = utils.getPref(Configs.phone);
        Log.d("Shared pic", pic);
//        Log.d("Shared pic 2",getResources().getString("profile_pic"));
        if (pic.equalsIgnoreCase("none")) {
            //no image saved
        } else {
            // Load image view with picture from string stored in preferences
            Bitmap bm = StringToBitmap(pic);
            Log.d(this.getClass().getSimpleName(), "preference decoded -- " + bm);
//            Log.d(this.getClass().getSimpleName(), "Image view " + profilePic);

            profilePic.setImageBitmap(bm);
        }
        result = (TextView) findViewById(R.id.testview);
        result.setText(userNumber);

        profilePicBtn = (ImageButton) findViewById(R.id.imageButton);
        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch menu
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.profile_popup, popupMenu.getMenu());
                // check for internet connectivity
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    popupMenu.show();
                } else {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        return false;
                        switch (menuItem.getItemId()) {
                            case R.id.camera:
                                //Toast.makeText(getApplicationContext(),"Camera chosen",Toast.LENGTH_SHORT).show();
                                takePicture();
                                return true;
                            case R.id.gallery:
//                                Toast.makeText(getApplicationContext(),"Gallery chosen",Toast.LENGTH_SHORT).show();
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                                pickPhoto.setType("image/");
                                startActivityForResult(pickPhoto, REQUEST_GET_PHOTO);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
            picUri = data.getData();
            cropImage();

        } else if (requestCode == REQUEST_GET_PHOTO && resultCode == RESULT_OK && data != null) {
//            Toast.makeText(getApplicationContext(),"picture returned",Toast.LENGTH_SHORT).show();
            String picturePath = null;
            Bitmap bm = null;
            picUri = data.getData();
            cropImage();

        } else if (requestCode == REQEST_CROP_PHOTO && resultCode == RESULT_OK) {
            //handle cropped image
            Bundle extras = data.getExtras();
            Log.d(this.getClass().getSimpleName(), "Image cropped " + extras.toString());
            Bitmap croppedPic = extras.getParcelable("data");
            Log.d(this.getClass().getSimpleName(), "The bitmap " + croppedPic);
            profilePic.setImageBitmap(croppedPic);

            utils.savePref(Configs.userPic, BitmapToString(croppedPic));
            Log.d(this.getClass().getSimpleName(), "Preference encoding~: " + BitmapToString(croppedPic));

            //start service to upload image to server
            /*Intent upload = new Intent(this, Upload_Image_Service.class);
            upload.putExtra("data", BitmapToString(croppedPic));*/

            // check for internet connectivity
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                uploadImage(BitmapToString(croppedPic));
            } else {
                Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Deal with rotated images
     */
    public static Bitmap rotateImage(String src, Bitmap bitmap) {

        ExifInterface exifInterface = null;
        Matrix m = new Matrix();
        int orientation = 1;
        Bitmap rotatedImage = null;

        try {
            exifInterface = new ExifInterface(src);

            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d(DEBUG_TAG, "Exif orientation " + orientation);
            switch (orientation) {
                case 3:
                    m.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    m.setRotate(90);
                    break;
                default:
                    return bitmap;
            }

            //rotate image according to orientation
            rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "ExifInterface error");
        }

        return rotatedImage;
    }

    public String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytestream);
        Log.d(DEBUG_TAG, "");
        byte[] b = bytestream.toByteArray();
//        String encodedBitmap = Base64.encodeToString(b,Base64.DEFAULT);
        String encodedBitmap = Base64.encodeToString(b, 0);
        System.gc();
        return encodedBitmap;
    }

    public Bitmap StringToBitmap(String bmString) {
        byte[] c = Base64.decode(bmString, 0);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(c, 0, c.length);
        return decodedBitmap;
    }

    /**
     * Loading bitmaps efficiently
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */

   private void  uploadImage(final String image){
        // AsyncTask to upload image
       new AsyncTask<Void,Void,String>(){

           @Override
           protected void onPreExecute() {
               super.onPreExecute();
               progressBar.setVisibility(View.VISIBLE);
           }

           @SuppressWarnings("deprecation")
           @Override
           protected String doInBackground(Void... voids) {
               ArrayList<NameValuePair> data = new ArrayList<NameValuePair>(1);
               data.add(new BasicNameValuePair("pic", image));
               data.add(new BasicNameValuePair("filename",utils.getPref(Configs.phone)));
               try {
                   HttpClient httpClient = new DefaultHttpClient();
                   HttpPost httpPost = new HttpPost(Configs.picURL);
//                   Log.d(DEBUG_TAG, "in upload " + image);
                   httpPost.setEntity(new UrlEncodedFormEntity(data));
                   Log.i(DEBUG_TAG, "httppost" + httpPost);
                   HttpResponse response = httpClient.execute(httpPost);
                   Log.d(DEBUG_TAG, "Http response~ " + response);

                   InputStream inputStream = response.getEntity().getContent();
                   BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                   StringBuilder stringBuilder = new StringBuilder();
                   String line = null;
                   while ((line = reader.readLine()) != null) {
                       stringBuilder.append(line + "\n");
                   }
                   Log.d(DEBUG_TAG, "string response " + stringBuilder.toString());
                   inputStream.close();
                   return null;
               } catch (ClientProtocolException e) {
                   e.printStackTrace();
               } catch (UnsupportedEncodingException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               return null;
           }

           @Override
           protected void onPostExecute(String s) {
               super.onPostExecute(s);
               progressBar.setVisibility(View.GONE);
               utils.savePref(Configs.userPic, image);
               Toast.makeText(getApplicationContext(),"Profile picture changed",Toast.LENGTH_SHORT).show();
           }
       }.execute();
    }

    private void cropImage() {

        try {
            Log.d(DEBUG_TAG, "inside cropping method");
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 150);
            cropIntent.putExtra("outputY", 150);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQEST_CROP_PHOTO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Cropping not supported", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
