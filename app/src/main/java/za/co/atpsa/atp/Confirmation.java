package za.co.atpsa.atp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import za.co.atpsa.atp.helpers.HandlePayment;


public class Confirmation extends AppCompatActivity {
    private ImageView avatar,indicator;
    private TextView title,confirmation_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
        setTitle("Payment Confirmation");

        Bundle extras = getIntent().getExtras();
        String stitle="";
        String desc ="";
        String ref ="";
        int sp_id  = 0;
        boolean approved = false;
        String tid  ="";
        try {
            stitle = extras.getString("title") != null ? extras.getString("title") : "";
            desc = extras.getString("desc") != null ? extras.getString("desc") : "";
            ref = extras.getString("ref") != null ? extras.getString("ref") : "";
            approved = extras.getBoolean("approved") ? extras.getBoolean("approved") : false;
            tid = extras.getString("tid") != null ? extras.getString("tid") : "";
            sp_id = extras.getInt("sp_id");
        } catch(NullPointerException e){}

        Log.e("title", stitle);
        Log.e("desc", desc);
        Log.e("approved", String.valueOf(approved));
        Log.e("tid", String.valueOf(tid));

        avatar = findViewById(R.id.avatar);
        indicator = findViewById(R.id.indicator);
        confirmation_text = findViewById(R.id.confirmation_text);
        title = findViewById(R.id.title);

        title.setText(stitle);

        String message = "Your payment with reference number "+ref;
        message +=  approved ? " has been successful": " has failed with message: '"+
                desc+"'";

        confirmation_text.setText(message);

        indicator.setImageResource(approved ? R.drawable.ic_check : R.drawable.ic_payment_failed);
        avatar.setImageResource(approved ? R.drawable.ic_payment_confirmed_titled : R.drawable.ic_payment_failure);

        int sound = approved ? R.raw.success_alert : R.raw.faillure_alert;
        final MediaPlayer mp = MediaPlayer.create(this, sound);
        mp.start();



        HandlePayment handlePayment = new HandlePayment(Confirmation.this,
                approved, desc, tid,sp_id, ref);
        handlePayment.complete();
        try {
            handlePayment.sendnotify();
            handlePayment.in_app_notify();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    //
       //

         //


         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                continue_process(null);
            }
        }, 12000);
    }






    public void continue_process(View view) {
        Intent i = new Intent(Confirmation.this, Content.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


}
