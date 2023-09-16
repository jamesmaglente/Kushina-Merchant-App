package com.kushina.merchant.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.navigations.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.merchant.android.globals.Endpoints.API_NODE_LOGIN;

public class LoginActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    @BindView(R.id.edt_login_email) TextInputEditText edtLoginEmail;
    @BindView(R.id.til_login_email) TextInputLayout tilLoginEmail;
    @BindView(R.id.edt_login_password) TextInputEditText edtLoginPassword;
    @BindView(R.id.til_login_password) TextInputLayout tilLoginPassword;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_login_forgot_password) TextView btnLoginForgotPassword;
    @BindView(R.id.btn_login_create_account) TextView btnLoginCreateAccount;
    @BindView(R.id.tv_or) TextView tvOr;
    @BindView(R.id.tv_version) TextView tvVersion;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                       // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    //    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle("Login");

        btnLoginForgotPassword.setVisibility(View.GONE);
        tvOr.setVisibility(View.GONE);

        String current_version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText(current_version);
    }

    @OnClick({R.id.btn_login, R.id.btn_login_forgot_password, R.id.btn_login_create_account})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_login_forgot_password:
                break;
        }
    }

    private void login(){
        //mGlobals.showLoadingDialog();

        if (!mGlobals.validateField(tilLoginEmail, edtLoginEmail, true, getString(R.string.err_msg_email)) ||
                !mGlobals.validateField(tilLoginPassword, edtLoginPassword, true, getString(R.string.err_msg_password))) {
//            mGlobals.dismissLoadingDialog();
            return;

        } else {

            final Map<String, String> request_data = new HashMap<String, String>();

            request_data.put("email", edtLoginEmail.getText().toString().trim()); //Add the data you'd like to send to the server.
            request_data.put("password", edtLoginPassword.getText().toString().trim());
            request_data.put("application", "kushina_admin");

            // api call
            mAPI.api_request("POST",
                    API_NODE_LOGIN,
                    request_data,
                    true,
                    LoginActivity.this,
                    new API.VolleyCallback(){
                        @Override
                        public void onResponseCallback(JSONObject jsonObject){
                            mGlobals.log(TAG, String.valueOf(jsonObject));
                            try {
                                // parse response object
                                Integer status_code = jsonObject.getInt("status_code");
                                boolean ok = jsonObject.getBoolean("status_ok");
                                String message = jsonObject.getString("status_message");

                                if(ok){

                                    JSONObject data = jsonObject.getJSONObject("data");

                                    // get each data from response object
                                    Integer user_id = Integer.parseInt(data.get("user_id").toString());
                                    String username = data.get("username").toString();
                                    String first_name = data.get("firstname").toString();
                                    String last_name = data.get("lastname").toString();
                                    String full_name = first_name + " " + last_name;
                                    String email = data.get("email").toString();
                                    Integer user_group_id = Integer.parseInt(data.get("user_group_id").toString());
                                    String code = data.get("code").toString();
                                    String token = data.get("token").toString();
                                    String profile_picture = data.get("profile_picture").toString();
                                    String mobile = data.get("mobile").toString();
                                    String membership_type_id = data.get("membership_type_id").toString();
                                    String membership_type = data.get("membership_type").toString();

                                    // mGlobals.toast(token);

                                    // save share preferences
                                    mPreferences.setIsLogged(true);
                                    mPreferences.setUserId(user_id);
                                    mPreferences.setUserToken(token);
                                    mPreferences.setUserCode(code);
                                    mPreferences.setUsername(username);
                                    mPreferences.setFirstname(first_name);
                                    mPreferences.setLastname(last_name);
                                    mPreferences.setEmail(email);
                                    mPreferences.setUserProfilePicture(profile_picture);
                                    mPreferences.setMembershipTypeId(membership_type_id);
                                    mPreferences.setMembershipType(membership_type);
                                    mPreferences.setMobile(mobile);


                                    //   mGlobals.dismissLoadingDialog();
                                    // move to next activity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    // show welcome back message
                                    mGlobals.toast("Welcome back " + full_name + "!");
                                }else{
                                    // show error
                                    // mGlobals.dismissLoadingDialog();
                                    mGlobals.showErrorMessage(message,
                                            true,
                                            new Globals.Callback() {
                                                @Override
                                                public void onPickCallback(Boolean result) {
                                                    if (result) {
                                                        return;
                                                    }
                                                }
                                            }
                                    );
                                }
                            }catch(Exception e) {
                                // show exception error
                                //    mGlobals.dismissLoadingDialog();
                                Log.d(TAG, jsonObject+e.toString());
                                mGlobals.dialog(jsonObject+e.toString());
                            }
                        }
                    });

        }
    }
}
