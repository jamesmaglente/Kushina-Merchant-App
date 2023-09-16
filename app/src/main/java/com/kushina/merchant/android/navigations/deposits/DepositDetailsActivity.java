package com.kushina.merchant.android.navigations.deposits;

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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_ECASH;

public class DepositDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_deposit_details_image)
    ImageView ivDepositDetailsImage;
    @BindView(R.id.tv_deposit_details_amount)
    TextView tvDepositDetailsAmount;
    @BindView(R.id.tv_deposit_details_deposit_method)
    TextView tvDepositDetailsDepositMethod;
    @BindView(R.id.tv_deposit_details_status)
    TextView tvDepositDetailsStatus;
    @BindView(R.id.tv_deposit_details_deposit_id)
    TextView tvDepositDetailsDepositId;
    @BindView(R.id.tv_deposit_details_date_submitted)
    TextView tvDepositDetailsDateSubmitted;
    @BindView(R.id.btn_deny_deposit)
    Button btnDenyDeposit;
    @BindView(R.id.btn_approve_deposit)
    Button btnApproveDeposit;
    @BindView(R.id.tv_deposit_details_customer_name)
    TextView tvDepositDetailsCustomerName;
    @BindView(R.id.tv_deposit_customer_number)
    TextView tvDepositCustomerNumber;


    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.ll_deposit_buttons)
    LinearLayout llDepositButtons;

    private String depositID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_details);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        try {
            Picasso.get()
                    .load(getIntent().getStringExtra("proof_of_payment_image"))
                    //.load(item.getItemImage())
                    .resize(400, 400)
                    .placeholder(R.drawable.applogo)
                    .into(ivDepositDetailsImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        depositID = getIntent().getStringExtra("deposit_id");

        tvDepositDetailsAmount.setText(mGlobals.moneyFormatter(getIntent().getStringExtra("deposit_amount")));
        tvDepositDetailsDepositMethod.setText(getIntent().getStringExtra("deposit_method"));
        tvDepositDetailsDepositId.setText(getIntent().getStringExtra("deposit_id"));
        tvDepositDetailsStatus.setText(getIntent().getStringExtra("deposit_status"));
        tvDepositDetailsDateSubmitted.setText(getIntent().getStringExtra("date"));
        tvDepositCustomerNumber.setText(getIntent().getStringExtra("customer_number"));
        tvDepositDetailsCustomerName.setText(getIntent().getStringExtra("customer_fullname"));

        if(getIntent().getStringExtra("deposit_status").toLowerCase().equals("approved")){
            tvDepositDetailsStatus.setTextColor(getResources().getColor(R.color.colorSuccess));
        }else{
            tvDepositDetailsStatus.setTextColor(getResources().getColor(R.color.colorError));
        }

        if (!getIntent().getStringExtra("deposit_status").toLowerCase().equals("pending")) {
                llDepositButtons.setVisibility(View.GONE);
        }

        ivDepositDetailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepositDetailsActivity.this,ZoomImageActivity.class);
                intent.putExtra("image",getIntent().getStringExtra("proof_of_payment_image"));
                startActivity(intent);
            }
        });

    }

    @OnClick({R.id.btn_deny_deposit, R.id.btn_approve_deposit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_deny_deposit:
                mGlobals.showChoiceDialogwithCustomTitle("Deny", "Are you sure you want to deny this deposit request?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if (result) {
                            updateStatus("denied");
                        }
                    }
                });
                break;
            case R.id.btn_approve_deposit:
                mGlobals.showChoiceDialogwithCustomTitle("Approve", "Are you sure you want to approve this deposit request?", true, new Globals.Callback() {
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
        request_data.put("deposit_id", depositID);
        request_data.put("status", status);


        mAPI.api_request("POST",
                API_NODE_ECASH + "updateDepositStatus",
                request_data,
                true,
                DepositDetailsActivity.this,
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
