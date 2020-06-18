package com.pikopako.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.RestroInfoAdapter;
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

import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class RestroRatingActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.recycleview_rating)
    RecyclerView mRecycleview;

    @BindView(R.id.recyclelyt)
    RelativeLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;
    RestroInfoAdapter productListAdapter;
    String language="";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_rating);
        ButterKnife.bind(this);
        listners();
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
            callApi();
    }


    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("  ");
        mTitle.setText(R.string.reviews);
    }



    private void callApi(){
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("restaurant_id", getIntent().getExtras().getString("id"));
        jsonObject.addProperty("coordinate_id", getIntent().getExtras().getString("coordinate_id"));
        jsonObject.addProperty("timezone",tz.getID());
        jsonObject.addProperty("language",language);


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getReviews(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONArray categoriesArray = jsonObject1.getJSONObject("data").getJSONArray("review_list");
                         String image=   jsonObject1.getJSONObject("data").getString("profile_image_base_url");
                            Log.e("base url", "Success: "+  jsonObject1.getJSONObject("data").getString("profile_image_base_url"));
                            mRecycleview.setLayoutManager(new LinearLayoutManager(RestroRatingActivity.this));
                            productListAdapter    =new RestroInfoAdapter(RestroRatingActivity.this,categoriesArray,image);
                            mRecycleview.setAdapter(productListAdapter);

                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                                Intent intent=new Intent(RestroRatingActivity.this,LoginActivity.class);
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
                if(progressDialog!=null)
                    progressDialog.dismiss();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {

    }
}
