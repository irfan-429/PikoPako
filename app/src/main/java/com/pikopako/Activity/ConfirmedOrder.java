package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.NearYouFragment;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class ConfirmedOrder extends BaseActivity {

    @BindView(R.id.ff)
    ScrollView mSnackView;

    @BindView(R.id.btn_proceed)
    Button btn_proceed;


//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//
//    @BindView(R.id.tvTitle)
//    CustomTextViewBold mTitle;

    @BindView(R.id.txt_time)
    CustomTextViewBold txt_time;

    @BindView(R.id.txt_meet)
    CustomTextViewBold txt_meet;

    String restaurant_id;
    String restro_image,restro_location,restro_name,restro_status;
    String language="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmed_layout);
        ButterKnife.bind(this);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
        getData();
        restaurant_id=getIntent().getStringExtra("restaurant_id");
        restro_image=getIntent().getStringExtra("restro_image");
        restro_location=getIntent().getStringExtra("restro_location");
        restro_name=getIntent().getStringExtra("restro_name");
        restro_status=getIntent().getStringExtra("restro_status");

        Log.e("ConfirmedOrder", "onCreate:Restro id "+BaseApplication.getInstance().getSession().getRestroId() );

//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final Intent mainIntent = new Intent(ConfirmedOrder.this, Rating_reviews_Activity.class);
//                mainIntent.putExtra("restaurant_id",restaurant_id);
//                mainIntent.putExtra("restro_image",restro_image);
//                mainIntent.putExtra("restro_location",restro_location);
//                mainIntent.putExtra("restro_name",restro_name);
//                mainIntent.putExtra("restro_status",restro_status);
//                ConfirmedOrder.this.startActivity(mainIntent);
//                ConfirmedOrder.this.finish();
//            }
//        }, 8000);

      //  setActionBarTitle();


        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseApplication.getInstance().getSession().isLoggedIn()) {
//                                        final Intent mainIntent = new Intent(ConfirmedOrder.this, Rating_reviews_Activity.class);
//                                        mainIntent.putExtra("restaurant_id", restaurant_id);
//                                        mainIntent.putExtra("restro_image", restro_image);
//                                        mainIntent.putExtra("restro_location", restro_location);
//                                        mainIntent.putExtra("restro_name", restro_name);
//                                        mainIntent.putExtra("restro_status", restro_status);
//                                        ConfirmedOrder.this.startActivity(mainIntent);

                    Intent inn=new Intent(ConfirmedOrder.this, MainActivity.class);
                    inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(inn);
                    finishAffinity();

                } else {
                    Intent inn = new Intent(ConfirmedOrder.this, DefaultActivity.class);
                    inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(inn);
                    finishAffinity();
                }
            }
        });



    }
