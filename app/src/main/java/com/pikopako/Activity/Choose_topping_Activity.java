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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.Choose_topping_adapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Choose_topping_Activity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.recycleviewtool)
    RecyclerView recyclerView;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.tvSelectAll)
    CustomTextViewBold tvSelectAll;

    @BindView(R.id.tvDeSelectAll)
    CustomTextViewBold tvDeSelectAll;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    Choose_topping_adapter my_favourite_list_adapter;
    String food_id;
    String language = "";
    ArrayList<Ingrediants_modal> ingrediants_modalArrayList = new ArrayList<>();
    Ingrediants_modal ingrediants_modal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_topping_activity);
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        listners();

        food_id = getIntent().getStringExtra("food_id");


        callApi();

    }

    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(R.string.extras);

        btnApply.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvDeSelectAll.setOnClickListener(this);
    }


    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("food_id", food_id);
        jsonObject.addProperty("language", language);
        Log.e("food_id", "" + food_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getIngredients(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONObject data = jsonObject1.getJSONObject("data");
                            JSONArray toppingarray = data.getJSONArray("food_toppings");


                            ingrediants_modal = new Ingrediants_modal();
                            ingrediants_modal.parseIngrediants(toppingarray);
                            ingrediants_modalArrayList = ingrediants_modal.ingrediants_modalArrayList;

                            if (getIntent().hasExtra("toppings_array")) {

                                JSONArray preSelected_ToppingsArray = new JSONArray(getIntent().getStringExtra("toppings_array"));

                                for (int i = 0; i < preSelected_ToppingsArray.length(); i++) {

                                    for (int j = 0; j < ingrediants_modalArrayList.size(); j++) {

                                        if (preSelected_ToppingsArray.getJSONObject(i).getString("id").equalsIgnoreCase(ingrediants_modalArrayList.get(j).ingredients_id))
                                            ingrediants_modalArrayList.get(j).isIngredient_add = true;

                                    }
//                                    if (ingrediants_modalArrayList.get(i).isIngredient_add){
//                                        tvDeSelectAll.setVisibility(View.VISIBLE);
//                                        tvSelectAll.setVisibility(View.GONE);
//
//                                    }
                                }


                            }


                            my_favourite_list_adapter = new Choose_topping_adapter(Choose_topping_Activity.this, ingrediants_modalArrayList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Choose_topping_Activity.this));
                            recyclerView.setAdapter(my_favourite_list_adapter);




                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(Choose_topping_Activity.this, LoginActivity.class);
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


//        if (getIntent().getExtras().getString("isTopping").equals("false"))
//            applyTopping();

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
            case R.id.btnCancel:
                finish();
                break;

            case R.id.btnApply:

                applyTopping();
                break;

            case R.id.tvSelectAll:
                my_favourite_list_adapter.SelectALl();
                tvSelectAll.setVisibility(View.GONE);
                tvDeSelectAll.setVisibility(View.VISIBLE);
                break;

            case R.id.tvDeSelectAll:
                my_favourite_list_adapter.DeSelectALl();
                tvSelectAll.setVisibility(View.VISIBLE);
                tvDeSelectAll.setVisibility(View.GONE);
                break;

        }
    }

    private void applyTopping() {

        for (int i=0; i<my_favourite_list_adapter.adapter_arraylist.size(); i++){
            Log.e("===>>", "applyTopping: "+my_favourite_list_adapter.adapter_arraylist.get(i).getToppingname() );
        }


        Intent intent = new Intent(this, ViewCartActivity.class);
        Log.e("===>>", "SIZE : "+ my_favourite_list_adapter.adapter_arraylist.toString());
        intent.putExtra("data", my_favourite_list_adapter.adapter_arraylist);
        intent.putExtra("product_id", food_id);
        Log.e("TAG", "Customize: " + getIntent().getStringExtra("customize"));
        if (getIntent().hasExtra("customize")) {
            intent.putExtra("customize", "true");
//                    Log.e("TAG", "position in choose toping: "+getIntent().getIntExtra("pos",0) );
            intent.putExtra("pos", getIntent().getIntExtra("pos", 0));
            Log.e("===>>", "if pos: " + getIntent().getIntExtra("pos", 0));
        } else {
            intent.putExtra("customize", "false");
//            Log.e("===>>", "else: " + getIntent().getIntExtra("pos", 0));
        }

//        Log.e("===>>T", "data: " + my_favourite_list_adapter.adapter_arraylist.get(0).getIngredientPrice());
        Log.e("===>>T", "product_id: " + food_id);
//                Log.e("===>>T", "data: "+  my_favourite_list_adapter.adapter_arraylist);


        if (my_favourite_list_adapter.adapter_arraylist != null) ;
        setResult(RESULT_OK, intent);
        finish();
    }
}
