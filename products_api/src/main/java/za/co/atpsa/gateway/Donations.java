package za.co.atpsa.gateway;

import android.content.Context;

import org.json.JSONObject;

import za.co.atpsa.common.OnServiceResponseListener;

public class Donations extends GatewayApi {

    public enum Module {DONATIONS, PAYMENTS};

    public Donations(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);
    }

    public void send_request(final JSONObject post, Module module){
        execute_post(module.equals(Module.DONATIONS) ? "donations":"repeat_payments", post);
    }
}
