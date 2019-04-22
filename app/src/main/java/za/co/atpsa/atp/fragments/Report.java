package za.co.atpsa.atp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.FragmentsAdapter;
import za.co.atpsa.atp.fragments.report.Bills;
import za.co.atpsa.atp.fragments.report.Collections;
import za.co.atpsa.atp.fragments.report.EVSReport;
import za.co.atpsa.atp.fragments.report.Payments;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;
import za.co.atpsa.products.Products;

public class Report  extends Fragment implements  ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;

    FragmentsAdapter fragmentsAdapter;
    Fragment bills;
    Fragment collections ;
    Fragment evs;
    Fragment payments ;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evs,
                container, false);

        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);

        viewPager.addOnPageChangeListener(this);
        FragmentsAdapter fragmentsAdapter =  new FragmentsAdapter(getChildFragmentManager(), getActivity());

        Fragment bills = new Bills();
        Fragment collections = new Collections();
        Fragment evs = new EVSReport();
        Fragment payments = new Payments();

        Set<String> product_list = spref.getStringSet("product_list", null);

        if(product_list.contains("3")){

            fragmentsAdapter.addFragment(bills,"Bills");
        }
        if(product_list.contains("4")
                || product_list.contains("5")){
            fragmentsAdapter.addFragment(collections,"Collects");
        }

        if(product_list.contains("2")){
            fragmentsAdapter.addFragment(evs,"EVS");
        }
        if(product_list.contains("1")){
            fragmentsAdapter.addFragment(payments,"Payments");
        }

        viewPager.setAdapter(fragmentsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        int position = 0;
        if(spref.contains("position") )
            position = spref.getInt("position",0);



        viewPager.setCurrentItem(position);

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        editor.putInt("position", i).commit();
       // Toast.makeText(getActivity(), ""+spref.getInt("position",0), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


}
