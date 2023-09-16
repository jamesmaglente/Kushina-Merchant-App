package com.kushina.merchant.android.navigations.users;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_USER;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;
    List<RVUsersListModel> ulModel;
    RVUsersListAdapter rvAdapter;
    @BindView(R.id.ll_no_users_yet)
    LinearLayout llNoUsersYet;
    @BindView(R.id.rv_users_list)
    RecyclerView rvUsersList;
    @BindView(R.id.edt_search_items)
    TextInputEditText edtSearchItems;
    @BindView(R.id.til_search_items)
    TextInputLayout tilSearchItems;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadUsers();
        edtSearchItems.addTextChangedListener(new MyTextWatcher(edtSearchItems));
    }

    private void loadUsers() {

        ulModel = new ArrayList<>();
        ulModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_USER + "getAllUsers",
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

                                JSONArray items = data_array.getJSONArray("users");


                                for (int i = 0; i < items.length(); i++) {

                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String user_group_id = ((JSONObject) items.get(i)).get("user_group_id").toString();
                                    String group_name = ((JSONObject) items.get(i)).get("group_name").toString();
                                    String membership_type_id = ((JSONObject) items.get(i)).get("membership_type_id").toString();
                                    String membership_type = ((JSONObject) items.get(i)).get("membership_type").toString();
                                    String refer_by_id = ((JSONObject) items.get(i)).get("refer_by_id").toString();
                                    String code = ((JSONObject) items.get(i)).get("code").toString();
                                    String available_ecash_claims = ((JSONObject) items.get(i)).get("available_ecash_claims").toString();
                                    String username = ((JSONObject) items.get(i)).get("username").toString();
                                    String ecash = ((JSONObject) items.get(i)).get("ecash").toString();
                                    String toque = ((JSONObject) items.get(i)).get("toque").toString();
                                    String email = ((JSONObject) items.get(i)).get("email").toString();
                                    String mobile = ((JSONObject) items.get(i)).get("mobile").toString();
                                    String firstname = ((JSONObject) items.get(i)).get("firstname").toString();
                                    String middlename = ((JSONObject) items.get(i)).get("middlename").toString();
                                    String lastname = ((JSONObject) items.get(i)).get("lastname").toString();
                                    String gender = ((JSONObject) items.get(i)).get("gender").toString();
                                    String age = ((JSONObject) items.get(i)).get("age").toString();
                                    String profile_picture = ((JSONObject) items.get(i)).get("profile_picture").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    ulModel.add(new RVUsersListModel(user_id, code_id, user_group_id, group_name, membership_type_id, membership_type,
                                            refer_by_id, code, available_ecash_claims, username, ecash, toque, email, mobile, firstname, middlename, lastname,
                                            gender, age, profile_picture, status, date_created));

                                }

                                rvUsersList.setHasFixedSize(true);
                                rvUsersList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                rvAdapter = new RVUsersListAdapter(getActivity(), ulModel);
                                rvUsersList.setAdapter(rvAdapter);


                                if (ulModel.isEmpty()) {
                                    llNoUsersYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoUsersYet.setVisibility(View.GONE);
                                }

                                rvAdapter.setOnItemClickListener(new RVUsersListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {

                                        RVUsersListModel clickedItem = ulModel.get(position);

                                        if (clickedItem.getStatus().toLowerCase().equals("active")) {
                                            mGlobals.showChoiceDialog("Disable the account of " + clickedItem.getFirstname() + " " + clickedItem.getLastname() + "?", true, new Globals.Callback() {
                                                @Override
                                                public void onPickCallback(Boolean result) {
                                                    if (result) {
                                                        updateUserStatus(clickedItem.getUserID(), "disable");
                                                    }
                                                }
                                            });
                                        } else {
                                            mGlobals.showChoiceDialog("Enable the account of " + clickedItem.getFirstname() + " " + clickedItem.getLastname() + "?", true, new Globals.Callback() {
                                                @Override
                                                public void onPickCallback(Boolean result) {
                                                    if (result) {
                                                        updateUserStatus(clickedItem.getUserID(), "enable");
                                                    }
                                                }
                                            });
                                        }


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

    private void updateUserStatus(String accountUserID, String status) {

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("account_user_id", accountUserID);
        request_data.put("status", status);


        mAPI.api_request("POST",
                API_NODE_USER + "updateUserAccountStatus",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            getActivity().recreate();
                                        }
                                    }
                                });

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_search_items:
                    rvAdapter.getFilter().filter(edtSearchItems.getText().toString());
                    break;

            }
        }
    }
}
