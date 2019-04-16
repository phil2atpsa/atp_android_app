package za.co.atpsa.products;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.ui.TransparentProgressDialog;

public class Credits extends ProductApi  {

    public Credits(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);
    }



    public void history(String access_token, int days){
        report(access_token,"credits/history", days );


    }
    public void request(String access_token, int days){
        report(access_token,"credits/request", days );
    }
    public void transanctions(String access_token, int days){
        report(access_token,"credits/transactions", days );

    }
    public void purchase(String access_token, JSONObject post){
        StringEntity stringEntity = new StringEntity(post.toString(),"UTF-8");
        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }

        post(context, AppConfig.MOBILE_ENDPOINT.concat("credits/purchase"), stringEntity,
                "application/json;charset=utf-8", new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                        // Log.e("Response", responseBody.toString());
                        callback.completed(responseBody);

                        if(showProgressDialog)
                            progressBar.dismiss();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jSONObject){


                        if(showProgressDialog)
                            progressBar.dismiss();

                        callback.failed(new ServiceException(throwable));
                    }

        });
    }
}
