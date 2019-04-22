package za.co.atpsa.atp.helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.atp.CreditCard;
import za.co.atpsa.atp.R;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.sp_manager.Paygate;
import za.co.atpsa.sp_manager.ServiceProvider;

public class HandlePayment  {
    private boolean status;
    private String outcome;
    private String transactionID;
    private Context context;
    private int sp_id;
    private String ref;


    public HandlePayment(Context context, boolean status,
                         String outcome, String transactionID, int sp_id, String ref){
        this.status = status;
        this.outcome = outcome;
        this.transactionID = transactionID;
        this.context = context;
        this.sp_id = sp_id;
        this.ref = ref;

    }

    public  void complete(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("status", status ? "approved" : "failed");
            obj.put("outcome", outcome);
            obj.put("transactionID", transactionID);

            new Paygate(context, new OnServiceResponseListener() {
                @Override
                public void completed(Object object) {

                }

                @Override
                public void failed(ServiceException e) {

                }
            }, false).outcome(obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendnotify() throws JSONException {
        final JSONObject post = new JSONObject();
        post.put("success", status ? 1: 0);
        post.put("message", outcome);
        post.put("ATP_ref", ref);

        new ServiceProvider(context, new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONObject obj = (JSONObject)object;
                String payment_notify_page_url = obj.optString("payment_notify_page_url");

                if(!TextUtils.isEmpty(payment_notify_page_url)){
                    AsyncHttpClient client = new AsyncHttpClient();
                    StringEntity stringEntity = new StringEntity(post.toString(), "UTF-8");
                    client.post(context, payment_notify_page_url, stringEntity, "application/json", new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        }
                    });


                }


            }

            @Override
            public void failed(ServiceException e) {

            }
        }, false).settings(sp_id);
    }

    public  void in_app_notify() throws JSONException {
        final JSONObject post = new JSONObject();
        String message = "Your payment with reference number "+ref;
        message +=  status ? " has been successful": " has failed with message: '"+ outcome+"'";
        post.put("success", status ? 1: 0);
        post.put("message",message);



        long seconds = Calendar.getInstance().getTimeInMillis();
        Random random = new Random(seconds);
        int r = random.nextInt(112);

        final String channel_id = String.valueOf(seconds).concat(String.valueOf(sp_id)).concat(String.valueOf(r));
        PushNotifications.start(context, context.getString(R.string.pusher_key));
        PushNotifications.addDeviceInterest(channel_id);

        String notification_message = "{\"interests\":[\""+channel_id+"\"],\"fcm\":{\"data\":"+post.toString()+"}}";
        Log.e("Notification", notification_message);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("content-type", "application/json");
        client.addHeader("authorization", "Bearer "+ context.getString(R.string.pusher_auth_key));

        client.post(context, context.getString(R.string.pusher_notify_endpoint),
                new StringEntity(notification_message, "UTF-8"), "application/json",
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                       // PushNotifications.removeDeviceInterest(channel_id);
                        Log.e("Response", responseBody.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jsonObject){
                      //  PushNotifications.removeDeviceInterest(channel_id);
                        Log.e("Failure", jsonObject.toString());
                        Log.e("Failure", throwable.getMessage());
                    }

                });



    }
}
