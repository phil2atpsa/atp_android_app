package za.co.atpsa.atp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity {

    TextView bottom_text;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_text = findViewById(R.id.bottom_text);
        spref = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        Calendar c = Calendar.getInstance();
        bottom_text.setText(Html.fromHtml("&copy Advanced Technology Partner " + c.get(Calendar.YEAR)));

        if (spref.contains("first_time_run") && spref.contains("access_token")) {
            startActivity(new Intent(MainActivity.this, Content.class));
            finish();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

