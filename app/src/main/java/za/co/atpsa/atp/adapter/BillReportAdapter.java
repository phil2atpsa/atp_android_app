package za.co.atpsa.atp.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.BillReportItem;
import za.co.atpsa.atp.entities.CreditHistory;
import za.co.atpsa.atp.fragments.Report;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Credits;


public class BillReportAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<BillReportItem> items = new ArrayList<>();
    ArrayList<BillReportItem> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public BillReportAdapter(Context context, List<BillReportItem> items){

        this.context = context;
        this.items = items;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(items);

        items.clear();
        for(BillReportItem item : temp){
            if(item.getDate().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getRef().toLowerCase().contains(terms.toLowerCase())  ||
                    item.getName().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getStatus().toLowerCase().contains(terms.toLowerCase())  ||
                    String.valueOf(item.getAmount()).toLowerCase().contains(terms.toLowerCase()) ){
                if(!items.contains(item)) {
                    items.add(item);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(BillReportItem item){
        items.add(item);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public BillReportItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        BillReportItem billReportItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.bills_report_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population


        TextView date = view.findViewById(R.id.date);
        TextView ref = view.findViewById(R.id.ref);
        TextView name =  view.findViewById(R.id.name);

     //   TextView email =  view.findViewById(R.id.email);
       // TextView cell_no =  view.findViewById(R.id.cell_no);
        //TextView description =  view.findViewById(R.id.description);
        TextView amount =  view.findViewById(R.id.amount);

        ImageView status = view.findViewById(R.id.status);

        // Populate the data into the template view using the data object

        ref.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        name.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        amount.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));

        ref.setText(billReportItem.getRef());
        name.setText(billReportItem.getName());
        date.setText(billReportItem.getDate());
        amount.setText(String.valueOf(billReportItem.getAmount()));

        if(billReportItem.getStatus().toLowerCase().equals("success")){
            status.setImageResource(R.drawable.ic_check);
        } else if(billReportItem.getStatus().toLowerCase().equals("pending")){
            status.setImageResource(R.drawable.ic_pending);
        } else if(billReportItem.getStatus().toLowerCase().equals("failed")){
            status.setImageResource(R.drawable.ic_payment_failed_white);

        }


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new za.co.atpsa.products.Report(context, this, showProgressDialog).bills(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        items.clear();
        for(int i = 0; i < ((JSONArray) object).length(); i++){

                JSONObject jsonObject = ((JSONArray) object).optJSONObject(i);
                BillReportItem item = new BillReportItem();
                item.setAmount(jsonObject.optDouble("amount"));
                item.setCell_no(jsonObject.optString("cell_no"));
                item.setDescription(jsonObject.optString("description"));
                item.setDate(jsonObject.optString("date"));
                item.setEmail(jsonObject.optString("email"));
                item.setRef(jsonObject.optString("our_ref"));
                item.setName(jsonObject.optString("name"));
                item.setStatus(jsonObject.optString("status"));
                add(item);

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
