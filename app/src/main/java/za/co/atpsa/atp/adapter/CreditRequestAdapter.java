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
import za.co.atpsa.atp.entities.CreditRequest;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Credits;


public class CreditRequestAdapter extends BaseAdapter implements OnServiceResponseListener {

    List<CreditRequest> creditRequests = new ArrayList<>();
    ArrayList<CreditRequest> temp = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;


    public CreditRequestAdapter(Context context, List<CreditRequest> creditRequests){

        this.context = context;
        this.creditRequests = creditRequests;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }

    public void search(String terms){
        temp.addAll(creditRequests);
        creditRequests.clear();
        for(CreditRequest request : temp){
            if(request.getRef().toLowerCase().contains(terms.toLowerCase()) ||
                    request.getProduct_name().toLowerCase().contains(terms.toLowerCase())  ||
                    request.getDate().toLowerCase().contains(terms.toLowerCase())  ||
                    request.getUser().toLowerCase().contains(terms.toLowerCase())){
                if(!creditRequests.contains(request)) {
                    creditRequests.add(request);
                    notifyDataSetChanged();
                }
            }
        }


    }



    public void add(CreditRequest creditRequest){
        creditRequests.add(creditRequest);
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return creditRequests.size();
    }

    @Override
    public CreditRequest getItem(int position) {
        return creditRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        CreditRequest creditRequest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.credits_request_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population

        TextView date = view.findViewById(R.id.date);
        TextView product_name =  view.findViewById(R.id.product_name);
        TextView atp_ref =  view.findViewById(R.id.atp_ref);
        TextView user =  view.findViewById(R.id.user);
        TextView credits =  view.findViewById(R.id.credits);

        // Populate the data into the template view using the data object

        date.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        product_name.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        user.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        atp_ref.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));
        credits.setTextColor(position %2 == 0 ? context.getResources().getColor(R.color.colorWhite) :
                context.getResources().getColor(R.color.colorGray));

        date.setText(creditRequest.getDate());
        product_name.setText(creditRequest.getProduct_name());
        atp_ref.setText(creditRequest.getRef());
        user.setText(creditRequest.getUser());
        credits.setText(String.valueOf(creditRequest.getCredits()));


        convertView.setBackgroundColor(position %2 == 0 ? context.getResources().getColor(R.color.colorGray) :context.getResources().getColor(R.color.colorWhite));


        return convertView;
    }

    public void refresh(boolean showProgressDialog, int days){
        new Credits(context, this, showProgressDialog).request(spref.getString("access_token", ""), days);
    }

    @Override
    public void completed(Object object) {
        creditRequests.clear();

        JSONArray array = (JSONArray)object;
        for(int i =0; i < array.length(); i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                Log.e("JSON", jsonObject.toString());

                CreditRequest creditRequest = new CreditRequest();
                creditRequest.setCredits(jsonObject.optInt("credits"));
                creditRequest.setDate(jsonObject.optString("date"));
                creditRequest.setProduct_name(jsonObject.optString("product_name"));
                creditRequest.setUser(jsonObject.optString("name"));
                creditRequest.setRef(jsonObject.optString("our_ref"));
                add(creditRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void failed(ServiceException e) {

    }
}
