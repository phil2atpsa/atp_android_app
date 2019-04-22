package za.co.atpsa.atp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.ProductBalanceAdapter;
import za.co.atpsa.atp.entities.ProductBalance;

public class Dashboard extends Fragment {
    RecyclerView mRecycler;
    ProductBalanceAdapter productBalanceAdapter;
    SwipeRefreshLayout swiper;
    ArrayList<ProductBalance> productBalanceList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_balance_list,
                container, false);

        mRecycler = view.findViewById(R.id.mRecycler);
        swiper =  view.findViewById(R.id.swiper);
        productBalanceAdapter = new ProductBalanceAdapter(getActivity(),productBalanceList,swiper);

        mRecycler.setAdapter(productBalanceAdapter);
        LinearLayoutManager  mLinearLayoutManage = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLinearLayoutManage);
        mRecycler.setHasFixedSize(false);
        productBalanceAdapter.refresh(true);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productBalanceAdapter.refresh(false);
                swiper.setRefreshing(false);
            }
        });

        setHasOptionsMenu(true);
        return view;
    }
}
