package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.My_order_adapter;
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

public class My_orders extends BaseActivity implements My_order_adapter.ClickItemEvent {

    @BindView(R.id.fragment_container)
    RelativeLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.recycleviewtool)
    RecyclerView recyclerView;

    My_order_adapter my_favourite_list_adapter;
    String language="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycleview_with_toolbar);
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
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.string_myorders));

    }



    private void callApi(){
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();

             JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("language",language);
        //   jsonObject.addProperty("food_id", id);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOrders(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONArray categoriesArray = jsonObject1.getJSONArray("data");
                            recyclerView.setLayoutManager(new LinearLayoutManager(My_orders.this));
                            my_favourite_list_adapter=new My_order_adapter(My_orders.this,categoriesArray,My_orders.this);
                            recyclerView.setAdapter(my_favourite_list_adapter);

                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                                Intent intent=new Intent(My_orders.this,LoginActivity.class);
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
    public void onClickItem(String itemId) {
        Log.e("tagOnclickItem", " " + itemId);
//        Intent i = new Intent(My_orders.this, OrderList.class);
//        i.putExtra("cart_id",itemId)
////        Bundle bundle = new Bundle();
////        bundle.putSerializable("data", productListModelArrayList);
////        bundle.putString("position", position);
////        i.putExtras(bundle);
//        startActivity(i);
//        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }
}
