package com.kushina.merchant.android.navigations.categories;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_CATEGORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;

    List<RVCategoriesModel> cModel;
    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;
    @BindView(R.id.fab_label)
    TextView fabLabel;
    @BindView(R.id.btn_add_category)
    FloatingActionButton btnAddCategory;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
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

    @OnClick(R.id.btn_add_category)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(),AddEditCategoryActivity.class);
        intent.putExtra("task","add_category");
        startActivity(intent);
    }

    private void loadCategories() {

        cModel = new ArrayList<>();
        cModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();

        mAPI.api_request("POST",
                API_NODE_CATEGORY + "getAllCategories",
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
                                JSONArray items = data_array.getJSONArray("categories");

                                for (int i = 0; i < items.length(); i++) {

                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    cModel.add(new RVCategoriesModel(category_id, category, status, date_created));

                                }


                                rvCategories.setHasFixedSize(true);
                                rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVCategoriesAdapter rvAdapter = new RVCategoriesAdapter(getActivity(), cModel);
                                rvCategories.setAdapter(rvAdapter);

                                rvAdapter.setOnItemClickListener(position -> {

                                    RVCategoriesModel clickedItem = cModel.get(position);

                                    Intent intent = new Intent(getActivity(),AddEditCategoryActivity.class);
                                    intent.putExtra("task","update_category");
                                    intent.putExtra("category_id",clickedItem.getCategoryID());
                                    intent.putExtra("category",clickedItem.getDescription());
                                    intent.putExtra("category_status",clickedItem.getStatus());
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


}
