package za.co.atpsa.atp.fragments.credit_balances;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.resources.TextAppearance;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import za.co.atpsa.atp.CreditCard;
import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.CreditBalanceAdapter;
import za.co.atpsa.atp.entities.ProductBalance;

import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Credits;
import za.co.atpsa.products.Products;

public class Balance extends Fragment implements  View.OnClickListener, OnServiceResponseListener {

    public static final String PREFERENCE= "ATP";
    ListView listView;
    CreditBalanceAdapter creditBalanceAdapter;
    List<ProductBalance> productBalances = new ArrayList<>();
    TableLayout shopping_cart;
    Button btn_purchase;
    double total_amount = 0;
    LinearLayout parent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.balance, container, false);

        listView =  mView.findViewById(R.id.list_view);
        shopping_cart = mView.findViewById(R.id.shopping_cart);
        btn_purchase = mView.findViewById(R.id.btn_purchase);
        creditBalanceAdapter = new CreditBalanceAdapter(getActivity(), productBalances);
        parent = mView.findViewById(R.id.parent);
        listView.setAdapter(creditBalanceAdapter);
        View header = inflater.inflate(R.layout.credit_balances_header, listView, false);
        listView.addHeaderView(header);

        fill();
        btn_purchase.setOnClickListener(this);
        return mView;
    }


    private void fill(){
        SharedPreferences  spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        new Products(getActivity(), new OnServiceResponseListener() {
            @Override
            public void completed(Object object) {
                JSONArray array = (JSONArray)object;
                creditBalanceAdapter.clear();
                for(int i =0; i < array.length(); i++){
                    try {
                        JSONObject jsonObject = array.getJSONObject(i);
                       // Log.e("JSON", jsonObject.toString());

                        ProductBalance productBalance = new ProductBalance();
                        productBalance.setBalance(jsonObject.optInt("balance"));
                        productBalance.setProduct_name(jsonObject.optString("product_name"));
                        productBalance.setDescription(jsonObject.optString("description"));
                        productBalance.setIcon(jsonObject.optString("icon"));
                        productBalance.setCredit_price(jsonObject.optDouble("credit_price"));
                        productBalance.setProduct_id(jsonObject.optInt("product_id"));
                        creditBalanceAdapter.addProductBalances(productBalance);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                refreshed();
            }

            @Override
            public void failed(ServiceException e) {

            }
        }, true).balance(spref.getString("access_token", ""));
    }




    public void refreshed() {
        int ct = creditBalanceAdapter.getCount();
        for( int i = 0; i < ct; i++){
            try {
                final ProductBalance productBalance = creditBalanceAdapter.getItem(i);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,  45
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL;


                TableRow tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9,9,9,9);

                tableRow.setBackgroundColor( i % 2 == 0 ? getActivity().getResources().getColor(R.color.colorAccent) :
                        getActivity().getResources().getColor(R.color.colorWhite));


                TextView textView = new TextView(getActivity());
                textView.setTextColor( i % 2 == 0 ? getActivity().getResources().getColor(R.color.colorWhite) :
                        getActivity().getResources().getColor(R.color.colorAccent));

                textView.setPadding(12,12,12,12);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .55f
                );
                layoutParams.gravity=Gravity.CENTER_VERTICAL;

                textView.setLayoutParams(layoutParams);
                textView.setText(productBalance.getProduct_name());
                textView.setTextSize(14f);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .15f
                );

                layoutParams.gravity=Gravity.CENTER;

                EditText editText = new EditText(getActivity());
                editText.setLayoutParams(layoutParams);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);


                editText.setTextColor( i % 2 == 0 ? getActivity().getResources().getColor(R.color.colorWhite) :
                        getActivity().getResources().getColor(R.color.colorAccent));





                tableRow.addView(textView);
                tableRow.addView(editText);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .15f
                );
                layoutParams.gravity=Gravity.CENTER;

                textView = new TextView(getActivity());
                textView.setTextColor( i % 2 == 0 ? getActivity().getResources().getColor(R.color.colorWhite) :
                        getActivity().getResources().getColor(R.color.colorAccent));

                textView.setPadding(12,12,12,12);
                textView.setLayoutParams(layoutParams);
                textView.setText(String.valueOf(productBalance.getCredit_price()));
                textView.setTextSize(14f);

                tableRow.addView(textView);

                layoutParams = new TableRow.LayoutParams(
                        0,    ViewGroup.LayoutParams.WRAP_CONTENT, .15f
                );


                layoutParams.gravity=Gravity.CENTER;

                final TextView line_total = new TextView(getActivity());
                line_total.setLayoutParams(layoutParams);



                line_total.setTextColor( i % 2 == 0 ? getActivity().getResources().getColor(R.color.colorWhite) :
                        getActivity().getResources().getColor(R.color.colorAccent));

                tableRow.addView(line_total);


                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            int qty = Integer.valueOf(s.toString());
                            double price = productBalance.getCredit_price();
                            double total = qty * price;

                            line_total.setText(String.valueOf(total));
                            calculate_total();
                        } catch(NumberFormatException e){
                            int qty = 0;
                            double price = productBalance.getCredit_price();
                            double total = qty * price;

                            line_total.setText(String.valueOf(total));
                            calculate_total();
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                shopping_cart.addView(tableRow);




            } catch(NullPointerException e){}



        }
        try {
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 45
            );
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(layoutParams);
            tableRow.setPadding(9, 9, 9, 9);

            tableRow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorError));

            layoutParams = new TableRow.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, .85f
            );
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            TextView textView = new TextView(getActivity());
            textView.setPadding(12, 12, 12, 12);
            textView.setLayoutParams(layoutParams);
            textView.setText("TOTAL");
            textView.setTextSize(18f);
            textView.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            tableRow.addView(textView);


            layoutParams = new TableRow.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, .15f
            );
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            textView = new TextView(getActivity());
            textView.setPadding(12, 12, 12, 12);
            textView.setLayoutParams(layoutParams);

            textView.setTextSize(14f);
            textView.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            tableRow.addView(textView);

            shopping_cart.addView(tableRow);
        } catch(NullPointerException e){}



    }

    @Override
    public void onClick(View v) {
        SharedPreferences spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
      //  Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_LONG).show();
        JSONObject object = new JSONObject();
        try {
            object.put("access_token", spref.getString("access_token",""));
            object.put("total_amount", total_amount);
            int ct = creditBalanceAdapter.getCount();
            JSONArray credits  = new JSONArray();
            for(int i = 0; i < ct; i ++){
                TableRow tr = (TableRow) shopping_cart.getChildAt(i+1);
                ProductBalance productBalance = creditBalanceAdapter.getItem(i);
                try {
                    JSONObject p = new JSONObject();
                    p.put("product_id", productBalance.getProduct_id());
                    p.put("qty", Integer.valueOf(((EditText) tr.getChildAt(1)).getText().toString()));
                    p.put("line_total", Double.valueOf(((TextView) tr.getChildAt(3)).getText().toString()));
                    credits.put(p);
                } catch(NumberFormatException e){
                    continue;
                }
            }

            object.put("credits", credits);

            new Credits(getActivity(), Balance.this, true).purchase(
                    spref.getString("access_token", ""), object);


        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.e("REQUEST", object.toString());






    }

    private void calculate_total(){
        total_amount = 0;
        int ct = creditBalanceAdapter.getCount();

        for(int i = 0; i <= ct ; i ++){
            TableRow tr = (TableRow) shopping_cart.getChildAt(i);
            double total = 0;

            try {
                total = Double.valueOf( ( (TextView) tr.getChildAt(3)).getText().toString() );

            } catch(NumberFormatException e){

            }
            total_amount += total;
        }

        TableRow tr = (TableRow) shopping_cart.getChildAt(ct + 1);
        TextView tv = (TextView) tr.getChildAt(1);
        tv.setText(String.valueOf(total_amount));
        btn_purchase.setEnabled(total_amount > 0);


    }

    @Override
    public void completed(Object object) {
        JSONObject response = (JSONObject)object;
        if(response.optInt("success") == 1 ) {

            Bundle extras = new Bundle();
            extras.putInt("bill_id", response.optInt("bill_id"));
            extras.putString("ip", response.optString("ip"));
            extras.putString("ref", response.optString("ref"));
            extras.putDouble("total_amount", total_amount);
            extras.putString("currency", "ZAR");
            extras.putString("description", "Credits Purchase");

            startActivity(new Intent(getActivity(), CreditCard.class).putExtras(extras));
        } else {
            Snackbar snackbar = Snackbar.make(parent, response.optString("message"), Snackbar.LENGTH_LONG);

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
        Snackbar snackbar = Snackbar.make(parent, e.getMessage(), Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
        } else {
            sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
        }

        snackbar.show();
    }
}
