package com.kushina.merchant.android.navigations.orders;

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

public class RVFoodsOrderedAdapter extends RecyclerView.Adapter<RVFoodsOrderedAdapter.mViewHolder> {

    private Context mContext;
    private List<RVMyCartModel> rvModel;
    private RVFoodsOrderedAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVFoodsOrderedAdapter(Context mContext, List<RVMyCartModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVFoodsOrderedAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_my_cart_row_items, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVMyCartModel item = rvModel.get(position);

        try {
            Picasso.get()
                 //   .load(ITEMS_URL+item.getItemPicture())
                    .load(item.getItemPicture())
                    //.load(item.getItemImage())
                    .resize(200,200)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getItemPrice()));
        holder.tvItemQTY.setText("x"+item.getItemQTY());
        holder.tvItemTotal.setText(mGlobals.moneyFormatter(item.getTotalAmount()));
        holder.tvDiscount.setText("Total Discount: ("+mGlobals.moneyFormatter(item.getDiscount())+")");
        holder.ivRemoveItem.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemPicture,ivRemoveItem;
        TextView tvItemName,tvItemPrice,tvItemQTY,tvItemTotal,tvDiscount;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemPicture = itemView.findViewById(R.id.iv_my_cart_item_picture);
            ivRemoveItem = itemView.findViewById(R.id.iv_my_cart_remove_item);
            tvItemName = itemView.findViewById(R.id.tv_my_cart_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_my_cart_item_price);
            tvItemQTY = itemView.findViewById(R.id.tv_my_cart_item_qty);
            tvItemTotal = itemView.findViewById(R.id.tv_my_cart_total);
            tvDiscount = itemView.findViewById(R.id.tv_my_cart_total_discount);



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
