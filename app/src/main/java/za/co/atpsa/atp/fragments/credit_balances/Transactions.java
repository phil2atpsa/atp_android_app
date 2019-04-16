package za.co.atpsa.atp.fragments.credit_balances;

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
import za.co.atpsa.atp.adapter.CreditRequestAdapter;
import za.co.atpsa.atp.adapter.CreditTransactionAdapter;
import za.co.atpsa.atp.entities.CreditRequest;
import za.co.atpsa.atp.entities.CreditTransaction;

public class Transactions  extends Fragment {

    ListView listView;
    List<CreditTransaction> creditTransactions = new ArrayList<>();
    CreditTransactionAdapter creditTransactionAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.credit_transaction, container, false);
        listView = mView.findViewById(R.id.list_view);
        creditTransactionAdapter = new CreditTransactionAdapter(getContext(), creditTransactions);
        listView.setAdapter(creditTransactionAdapter);
        View header = inflater.inflate(R.layout.credit_transaction_header, listView, false);
        EditText search = header.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                creditTransactionAdapter.search(s.toString());
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
                    creditTransactionAdapter.refresh(true, Integer.valueOf(days));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.addHeaderView(header);
        creditTransactionAdapter.refresh(true, 0);
        return   mView;
    }
}
