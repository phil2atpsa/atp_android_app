package za.co.atpsa.products;

import android.content.Context;
import org.json.JSONObject;
import za.co.atpsa.common.OnServiceResponseListener;

public class Verification extends ProductApi  {
    public Verification(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);
    }

    public void telephone_trace(JSONObject params){
        execute_post("evs/telephone_trace", params);
    }
    public void consumer_trace(JSONObject params){
        execute_post("evs/consumer_trace", params);
    }
}
