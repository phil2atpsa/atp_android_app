package za.co.atpsa.products;

import android.content.Context;

import za.co.atpsa.common.OnServiceResponseListener;

public class Report extends ProductApi  {

    public Report(Context context, OnServiceResponseListener callback, boolean showProgressDialog){
        super(context,callback,showProgressDialog);
    }

    public void bills(String access_token, int days){
        report(access_token,"report/bills",days);
    }
    public void bill_status(String access_token, int days){
        report(access_token,"report/bills/status",days);
    }
    public void collections(String access_token, int days){
        report(access_token,"report/collections",days);
    }
    public void payments(String access_token, int days){
        report(access_token,"report/payments",days);
    }
    public void evs(String access_token, int days){
        report(access_token,"report/evs",days);
    }

}