//    private void setActionBarTitle() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");
//        mTitle.setText("Confirmed Order");
//
//
//
//
//    }
    private void getData(){
      final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("restaurant_id",BaseApplication.getInstance().getSession().getRestroId());
      //  jsonObject.addProperty("coordinate_id", coordinate_i);

        jsonObject.addProperty("timezone",tz.getID());
        jsonObject.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
        jsonObject.addProperty("language",language);
        Log.e("tag", "json object restro id detailitemfrag:- " + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantServices(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().get(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                        final JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        Log.e("restrodetailitemjson",jsonObject1.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {


                  //          if (!getIntent().getStringExtra("delivery_time").equalsIgnoreCase("") && getIntent().getStringExtra("delivery_time")!=null ) {

                          //      if (getIntent().hasExtra("delivery_time")){

                                    if (!getIntent().getStringExtra("delivery_time").equalsIgnoreCase("") && getIntent().getStringExtra("delivery_time")!=null ) {
                                String delivery_time = getIntent().getStringExtra("delivery_time");
                                txt_meet.setText(getResources().getString(R.string.our_deleivery_boy_meet_you_at));
                                String user_time=getIntent().getStringExtra("delivery_time");
                                String restro_time=jsonObject1.getJSONObject("data").getString("maximum_time_delivery");
                              //  txt_time.setText(getIntent().getStringExtra("delivery_time"));

                                int user_hour,user_minute,restro_hour,restro_minute,tot_hour,tot_minute;
                                String orderDeliveryTime="";

                                String s[]=user_time.split(":");
                                user_hour= Integer.parseInt(s[0]);
                                user_minute= Integer.parseInt(s[1]);

                                String ss[]=restro_time.split(":");
                                restro_hour=Integer.parseInt(ss[0]);
                                restro_minute=Integer.parseInt(ss[1]);

                                Log.e("TAG", "hour:- "+user_hour +" minute:- "+user_minute);

                                tot_minute=user_minute+restro_minute;
                                tot_hour=user_hour + restro_hour;
                                if (tot_minute<60){
                                    orderDeliveryTime=tot_hour+":"+tot_minute;
                                    Log.e("TAG", "delivered time: "+orderDeliveryTime );
                                }else {

                                    if (tot_minute==60){
                                        tot_hour=tot_hour+1;
                                        orderDeliveryTime=tot_hour+":"+"00";
                                        Log.e("TAG", "delivered time: "+orderDeliveryTime );
                                    }else {
                                        tot_minute=tot_minute-60;
                                        tot_hour=tot_hour+1;
                                        orderDeliveryTime=tot_hour+":"+tot_minute;
                                        Log.e("TAG", "delivered time: "+orderDeliveryTime );
                                    }

                                }

                                txt_time.setText(orderDeliveryTime);
                            }
                            else
                            txt_time.setText(jsonObject1.getJSONObject("data").getString("maximum_time_delivery")+getString(R.string.minutes));


//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    if (BaseApplication.getInstance().getSession().isLoggedIn()) {
////                                        final Intent mainIntent = new Intent(ConfirmedOrder.this, Rating_reviews_Activity.class);
////                                        mainIntent.putExtra("restaurant_id", restaurant_id);
////                                        mainIntent.putExtra("restro_image", restro_image);
////                                        mainIntent.putExtra("restro_location", restro_location);
////                                        mainIntent.putExtra("restro_name", restro_name);
////                                        mainIntent.putExtra("restro_status", restro_status);
////                                        ConfirmedOrder.this.startActivity(mainIntent);
//
//                                        Intent inn=new Intent(ConfirmedOrder.this, MainActivity.class);
//                                        inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                        inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(inn);
//                                        finish();
//
//                                    } else {
//                                        try {
//                                            UiHelper.showToast(ConfirmedOrder.this, jsonObject1.getString("message"));
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    Intent inn = new Intent(ConfirmedOrder.this, DefaultActivity.class);
//                                    inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(inn);
//                                    finish();
//                                }
//
//                                //    ConfirmedOrder.this.finish();
//                                }
//                            }, 5000);


                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
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
                UiHelper.showErrorMessage(mSnackView,error);
            }
            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(ConfirmedOrder.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (BaseApplication.getInstance().getSession().isLoggedIn()) {
//                                        final Intent mainIntent = new Intent(ConfirmedOrder.this, Rating_reviews_Activity.class);
//                                        mainIntent.putExtra("restaurant_id", restaurant_id);
//                                        mainIntent.putExtra("restro_image", restro_image);
//                                        mainIntent.putExtra("restro_location", restro_location);
//                                        mainIntent.putExtra("restro_name", restro_name);
//                                        mainIntent.putExtra("restro_status", restro_status);
//                                        ConfirmedOrder.this.startActivity(mainIntent);

            Intent inn=new Intent(ConfirmedOrder.this, MainActivity.class);
            inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(inn);
            finishAffinity();

        } else {
            Intent inn = new Intent(ConfirmedOrder.this, DefaultActivity.class);
            inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(inn);
            finishAffinity();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                if (BaseApplication.getInstance().getSession().isLoggedIn()) {
//                                        final Intent mainIntent = new Intent(ConfirmedOrder.this, Rating_reviews_Activity.class);
//                                        mainIntent.putExtra("restaurant_id", restaurant_id);
//                                        mainIntent.putExtra("restro_image", restro_image);
//                                        mainIntent.putExtra("restro_location", restro_location);
//                                        mainIntent.putExtra("restro_name", restro_name);
//                                        mainIntent.putExtra("restro_status", restro_status);
//                                        ConfirmedOrder.this.startActivity(mainIntent);

                    Intent inn=new Intent(ConfirmedOrder.this, MainActivity.class);
                    inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(inn);
                    finish();

                } else {
                    Intent inn = new Intent(ConfirmedOrder.this, DefaultActivity.class);
                    inn.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(inn);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
