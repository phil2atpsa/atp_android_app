package za.co.atpsa.sp_manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.common.Api;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.ui.TransparentProgressDialog;

public abstract  class ServiceProviderManagerApi extends AsyncHttpClient implements Api {

    protected OnServiceResponseListener<Object> callback;
    protected Context context;
    protected ProgressDialog progressBar;
    protected boolean  showProgressDialog;



    public ServiceProviderManagerApi(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        this.context = context;

        this.showProgressDialog = showProgressDialog;
        this.callback = callback;

        addHeader("Content-Type", "application/json");
        addHeader("Accept", "application/json");
        addHeader("Authorization", "Bearer "+ AppConfig.SP_MANAGER_TOKEN);

    }
    @Override
    public void execute_post(String module, JSONObject params) {
        if(showProgressDialog) {
            progressBar = TransparentProgressDialog.createProgressDialog(context);
        }
        StringEntity entity = new StringEntity(params.toString(), "UTF-8");
        //Log.e("Update",  AppConfig.SP_MANAGER_ENDPOINT.concat(module));

        post(context, AppConfig.SP_MANAGER_ENDPOINT.concat(module),entity,"application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

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
