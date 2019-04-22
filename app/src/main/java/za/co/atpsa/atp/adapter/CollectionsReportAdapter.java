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
import za.co.atpsa.atp.entities.BillReportItem;
import za.co.atpsa.atp.entities.Collections;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;


public class CollectionsReportAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<Collections> collections = new ArrayList<>();
    ArrayList<Collections> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public CollectionsReportAdapter(Context context, List<Collections> collections){

        this.context = context;
        this.collections = collections;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(collections);

        collections.clear();
        for(Collections item : temp){
            if(item.getDate().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(terms.toLowerCase())  ||
                    String.valueOf(item.getAmount()).toLowerCase().contains(terms.toLowerCase()) ){
                if(!collections.contains(item)) {
                    collections.add(item);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(Collections item){
        collections.add(item);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return collections.size();
    }

    @Override
    public Collections getItem(int position) {
        return collections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        Collections item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.collections_report_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population


        TextView date = view.findViewById(R.id.date);
        TextView amount = view.findViewById(R.id.amount);
        TextView name =  view.findViewById(R.id.name);

     //   TextView email =  view.findViewById(R.id.email);
       // TextView cell_no =  view.findViewById(R.id.cell_no);
        //TextView description =  view.findViewById(R.id.description);
        TextView description =  view.findViewById(R.id.description);

        ImageView processed = view.findViewById(R.id.processed);

        // Populate the data into the template view using the data object

        description.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        name.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        amount.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));

        description.setText(item.getDescription());
        name.setText(item.getName());
        date.setText(item.getDate());
        amount.setText(String.valueOf(item.getAmount()));

        if(item.getProcessed() == 1){
            processed.setImageResource(R.drawable.ic_check_green_24dp);
        } else{
            processed.setImageResource(R.drawable.ic_clear_red_24dp);
        }


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new za.co.atpsa.products.Report(context, this, showProgressDialog).collections(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        collections.clear();
        for(int i = 0; i < ((JSONArray) object).length(); i++){

                JSONObject jsonObject = ((JSONArray) object).optJSONObject(i);
                Collections item = new Collections();
                item.setAmount(jsonObject.optDouble("amount"));
                item.setDate(jsonObject.optString("created_at"));
                item.setDescription(jsonObject.optString("description"));
                item.setName(jsonObject.optString("name"));
                item.setProcessed(jsonObject.optInt("processed"));
                item.setNumber_of_payments(jsonObject.optInt("frequency"));
                add(item);

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
