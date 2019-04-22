package za.co.atpsa.gateway;

import android.content.Context;

import org.json.JSONObject;

import za.co.atpsa.common.OnServiceResponseListener;

public class Bills extends GatewayApi {

    public Bills(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);


    }

    public void send_bill(final JSONObject post) {
        execute_post("bills", post);
    }
}
