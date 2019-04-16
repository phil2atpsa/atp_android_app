package za.co.atpsa.atp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;

import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Access;

public class Login extends Activity {

    TextView disclaimer;
    EditText cell_no, password;
    private boolean outcome = false;

    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Calendar c = Calendar.getInstance();
        ((TextView) findViewById(R.id.bottom_text)).setText(Html.fromHtml("&copy Advanced Technology Partner " + c.get(Calendar.YEAR)));
        disclaimer = findViewById(R.id.disclaimer);
        spref = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
        disclaimer.setText(Html.fromHtml(getString(R.string.disclaimer)));

        cell_no = findViewById(R.id.cell_no);
        password = findViewById(R.id.password);

    }

    public void login(View view) {
        if(TextUtils.isEmpty(cell_no.getText())){
            cell_no.setError("Mobile phone required");
            cell_no.requestFocus();
        } else if(TextUtils.isEmpty(password.getText())){
            password.setError("Password required");
            password.requestFocus();
        } else {
            new Access(Login.this, new OnServiceResponseListener() {
                @Override
                public void completed(Object object) {
                    JSONObject response = (JSONObject)object;
                    if(response.optInt("success") == 0){
                        String message = response.optString("message");
                        String field = response.optString("field");
                        if(field.equals("cell_no")){
                            cell_no.setError(message);
                            cell_no.requestFocus();

                        } else {
                            password.setError(message);
                            password.requestFocus();

                        }
                        outcome = false;


                    } else {
                        //    Toast.makeText(Login.this, response.optString("access_token"), Toast.LENGTH_LONG).show();
                        outcome  = true;
                       // showProgress(false);
                    }
                    if (outcome) {
                        if(!spref.contains("first_time_run")) {
                            editor.putBoolean("first_time_run", true).commit();
                        }
                        editor.putString("access_token", response.optString("access_token")).commit();
                        startActivity(new Intent(Login.this, Content.class));
                        finish();
                    }


                }

                @Override
                public void failed(ServiceException e) {

                }
            }, true).login(cell_no.getText().toString(),
                    password.getText().toString());
        }
    }
}
