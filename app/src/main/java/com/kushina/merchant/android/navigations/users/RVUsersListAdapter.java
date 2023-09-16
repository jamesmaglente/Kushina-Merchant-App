package com.kushina.merchant.android.navigations.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.navigations.orders.RVOrderHistoryAdapter;
import com.kushina.merchant.android.navigations.orders.RVOrderHistoryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RVUsersListAdapter extends RecyclerView.Adapter<RVUsersListAdapter.mViewHolder> implements Filterable {

    private Context mContext;
    private List<RVUsersListModel> rvModel;
    private List<RVUsersListModel> rvModelAll;
    private RVUsersListAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVUsersListAdapter(Context mContext, List<RVUsersListModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
        rvModelAll = new ArrayList<>(rvModel);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<RVUsersListModel> filteredModel = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                filteredModel.addAll(rvModelAll);
            }else{
                for(int i = 0; i < rvModelAll.size(); i++){
                    if(rvModelAll.get(i).getEmail().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredModel.add(rvModelAll.get(i));
                    } else if(rvModelAll.get(i).getMobile().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredModel.add(rvModelAll.get(i));
                    }
//                    else if(rvModelAll.get(i).getFirstname().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                        filteredModel.add(rvModelAll.get(i));
//                    } else if(rvModelAll.get(i).getLastname().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                        filteredModel.add(rvModelAll.get(i));
//                    }
                }

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredModel;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            rvModel.clear();
            rvModel.addAll((Collection<? extends RVUsersListModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVUsersListAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_users_list_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVUsersListModel item = rvModel.get(position);


        holder.tvStatus.setText(item.getStatus());
        holder.tvFullName.setText(item.getFirstname()+" "+item.getLastname());
        holder.tvEmail.setText(item.getEmail());
        holder.tvMobile.setText(item.getMobile());
        holder.tvGroupName.setText(item.getGroupName());
        holder.tvMembershipType.setText(item.getMembershipType());
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDateCreated()));

        if(item.getStatus().toLowerCase().equals("active")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
            holder.tvHelperText.setText("Click to disable this user");
        }else if(item.getStatus().toLowerCase().equals("suspended")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
            holder.tvHelperText.setText("Click to enable this user");
        }

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvMembershipType,tvFullName,tvGroupName,tvEmail,tvMobile,tvStatus,tvDate,tvHelperText;

        public mViewHolder(View itemView) {
            super(itemView);

            tvMembershipType = itemView.findViewById(R.id.tv_users_list_membership_type);
            tvFullName = itemView.findViewById(R.id.tv_users_list_full_name);
            tvGroupName = itemView.findViewById(R.id.tv_users_list_group_name);
            tvEmail = itemView.findViewById(R.id.tv_users_list_email);
            tvMobile = itemView.findViewById(R.id.tv_users_list_mobile);
            tvStatus = itemView.findViewById(R.id.tv_users_list_status);
            tvDate = itemView.findViewById(R.id.tv_users_list_date);
            tvHelperText = itemView.findViewById(R.id.tv_helper_text);



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
