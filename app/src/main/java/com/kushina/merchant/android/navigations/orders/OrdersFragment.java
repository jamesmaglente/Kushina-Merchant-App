package com.kushina.merchant.android.navigations.orders;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.navigations.categories.RVCategoriesModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_CATEGORY;
import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ORDERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;

    List<RVOrderHistoryModel> ohModel;
    List<SpinnerStatusesModel> sModel;
    @BindView(R.id.tv_order_history_ongoing)
    TextView tvOrderHistoryOngoing;
    @BindView(R.id.btn_ongoing_orders)
    LinearLayout btnOngoingOrders;
    @BindView(R.id.tv_order_history_processed)
    TextView tvOrderHistoryProcessed;
    @BindView(R.id.btn_processed_orders)
    LinearLayout btnProcessedOrders;
    @BindView(R.id.ll_view_switcher)
    LinearLayout llViewSwitcher;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.ll_layout_bg)
    LinearLayout llLayoutBg;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;
    @BindView(R.id.rv_orders_history)
    RecyclerView rvOrdersHistory;
    @BindView(R.id.s_status)
    SearchableSpinner sStatus;
    @BindView(R.id.til_status)
    TextInputLayout tilStatus;


    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadStatuses();

    }

    private void loadOrders(String statusID) {

        ohModel = new ArrayList<>();
        ohModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        if(!statusID.equals("0")) {
            request_data.put("status_id", statusID);
        }
        mAPI.api_request("POST",
                API_NODE_ORDERS + "getOrders",
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
                                JSONArray items = data_array.getJSONArray("orders");

                                for (int i = 0; i < items.length(); i++) {

                                    String order_id = ((JSONObject) items.get(i)).get("order_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String cart_id = ((JSONObject) items.get(i)).get("cart_id").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String booker_name = ((JSONObject) items.get(i)).get("booker_name").toString();
                                    String payment_option_id = ((JSONObject) items.get(i)).get("payment_option_id").toString();
                                    String payment_option = ((JSONObject) items.get(i)).get("payment_option").toString();
                                    String address_id = ((JSONObject) items.get(i)).get("address_id").toString();
                                    String address_title = ((JSONObject) items.get(i)).get("address_title").toString();
                                    String customer_name = ((JSONObject) items.get(i)).get("customer_name").toString();
                                    String address_line = ((JSONObject) items.get(i)).get("address_line").toString();
                                    String house_address = ((JSONObject) items.get(i)).get("house_address").toString();
                                    String zip_code = ((JSONObject) items.get(i)).get("zip_code").toString();
                                    String landmarks = ((JSONObject) items.get(i)).get("landmarks").toString();
                                    String reference = ((JSONObject) items.get(i)).get("reference").toString();
                                    String total_amount = ((JSONObject) items.get(i)).get("total_amount").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    ohModel.add(new RVOrderHistoryModel(order_id,code_id, cart_id, status_id, status, user_id,booker_name, payment_option_id, payment_option, address_id, address_title, customer_name,address_line,house_address,zip_code,landmarks,reference, total_amount, date_created));

                                }


                                rvOrdersHistory.setHasFixedSize(true);
                                rvOrdersHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVOrderHistoryAdapter ohAdapter = new RVOrderHistoryAdapter(getActivity(), ohModel);
                                rvOrdersHistory.setAdapter(ohAdapter);


                                if (ohModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                ohAdapter.setOnItemClickListener(position -> {
                                    RVOrderHistoryModel clickedItem = ohModel.get(position);

                                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                                    intent.putExtra("order_id", clickedItem.getOrderID());
                                    intent.putExtra("cart_id", clickedItem.getCartID());
                                    intent.putExtra("reference", clickedItem.getReference());
                                    intent.putExtra("full_address",clickedItem.getHouseAddress()+" "+clickedItem.getAddressLine()+" "+clickedItem.getZipCode());
                                    intent.putExtra("landmarks",clickedItem.getLandmarks());
                                    intent.putExtra("booker_name",clickedItem.getBookerName());
//                                    intent.putExtra("item_image", clickedItem.getItemPictureLink());
                                    //     intent.putExtra("description", clickedItem.getDesc());
                                    startActivity(intent);
                                });


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

    @OnClick({R.id.btn_ongoing_orders, R.id.btn_processed_orders})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ongoing_orders:
                loadOrders("ongoing");
                tvOrderHistoryProcessed.setTextColor(getResources().getColor(R.color.colorOrange));
                tvOrderHistoryOngoing.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.btn_processed_orders:
                loadOrders("processed");
                tvOrderHistoryOngoing.setTextColor(getResources().getColor(R.color.colorOrange));
                tvOrderHistoryProcessed.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
        }
    }

    private void loadStatuses(){
        sModel = new ArrayList<>();
        sModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
      //  request_data.put("status", "active");

        mAPI.api_request("GET",
                API_NODE_ORDERS + "getAllOrderStatus",
                request_data,
                false,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());

                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            final ArrayList<String> spinner_statuses = new ArrayList<String>();

                            spinner_statuses.add("All Orders");
                            sModel.add(new SpinnerStatusesModel("0", "None"));

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("statuses");

                                for (int i = 0; i < items.length(); i++) {

                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();


                                    sModel.add(new SpinnerStatusesModel(status_id,status));
                                    spinner_statuses.add(status);

                                }


                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, spinner_statuses);

                                sStatus.setAdapter(adapter);


                                sStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        SpinnerStatusesModel selectedItem = sModel.get(position);


                                        loadOrders(selectedItem.getStatusID());


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


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
