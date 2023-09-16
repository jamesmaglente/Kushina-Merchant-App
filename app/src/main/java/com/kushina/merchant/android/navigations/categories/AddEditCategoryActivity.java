package com.kushina.merchant.android.navigations.categories;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_CATEGORY;

public class AddEditCategoryActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_category_name)
    TextInputEditText edtCategoryName;
    @BindView(R.id.til_category_name)
    TextInputLayout tilCategoryName;
    @BindView(R.id.switch_status)
    Switch switchStatus;
    @BindView(R.id.btn_save_category)
    Button btnSaveCategory;
    private String task, categoryID,category,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        task = getIntent().getStringExtra("task");
        validateFields();

        if(task.contains("update")){
            categoryID = getIntent().getStringExtra("category_id");
            category = getIntent().getStringExtra("category");
            status = getIntent().getStringExtra("category_status");
            edtCategoryName.setText(category);
            switchStatus.setText(status);
            if(status.toLowerCase().equals("active")){
                switchStatus.setChecked(true);

            }else{
                switchStatus.setChecked(false);
            }
        }else{
            switchStatus.setText("Inactive");
            status = "inactive";
        }

        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchStatus.setText("Active");
                    status = "active";
                }else{
                    switchStatus.setText("Inactive");
                    status = "inactive";
                }
            }
        });


    }



    @OnClick(R.id.btn_save_category)
    public void onViewClicked() {
        mGlobals.showChoiceDialog("Save this category?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if(result){
                    checkFields();
                }
            }
        });
    }

    private void checkFields(){
        if (

                        !mGlobals.validateField(tilCategoryName, edtCategoryName, true, getString(R.string.err_msg_category_name))
        ) {

            return;

        } else {
            if(task.toLowerCase().contains("add")){
                addCategory();
            }else {
                updateCategory();
            }
        }
    }

    private void addCategory(){
        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("description", edtCategoryName.getText().toString());
        request_data.put("status", status);

        // api call
        mAPI.api_request("POST",
                API_NODE_CATEGORY + "addNewCategory",
                request_data,
                true,
                AddEditCategoryActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());
                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            onBackPressed();
                                        }
                                    }
                                });
                                // mGlobals.dismissLoadingDialog();


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void updateCategory(){
        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("category_id", categoryID);
        request_data.put("description", edtCategoryName.getText().toString());
        request_data.put("status", status);

        // api call
        mAPI.api_request("POST",
                API_NODE_CATEGORY + "updateCategory",
                request_data,
                true,
                AddEditCategoryActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());
                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            onBackPressed();
                                        }
                                    }
                                });
                                // mGlobals.dismissLoadingDialog();


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void validateFields() {
        edtCategoryName.addTextChangedListener(new MyTextWatcher(edtCategoryName));

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

                case R.id.edt_category_name:
                    mGlobals.validateField(tilCategoryName, edtCategoryName, true, getString(R.string.err_msg_category_name));
                    break;

            }
        }
    }
}
