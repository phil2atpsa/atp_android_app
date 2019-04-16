package za.co.atpsa.atp.fragments.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.CollectionsReportAdapter;
import za.co.atpsa.atp.adapter.PaymentAdapter;
import za.co.atpsa.atp.entities.Collections;
import za.co.atpsa.atp.entities.Payment;

public class Payments extends Fragment   {

    public static final String PREFERENCE= "ATP";
    ListView listView;
    PaymentAdapter paymentAdapter;
    List<Payment> items = new ArrayList<>();
    private SharedPreferences spref;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.bills_report, container, false);

        listView =  mView.findViewById(R.id.list_view);


        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        paymentAdapter = new PaymentAdapter(getActivity(), items);

        listView.setAdapter(paymentAdapter);
        View header = inflater.inflate(R.layout.payments_report_header, listView, false);

        listView.addHeaderView(header);
        EditText search = header.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paymentAdapter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Spinner day_filter = header.findViewById(R.id.day_filter);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview,
                getActivity().getResources().getStringArray(R.array.day_filter));
        day_filter.setAdapter(adapter2);

        day_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String days = adapter2.getItem(position);
                if(!"ALL".equals(days)){
                    days = days.replace("days","").trim();
                    paymentAdapter.refresh(true, Integer.valueOf(days));


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        paymentAdapter.refresh(true, 0);
        return mView;
    }
}
