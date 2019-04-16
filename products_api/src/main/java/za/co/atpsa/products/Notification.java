package za.co.atpsa.products;

import android.content.Context;

import org.json.JSONObject;

import za.co.atpsa.common.OnServiceResponseListener;

public class Notification extends ProductApi {

    public Notification(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);
    }

    public void message(JSONObject params){
        execute_post("feedback", params);
    }
}
