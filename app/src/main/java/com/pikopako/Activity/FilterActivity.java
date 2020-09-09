package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Adapter.FilterAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.NearYouFragment;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Model.FilterModel;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class FilterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.tvSelectAll)
    CustomTextViewBold tvSelectAll;

    @BindView(R.id.tvDeSelectAll)
    CustomTextViewBold tvDeSelectAll;

    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.recycleviewtool)
    RecyclerView recyclerView;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    @BindView(R.id.btnApply)
            Button btnApply;

    FilterAdapter filterAdapter;
    ArrayList<FilterModel> category_models_list = new ArrayList<>();
    FilterModel filterModel;
     JSONArray ids = new JSONArray();
    String language="";
    int counterMatching = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filteractivity_recyclerview);
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        listners();



        String idd=BaseApplication.getInstance().getSession().getFilter();
        try {
            if (!idd.equalsIgnoreCase("") && idd!=null)
            ids = new JSONArray(idd);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e("ids list", "onCreate: "+ids );
        Log.e("id", "onCreate: "+  BaseApplication.getInstance().getSession().getFilter());
        if (category_models_list.isEmpty())
            callApi();
        else
            initilize();
    }

    private void initilize() {



        for (int i=0;i<category_models_list.size();i++){

            for (int j=0;j<ids.length();j++){
                try {
                    Log.e("both ids", "initilize: "+ids.getString(j) +" list id:- "+category_models_list.get(i).id);
                    if (ids.getString(j).equalsIgnoreCase(String.valueOf(category_models_list.get(i).id))){
                           category_models_list.get(i).isChecked=true;
                           counterMatching++;
                            }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (counterMatching==category_models_list.size()){

                tvDeSelectAll.setVisibility(View.VISIBLE);
                tvSelectAll.setVisibility(View.GONE);
                }

        }







        filterAdapter = new FilterAdapter(category_models_list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filterAdapter);

    }
    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
            mTitle.setText(R.string.choose_categories);
           btnCancel.setOnClickListener(this);
            btnApply.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvDeSelectAll.setOnClickListener(this);
    }

    private void callApi(){
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();

             JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("language",language);
        //   jsonObject.addProperty("food_id", id);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantCategories(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
//                            JSONArray categoriesArray = jsonObject1.getJSONArray("data");
//                            recyclerView.setLayoutManager(new LinearLayoutManager(FilterActivity.this));
//                            filterAdapter    =new FilterAdapter(FilterActivity.this,categoriesArray);
//                            recyclerView.setAdapter(filterAdapter);

                            filterModel = new FilterModel();
                            filterModel.initializeModel(jsonObject1);
                            category_models_list = filterModel.arrayList;
                            initilize();
                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                                Intent intent=new Intent(FilterActivity.this,LoginActivity.class);
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
        switch (view.getId()){
            case R.id.btnCancel:
                finish();
                break;

            case R.id.btnApply:

                ArrayList<FilterModel> newfilterAdapters=new ArrayList<>();
                JSONArray ids = new JSONArray();
                newfilterAdapters=  filterAdapter.filterModelArrayList;
                 for (int i=0;i<newfilterAdapters.size();i++){
                  if (newfilterAdapters.get(i).isChecked){
                      ids.put(String.valueOf(newfilterAdapters.get(i).id));
                  }
                }
                Intent intent=new Intent(this, NearYouFragment.class);
                intent.putExtra("data",filterAdapter.filterModelArrayList);
                intent.putExtra("id", ids.toString());
                Log.e("ids ", "onClick: "+ids );
                Log.e("ids string", "onClick: "+ids.toString() );
                BaseApplication.getInstance().getSession().setFilter(ids.toString());

                if (filterAdapter.filterModelArrayList != null);
                setResult(RESULT_OK, intent);
                finish();
                break;


            case R.id.tvSelectAll:
               filterAdapter.SelectALl();
               tvSelectAll.setVisibility(View.GONE);
               tvDeSelectAll.setVisibility(View.VISIBLE);
                break;

            case R.id.tvDeSelectAll:
                filterAdapter.DeSelectALl();
                tvSelectAll.setVisibility(View.VISIBLE);
                tvDeSelectAll.setVisibility(View.GONE);
                break;
        }
    }
}
