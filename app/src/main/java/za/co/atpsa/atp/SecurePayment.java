package za.co.atpsa.atp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONException;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import za.co.atpsa.atp.helpers.HandlePayment;

public class SecurePayment extends AppCompatActivity {

    WebView webView;
    Bundle bundle;
    int sp_id;
    String ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.secure_payment);
        setTitle("Secured Payment");
        bundle  = getIntent().getExtras();

        sp_id = bundle.getInt("sp_id");
        ref = bundle.getString("ref");





        if(bundle.getString("tid") != null) {
            FirebaseInstanceId.getInstance().getInstanceId() .addOnSuccessListener(SecurePayment.this,
                    new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();

                        }
                    });

            PushNotifications.start(getApplicationContext(), getString(R.string.pusher_key));
            PushNotifications.addDeviceInterest(bundle.getString("tid"));

        }



        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        if(bundle.getString("redirect") != null)
            webView.loadUrl( bundle.getString("redirect"));




    }

    private int getScale(){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(display.getHeight());
        val = val * 100d;
        return val.intValue();
    }


    @Override
    protected void onResume() {
        super.onResume();
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {




                if (remoteMessage.getData().size() > 0) {

                    final Map<String, String> data =  remoteMessage.getData();

                    if(data.containsKey("PAYGATE_ID")) {

                        boolean approved = data.get("RESULT_CODE").trim().equals("990017") ;
                        String desc = data.get("RESULT_DESC").concat(" ").concat(data.get("RESULT_CODE"));
                        String title = approved ? "Payment Successful" :"Payment Failed";
                         Bundle extras = new Bundle();
                        extras.putString("title", title);
                        extras.putString("desc", desc);
                        extras.putString("ref", ref);
                        extras.putString("tid", data.get("TRANSACTION_ID"));
                        extras.putInt("sp_id", sp_id);
                        extras.putBoolean("approved", approved);


                        Log.e("title",title);
                        Log.e("desc", desc);
                        Log.e("ref", ref);
                        Log.e("tid",  data.get("TRANSACTION_ID"));
                        Log.e("sp_id",  ""+sp_id);


                       PushNotifications.removeDeviceInterest(data.get("TRANSACTION_ID"));

                       Intent intent = new Intent(SecurePayment.this, Confirmation.class).putExtras(extras);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();

                    }



                }


            }
        });
    }
}
