package za.co.atpsa.atp.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.CreditHistory;
import za.co.atpsa.atp.entities.CreditTransaction;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Credits;


public class CreditTransactionAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<CreditTransaction> creditTransactions = new ArrayList<>();
    ArrayList<CreditTransaction> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public CreditTransactionAdapter(Context context, List<CreditTransaction> creditTransactions){

        this.context = context;
        this.creditTransactions = creditTransactions;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(creditTransactions);
        creditTransactions.clear();
        for(CreditTransaction transaction : temp){
            if(transaction.getRef().toLowerCase().contains(terms.toLowerCase()) ||
                    transaction.getCurrency().toLowerCase().contains(terms.toLowerCase())  ||
                    transaction.getPayment_date().toLowerCase().contains(terms.toLowerCase()) ||
                    transaction.getRequest_date().toLowerCase().contains(terms.toLowerCase()) ||
                    transaction.getStatus().toLowerCase().contains(terms.toLowerCase())){
                if(!creditTransactions.contains(transaction)) {
                    creditTransactions.add(transaction);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(CreditTransaction creditTransaction){
        creditTransactions.add(creditTransaction);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return creditTransactions.size();
    }

    @Override
    public CreditTransaction getItem(int position) {
        return creditTransactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        CreditTransaction creditTransaction = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.credit_transaction_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population

    //    TextView request_date = view.findViewById(R.id.request_date);
        TextView payment_date =  view.findViewById(R.id.payment_date);

        TextView atp_ref =  view.findViewById(R.id.atp_ref);
        TextView amount =  view.findViewById(R.id.amount);
      //  TextView currency =  view.findViewById(R.id.currency);
        TextView status =  view.findViewById(R.id.status);
       // TextView outcome =  view.findViewById(R.id.outcome);




        // Populate the data into the template view using the data object

        atp_ref.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        amount.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        payment_date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
      /*  request_date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        currency.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));*/
        status.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
      /*  outcome.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));*/

        atp_ref.setText(creditTransaction.getRef());
        Double d = creditTransaction.getAmount();
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        amount.setText(decimalFormat.format(d));
        payment_date.setText(creditTransaction.getPayment_date());
        //request_date.setText(creditTransaction.getRequest_date());
        //currency.setText(creditTransaction.getCurrency());
        status.setText(creditTransaction.getStatus());
        //outcome.setText(creditTransaction.getOutcome());


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new Credits(context, this, showProgressDialog).transanctions(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        creditTransactions.clear();

        JSONArray array = (JSONArray)object;
        for(int i =0; i < array.length(); i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                Log.e("JSON", jsonObject.toString());

                CreditTransaction creditTransaction = new CreditTransaction();
                creditTransaction.setAmount(jsonObject.optDouble("amount"));
                creditTransaction.setCurrency(jsonObject.optString("currency"));
                creditTransaction.setOutcome(jsonObject.optString("outcome"));
                creditTransaction.setPayment_date(jsonObject.optString("pay_date"));
                creditTransaction.setRequest_date(jsonObject.optString("request_date"));
                creditTransaction.setStatus(jsonObject.optString("status"));
                creditTransaction.setRef(jsonObject.optString("our_ref"));
                add(creditTransaction);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
