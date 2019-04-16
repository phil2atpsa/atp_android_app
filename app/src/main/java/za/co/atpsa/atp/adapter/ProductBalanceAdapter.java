package za.co.atpsa.atp.adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.ProductBalance;

import za.co.atpsa.products.Products;
import za.co.atpsa.common.OnServiceResponseListener;
import za.co.atpsa.common.ServiceException;


public class ProductBalanceAdapter extends RecyclerView.Adapter<ProductBalanceAdapter.ProductBalanceViewHolder> implements OnServiceResponseListener {

    Context c;
    ArrayList<ProductBalance> productBalances;
    SwipeRefreshLayout swiper;

    ProgressDialog progressDialog ;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;




    public ProductBalanceAdapter(Context c, ArrayList<ProductBalance> productBalances, SwipeRefreshLayout swiper ) {
        this.c = c;
        this.productBalances = productBalances;
        this.swiper = swiper;
       // progressDialog = TransparentProgressDialog.createProgressDialog(c);
        spref = c.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);



    }

    public void addProductBalance(ProductBalance productBalance){
        productBalances.add(productBalance);
        notifyDataSetChanged();
    }

    public ProductBalance getProductBalance(int position){
        return productBalances.get(position);
    }

    @Override
    public ProductBalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_balance_items,parent,false);
        ProductBalanceAdapter.ProductBalanceViewHolder holder=new ProductBalanceViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductBalanceAdapter.ProductBalanceViewHolder holder, final int position) {

        final ProductBalance productBalance = getProductBalance(position);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

      //  Picasso.with(c).load("http://atpevs.co.za/storage/app/"+productBalance.getIcon()).into(holder.avatar);

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);


        Glide.with(c).load("https://atpevs.co.za/storage/app/"+productBalance.getIcon()).apply(options).into(holder.avatar);

        holder.product_name.setText(productBalance.getProduct_name());
        holder.description.setText(productBalance.getDescription());
        holder.credits.setText("Credits Balance : "+productBalance.getBalance());
    }

    @Override
    public int getItemCount() {
        return productBalances.size();
    }

    public  void refresh(boolean showProgressDialog) {

        new Products(c, this, showProgressDialog).balance(spref.getString("access_token", ""));
    }

    @Override
    public void completed(Object object) {

        productBalances.clear();

        JSONArray array = (JSONArray)object;
        for(int i =0; i < array.length(); i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                ProductBalance productBalance = new ProductBalance();
                productBalance.setBalance(jsonObject.optInt("balance"));
                productBalance.setProduct_name(jsonObject.optString("product_name"));
                productBalance.setDescription(jsonObject.optString("description"));
                productBalance.setIcon(jsonObject.optString("icon"));
                productBalance.setCredit_price(jsonObject.optDouble("credit_price"));
                productBalance.setProduct_id(jsonObject.optInt("product_id"));
                addProductBalance(productBalance);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void failed(ServiceException e) {

    }


    class ProductBalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView avatar;
        public TextView product_name,description, credits;





        public ProductBalanceViewHolder(View itemView) {
            super(itemView);
            avatar =  itemView.findViewById(R.id.avatar);
            description  =  itemView.findViewById(R.id.description);
            credits  =  itemView.findViewById(R.id.credits);
            product_name  =  itemView.findViewById(R.id.product_name);

           // itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {

        }
    }
}
