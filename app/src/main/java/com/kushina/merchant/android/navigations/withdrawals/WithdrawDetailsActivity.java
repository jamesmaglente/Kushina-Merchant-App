package com.kushina.merchant.android.navigations.withdrawals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.navigations.deposits.DepositDetailsActivity;
import com.kushina.merchant.android.navigations.deposits.ZoomImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ECASH;

public class WithdrawDetailsActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.iv_withdraw_details_image)
    ImageView ivWithdrawDetailsImage;
    @BindView(R.id.tv_withdraw_details_amount)
    TextView tvWithdrawDetailsAmount;
    @BindView(R.id.tv_withdraw_details_account_name)
    TextView tvWithdrawDetailsAccountName;
    @BindView(R.id.tv_withdraw_details_account_number)
    TextView tvWithdrawDetailsAccountNumber;
    @BindView(R.id.tv_withdraw_details_withdraw_method)
    TextView tvWithdrawDetailsWithdrawMethod;
    @BindView(R.id.tv_withdraw_details_status)
    TextView tvWithdrawDetailsStatus;
    @BindView(R.id.tv_withdraw_details_withdraw_id)
    TextView tvWithdrawDetailsWithdrawId;
    @BindView(R.id.tv_withdraw_details_date_submitted)
    TextView tvWithdrawDetailsDateSubmitted;
    @BindView(R.id.tv_withdraw_details_customer_name)
    TextView tvWithdrawDetailsCustomerName;
    @BindView(R.id.tv_withdraw_details_customer_number)
    TextView tvWithdrawDetailsCustomerNumber;
    @BindView(R.id.btn_deny_withdraw)
    Button btnDenyWithdraw;
    @BindView(R.id.btn_approve_withdraw)
    Button btnApproveWithdraw;
    @BindView(R.id.ll_withdraw_buttons)
    LinearLayout llWithdrawButtons;

    private String withdrawID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_details);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        withdrawID = getIntent().getStringExtra("withdraw_id");

//        try {
//            Picasso.get()
//                    .load(getIntent().getStringExtra("proof_of_payment_image"))
//                    //.load(item.getItemImage())
//                    .resize(400, 400)
//                    .placeholder(R.drawable.applogo)
//                    .into(ivDepositDetailsImage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




        tvWithdrawDetailsAmount.setText(mGlobals.moneyFormatter(getIntent().getStringExtra("withdraw_amount")));
        tvWithdrawDetailsAccountName.setText(getIntent().getStringExtra("account_name"));
        tvWithdrawDetailsAccountNumber.setText(getIntent().getStringExtra("account_number"));
        tvWithdrawDetailsWithdrawMethod.setText(getIntent().getStringExtra("withdraw_method"));
        tvWithdrawDetailsWithdrawId.setText(getIntent().getStringExtra("code_id"));
        tvWithdrawDetailsStatus.setText(getIntent().getStringExtra("withdraw_status"));
        tvWithdrawDetailsDateSubmitted.setText(getIntent().getStringExtra("date"));
        tvWithdrawDetailsCustomerNumber.setText(getIntent().getStringExtra("customer_number"));
        tvWithdrawDetailsCustomerName.setText(getIntent().getStringExtra("customer_fullname"));

        if(getIntent().getStringExtra("withdraw_status").toLowerCase().equals("approved")){
            tvWithdrawDetailsStatus.setTextColor(getResources().getColor(R.color.colorSuccess));
        }else{
            tvWithdrawDetailsStatus.setTextColor(getResources().getColor(R.color.colorError));
        }

        if (!getIntent().getStringExtra("withdraw_status").toLowerCase().equals("pending")) {
            llWithdrawButtons.setVisibility(View.GONE);
        }

        ivWithdrawDetailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawDetailsActivity.this, ZoomImageActivity.class);
                intent.putExtra("image",getIntent().getStringExtra("proof_of_payment_image"));
                startActivity(intent);
            }
        });

    }

    @OnClick({R.id.btn_deny_withdraw, R.id.btn_approve_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_deny_withdraw:
                mGlobals.showChoiceDialogwithCustomTitle("Deny", "Are you sure you want to deny this withdraw request?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if (result) {
                            updateStatus("denied");
                        }
                    }
                });
                break;
            case R.id.btn_approve_withdraw:
                mGlobals.showChoiceDialogwithCustomTitle("Approve", "Are you sure you want to approve this withdraw request?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if (result) {
                            updateStatus("approved");
                        }
                    }
                });
                break;
        }
    }

    private void updateStatus(String status) {

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("withdraw_id", withdrawID);
        request_data.put("status", status);
        if(status.toLowerCase().equals("approved")){
            request_data.put("approved_image", "eqeqeq");
        }


        mAPI.api_request("POST",
                API_NODE_ECASH + "updateWithdrawStatus",
                request_data,
                true,
                WithdrawDetailsActivity.this,
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
                                            onBackPressed();
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
}