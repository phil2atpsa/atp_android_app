package za.co.atpsa.atp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentsAdapter extends FragmentStatePagerAdapter {

    Context mContext;
    FragmentManager mFragmentManager;
    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();


    public FragmentsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;
    }

    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }

    public boolean contains(Fragment fragment){
        return fragments.contains(fragment);
    }
    public void clear(){
        fragments.clear();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public String getPageTitle(int position) {
        return titles.get(position);
    }
}
