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

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.CreditHistory;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Credits;


public class CreditHistoryAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<CreditHistory> creditHistories = new ArrayList<>();
    ArrayList<CreditHistory> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public CreditHistoryAdapter(Context context, List<CreditHistory> creditHistories){

        this.context = context;
        this.creditHistories = creditHistories;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(creditHistories);
        creditHistories.clear();
        for(CreditHistory history : temp){
            if(history.getDescription().toLowerCase().contains(terms.toLowerCase()) ||
            history.getProduct_name().toLowerCase().contains(terms.toLowerCase())  ||
                    history.getDate().toLowerCase().contains(terms.toLowerCase())){
                if(!creditHistories.contains(history)) {
                    creditHistories.add(history);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(CreditHistory creditHistory){
        creditHistories.add(creditHistory);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return creditHistories.size();
    }

    @Override
    public CreditHistory getItem(int position) {
        return creditHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        CreditHistory creditHistory = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.credits_history_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population

        TextView date = view.findViewById(R.id.date);
        TextView product_name =  view.findViewById(R.id.product_name);

        TextView description =  view.findViewById(R.id.description);
        TextView credits =  view.findViewById(R.id.credits);

        // Populate the data into the template view using the data object

        date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        product_name.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        description.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        credits.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));

        date.setText(creditHistory.getDate());
        product_name.setText(creditHistory.getProduct_name());
        description.setText(creditHistory.getDescription());
        credits.setText(String.valueOf(creditHistory.getCredits()));


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new Credits(context, this, showProgressDialog).history(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        creditHistories.clear();

        JSONArray array = (JSONArray)object;
        for(int i =0; i < array.length(); i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                Log.e("JSON", jsonObject.toString());

                CreditHistory creditHistory = new CreditHistory();
                creditHistory.setCredits(jsonObject.optInt("credits"));
                creditHistory.setDate(jsonObject.optString("date"));
                creditHistory.setProduct_name(jsonObject.optString("product_name"));
                creditHistory.setDescription(jsonObject.optString("description"));
                add(creditHistory);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
