package za.co.atpsa.atp.fragments.evs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import za.co.atpsa.atp.R;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Verification;

public class IDCheck extends Fragment implements View.OnClickListener, OnServiceResponseListener {

    View mView;
    TextView name, surname, id_number, res_name, res_surname, res_id_number, res_dec, res_dec_date,
            title_employers, title_addresses, title_telephones;
    Button search_btn;
    CardView results_panel;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private TableLayout employers_list, telephones_list, addresses_list;
    private LinearLayout employers,telephones,addresses;

    final static String RSA_ID  = "(?<Year>[0-9][0-9])(?<Month>([0][1-9])|([1][0-2]))(?<Day>([0-2][0-9])|([3][0-1]))(?<Gender>[0-9])(?<Series>[0-9]{3})(?<Citizenship>[0-9])(?<Uniform>[0-9])(?<Control>[0-9])";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.id_check, container, false);
        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        name = mView.findViewById(R.id.name);
        surname = mView.findViewById(R.id.surname);
        id_number = mView.findViewById(R.id.id_number);
        search_btn = mView.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);

        res_name  = mView.findViewById(R.id.res_name);
        res_surname  = mView.findViewById(R.id.res_surname);
        res_id_number = mView.findViewById(R.id.res_id_number);
        res_dec = mView.findViewById(R.id.res_dec);
        res_dec_date = mView.findViewById(R.id.res_dec_date);
        results_panel = mView.findViewById(R.id.results_panel);
        employers_list =  mView.findViewById(R.id.employers_list);
        employers = mView.findViewById(R.id.employers);
        title_employers = mView.findViewById(R.id.title_employers);

        telephones_list =  mView.findViewById(R.id.telephones_list);
        telephones = mView.findViewById(R.id.telephones);
        title_telephones = mView.findViewById(R.id.title_telephones);

        addresses_list =  mView.findViewById(R.id.addresses_list);
        addresses = mView.findViewById(R.id.addresses);
        title_addresses = mView.findViewById(R.id.title_addresses);


       // SharedPreferences p2 = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(spref.contains("id_number")
                && spref.contains("name")
                && spref.contains("surname")){

            name.setText(spref.getString("name",""));
            id_number.setText(spref.getString("id_number",""));
            surname.setText(spref.getString("surname",""));
            search_btn.callOnClick();


        }

        return mView;

    }

    @Override
    public void onClick(View v) {

        if(id_number.getText().toString().length() == 0){
            id_number.setError("Id number required");
        } else if( name.getText().toString().length() == 0){
            name.setError("Name required");
        } else if(surname.getText().length() == 0){
            surname.setError("Surname required");
        } else {
           Pattern pattern = Pattern.compile(RSA_ID);
            Matcher matcher = pattern.matcher(id_number.getText());
            if(matcher.matches()){

                JSONObject p = new JSONObject();
                try {
                    p.put("access_token", spref.getString("access_token",""));
                    p.put("name", name.getText());
                    p.put("surname", surname.getText());
                    p.put("id_number", id_number.getText());
                    new Verification(getActivity(), this, true).consumer_trace(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            } else {
                id_number.setError("Invalid Id Number");
            }
        }

    }

    @Override
    public void completed(Object object) {
        Log.e("Response", object.toString());
        results_panel.setVisibility(View.VISIBLE);


        try {
            JSONObject obj = new JSONObject( object.toString());
            res_surname.setText(obj.getString("surname"));
            res_name.setText(obj.optString("name"));
            res_id_number.setText(obj.optString("id_number"));
            res_dec.setText(obj.getJSONObject("deceased").optString("deceased_flag"));
            res_dec_date.setText(obj.getJSONObject("deceased").optString("deceased_date"));

            JSONArray employers_data = obj.optJSONArray("employers");
            JSONArray telephone_data = obj.optJSONArray("telephones");
            JSONArray addresses_data = obj.optJSONArray("addresses");

            if(employers_data != null) {
                employers.setVisibility(View.VISIBLE);
                title_employers.setText("Employers");

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 45
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;


                TableRow tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9, 9, 9, 9);
                tableRow.setBackgroundResource(R.drawable.title_bg);


                TextView textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3, 3, 3, 3);

                layoutParams = new TableRow.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Created");
                textView.setTextSize(12f);
                tableRow.addView(textView);


                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3, 3, 3, 3);

                layoutParams = new TableRow.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Name");
                textView.setTextSize(12f);
                tableRow.addView(textView);

                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3, 3, 3, 3);

                layoutParams = new TableRow.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Occupation");
                textView.setTextSize(12f);
                tableRow.addView(textView);


                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3, 3, 3, 3);

                layoutParams = new TableRow.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Date");
                textView.setTextSize(12f);
                tableRow.addView(textView);


                employers_list.addView(tableRow);

                for (int i = 0; i < employers_data.length(); i++) {
                    JSONObject emp = employers_data.optJSONObject(i);
                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(layoutParams);
                    tableRow.setPadding(9, 9, 9, 9);
                    // tableRow.setBackgroundResource(R.drawable.title_bg);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(emp.optString("date_created"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(emp.optString("employer_name"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView = new TextView(getActivity());
                    textView.setLayoutParams(layoutParams);
                    textView.setText(emp.optString("occupation"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(emp.optString("employer_created"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    employers_list.addView(tableRow);
                }
            }

            if(telephone_data != null){
                telephones.setVisibility(View.VISIBLE);
                title_telephones.setText("Telephones");

                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,  45
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;


                TableRow tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9,9,9,9);
                tableRow.setBackgroundResource(R.drawable.title_bg);


                TextView textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3,3,3,3);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity=Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Created");
                textView.setTextSize(12f);
                tableRow.addView(textView);


                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3,3,3,3);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity=Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Number");
                textView.setTextSize(12f);
                tableRow.addView(textView);

                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3,3,3,3);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity=Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Type");
                textView.setTextSize(12f);
                tableRow.addView(textView);





                textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                textView.setPadding(3,3,3,3);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                );
                layoutParams.gravity=Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText("Date");
                textView.setTextSize(12f);
                tableRow.addView(textView);


                telephones_list.addView(tableRow);

                for(int i = 0; i < telephone_data.length(); i++){
                    JSONObject tel = telephone_data.optJSONObject(i);
                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(layoutParams);
                    tableRow.setPadding(9,9,9,9);
                    // tableRow.setBackgroundResource(R.drawable.title_bg);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3,3,3,3);

                    layoutParams = new TableRow.LayoutParams(
                            0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(tel.optString("date_created"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3,3,3,3);

                    layoutParams = new TableRow.LayoutParams(
                            0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(tel.optString("telephone_number"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3,3,3,3);

                    layoutParams = new TableRow.LayoutParams(
                            0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;

                    textView = new TextView(getActivity());
                    textView.setLayoutParams(layoutParams);
                    textView.setText(tel.optString("telephone_type"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3,3,3,3);

                    layoutParams = new TableRow.LayoutParams(
                            0,    ViewGroup.LayoutParams.WRAP_CONTENT, .20f
                    );
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(tel.optString("telephone_created"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);
                    
                    telephones_list.addView(tableRow);
                }



            }
            if(addresses_data != null){
                addresses.setVisibility(View.VISIBLE);
                title_addresses.setText("Addresses");
                for(int i = 0; i < addresses_data.length(); i++){
                    JSONObject addr = addresses_data.optJSONObject(i);

                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 45
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;


                    TableRow tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(layoutParams);
                    tableRow.setPadding(9, 9, 9, 9);
                    tableRow.setBackgroundResource(R.drawable.title_bg);


                    TextView textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(addr.optString("address_type"));
                    textView.setTextSize(12f);

                    tableRow.addView(textView);


                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorWhiteClear));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(addr.optString("address_created"));
                    textView.setPadding(12,0,0,0);
                    textView.setTextSize(12f);
                    tableRow.addView(textView);





                    addresses_list.addView(tableRow);


                    layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;



                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(layoutParams);
                    tableRow.setPadding(9, 9, 9, 9);
                    //tableRow.setBackgroundResource(R.drawable.title_bg);

                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3,3,3,3);

                    layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;

                    textView.setLayoutParams(layoutParams);
                    textView.setText(addr.optString("line1"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);
                    addresses_list.addView(tableRow);


                    if(!TextUtils.isEmpty(addr.optString("line2"))){
                        tableRow = new TableRow(getActivity());
                        tableRow.setLayoutParams(layoutParams);
                        tableRow.setPadding(9, 9, 9, 9);
                        //tableRow.setBackgroundResource(R.drawable.title_bg);

                        textView = new TextView(getActivity());
                        textView.setTextColor(getResources().getColor(R.color.colorBlack));

                        textView.setPadding(3,3,3,3);

                        layoutParams = new TableRow.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity=Gravity.CENTER_VERTICAL;

                        textView.setLayoutParams(layoutParams);
                        textView.setText(addr.optString("line2"));
                        textView.setTextSize(12f);
                        tableRow.addView(textView);
                        addresses_list.addView(tableRow);
                    }

                    if(!TextUtils.isEmpty(addr.optString("line3"))){
                        tableRow = new TableRow(getActivity());
                        tableRow.setLayoutParams(layoutParams);
                        tableRow.setPadding(9, 9, 9, 9);
                        //tableRow.setBackgroundResource(R.drawable.title_bg);

                        textView = new TextView(getActivity());
                        textView.setTextColor(getResources().getColor(R.color.colorBlack));

                        textView.setPadding(3,3,3,3);

                        layoutParams = new TableRow.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity=Gravity.CENTER_VERTICAL;

                        textView.setLayoutParams(layoutParams);
                        textView.setText(addr.optString("line3"));
                        textView.setTextSize(12f);
                        tableRow.addView(textView);
                        addresses_list.addView(tableRow);
                    }

                    if(!TextUtils.isEmpty(addr.optString("line4"))){
                        tableRow = new TableRow(getActivity());
                        tableRow.setLayoutParams(layoutParams);
                        tableRow.setPadding(9, 9, 9, 9);
                        //tableRow.setBackgroundResource(R.drawable.title_bg);

                        textView = new TextView(getActivity());
                        textView.setTextColor(getResources().getColor(R.color.colorBlack));

                        textView.setPadding(3,3,3,3);

                        layoutParams = new TableRow.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity=Gravity.CENTER_VERTICAL;

                        textView.setLayoutParams(layoutParams);
                        textView.setText(addr.optString("line4"));
                        textView.setTextSize(12f);
                        tableRow.addView(textView);
                        addresses_list.addView(tableRow);
                    }


                    tableRow = new TableRow(getActivity());
                    tableRow.setLayoutParams(layoutParams);
                    tableRow.setPadding(9, 9, 9, 9);

                    textView = new TextView(getActivity());
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));

                    textView.setPadding(3, 3, 3, 3);

                    layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;


                    textView = new TextView(getActivity());
                    textView.setLayoutParams(layoutParams);
                    textView.setText(addr.optString("postal_code"));
                    textView.setTextSize(12f);
                    tableRow.addView(textView);

                    addresses_list.addView(tableRow);

                }
                
                
            }



        } catch (JSONException e) {
           // e.printStackTrace();
        }
       // editor.remove("name").remove("id_number").remove("surname").commit();

      //  PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("name").remove("id_number").remove("surname").commit();
    }

    @Override
    public void failed(ServiceException e) {
        e.printStackTrace();
    }


}
