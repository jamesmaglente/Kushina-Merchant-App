package com.kushina.merchant.android.navigations.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RVItemsListAdapter extends RecyclerView.Adapter<RVItemsListAdapter.mViewHolder> {

    private Context mContext;
    private List<RVItemsListModel> rvModel;
    private RVItemsListAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVItemsListAdapter(Context mContext, List<RVItemsListModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVItemsListAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_item_list_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVItemsListModel item = rvModel.get(position);

        try {
            Picasso.get()
                    //   .load(ITEMS_URL+item.getItemPicture())
                    .load(item.getImage())
                    //.load(item.getItemImage())
                    .resize(200,200)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getSrp()));
        holder.tvItemQTY.setText("Total Stocks: "+item.getQuantity());
        holder.tvItemStatus.setText(item.getStatus());

        if(item.getStatus().toLowerCase().equals("available")){
            holder.tvItemStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        }else{
            holder.tvItemStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }


    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemPicture;
        TextView tvItemName,tvItemPrice,tvItemQTY,tvItemStatus;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemPicture = itemView.findViewById(R.id.iv_items_list_image);
            tvItemName = itemView.findViewById(R.id.tv_items_list_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_items_list_item_price);
            tvItemQTY = itemView.findViewById(R.id.tv_items_list_qty);
            tvItemStatus = itemView.findViewById(R.id.tv_items_list_status);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
