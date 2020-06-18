package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chaos.view.PinView;
import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class OtpVerificationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.txt_email)
    TextView txt_email;

    @BindView(R.id.textView4)
    TextView txt_resend;

    PinView edt_otp;

    String language = "";

    String restro_image, restro_location, restro_name, restro_status, restaurant_id, remarks, delivery_time;
    String houseno, landmark, location, address_title, coordinate_id, user_name, user_contact, user_email, user_type;
    double latitude, longitude;
    float deleivery_charge, promo_code_id, promo_code_price, discount_amount, paid_amount;
    String isPromoApplied = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        setActionBarTitle();
        edt_otp = findViewById(R.id.edt_otp);


        if (Constant.isComingFromLogin) {

            Log.i("TAG", "coming from " + getIntent().getStringExtra("cart") + " , " + getIntent().getStringExtra("cartfragment"));
            user_email = getIntent().getStringExtra("email");
            txt_email.setText(Html.fromHtml(getResources().getString(R.string.sendto) +"<b>" + " "+user_email+"</b>"));
            user_name = getIntent().getStringExtra("name");
            user_contact = getIntent().getStringExtra("contact_number");


        }
        else if (Constant.isComingFromRegister || getIntent().hasExtra("SetLocation")){

          //  Log.i("TAG", "coming from " + getIntent().getStringExtra("cart") + " , " + getIntent().getStringExtra("cartfragment"));
            user_email = getIntent().getStringExtra("email");


            txt_email.setText(Html.fromHtml(getResources().getString(R.string.sendto) +"<b>"+" "+user_email+"</b>"));
            user_name = getIntent().getStringExtra("name");
            user_contact = getIntent().getStringExtra("contact_number");

        }

        else {

            getData();
            txt_email.setText(Html.fromHtml(getResources().getString(R.string.sendto) + "<b>"+" "+user_email+"</b>"));
        }

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";
    }

    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getResources().getString(R.string.otpverification));
        btn_submit.setOnClickListener(this);
        txt_resend.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_submit:

                if (Constant.isComingFromRegister ||  Constant.isComingFromLogin || getIntent().hasExtra("SetLocation")){


                    if (edt_otp.getText().toString().trim().length() < 4) {
                        UiHelper.showToast(OtpVerificationActivity.this, getResources().getString(R.string.pls_enter_otp));
                    } else {
                        JsonObject jsonObject = new JsonObject();
                        //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
                        jsonObject.addProperty("email", user_email);
                        jsonObject.addProperty("otp", edt_otp.getText().toString().trim());
                        jsonObject.addProperty("language", language);
                        jsonObject.addProperty("device_token",BaseApplication.getInstance().getSession().getDeviceToken());
                        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
                        Log.i("TAG", "request for otp: " + jsonObject.toString());
                        postVerifyOtpForRegister(jsonObject);
                    }


                }else {
                    if (edt_otp.getText().toString().trim().length() < 4) {
                        UiHelper.showToast(OtpVerificationActivity.this, getResources().getString(R.string.pls_enter_otp));
                    } else {
                        JsonObject jsonObject = new JsonObject();
                        //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
                        jsonObject.addProperty("email", user_email);
                        jsonObject.addProperty("otp", edt_otp.getText().toString().trim());
                        Log.i("TAG", "request for otp: " + jsonObject.toString());
                        jsonObject.addProperty("language", language);
                        postVerifyOtp(jsonObject);
                    }
                }


                break;

            case R.id.textView4:

                if (Constant.isComingFromRegister){
                    callApiforResendForRegister();

                }else {
                    callApiforResend();
                }



                break;

        }
    }

    private void callApiforResendForRegister() {


        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
        jsonObject.addProperty("name", user_name);
        jsonObject.addProperty("email", user_email);
        jsonObject.addProperty("phone_number", user_contact);
        jsonObject.addProperty("language", language);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().postResendOtpForRegister(jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                                 UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));

                        } else {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(OtpVerificationActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(OtpVerificationActivity.this, getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

    private void postVerifyOtpForRegister(JsonObject jsonObject) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().postVerifyOtpForRegister(jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));


                            BaseApplication.getInstance().getSession().setIsLoggedIn();
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));
                            BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));

                                Intent intent=new Intent();
                                if (getIntent().hasExtra("viewcart")) {
                                    Constant.isComingFromRegister=false;
                                    Constant.isComingFromLogin=false;
//                                  Log.e("login extra", "Success: "+getIntent().getStringExtra("cart") );
                                    boolean items = false;
                                    intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent.putExtra("viewcart", items);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra(Constant.IS_SIGNUP, true);

                                } else if (getIntent().hasExtra("cartfragment")) {

                                    Constant.isComingFromRegister=false;
                                    Constant.isComingFromLogin=false;

                                    Log.e("login extra", "Success: ");
                                    boolean items = false;
                                    intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent.putExtra("cartfragment", items);
                                    intent.putExtra(Constant.IS_SIGNUP, true);

                                }

                                else if (getIntent().hasExtra("SetLocation")){

                                    Intent intent4 = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent4.putExtra(Constant.IS_SIGNUP, true);
                                    startActivity(intent4);
                                    finishAffinity();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                }

                                else
                                {
                                    if (Constant.isComingFromRegister){

                                        Constant.isComingFromRegister=false;
                                        intent = new Intent(OtpVerificationActivity.this, ConfirmLocationActivity.class);
                                        intent.putExtra(Constant.IS_SIGNUP,true);

                                    }if (Constant.isComingFromLogin){

                                    Constant.isComingFromLogin=false;
                                    intent = new Intent(OtpVerificationActivity.this, ConfirmLocationActivity.class);
                                    intent.putExtra(Constant.IS_SIGNUP,true);
                                }

                                }

                                startActivity(intent);
                                finishAffinity();
                                //Log.e("tag","data:-"+jsonObject1.getJSONObject("data"));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(OtpVerificationActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(OtpVerificationActivity.this, getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }


    private void callApiforResend() {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
        jsonObject.addProperty("name", user_name);
        jsonObject.addProperty("email", user_email);
        jsonObject.addProperty("phone_number", user_contact);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().postGuestUserRegister(jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //     UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));

                        } else {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(OtpVerificationActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(OtpVerificationActivity.this, getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });


    }

    private void postVerifyOtp(JsonObject jsonObject) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().postVerifyOtp(jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));

                            if (Constant.isComingFromLogin) {
                                Constant.isComingFromLogin = false;

                                BaseApplication.getInstance().getSession().setIsLoggedIn();
                                Intent intent;
                                if (getIntent().hasExtra("viewcart")) {
//                                  Log.e("login extra", "Success: "+getIntent().getStringExtra("cart") );
                                    boolean items = false;
                                    intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent.putExtra("viewcart", items);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra(Constant.IS_SIGNUP, true);

                                } else if (getIntent().hasExtra("cartfragment")) {
                                    Log.e("login extra", "Success: ");
                                    boolean items = false;
                                    intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent.putExtra("cartfragment", items);
                                    intent.putExtra(Constant.IS_SIGNUP, true);

                                } else {
                                    intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                    intent.putExtra(Constant.IS_SIGNUP, true);
                                }


                                startActivity(intent);
                                finish();
                                //Log.e("tag","data:-"+jsonObject1.getJSONObject("data"));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                            } else {
                                senddetail();
                            }


                        } else {
                            UiHelper.showToast(OtpVerificationActivity.this, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showToast(OtpVerificationActivity.this, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(OtpVerificationActivity.this, getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

    private void senddetail() {

       /* String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String contact = edt_contact.getText().toString().trim();*/

        Intent intent = new Intent(this, Make_payment.class);
        intent.putExtra("name", user_name);
        intent.putExtra("email", user_email);
        intent.putExtra("contact", user_contact);
        intent.putExtra("paid_amount", paid_amount);
        intent.putExtra("discount_amount", discount_amount);
        intent.putExtra("delivery_charge", deleivery_charge);
        intent.putExtra("is_promo_code_applied", isPromoApplied);
        intent.putExtra("house_number", houseno);
        intent.putExtra("landmark", landmark);
        intent.putExtra("address", location);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("restaurant_id", restaurant_id);
        intent.putExtra("address_title", address_title);
        intent.putExtra("coordinate_id", coordinate_id);
        intent.putExtra("promo_code_id", promo_code_id);
        intent.putExtra("promo_code_price", promo_code_price);
        intent.putExtra("restro_image", restro_image);
        intent.putExtra("restro_location", restro_location);
        intent.putExtra("restro_name", restro_name);
        intent.putExtra("restro_status", restro_status);
        intent.putExtra("remarks", remarks);
        intent.putExtra("delivery_time", delivery_time);
        Log.e("tag", "senddetail: " + restro_image + "  ff" + paid_amount);

        startActivity(intent);
        finish();

    }

    private void getData() {

        Intent intent = getIntent();

        houseno = intent.getStringExtra("house_number");
        landmark = intent.getStringExtra("landmark");
        location = intent.getStringExtra("address");
        paid_amount = intent.getFloatExtra("paid_amount", 0);
        coordinate_id = intent.getStringExtra("coordinate_id");
        discount_amount = intent.getFloatExtra("discount_amount", 0);
        address_title = intent.getStringExtra("address_title");
        promo_code_id = intent.getFloatExtra("promo_code_id", 0);
        promo_code_price = intent.getFloatExtra("promo_code_price", 0);
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        deleivery_charge = intent.getFloatExtra("delivery_charge", 0);
        isPromoApplied = intent.getStringExtra("is_promo_code_applied");
        restro_image = intent.getStringExtra("restro_image");
        restro_location = intent.getStringExtra("restro_location");
        restro_name = intent.getStringExtra("restro_name");
        restro_status = intent.getStringExtra("restro_status");
        restaurant_id = intent.getStringExtra("restaurant_id");
        remarks = intent.getStringExtra("remarks");
        delivery_time = intent.getStringExtra("delivery_time");
        user_email = intent.getStringExtra("email");
        user_name = intent.getStringExtra("name");
        user_contact = intent.getStringExtra("contact");

        Log.e("tag", "getData: " + houseno + " latitude" + latitude + "deleivrychrg" + deleivery_charge);

    }

}
