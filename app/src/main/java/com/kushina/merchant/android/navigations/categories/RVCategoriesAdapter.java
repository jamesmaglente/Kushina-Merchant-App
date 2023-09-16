package com.kushina.merchant.android.navigations.categories;

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

import java.util.List;

public class RVCategoriesAdapter extends RecyclerView.Adapter<RVCategoriesAdapter.mViewHolder> {

    private Context mContext;
    private List<RVCategoriesModel> rvModel;
    private RVCategoriesAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVCategoriesAdapter(Context mContext, List<RVCategoriesModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVCategoriesAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_common_list_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVCategoriesModel item = rvModel.get(position);


        holder.tvDescription.setText(item.getDescription());
        holder.tvStatus.setText(item.getStatus());

        if(item.getStatus().toLowerCase().equals("active")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        }else{
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvDescription,tvStatus;

        public mViewHolder(View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tv_common_list_description);
            tvStatus = itemView.findViewById(R.id.tv_common_list_status);



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
