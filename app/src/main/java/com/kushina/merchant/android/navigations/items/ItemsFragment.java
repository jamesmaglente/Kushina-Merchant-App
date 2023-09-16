package com.kushina.merchant.android.navigations.items;


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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ITEMS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;
    @BindView(R.id.rv_items_list)
    RecyclerView rvItemsList;

    List<RVItemsListModel> ilModel;
    List<RVCategoriesModel> cModel;
    @BindView(R.id.s_category)
    SearchableSpinner sCategory;
    @BindView(R.id.til_category)
    TextInputLayout tilCategory;
    @BindView(R.id.fab_label)
    TextView fabLabel;
    @BindView(R.id.btn_add_item)
    FloatingActionButton btnAddItem;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_no_items_yet)
    LinearLayout llNoItemsYet;


    public ItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadCategories();


    }

    @OnClick(R.id.btn_add_item)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), AddEditItemActivity.class);
        intent.putExtra("task", "add_item");
        startActivity(intent);
    }

    private void loadItems(String categoryID) {

        ilModel = new ArrayList<>();
        ilModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        if (!categoryID.equals("0")) {
            request_data.put("category_id", categoryID);
        }

        mAPI.api_request("POST",
                API_NODE_ITEMS + "getAllItems",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());

                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("items");

                                for (int i = 0; i < items.length(); i++) {

                                    String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String item_name = ((JSONObject) items.get(i)).get("item_name").toString();
                                    String long_description = ((JSONObject) items.get(i)).get("long_description").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();
                                    String sku = ((JSONObject) items.get(i)).get("sku").toString();
                                    String srp = ((JSONObject) items.get(i)).get("srp").toString();
                                    String toque = ((JSONObject) items.get(i)).get("toque").toString();
                                    String quantity = ((JSONObject) items.get(i)).get("quantity").toString();
                                    String merchant_id = ((JSONObject) items.get(i)).get("merchant_id").toString();
                                    String rating = ((JSONObject) items.get(i)).get("rating").toString();
                                    String likes = ((JSONObject) items.get(i)).get("likes").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    ilModel.add(new RVItemsListModel(item_id, code_id, category_id, category, item_name, long_description, image, sku, srp, toque, quantity, merchant_id, rating, likes, status, date_created));

                                }


                                rvItemsList.setHasFixedSize(true);
                                rvItemsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVItemsListAdapter rvAdapter = new RVItemsListAdapter(getActivity(), ilModel);
                                rvItemsList.setAdapter(rvAdapter);

                                if(ilModel.isEmpty()){
                                    llNoItemsYet.setVisibility(View.VISIBLE);
                                }else{
                                    llNoItemsYet.setVisibility(View.GONE);
                                }

                                rvAdapter.setOnItemClickListener(position -> {

                                    RVItemsListModel clickedItem = ilModel.get(position);

                                    Intent intent = new Intent(getActivity(), AddEditItemActivity.class);
                                    intent.putExtra("task", "update_item");
                                    intent.putExtra("item_id", clickedItem.getItemID());
                                    intent.putExtra("code_id", clickedItem.getCodeID());
                                    intent.putExtra("category_id", clickedItem.getCategoryID());
                                    intent.putExtra("category", clickedItem.getCategory());
                                    intent.putExtra("item_name", clickedItem.getItemName());
                                    intent.putExtra("item_description", clickedItem.getLongDescription());
                                    intent.putExtra("item_image", clickedItem.getImage());
                                    intent.putExtra("item_sku", clickedItem.getSku());
                                    intent.putExtra("item_srp", clickedItem.getSrp());
                                    intent.putExtra("item_toque", clickedItem.getToque());
                                    intent.putExtra("item_quantity", clickedItem.getQuantity());
                                    intent.putExtra("merchant_id", clickedItem.getMerchantID());
                                    intent.putExtra("item_rating", clickedItem.getRating());
                                    intent.putExtra("item_likes", clickedItem.getLikes());
                                    intent.putExtra("item_status", clickedItem.getStatus());
                                    intent.putExtra("date_created", clickedItem.getDateCreated());
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

    private void loadCategories() {

        cModel = new ArrayList<>();
        cModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("status", "active");

        mAPI.api_request("POST",
                API_NODE_CATEGORY + "getAllCategories",
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

                            final ArrayList<String> spinner_categories = new ArrayList<String>();

                            spinner_categories.add("All Items");
                            cModel.add(new RVCategoriesModel("0", "All Items", "avail", ""));

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("categories");

                                for (int i = 0; i < items.length(); i++) {

                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    cModel.add(new RVCategoriesModel(category_id, category, status, date_created));
                                    spinner_categories.add(category);

                                }


                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, spinner_categories);

                                sCategory.setAdapter(adapter);


                                sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        RVCategoriesModel selectedItem = cModel.get(position);


                                        loadItems(selectedItem.getCategoryID());


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
