package za.co.atpsa.products;
import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;


import cz.msebera.android.httpclient.Header;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.ui.TransparentProgressDialog;


public class Products extends ProductApi {

    public Products(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);

    }


    public void list(String access_token){

        // Log.e("Access Token", access_token);
        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }
        RequestParams params = new RequestParams("access_token", access_token);

        get(AppConfig.MOBILE_ENDPOINT.concat("products"),params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                // Log.e("Response", responseBody.toString());
                callback.completed(responseBody);

                if(showProgressDialog)
                    progressBar.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if(showProgressDialog)
                    progressBar.dismiss();

                callback.failed(new ServiceException(throwable));
            }
        });
    }

    public void balance(String access_token){

        // Log.e("Access Token", access_token);
        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }
        RequestParams params = new RequestParams("access_token", access_token);

        get(AppConfig.MOBILE_ENDPOINT.concat("products/balance"),params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {
                // Log.e("Response", responseBody.toString());
                callback.completed(responseBody);

                if(showProgressDialog)
                    progressBar.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if(showProgressDialog)
                    progressBar.dismiss();

                callback.failed(new ServiceException(throwable));
            }
        });
    }

}
