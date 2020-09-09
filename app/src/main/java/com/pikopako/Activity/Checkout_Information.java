package com.pikopako.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
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

public class Checkout_Information extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.edt_name)
    CustomEditTextBold edt_name;

    @BindView(R.id.edt_email)
    CustomEditTextBold edt_email;

    @BindView(R.id.edt_contact)
    CustomEditTextBold edt_contact;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.dd)
    ScrollView mSnackView;

    String language = "";

    String restro_image, restro_location, restro_name, restro_status, restaurant_id, remarks, delivery_time;
    String houseno, landmark, location, address_title, coordinate_id, user_name, user_contact, user_email, user_type;
    double latitude, longitude;
    float deleivery_charge, promo_code_id, promo_code_price, discount_amount, paid_amount;
    String isPromoApplied = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_information_layout);
        ButterKnife.bind(this);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";

        setActionBarTitle();
        getData();
    }


    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.checkout));
        btn_submit.setOnClickListener(this);

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
                if (edt_name.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this, getString(R.string.pls_enter_name));
                } else if (edt_name.getText().toString().trim().length() < 3) {
                    UiHelper.showToast(this, getString(R.string.name_should_3minimum_chrcters));
                } else if (edt_name.getText().toString().trim().length() > 35) {
                    UiHelper.showToast(this, getString(R.string.name_cannot_exceed));
                } else if (edt_email.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this, getString(R.string.pls_enter_email));
                } else if (!UiHelper.isValidEmail(edt_email.getText().toString().trim())) {
                    UiHelper.showToast(this, getString(R.string.pls_provide_valid_email));
                } else if (edt_contact.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this, getString(R.string.pls_enter_contact_no));
                } else {

                    JsonObject jsonObject = new JsonObject();
                    //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
                    jsonObject.addProperty("name", edt_name.getText().toString().trim());
                    jsonObject.addProperty("email", edt_email.getText().toString().trim());
                    jsonObject.addProperty("phone_number", edt_contact.getText().toString().trim());
                    postGuestUserRegister(jsonObject);
                }




                break;
        }
    }


    private void postGuestUserRegister(JsonObject jsonObject) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().postGuestUserRegister(jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            senddetail();

//                            AlertDialog.Builder builder = new AlertDialog.Builder(Checkout_Information.this);
//                            builder.setTitle(getString(R.string.app_name));
//                            builder.setMessage(getResources().getString(R.string.your_account_is_not_verified));
//                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    senddetail();
//                                }
//                            });
//                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                           AlertDialog alertDialog = builder.create();
//                            alertDialog.show();
//



                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
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
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(Checkout_Information.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

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
        Log.e("tag", "getData: " + houseno + " latitude" + latitude + "deleivrychrg" + deleivery_charge);

    }

    private void senddetail() {

        String name = edt_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String contact = edt_contact.getText().toString().trim();


        Intent intent = new Intent(this, Make_payment.class); //OTP ver ACT
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("contact", contact);
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

}
