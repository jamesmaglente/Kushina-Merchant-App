package com.kushina.merchant.android.navigations.dashboard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_DASHBOARD;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;
    @BindView(R.id.tv_dashboard_total_income)
    TextView tvDashboardTotalIncome;
    @BindView(R.id.tv_dashboard_total_sales)
    TextView tvDashboardTotalSales;
    @BindView(R.id.tv_dashboard_total_system_fee)
    TextView tvDashboardTotalSystemFee;
    @BindView(R.id.tv_dashboard_total_orders)
    TextView tvDashboardTotalOrders;
    @BindView(R.id.tv_dashboard_total_customers)
    TextView tvDashboardTotalCustomers;
    @BindView(R.id.ll_no_rankings_yet)
    LinearLayout llNoRankingsYet;
    @BindView(R.id.rv_leaderboard)
    RecyclerView rvLeaderboard;
    @BindView(R.id.tv_dashboard_total_commissions)
    TextView tvDashboardTotalCommissions;
    @BindView(R.id.tv_dashboard_total_tax_collected)
    TextView tvDashboardTotalTaxCollected;

    List<RVLeaderboardModel> lbModel;
    @BindView(R.id.tv_dashboard_total_discounts)
    TextView tvDashboardTotalDiscounts;
    @BindView(R.id.tv_dashboard_total_pending_orders)
    TextView tvDashboardTotalPendingOrders;
    @BindView(R.id.tv_dashboard_total_ongoing_orders)
    TextView tvDashboardTotalOngoingOrders;
    @BindView(R.id.tv_dashboard_total_delivered_orders)
    TextView tvDashboardTotalDeliveredOrders;
    @BindView(R.id.tv_dashboard_total_cancelled_orders)
    TextView tvDashboardTotalCancelledOrders;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadDashboard();
    }


    private void loadDashboard() {

        lbModel = new ArrayList<>();
        lbModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_DASHBOARD + "getAdminDashboard",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {


                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                String total_income = data_array.getString("total_income");
                                String total_discount_given = data_array.getString("total_discount_given");
                                String total_sales = data_array.getString("total_sales");
                                String total_system_fee = data_array.getString("total_system_fee");
                                String total_pending_orders = data_array.getString("total_pending_orders");
                                String total_ongoing_orders = data_array.getString("total_ongoing_orders");
                                String total_cancelled_orders = data_array.getString("total_cancelled_orders");
                                String total_delivered_orders = data_array.getString("total_delivered_orders");
                                String total_overall_orders = data_array.getString("total_overall_orders");
                                String total_customer = data_array.getString("total_customer");
                                String total_commission_given = data_array.getString("total_commission_given");
                                String total_tax_collected = data_array.getString("total_tax_collected");
                        //        JSONArray items = data_array.getJSONArray("rankings");


                                tvDashboardTotalSales.setText(mGlobals.moneyFormatter(total_sales));
                                tvDashboardTotalIncome.setText(mGlobals.moneyFormatter(total_income));
                                tvDashboardTotalSystemFee.setText(mGlobals.moneyFormatter(total_system_fee));
                                tvDashboardTotalCommissions.setText(mGlobals.moneyFormatter(total_commission_given));
                                tvDashboardTotalDiscounts.setText(mGlobals.moneyFormatter(total_discount_given));
                                tvDashboardTotalTaxCollected.setText(mGlobals.moneyFormatter(total_tax_collected));
                                tvDashboardTotalPendingOrders.setText(total_pending_orders);
                                tvDashboardTotalOngoingOrders.setText(total_ongoing_orders);
                                tvDashboardTotalCancelledOrders.setText(total_cancelled_orders);
                                tvDashboardTotalDeliveredOrders.setText(total_delivered_orders);
                                tvDashboardTotalOrders.setText(total_overall_orders);
                                tvDashboardTotalCustomers.setText(total_customer);

//                                for (int i = 0; i < items.length(); i++) {
//
//                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
//                                    String firstname = ((JSONObject) items.get(i)).get("firstname").toString();
//                                    String total_purchased = ((JSONObject) items.get(i)).get("total_purchased").toString();
//
//
//                                    lbModel.add(new RVLeaderboardModel(user_id, firstname, total_purchased));
//
//                                }
//
//
//                                rvLeaderboard.setHasFixedSize(true);
//                                rvLeaderboard.setLayoutManager(new LinearLayoutManager(getActivity()));
//                                RVLeaderboardAdapter lbAdapter = new RVLeaderboardAdapter(getActivity(), lbModel);
//                                rvLeaderboard.setAdapter(lbAdapter);
//
//                                if (lbModel.isEmpty()) {
//                                    llNoRankingsYet.setVisibility(View.VISIBLE);
//                                } else {
//                                    llNoRankingsYet.setVisibility(View.GONE);
//                                }


                            } else {
                                // show error
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());
                        }


                    }
                });


    }


}
