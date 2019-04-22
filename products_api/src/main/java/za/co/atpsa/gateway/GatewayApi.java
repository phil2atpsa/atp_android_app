package za.co.atpsa.gateway;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import za.co.atpsa.common.Api;
import za.co.atpsa.common.AppConfig;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Access;
import za.co.atpsa.ui.TransparentProgressDialog;

public abstract  class GatewayApi extends AsyncHttpClient  implements Api {
    public static final String PREFERENCE= "ATP";

    protected OnServiceResponseListener<Object> callback;
    protected Context context;
    protected ProgressDialog progressBar;
    protected boolean  showProgressDialog;

    public GatewayApi(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        this.context = context;
        this.showProgressDialog = showProgressDialog;
        this.callback = callback;

        addHeader("Content-Type", "application/json");
        addHeader("Accept", "application/json");
        addHeader("api-key", AppConfig.GATEWAY_API_KEY);
    }


    public  void execute_post(final String module, final JSONObject params){
        SharedPreferences spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        new Access(context, new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONObject jsonObject = (JSONObject) object;


                setBasicAuth(jsonObject.optJSONObject("sp").optString("webservice_username"),
                        jsonObject.optJSONObject("sp").optString("webservice_password"));
                StringEntity entity = null;
                try {
                    entity = new StringEntity(params.toString());



                    if(showProgressDialog) {
                        progressBar = TransparentProgressDialog.createProgressDialog(context);
                    }
                    post(context, AppConfig.GATEWAY_ENDPOINT.concat(module),entity,"application/json", new JsonHttpResponseHandler(){
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(ServiceException e) {
                Log.e("Request Failed", e.getMessage());

            }
        }, false).authorize(spref.getString("access_token", ""));
    }


}
