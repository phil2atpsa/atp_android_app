package za.co.atpsa.atp.fragments.evs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.TelephoneResultsAdapter;
import za.co.atpsa.atp.adapter.listeners.TelephoneResultsListener;
import za.co.atpsa.atp.fragments.Evs;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Verification;

public class TelephoneCheck extends Fragment implements View.OnClickListener, OnServiceResponseListener, TelephoneResultsListener {
    View mMainView;
    TextView telephone_number;
    Button search_btn;
    final static String RSA_CELL_MATCH = "0((60[3-9]|61[3-9]|62[0-5]|64[0-5]|63[0-5]|65[0-5]|66[0-5])\\d{6}|(7[1-4689]|6[1-3]|8[1-4])\\d{7})";
    String response = null;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    TelephoneResultsAdapter adapter;
    CardView results_panel;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    LinearLayout parent;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        mMainView = inflater.inflate(R.layout.telephone_check, container, false);
        telephone_number =  mMainView.findViewById(R.id.telephone_number);
        search_btn = mMainView.findViewById(R.id.search_btn);

        search_btn.setOnClickListener( this);
        recyclerView = mMainView.findViewById(R.id.recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new TelephoneResultsAdapter(new JSONArray());
        adapter.setTelephoneResultsListener(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        results_panel = mMainView.findViewById(R.id.results_panel);
        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();
        parent = mMainView.findViewById(R.id.parent);












        return mMainView;
    }

    @Override
    public void onClick(View v) {
        if(telephone_number.getText().toString().length() == 0){
            telephone_number.setError("Telephone number required");
        } else {
            Pattern pattern = Pattern.compile(RSA_CELL_MATCH);
            Matcher matcher = pattern.matcher(telephone_number.getText());
            if(matcher.matches()) {
                JSONObject p = new JSONObject();
                try {
                    p.put("access_token", spref.getString("access_token",""));
                    p.put("telephone_number", telephone_number.getText());
                    new Verification(getActivity(), this, true).telephone_trace(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                telephone_number.setError("Invalid Cellphone format");
            }
        }
    }

    @Override
    public void completed(Object object) {
        JSONObject jsonObject = (JSONObject)object;
        String error =  jsonObject.optString("error");

        if(TextUtils.isEmpty(error)) {

            try {
                // JSONObject jsonObject = new JSONObject(response);


                Log.e("array", jsonObject.getJSONArray("results").toString());
                Log.e("count", "" + jsonObject.getJSONArray("results").length());
                JSONArray results = jsonObject.getJSONArray("results");
                results_panel.setVisibility(results.length() > 0 ? View.VISIBLE : View.GONE);
                for (int i = 0; i < results.length(); i++) {
                    adapter.add(results.getJSONObject(i));

                }

            } catch (JSONException e) {
                results_panel.setVisibility(View.GONE);

            }
        } else {
            Snackbar snackbar = Snackbar.make(parent, error, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
            } else {
                sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
            }

            snackbar.show();
        }

    }

    @Override
    public void failed(ServiceException e) {
        Snackbar snackbar = Snackbar.make(parent,e.getMessage(), Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
        } else {
            sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
        }

        snackbar.show();
    }


    @Override
    public void onSearchRequested(String id_number, String name, String surname) {


        editor.putString("id_number",id_number)
        .putString("name", name)
        .putString("surname", surname).commit();

        Evs evs =  (Evs) TelephoneCheck.this.getParentFragment();
        evs.selectPage(1);

    }
}
