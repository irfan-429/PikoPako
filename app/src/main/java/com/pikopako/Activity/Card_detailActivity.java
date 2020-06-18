package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Card_detailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.btn_Pay)
    Button btn_Pay;
    String user_name = "";
    String user_contact = "";
    String user_email = "";
    String TAG = "Card Detail Activity";
    String language = "";
    private CardInputWidget mCardInputWidget;
    private String mPublicKey = "";
    private String mPrivateKey = "";
    private String houseno, landmark, location, address_title, coordinate_id, user_type, remarks, delivery_time;
    private double latitude, longitude;
    private float deleivery_charge, promo_code_id, promo_code_price, discount_amount, paid_amount;
    private String isPromoApplied = "";
    private String restro_image, restro_location, restro_name, restro_status, restaurant_id;
    private String radioButton_checked = "";
    private int month, year;
    private JsonObject payload = new JsonObject();
    private String stripe_token = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail_layout);
        ButterKnife.bind(this);


        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";

        listners();
        getCartData();
        mPublicKey = " pk_test_rLlkAeTE94hTOk6SPjZxRnHM ";
        mPrivateKey = "sk_test_1V4T5UXM2odNyjEHAqBA9vt8";

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Pay:
                //TODO Add Delay
                StartPay();
                break;
        }
    }

    private void StartPay() {
        //Get Card data & check
        Card cardToSave = mCardInputWidget.getCard();

        if (cardToSave == null) {
            UiHelper.showToast(this, "Invalid Card Data");
        }
        //Create Token for card detail
        else {

            final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
            progressDialog.show();

            Stripe stripe = new Stripe(this, "pk_test_X1w6crndFSEl4GiwRDzMs6uI");


            stripe.createToken(mCardInputWidget.getCard(), new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    progressDialog.dismiss();
                    Log.e(TAG, "" + error.getMessage());
                    Log.e(TAG, "onError: " + error.getLocalizedMessage());
                    UiHelper.showToast(Card_detailActivity.this, error.getMessage());
                }

                @Override
                public void onSuccess(Token token) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onSuccess string: " + token.toString());
                    Log.e(TAG, "onSuccess: ID " + token.getId());
                    stripe_token = token.getId();
                    callApi(stripe_token);
                }
            });
        }

    }


    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getResources().getString(R.string.pay));
        btn_Pay.setOnClickListener(this);
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

    private void getCartData() {
        Intent intent = getIntent();
        //


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
        user_type = intent.getStringExtra("user_type");
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
            payload.addProperty("payment_method", "Stripe");

            payload.addProperty("address_title", address_title);
            payload.addProperty("promo_code_id", promo_code_id);
            payload.addProperty("promo_code_price", promo_code_price);
            payload.addProperty("coordinate_id", BaseApplication.getInstance().getSession().getCoordinateId());
            payload.addProperty("restaurant_id", BaseApplication.getInstance().getSession().getRestroId());
            payload.addProperty("language", language);
            payload.addProperty("user_type", user_type);
            payload.addProperty("remarks", remarks);
            payload.addProperty("delivery_time", delivery_time);

            //    if (!BaseApplication.getInstance().getSession().isLoggedIn()){
            //        JsonObject userinfo_object=new JsonObject();
            payload.addProperty("name", user_name);
            payload.addProperty("contact_number", user_contact);
            payload.addProperty("email", user_email);
            // payload.add("user_information",userinfo_object);
            //  }

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

    private void callApi(String token) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        payload.addProperty("stripe_token", token);

        Log.e(TAG, "callApi: " + payload);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addCart(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                            UiHelper.showToast(Card_detailActivity.this, jsonObject1.getString("message"));

                            Intent intent = new Intent(Card_detailActivity.this, ConfirmedOrder.class);
                            intent.putExtra("restro_image", restro_image);
                            intent.putExtra("restro_location", restro_location);
                            intent.putExtra("restro_name", restro_name);
                            intent.putExtra("restro_status", restro_status);
                            intent.putExtra("restaurant_id", restaurant_id);
                            intent.putExtra("delivery_time", delivery_time);
                            BaseApplication.getInstance().getSession().setCartItmes("");
                            BaseApplication.getInstance().getSession().setSimplifiedCartData("");

                            startActivity(intent);
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
                    UiHelper.showNetworkError(Card_detailActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }


}

