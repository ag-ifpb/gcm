package gcm.play.android.samples.com.gcmquickstart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dk on 6/25/2015.
 */
public class Login extends Activity {

    TextView etPhone;
    Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences msharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String registered = msharedPreferences.getString(Configs.userPref,Configs.login_false);
        if (!registered.equalsIgnoreCase(Configs.login_false)){
            Intent login = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(login);
//            Toast.makeText(getApplicationContext(),"logg "+registered,Toast.LENGTH_SHORT).show();
            finish();
        }

        etPhone = (TextView) findViewById(R.id.etPhone);
        btnReg = (Button) findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Button clicked, register user
                String userNumber = etPhone.getText().toString();
                if (!userNumber.isEmpty()){
                    //Send user with entered number
                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                    main.putExtra("userNumber",userNumber);
                    startActivity(main);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter number",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
