package za.co.atpsa.sp_manager;

import android.content.Context;

import org.json.JSONObject;

import za.co.atpsa.common.OnServiceResponseListener;

public class Paygate extends ServiceProviderManagerApi  {

    public Paygate(Context context, OnServiceResponseListener callback, boolean showProgressDialog) {
        super(context, callback, showProgressDialog);
    }


    public void initiate(JSONObject params){
        execute_post("paygate/initiate", params);
    }

    public void outcome(JSONObject params){
        execute_post("paygate/outcome", params);

    }



}
