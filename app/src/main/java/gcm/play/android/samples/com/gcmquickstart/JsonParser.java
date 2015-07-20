package gcm.play.android.samples.com.gcmquickstart;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by dk on 5/13/2015.
 */
public class JsonParser {

    static InputStream is = null;
    static JSONObject jsonObject = null;
    static String json;

    public JsonParser(){
        //
    }

    public JSONObject getJSONFromUrl(String url){

        //make http request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //read json data
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            is.close();
            json = stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            Log.e("JSON Encoding Error", "Error encoding data " + e.toString());
        } catch (IOException e) {
            Log.e("JSON Buffer error", "Error reading Json data" + e.toString());
            //e.printStackTrace();
        }

        //parse returned string to a Json Object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        //return Json Object
        return jsonObject;
    }

}
