package za.co.atpsa.sp_manager;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.ui.TransparentProgressDialog;

public class ServiceProvider   extends ServiceProviderManagerApi  {

    public ServiceProvider(Context context, OnServiceResponseListener callback, boolean showProgressDialog) {
        super(context, callback, showProgressDialog);
    }


    public void settings(int sp_id){

        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }
        //Log.e("settings", AppConfig.SP_MANAGER_ENDPOINT.concat("service_providers/settings/"+sp_id));

        get(AppConfig.SP_MANAGER_ENDPOINT.concat("service_providers/settings/"+sp_id), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
              //  Log.e("settings",responseBody.toString());


                if(showProgressDialog)
                    progressBar.dismiss();



                callback.completed(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,  JSONObject jsonObject){

                Log.e("Error", jsonObject.toString());
                if(showProgressDialog)
                    progressBar.dismiss();

                callback.failed(new ServiceException(throwable));
            }


        });
    }


}
