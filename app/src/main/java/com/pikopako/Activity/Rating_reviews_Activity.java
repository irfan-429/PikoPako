package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Rating_reviews_Activity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_food)
    ImageView img_food;



//    @BindView(R.id.txt_food_price)
//    CustomTextViewBold txt_food_price;

    @BindView(R.id.ratingstar)
    AppCompatRatingBar ratingstar;

    @BindView(R.id.edtReviewTxt)
    EditText edtReviewTxt;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;


    @BindView(R.id.txt_restroname)
    CustomTextViewBold txt_restroname;

    @BindView(R.id.txt_address)
    CustomTextViewNormal txt_address;

    @BindView(R.id.txt_status)
    CustomTextViewBold txt_status;

    @BindView(R.id.txt_skip)
    CustomTextViewBold txt_skip;

    String restro_image,restro_location,restro_name,restro_status,restro_id,cart_id;
    String language="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_layout);
        ButterKnife.bind(this);
        btnSubmit.setOnClickListener(this);
        txt_skip.setOnClickListener(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";

        Intent intent=getIntent();
        restro_image=getIntent().getStringExtra("restro_image");
    //    restro_location=getIntent().getStringExtra("restro_location");
        restro_name=getIntent().getStringExtra("restro_name");
     //   restro_status=getIntent().getStringExtra("restro_status");
        restro_id=getIntent().getStringExtra("restro_id");
        cart_id=getIntent().getStringExtra("cart_id");

        txt_restroname.setText(restro_name);
     //   txt_address.setText(restro_location);
     //   txt_status.setText(restro_status);

        Glide.with(Rating_reviews_Activity.this).
                load(restro_image)
                .error(R.drawable.profileicon)
                .into(img_food);


    }


    private void callApi(){
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rating",ratingstar.getRating());
        jsonObject.addProperty("review",edtReviewTxt.getText().toString());
        jsonObject.addProperty("restaurant_id", restro_id);
        jsonObject.addProperty("cart_id",cart_id);
        jsonObject.addProperty("language",language);
      //  Log.e("food_idd",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addRating(BaseApplication.getInstance().getSession().getToken(),jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                           UiHelper.showToast(Rating_reviews_Activity.this,jsonObject1.getString("message"));

                         Intent inn=new Intent(Rating_reviews_Activity.this, MainActivity.class);
                         inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                         inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(inn);


                                    finish();
                        }
                        else if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                            Log.e("else", "else condiotin ");

//                             UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
//                             if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                            Intent intent=new Intent(Rating_reviews_Activity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        //    }


                    } catch (JSONException e) {
                        Log.e("status ", "error:catch" );
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void Error(String error) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("status ", "error:" );
//                UiHelper.showErrorMessage(mSnackView,error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
//                    UiHelper.showNetworkError(FoodDetailActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSubmit:
                if (ratingstar.getRating()<1)
                    UiHelper.showToast(Rating_reviews_Activity.this,getResources().getString(R.string.pls_give_some_rating));
             else   if (edtReviewTxt.getText().toString().trim().isEmpty()){
                   edtReviewTxt.setError(getString(R.string.pls_enter_some_comment));
               }
               else
                   callApi();
                break;

            case R.id.txt_skip:
                callForClose();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callForClose();
    }

    private void callForClose() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("cart_id",cart_id);
        jsonObject.addProperty("language",language);
        //  Log.e("food_idd",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().skip_rating(BaseApplication.getInstance().getSession().getToken(),jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {


                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            finish();
                        }
                        else if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                            Log.e("else", "else condiotin ");
                            Intent intent=new Intent(Rating_reviews_Activity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        //    }


                    } catch (JSONException e) {
                        Log.e("status ", "error:catch" );
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void Error(String error) {

                Log.e("status ", "error:" );
//                UiHelper.showErrorMessage(mSnackView,error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {

//                    UiHelper.showNetworkError(FoodDetailActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }

        });


    }
}
