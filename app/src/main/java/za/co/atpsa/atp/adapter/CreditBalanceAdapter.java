package za.co.atpsa.atp.adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import java.util.List;
import za.co.atpsa.atp.R;
import za.co.atpsa.atp.entities.ProductBalance;



public class CreditBalanceAdapter extends BaseAdapter  {

    List<ProductBalance> productBalances = new ArrayList<>();
    Context context;
    public static final String PREFERENCE= "ATP";
    private SharedPreferences spref;

    public CreditBalanceAdapter(Context context, List<ProductBalance> productBalances){

        this.context = context;
        this.productBalances = productBalances;
        spref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


    }


    public void addProductBalances(ProductBalance productBalance){
        productBalances.add(productBalance);
        notifyDataSetChanged();

    }

    public void clear(){
        productBalances.clear();
    }
    @Override
    public int getCount() {
        return productBalances.size();
    }

    @Override
    public ProductBalance getItem(int position) {
        return productBalances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return   0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  View view;
        ProductBalance productBalance = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView =  LayoutInflater.from(context).inflate(R.layout.credits_balance_items, parent, false);
            view =convertView;
        } else {
            view = convertView;
        }
        // Lookup view for data population

        TextView product_name =  view.findViewById(R.id.product_name);
        ImageView product_image = view.findViewById(R.id.product_image);
        TextView balance =  view.findViewById(R.id.product_balance);

        // Populate the data into the template view using the data object

        product_name.setText(productBalance.getProduct_name());
        balance.setText(String.valueOf(productBalance.getBalance()));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

       // Log.e("Image", "https://atpevs.co.za/storage/app/"+productBalance.getIcon());

        Glide.with(context).load("https://atpevs.co.za/storage/app/"+productBalance.getIcon()).apply(options).into(product_image);

        return convertView;
    }

}
