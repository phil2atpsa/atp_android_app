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
import za.co.atpsa.atp.entities.EvsReport;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;


public class EvsReportAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<EvsReport> items = new ArrayList<>();
    ArrayList<EvsReport> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public EvsReportAdapter(Context context, List<EvsReport> items){

        this.context = context;
        this.items = items;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(items);

        items.clear();
        for(EvsReport item : temp){
            if(item.getCreated_at().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getName().toLowerCase().contains(terms.toLowerCase())  ||
                    item.getOrigin_ip().toLowerCase().contains(terms.toLowerCase()) ||
                    item.getType().toLowerCase().contains(terms.toLowerCase())){
                if(!items.contains(item)) {
                    items.add(item);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(EvsReport item){
        items.add(item);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public EvsReport getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        EvsReport item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.evs_report_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population


        TextView name = view.findViewById(R.id.name);
        TextView type = view.findViewById(R.id.type);
        TextView date =  view.findViewById(R.id.date);

     //   TextView email =  view.findViewById(R.id.email);
       // TextView cell_no =  view.findViewById(R.id.cell_no);
        //TextView description =  view.findViewById(R.id.description);
        TextView ip =  view.findViewById(R.id.ip);



        // Populate the data into the template view using the data object

        type.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        name.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        ip.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));

        type.setText(item.getType());
        name.setText(item.getName());
        date.setText(item.getCreated_at());
        ip.setText(item.getOrigin_ip());




        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new za.co.atpsa.products.Report(context, this, showProgressDialog).evs(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        items.clear();
        for(int i = 0; i < ((JSONArray) object).length(); i++){

            JSONObject jsonObject = ((JSONArray) object).optJSONObject(i);
            EvsReport item = new EvsReport();
            item.setCreated_at(jsonObject.optString("created_at"));
            item.setName(jsonObject.optString("name"));
            item.setOrigin_ip(jsonObject.optString("origin_ip"));
            item.setType(jsonObject.optString("type"));
            add(item);

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
