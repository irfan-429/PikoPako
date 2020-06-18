package com.pikopako.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.OrderId_Adapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class OrderIdActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.snackView)
    LinearLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.recycleviewtool)
    RecyclerView recyclerView;



    OrderId_Adapter my_favourite_list_adapter;
    String food_id;

    int order_id;

    String language="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview_with_toolbar);
        ButterKnife.bind(this);
        listners();


        order_id=getIntent().getIntExtra("order_id",-1);
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
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.choose_order);


    }


    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("status", "Pending");
        jsonObject.addProperty("language",language);


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOrderId(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONArray data = jsonObject1.getJSONArray("data");

                            Log.e("data orderid", "Success: "+data.toString() );
//                            JSONArray toppingarray = data.getJSONArray("food_toppings");
                            recyclerView.setLayoutManager(new LinearLayoutManager(OrderIdActivity.this));
                            my_favourite_list_adapter = new OrderId_Adapter(OrderIdActivity.this, data,order_id);
                            recyclerView.setAdapter(my_favourite_list_adapter);

                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(OrderIdActivity.this, LoginActivity.class);
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


    public void setRadio(int orderid){

        Intent intent=new Intent();
        intent.putExtra("order_id",orderid);
        Log.e("order id activity", "setRadio: "+orderid );
        setResult(Activity.RESULT_OK, intent);
        finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }
    }


}