package za.co.atpsa.atp.fragments.report;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.BillReportAdapter;

import za.co.atpsa.atp.dialog.ReportItem;
import za.co.atpsa.atp.entities.BillReportItem;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Report;

public class Bills  extends Fragment implements OnServiceResponseListener, AdapterView.OnItemClickListener {

    public static final String PREFERENCE= "ATP";
    ListView listView;
    BillReportAdapter billReportAdapter;
    List<BillReportItem> items = new ArrayList<>();
    LinearLayout linearLayout;
    private SharedPreferences spref;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.bills_report, container, false);

        listView =  mView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);

        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        billReportAdapter = new BillReportAdapter(getActivity(), items);

        listView.setAdapter(billReportAdapter);
        View header = inflater.inflate(R.layout.bills_report_header, listView, false);
        linearLayout = header.findViewById(R.id.status_list);
        listView.addHeaderView(header);
        EditText search = header.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                billReportAdapter.search(s.toString());
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
                    billReportAdapter.refresh(true, Integer.valueOf(days));
                    new Report(getActivity(), Bills.this, false).bill_status(spref.getString("access_token",""),Integer.valueOf(days));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        billReportAdapter.refresh(true, 0);

        new Report(getActivity(), this, false).bill_status(spref.getString("access_token",""),0);

        return mView;
    }

    @Override
    public void completed(Object object) {
        linearLayout.removeAllViews();
        JSONArray jsonArray = (JSONArray) object;
        for(int i =0; i < jsonArray.length(); i++){
            JSONObject obj = jsonArray.optJSONObject(i);
            View status_view = getLayoutInflater().inflate(R.layout.bills_status_report, null);
            TextView status = status_view.findViewById(R.id.status);
            TextView amount = status_view.findViewById(R.id.amount);
            TextView items_number = status_view.findViewById(R.id.items_number);

          /*  if(obj.optString("status").toLowerCase().equals("success")){
                status.setImageResource(R.drawable.ic_check);
            }
            if(obj.optString("status").toLowerCase().equals("pending")){
                status.setImageResource(R.drawable.ic_pending);
            }
            if(obj.optString("status").toLowerCase().equals("failed")){
                status.setImageResource(R.drawable.ic_failed);
            }*/
            status.setText(obj.optString("status")+" :");
            amount.setText("ZAR " + String.valueOf(obj.optDouble("amount")));

            items_number.setText("["+String.valueOf(obj.optInt("requests"))+"]");

            linearLayout.addView(status_view, i);
         //   horizontalScrollView.arrowScroll(HorizontalScrollView.TEXT_DIRECTION_ANY_RTL);

        }


    }

    @Override
    public void failed(ServiceException e) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BillReportItem billReportItem = billReportAdapter.getItem(position -1);
        Bundle b = new Bundle();
        b.putSerializable("billReportItem", billReportItem);
        startActivity(new Intent(getActivity(), ReportItem.class).putExtras(b));
    }
}
