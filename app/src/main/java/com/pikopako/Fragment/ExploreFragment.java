package com.pikopako.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Activity.FilterActivity;
import com.pikopako.Activity.LoginActivity;
import com.pikopako.Activity.RestroInfoActivity;
import com.pikopako.Adapter.ProductListAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Model.ProductListModel;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;



public class ExploreFragment extends Fragment implements ProductListAdapter.ClickItemEvent {

    @BindView(R.id.recycleview)
    RecyclerView mRecycleview;

    @BindView(R.id.snackView)
    LinearLayout mSnackView;

    @BindView(R.id.edt_search)
    CustomEditTextBold edt_search;

    //yha productmodel ki jagah ProductListModel
    ArrayList<ProductListModel> productListModelArrayList = new ArrayList<>();
    ProductListModel productListModel;
    ProductListAdapter productListAdapter;
    ProgressDialog progressDialog;
    Double latitude, longitude;

    long click_time=0;
   // long delay=4000;
    String text;
    ArrayList<String> selected_category_ids = new ArrayList<>();
    private Timer timer;
    String language="";

    long delay=1500;
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_finish_checker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        mRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //  callApi();


        input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    text=edt_search.getText().toString();
                    if (!text.equals("")){
                        callApi(text);
                    }

                }
            }
        };

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(input_finish_checker);
            }
            @Override
            public void afterTextChanged(final Editable editable) {

                if (editable.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else {
                    if (productListAdapter!=null) {
                        productListAdapter.arrayList.clear();
                        productListAdapter.notifyDataSetChanged();
                    }
                }


            }
        });


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }

    private void callApi(String text) {

     //   final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
      //  progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        JsonObject payload = new JsonObject();

        try {
            JSONObject profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
            Log.e("Address Maintitlebar", profileData.toString());

         //   latitude = profileData.getDouble("latitude");
         //   longitude = profileData.getDouble("longitude");

//            if (profileData.has("latitude") && profileData.has("longitude") &&!profileData.getString("latitude").equalsIgnoreCase("null") && !profileData.getString("longitude").equalsIgnoreCase("null")){
//                latitude = profileData.getDouble("latitude");
//                longitude = profileData.getDouble("longitude");}
//            else {
                latitude = Double.valueOf(BaseApplication.getInstance().getSession().getDeliveryLatitudeSet());
                longitude=Double.valueOf(BaseApplication.getInstance().getSession().getDeliveryLongitudeSet());
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        payload.addProperty("keyword", text);
        payload.addProperty("timezone", tz.getID());
        payload.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
        payload.addProperty("restaurant_categories", String.valueOf(selected_category_ids));
        payload.addProperty("latitude", BaseApplication.getInstance().getSession().getDeliveryLatitudeSet());
        payload.addProperty("longitude", BaseApplication.getInstance().getSession().getDeliveryLongitudeSet());
        payload.addProperty("language",language);
        Log.e("tag", "payload of near you" + payload.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantDetail(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

           //     progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            productListModel = new ProductListModel();
                            productListModel.initialize(jsonObject1);
                            productListModelArrayList = productListModel.arraylist;

                            UiHelper.hideSoftKeyboard1(getActivity());
                            Log.e("near you fragment", "json object dta" + jsonObject1.getString("data"));
                            //    BaseApplication.getInstance().getSession().set_RestaurantID(jsonObject1.getJSONObject("data").getString("restaurant_id"));

                            init();
                        }
                        if (jsonObject1.getJSONArray("data").length() < 1) {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            UiHelper.hideSoftKeyboard1(getActivity());
                            if (productListAdapter!=null) {
                                productListAdapter.arrayList.clear();
                                productListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            UiHelper.hideSoftKeyboard1(getActivity());
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
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
          //      if (progressDialog != null)
              //      progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
            //        if (progressDialog != null)
             //           progressDialog.dismiss();
                    UiHelper.showNetworkError(getActivity(), mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });


    }

    private void init() {
        productListAdapter = new ProductListAdapter(getActivity(), productListModelArrayList, ExploreFragment.this);
        mRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleview.setAdapter(productListAdapter);
        //    productListAdapter.notifyDataSetChanged();

    }


    @Override
    public void onClickItem(String itemId, String position) {
        Log.e("tagOnclickItem", " " + itemId + "  " + position);
        Intent i = new Intent(getActivity(), RestroInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", productListModelArrayList);
        bundle.putString("position", position);
        i.putExtras(bundle);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        //        Fragment restrofragment=new RestroDetailItemFragment();
        //        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        //        FragmentTransaction transaction=fragmentManager.beginTransaction();
        //        transaction.replace(R.id.frame,restrofragment);
        //        transaction.addToBackStack(null);
        //        transaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                Intent nn = new Intent(getContext(), FilterActivity.class);
                startActivityForResult(nn,443);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 443:
                    //           category_list   = (ArrayList<FilterModel>) data.getSerializableExtra("data");

                    selected_category_ids = data.getStringArrayListExtra("id");
                    callApi(text);
                    Log.e("selected ids", "onActivityResult: "+selected_category_ids );
                    break;
            }

        }
    }
}
