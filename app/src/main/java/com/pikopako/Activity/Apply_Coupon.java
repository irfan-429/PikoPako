package com.pikopako.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.My_Offers_list_Adapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Model.CouponCodeModel;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Apply_Coupon extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ed_apply_code)
    CustomEditTextBold ed_apply_code;

    @BindView(R.id.btnUpdatecode)
    Button btnUpdatecode;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.toplinear)
    LinearLayout mSnackView;


    @BindView(R.id.recycleviewtool)
    RecyclerView recyclerView;

    My_Offers_list_Adapter my_favourite_list_adapter;
    float price = 0;

    ArrayList<CouponCodeModel> codeDetailsModelArrayList = new ArrayList<>();

    String language = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_coupon);
        ButterKnife.bind(this);


        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";


        Intent intent = getIntent();
        String fff = String.valueOf(intent.getStringExtra("price"));
        if (fff.equals("0") || fff.isEmpty()) fff = "€0";
        String[] diss = fff.split("\\€");
        Log.e("Tag", "diss length: " + diss[1]);

        price = Float.parseFloat(diss[1]);
        Log.e("coupon price to check", "onCreate: " + price);
        Log.e("token", "onCreate: " + BaseApplication.getInstance().getSession().getToken().toString());
        setActionBarTitle();

        callApi();
    }


    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.apply_coupon);


        btnUpdatecode.setOnClickListener(this);

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
            case R.id.btnUpdatecode:


                if (ed_apply_code.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this, getString(R.string.please_enter_coupon));
                } else {
                    applyCoupon(ed_apply_code.getText().toString().trim());
                }
                break;
        }
    }

    private void fetchCouponCodeDetails(final String couponCode) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("language", language);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getAllCouponDetails(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {

            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            Log.e("data", "" + jsonArray.toString());
                            codeDetailsModelArrayList = new ArrayList<CouponCodeModel>();
                            codeDetailsModelArrayList.clear();
                            CouponCodeModel coupenCodeDetailsModel = new CouponCodeModel();
                            coupenCodeDetailsModel.getAllCoupenCodeDetails(jsonArray);
                            codeDetailsModelArrayList.addAll(coupenCodeDetailsModel.codeDetailsModelArrayList);

                            UiHelper.showToast(Apply_Coupon.this, jsonObject1.getString("message"));

                            applyCoupon(couponCode);

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

            }

            @Override
            public void isConnected(boolean isConnected) {

            }
        });
    }

    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("language", language);
        //   jsonObject.addProperty("food_id", id);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOfferDetail(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONArray categoriesArray = jsonObject1.getJSONArray("data");
                            recyclerView.setLayoutManager(new LinearLayoutManager(Apply_Coupon.this));
                            my_favourite_list_adapter = new My_Offers_list_Adapter(Apply_Coupon.this, categoriesArray, price);
                            recyclerView.setAdapter(my_favourite_list_adapter);

                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(Apply_Coupon.this, LoginActivity.class);
                                startActivity(intent);

                            }
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

    private void applyCoupon(String ed_apply_code) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("coupon", ed_apply_code);
        jsonObject.addProperty("price", price);

        jsonObject.addProperty("language", language);
        Log.e("tag", "json object apply coupn " + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().applyCoupon(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            String id = jsonObject1.getJSONObject("data").getString("id");
                            String couponCode = jsonObject1.getJSONObject("data").getString("coupon_code");
                            String discount_percentage = jsonObject1.getJSONObject("data").getString("discount_percentage");
                            String status = jsonObject1.getJSONObject("data").getString("status");

                            Intent intent = new Intent();
                            intent.putExtra("id", id);
                            intent.putExtra("couponCode", couponCode);
                            intent.putExtra("discount", discount_percentage);
                            setResult(Activity.RESULT_OK, intent);

                            //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e("tag", "Coupon Updated Successfully" + id + "coupon Code:-" + couponCode);
                            UiHelper.showToast(Apply_Coupon.this, jsonObject1.getString("message"));
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
                    UiHelper.showNetworkError(Apply_Coupon.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }


}
