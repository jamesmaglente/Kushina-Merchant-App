package com.kushina.merchant.android.navigations.withdrawals;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.navigations.orders.SpinnerStatusesModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ECASH;
import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ORDERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawalsFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    List<SpinnerStatusesModel> sModel;

    Unbinder unbinder;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;
    @BindView(R.id.rv_withdraw_history)
    RecyclerView rvWithdrawHistory;

    List<RVWithdrawHistoryModel> whModel;
    @BindView(R.id.s_status)
    SearchableSpinner sStatus;
    @BindView(R.id.til_status)
    TextInputLayout tilStatus;


    public WithdrawalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdrawals, container, false);
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

    private void loadWithdraws(String statusID) {


        whModel = new ArrayList<>();
        whModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        if(!statusID.equals("0")) {
            request_data.put("status_id", statusID);
        }

        mAPI.api_request("POST",
                API_NODE_ECASH + "getAllWithdraw",
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
                                JSONArray items = data_array.getJSONArray("withdraws");

                                for (int i = 0; i < items.length(); i++) {

                                    String withdraw_id = ((JSONObject) items.get(i)).get("withdraw_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String customer_name = ((JSONObject) items.get(i)).get("customer_name").toString();
                                    String customer_number = ((JSONObject) items.get(i)).get("customer_mobile").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String withdraw_method_id = ((JSONObject) items.get(i)).get("withdraw_method_id").toString();
                                    String withdraw_method = ((JSONObject) items.get(i)).get("withdraw_method").toString();
                                    String account_name = ((JSONObject) items.get(i)).get("account_name").toString();
                                    String account_number = ((JSONObject) items.get(i)).get("account_number").toString();
                                    String amount = ((JSONObject) items.get(i)).get("amount").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    whModel.add(new RVWithdrawHistoryModel(withdraw_id, code_id, user_id, customer_name, customer_number, status_id, status, withdraw_method_id, withdraw_method, account_name, account_number, amount, date_created));

                                }


                                rvWithdrawHistory.setHasFixedSize(true);
                                rvWithdrawHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVWithdrawHistoryAdapter whAdapter = new RVWithdrawHistoryAdapter(getActivity(), whModel);
                                rvWithdrawHistory.setAdapter(whAdapter);


                                if (whModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                whAdapter.setOnItemClickListener(position -> {
                                    RVWithdrawHistoryModel clickedItem = whModel.get(position);

                                    Intent intent = new Intent(getActivity(), WithdrawDetailsActivity.class);
                                    intent.putExtra("withdraw_amount", clickedItem.getAmount());
                                    intent.putExtra("account_name", clickedItem.getAccountName());
                                    intent.putExtra("account_number", clickedItem.getAccountNumber());
                                    intent.putExtra("withdraw_method", clickedItem.getWithdrawMethod());
                                    intent.putExtra("withdraw_status", clickedItem.getStatus());
                                    intent.putExtra("withdraw_id", clickedItem.getWithdrawID());
                                    intent.putExtra("code_id", clickedItem.getCodeID());
                                    intent.putExtra("date", clickedItem.getDateCreated());
                                    //   intent.putExtra("proof_of_payment_image", clickedItem.getProofOfPaymentImage());
                                    intent.putExtra("customer_fullname", clickedItem.getCustomerName());
                                    intent.putExtra("customer_number", clickedItem.getCustomerNumber());
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

    private void loadStatuses(){
        sModel = new ArrayList<>();
        sModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        //  request_data.put("status", "active");

        mAPI.api_request("GET",
                API_NODE_ECASH + "getWithdrawStatus",
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

                            spinner_statuses.add("All Withdraws");
                            sModel.add(new SpinnerStatusesModel("0", "None"));

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("withdraw_statuses");

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


                                        loadWithdraws(selectedItem.getStatusID());


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
