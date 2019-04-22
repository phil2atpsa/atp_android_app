package za.co.atpsa.products;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.ui.TransparentProgressDialog;

public class Access extends ProductApi {





    public Access(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
       super(context,callback,showProgressDialog);

    }

    public void login(String username, String password){
        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("cell_no",username);
            requestBody.put("password",password);


            post(context, AppConfig.MOBILE_ENDPOINT.concat("login"),
                    new StringEntity(requestBody.toString(),"UTF-8"),
                    "application/json;charset=utf-8", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                            callback.completed(responseBody);
                            if(showProgressDialog)
                                progressBar.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jsonObject){

                            Log.e("Error", jsonObject.toString());
                            if(showProgressDialog)
                                progressBar.dismiss();


                            callback.failed(new ServiceException(throwable));
                        }



                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void authorize(String access_token){

        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }

       // Log.e("Access Token", access_token);

        RequestParams params = new RequestParams("access_token", access_token);
       // Log.e("authorize",AppConfig.MOBILE_ENDPOINT.concat("authorize?access_token="+access_token));

        get(AppConfig.MOBILE_ENDPOINT.concat("authorize"),params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
               // Log.e("Response", responseBody.toString());
                callback.completed(responseBody);
                if(showProgressDialog)
                    progressBar.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jsonObject){


                if(showProgressDialog)
                    progressBar.dismiss();

                Log.e("Error", throwable.getMessage());
                Log.e("Error", jsonObject.toString());
                callback.failed(new ServiceException(throwable));
            }
        });
    }
}
