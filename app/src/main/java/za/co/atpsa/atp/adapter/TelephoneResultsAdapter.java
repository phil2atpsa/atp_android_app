package za.co.atpsa.atp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import za.co.atpsa.atp.R;
import za.co.atpsa.atp.adapter.listeners.TelephoneResultsListener;


public class TelephoneResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private TelephoneResultsListener telephoneResultsListener;

    JSONArray items;

    public TelephoneResultsAdapter(JSONArray items){
        this.items = items;
    }

    public void setTelephoneResultsListener(TelephoneResultsListener telephoneResultsListener) {
        this.telephoneResultsListener = telephoneResultsListener;
    }

    public void add (JSONObject item){
        items.put(item);
        notifyDataSetChanged();
    }

    public JSONObject getItem(int position){
        return items.optJSONObject(position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.telephone_check_items_header, parent, false);
            return  new HeaderViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.telephone_check_items, parent, false);
            return new ItemViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof HeaderViewHolder)
        {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
           // VHheader.txtTitle.setText(header.getHeader());
        }
        else if(viewHolder instanceof ItemViewHolder)
        {
            try {
                Log.e("Position", ""+position);
                JSONObject currentItem = items.getJSONObject (position -1);
                ItemViewHolder itemViewHolder = (ItemViewHolder)viewHolder;
                itemViewHolder.id_number.setText(currentItem.optString("id_number"));
                itemViewHolder.name.setText(currentItem.optString("name"));
                itemViewHolder.surname.setText(currentItem.optString("surname"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return items.length() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView txtHeader1;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            txtHeader1 = (TextView)itemView.findViewById(R.id.txtHeader1);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView id_number;
        public TextView name;
        public TextView surname;
        public ImageButton btn_search;

        public ItemViewHolder(View itemView) {
            super(itemView);
            id_number = itemView.findViewById(R.id.id_number);
            name =  itemView.findViewById(R.id.name);
            surname =  itemView.findViewById(R.id.surname);
            btn_search =itemView.findViewById(R.id.btn_more);
            btn_search.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            JSONObject object = getItem(getAdapterPosition() - 1);

            telephoneResultsListener.onSearchRequested(object.optString("id_number"),
                    object.optString("name"), object.optString("surname"));
            //Log.e("Position", ""+getAdapterPosition());


        }
    }
}
