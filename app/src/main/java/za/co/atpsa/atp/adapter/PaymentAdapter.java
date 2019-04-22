package za.co.atpsa.atp.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.Payment;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;


public class PaymentAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<Payment> payments = new ArrayList<>();
    ArrayList<Payment> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public PaymentAdapter(Context context, List<Payment> payments){

        this.context = context;
        this.payments = payments;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(payments);

        payments.clear();
        for(Payment item : temp){
            if(item.getBill_date().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getRef().toLowerCase().contains(terms.toLowerCase())  ||
                    item.getDescription().toLowerCase().contains(terms.toLowerCase())  ||
                    item.getPayment_date().toLowerCase().contains(terms.toLowerCase())  ||
                    item.getName().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getStatus().toLowerCase().contains(terms.toLowerCase())  ||
                    String.valueOf(item.getAmount()).toLowerCase().contains(terms.toLowerCase()) ){
                if(!payments.contains(item)) {
                    payments.add(item);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(Payment payment){
        payments.add(payment);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return payments.size();
    }

    @Override
    public Payment getItem(int position) {
        return payments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        Payment payment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.payments_report_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population


        TextView bill_date = view.findViewById(R.id.bill_date);
        TextView payment_date = view.findViewById(R.id.payment_date);
        TextView description =  view.findViewById(R.id.description);

     //   TextView email =  view.findViewById(R.id.email);
       // TextView cell_no =  view.findViewById(R.id.cell_no);
        //TextView description =  view.findViewById(R.id.description);
        TextView amount =  view.findViewById(R.id.amount);

        ImageView status = view.findViewById(R.id.status);

        // Populate the data into the template view using the data object

        bill_date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        payment_date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        description.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        amount.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));


        bill_date.setText(payment.getBill_date());
        payment_date.setText(payment.getPayment_date());
        description.setText(payment.getDescription());
        amount.setText(String.valueOf(payment.getAmount()));

        status.setImageResource(R.drawable.ic_check_green_24dp);


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new za.co.atpsa.products.Report(context, this, showProgressDialog).payments(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        payments.clear();
        for(int i = 0; i < ((JSONArray) object).length(); i++){

                JSONObject jsonObject = ((JSONArray) object).optJSONObject(i);
                Payment payment = new Payment();

                payment.setAmount(jsonObject.optDouble("amount"));
                payment.setDescription(jsonObject.optString("description"));
                payment.setBill_date(jsonObject.optString("bill_date"));
                payment.setPayment_date(jsonObject.optString("payment_date"));
                payment.setRef(jsonObject.optString("our_ref"));
                payment.setName(jsonObject.optString("name"));
                payment.setStatus(jsonObject.optString("status"));
                add(payment);

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
