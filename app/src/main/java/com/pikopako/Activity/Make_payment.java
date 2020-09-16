package com.pikopako.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.pikopako.Adapter.My_favourite_list_Adapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Make_payment extends AppCompatActivity {

    @BindView(R.id.rb_cashondeleivery)
    RadioButton rb_casOnDeleivery;

    @BindView(R.id.radiogrp)
    RadioGroup radioGroup;

    @BindView(R.id.rb_paypal)
    RadioButton rb_paypal;

    @BindView(R.id.rb_creditcard)
    RadioButton rb_creditcard;

    @BindView(R.id.toplinear)
    LinearLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.btn_makePayment)
    Button btn_makePayment;

    String total, restaurant_id;
    private String paymentAmount;
    public static final int PAYPAL_REQUEST_CODE = 123;
    JsonObject payload = new JsonObject();

    private String radioButton_checked = "";
    String language = "";
    String restro_image, restro_location, restro_name, restro_status;
    String houseno, landmark, location, address_title, coordinate_id, user_type, remarks, delivery_time;
    double latitude, longitude;
    float deleivery_charge, promo_code_id, promo_code_price, discount_amount, paid_amount;
    String isPromoApplied = "";
    String user_name = "";
    String user_contact = "";
    String user_email = "";
    //Paypal Configuration Object
    String cart_id;
    private static PayPalConfiguration payPalConfiguration;
    String PAYPAL_CLIENT_ID = "AWenZtgj2qX5Z2je9ukBJuGNbEhgLvFWDZxsCqQhJ2nqvUUrXptFmMmEEjpW4LYDQbUIRGX23ayzjMhv";

    private RadioButton radioButton;


    private void getCartData() {
        Intent intent = getIntent();
        //

        if (!BaseApplication.getInstance().getSession().isLoggedIn()) {
            user_type = "Guest";
        } else user_type = "Site";

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
        if (intent.hasExtra("name") && intent.hasExtra("contact") && intent.hasExtra("email")) {
            user_name = intent.getStringExtra("name");
            user_contact = intent.getStringExtra("contact");
            user_email = intent.getStringExtra("email");

        }

        Log.e("Tag", "intent get: " + "is promo Apllied" + isPromoApplied + "dfs" + landmark + "location" + location + "restro id" + restaurant_id);

        //Simplified cart items
        try {

            JSONArray simplifiedJsonArray = new JSONArray(BaseApplication.getInstance().getSession().getSimplifiedCartData());

            //payload making

            payload.addProperty("house_number", houseno);
            payload.addProperty("landmark", landmark);
            payload.addProperty("address", location);
            payload.addProperty("latitude", latitude);
            payload.addProperty("longitude", longitude);
            payload.addProperty("paid_amount", paid_amount);
            payload.addProperty("discount_amount", discount_amount);
            payload.addProperty("is_promo_code_applied", isPromoApplied);
            payload.addProperty("delivery_charge", deleivery_charge);
            payload.addProperty("payment_method", radioButton_checked);
            Log.e("payload ", "radio button state:- : " + radioButton_checked);
            payload.addProperty("address_title", address_title);
            payload.addProperty("promo_code_id", promo_code_id);
            payload.addProperty("promo_code_price", promo_code_price);
            payload.addProperty("coordinate_id", coordinate_id);
            payload.addProperty("restaurant_id", restaurant_id);
            payload.addProperty("language", language);
            payload.addProperty("user_type", user_type);
            payload.addProperty("remarks", remarks);
            payload.addProperty("delivery_time", delivery_time);

            //      if (!BaseApplication.getInstance().getSession().isLoggedIn()){
            //        JsonObject userinfo_object=new JsonObject();
            payload.addProperty("name", user_name);
            payload.addProperty("contact_number", user_contact);
            payload.addProperty("email", user_email);
            //      payload.add("user_information",userinfo_object);
            //   }
            //product
            JsonArray productArray = new JsonArray();


            JSONObject simplified_product_object = new JSONObject();
            for (int i = 0; i < simplifiedJsonArray.length(); i++) {

                simplified_product_object = simplifiedJsonArray.getJSONObject(i);

                JsonObject productObject = new JsonObject();
                productObject.addProperty("food_item_id", simplified_product_object.getString("product_id"));
                productObject.addProperty("price", simplified_product_object.getString("product_price"));
                productObject.addProperty("discount_price", simplified_product_object.getString("product_discount"));
                productObject.addProperty("quantity", simplified_product_object.getString("product_quantity"));

                JsonArray toppingsArray = new JsonArray();
                for (int j = 0; j < simplified_product_object.getJSONArray("toppings").length(); j++) {

                    JsonObject toppings_object = new JsonObject();

                    toppings_object.addProperty("topping_id", simplified_product_object.getJSONArray("toppings").getJSONObject(j).getString("id"));
                    toppings_object.addProperty("topping_name", simplified_product_object.getJSONArray("toppings").getJSONObject(j).getString("name"));
                    toppings_object.addProperty("topping_price", simplified_product_object.getJSONArray("toppings").getJSONObject(j).getString("price"));

                    toppingsArray.add(toppings_object);
                }

                productObject.add("toppings", toppingsArray);
                productArray.add(productObject);
            }

            payload.add("product", productArray);
            Log.e("Bug", "Final Payload :-=>" + payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_payment);
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        listners();

        payPalConfiguration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                .clientId(PAYPAL_CLIENT_ID);


        getCartData();


        //  restaurant_id=getIntent().getStringExtra("restaurant_id");
        Log.e("kitne paise", "onCreate: " + getIntent().getExtras().getFloat("paid_amount"));
        // total=getIntent().getExtras().getString("total");


        rb_paypal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rb_paypal.setChecked(true);
                rb_paypal.setTextColor(getResources().getColor(R.color.colorPrimary));
                rb_casOnDeleivery.setTextColor(getResources().getColor(R.color.black));
                rb_creditcard.setTextColor(getResources().getColor(R.color.black));

                Intent intent = new Intent(Make_payment.this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
                startService(intent);

                rb_casOnDeleivery.setChecked(false);
                btn_makePayment.setText(R.string.pay_using_paypal);
                rb_creditcard.setChecked(false);

                radioButton_checked = getString(R.string.payment_by_paypal);
                Log.e("radio button paypal", "onCreate: " + radioButton_checked);
            }
        });

        rb_casOnDeleivery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btn_makePayment.setText(R.string.pay_using_cash);

                rb_casOnDeleivery.setTextColor(getResources().getColor(R.color.colorPrimary));
                rb_paypal.setTextColor(getResources().getColor(R.color.black));
                rb_creditcard.setTextColor(getResources().getColor(R.color.black));

                rb_casOnDeleivery.setChecked(true);
                rb_paypal.setChecked(false);
                rb_creditcard.setChecked(false);

                radioButton_checked = getString(R.string.payment_by_cash);
                Log.e("radio button cash", "onCreate: " + radioButton_checked);
            }
        });

        rb_creditcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_makePayment.setText(R.string.pay_using_credit_debit);

                rb_creditcard.setTextColor(getResources().getColor(R.color.colorPrimary));
                rb_paypal.setTextColor(getResources().getColor(R.color.black));
                rb_casOnDeleivery.setTextColor(getResources().getColor(R.color.black));


                rb_casOnDeleivery.setChecked(false);
                rb_paypal.setChecked(false);
                rb_creditcard.setChecked(true);

                radioButton_checked = getString(R.string.payment_by_card);
                Log.e("radio button credit", "onCreate: " + radioButton_checked);
            }
        });


        if (rb_paypal.isChecked()) {
            btn_makePayment.setText(R.string.pay_using_paypal);

        } else if (rb_casOnDeleivery.isChecked()) {
            btn_makePayment.setText(R.string.pay_using_cash);

        } else if (rb_creditcard.isChecked()) {
            btn_makePayment.setText(R.string.pay_using_credit_debit);

        }

        btn_makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payload.addProperty("payment_method", radioButton_checked);
                if (rb_casOnDeleivery.isChecked()) {

                    callApi();
                } else if (rb_paypal.isChecked()) {
                    Log.e("tag paypal", "onClick: ");

                    callApi_paypal();
                    // getPayment();
                } else if (rb_creditcard.isChecked()) {
                    Intent intent1 = new Intent(Make_payment.this, Card_detailActivity.class);

                    intent1.putExtra("house_number", houseno);
                    intent1.putExtra("landmark", landmark);
                    intent1.putExtra("address", location);
                    intent1.putExtra("latitude", latitude);
                    intent1.putExtra("longitude", longitude);
                    intent1.putExtra("paid_amount", paid_amount);
                    intent1.putExtra("discount_amount", discount_amount);
                    intent1.putExtra("is_promo_code_applied", isPromoApplied);
                    intent1.putExtra("delivery_charge", deleivery_charge);
                    intent1.putExtra("payment_method", radioButton_checked);

                    intent1.putExtra("address_title", address_title);
                    intent1.putExtra("promo_code_id", promo_code_id);
                    intent1.putExtra("promo_code_price", promo_code_price);
                    intent1.putExtra("coordinate_id", BaseApplication.getInstance().getSession().getCoordinateId());
                    intent1.putExtra("restaurant_id", BaseApplication.getInstance().getSession().getRestroId());
                    intent1.putExtra("restro_image", restro_image);
                    intent1.putExtra("restro_location", restro_location);
                    intent1.putExtra("restro_name", restro_name);
                    intent1.putExtra("restro_status", restro_status);
                    intent1.putExtra("user_type", user_type);
                    intent1.putExtra("remarks", remarks);
                    intent1.putExtra("delivery_time", delivery_time);
                    if (!BaseApplication.getInstance().getSession().isLoggedIn()) {

                        intent1.putExtra("name", user_name);
                        intent1.putExtra("contact", user_contact);
                        intent1.putExtra("email", user_email);
                    }

                    startActivity(intent1);
                }
            }
        });
    }


    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.payment_option);
    }

    private void getPayment() {
        //Getting the amount from editText
        paymentAmount = getIntent().getExtras().getString("paid_amount");

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(paymentAmount), "USD", "Pika Pako food",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    private void callApi() {

        Log.e("TAG", "callApi: cash " + payload);
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addCart(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                Log.e("TAG", "Success: " + jsonObject);
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            //       UiHelper.showToast(Make_payment.this,jsonObject1.getString("message"));

                            String cart_id = jsonObject1.getJSONObject("data").getString("cart_id");
                            Intent intent = new Intent(Make_payment.this, ConfirmedOrder.class);
                            intent.putExtra("cart_id", cart_id);
                            intent.putExtra("restro_image", restro_image);
                            intent.putExtra("restro_location", restro_location);
                            intent.putExtra("restro_name", restro_name);
                            intent.putExtra("restro_status", restro_status);
                            intent.putExtra("restaurant_id", restaurant_id);
                            intent.putExtra("delivery_time", delivery_time);
                            BaseApplication.getInstance().getSession().setCartItmes("");
                            BaseApplication.getInstance().getSession().setSimplifiedCartData("");

                            startActivity(intent);
                            Toast.makeText(Make_payment.this, R.string.str_order_successfully, Toast.LENGTH_SHORT).show();
                            finish();

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
                    UiHelper.showNetworkError(Make_payment.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }


    private void callApi_paypal() {
        Log.e("tag", "callApi_paypal: " + radioButton_checked);
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addCart(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            //     UiHelper.showToast(Make_payment.this,jsonObject1.getString("message"));

                            cart_id = jsonObject1.getJSONObject("data").getString("cart_id");


                            paymentAmount = String.valueOf(getIntent().getExtras().getFloat("paid_amount"));

                            //Creating a paypalpayment
                            PayPalPayment payment = new PayPalPayment(new BigDecimal(paymentAmount), "EUR", "PikoPako",
                                    PayPalPayment.PAYMENT_INTENT_SALE);

                            //Creating Paypal Payment activity intent
                            Intent intent = new Intent(Make_payment.this, PaymentActivity.class);

                            //putting the paypal configuration to the intent
                            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);

                            //Puting paypal payment to the intent
                            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                            intent.putExtra("invoice_number", cart_id);

                            Log.e("cartid", "Success: " + cart_id);
                            intent.putExtra("restro_image", restro_image);
                            intent.putExtra("restro_location", restro_location);
                            intent.putExtra("restro_name", restro_name);
                            intent.putExtra("restro_status", restro_status);
                            intent.putExtra("delivery_time", delivery_time);
                            //Starting the intent activity for result
                            //the request code will be used on the method onActivityResult

//                            Intent intent=new Intent(Make_payment.this,ConfirmedOrder.class);


                            BaseApplication.getInstance().getSession().setCartItmes("");
                            BaseApplication.getInstance().getSession().setSimplifiedCartData("");
                            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                            //      startActivity(intent);

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
                    UiHelper.showNetworkError(Make_payment.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentDetails", paymentDetails);
                        Log.i("paymentDetails obj", String.valueOf(confirm.toJSONObject()));

                        callApiAfterPaypal(paymentDetails);


                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void callApiAfterPaypal(String paymentDetails) throws JSONException {
        //get data from paypal json response
        JSONObject object = new JSONObject(paymentDetails);
        String transaction_id = object.getJSONObject("response").getString("id");
        Log.e("====>", "callApiAfterPaypal: "+transaction_id );

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("transaction_id", transaction_id);
        jsonObject.addProperty("cart_id", cart_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().apiPayPalPayment(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //Starting a new activity for the payment details and also putting the payment details with intent
                            startActivity(new Intent(Make_payment.this, ConfirmedOrder.class)
                                    .putExtra("PaymentDetails", paymentDetails)
                                    .putExtra("delivery_time", delivery_time)
                                    .putExtra("PaymentAmount", paymentAmount));
                            Toast.makeText(Make_payment.this, R.string.str_order_successfully, Toast.LENGTH_SHORT).show();

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
//                UiHelper.showErrorMessage(mSnackView,error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
//                    UiHelper.showNetworkError(FoodDetailActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }


        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
