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

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.FragmentsAdapter;
import za.co.atpsa.atp.fragments.donation.RepeatDonations;
import za.co.atpsa.atp.fragments.donation.RepeatPayments;
import za.co.atpsa.atp.fragments.evs.IDCheck;
import za.co.atpsa.atp.fragments.evs.TelephoneCheck;


public class Evs extends Fragment implements ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    FragmentsAdapter fragmentsAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.evs,
                container, false);
        viewPager =  view.findViewById(R.id.viewPager);
        tabLayout =  view.findViewById(R.id.tabLayout);

        spref = getActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = spref.edit();

        Fragment telephoneCheck = new TelephoneCheck();
        Fragment id_check = new IDCheck();

        fragmentsAdapter =  new FragmentsAdapter(getChildFragmentManager(), getActivity());
        fragmentsAdapter.addFragment(telephoneCheck, "Telephone Trace");
        fragmentsAdapter.addFragment(id_check, "Consumer Trace");

        viewPager.setAdapter(fragmentsAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);

        int position = spref.contains("position") ?  spref.getInt("position",1) : 0;
        selectPage(position);
        setHasOptionsMenu(true);
        return view;

    }

    public void selectPage(int page){
        viewPager.setCurrentItem(page);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        editor.putInt("position", i).commit();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
