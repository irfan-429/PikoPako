package com.pikopako.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.pikopako.Adapter.OrderListAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
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


public class OrderList extends AppCompatActivity {

    private static final String TAG = "OrderList";
    @BindView(R.id.showcart)
    ScrollView mSnackView;
    @BindView(R.id.recyclevieww)
    RecyclerView mRecycleview;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.img_restaurant)
    ImageView img_restaurant;

    @BindView(R.id.txt_restroname)
    CustomTextViewBold txt_restroname;

    @BindView(R.id.txt_address)
    CustomTextViewNormal txt_address;

    @BindView(R.id.txt_ordertime)
    CustomTextViewNormal txt_ordertime;

    @BindView(R.id.txt_deleiveryaddress)
    CustomTextViewNormal txt_deleiveryaddress;

    @BindView(R.id.txt_itemTotal)
    CustomTextViewNormal txt_itemTotal;

    @BindView(R.id.txt_discount)
    CustomTextViewNormal txt_discount;

    @BindView(R.id.txt_total_discount)
    CustomTextViewNormal txt_total_discount;

    @BindView(R.id.txt_deleiveryCharges)
    CustomTextViewNormal txt_deleiveryCharges;

    @BindView(R.id.txt_paidvia)
    CustomTextViewBold txt_paidvia;

    @BindView(R.id.txt_toPay)
    CustomTextViewBold txt_toPay;

    @BindView(R.id.btn_reorder)
    Button btn_reorder;


    OrderListAdapter orderListAdapter;
    String language="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlist_activity);
        ButterKnife.bind(this);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        callApi();

            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            mTitle.setText("PIKOPAKO#"+getIntent().getStringExtra("cart_id"));
        Log.e("list me", "onCreate: "+getIntent().getStringExtra("cart_id") );

        BaseApplication.getInstance().getSession().setRestroId("");
        btn_reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             //   reOrder(getIntent().getStringExtra("cart_id"));
                try {
                    //get Old cart data if exist
                    String oldCart = BaseApplication.getInstance().getSession().getCartItems();
                    if (!oldCart.isEmpty()){

                        JSONObject oldCartData = new JSONObject(oldCart);

                        if (oldCartData.getInt("total_items") <= 0)
                            reOrder(getIntent().getStringExtra("cart_id"));
                         //   callApi(jsonArray.getJSONObject(position).getString("id"));
                        else{

                           // UiHelper.showToast(mContext,"Already have item in cart, can't reorder !");

                            final Dialog dialog = new Dialog(OrderList.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.reorder_dialog);

                            dialog.show();

                            Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
                            Button btn_yes=(Button)dialog.findViewById(R.id.btn_yes);

                            //Cancel
                            btn_no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Close dialog
                                    dialog.dismiss();
                                }
                            });


                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    BaseApplication.getInstance().getSession().setCartItmes("");

                                    //    reOrder(jsonArray.getJSONObject(position).getString("id"));
                                        reOrder(getIntent().getStringExtra("cart_id"));
                                            dialog.dismiss();
                                }
                            });




                        }


                    }
                    else
                        reOrder(getIntent().getStringExtra("cart_id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void callApi(){
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();

             JsonObject jsonObject = new JsonObject();

          jsonObject.addProperty("cart_id", getIntent().getStringExtra("cart_id"));
        jsonObject.addProperty("language",language);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOrdersDetail(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //JSONArray categoriesArray = jsonObject1.getJSONArray("data");
//                            recyclerView.setLayoutManager(new LinearLayoutManager(OrderList.this));
//                            my_favourite_list_adapter=new My_order_adapter(OrderList.this,categoriesArray,My_orders.this);
//                            recyclerView.setAdapter(my_favourite_list_adapter);

                            JSONObject data=jsonObject1.getJSONObject("data");

                            Glide.with(OrderList.this).
                                    load(data.getString("restaurant_logo"))
                                    .error(R.drawable.profileicon)
                                    .into(img_restaurant);

                            txt_restroname.setText(data.getString("restaurant_name"));
                            txt_address.setText(data.getString("restaurant_address"));
                            txt_deleiveryaddress.setText(data.getString("address"));
                            txt_toPay.setText("€"+data.getString("paid_amount"));
                            txt_deleiveryCharges.setText("€"+data.getString("delivery_charge"));
                            txt_paidvia.setText(getString(R.string.paid_via)+" "+data.getString("payment_method"));

                            if (data.getString("order_status").equalsIgnoreCase("Pending"))
                            txt_ordertime.setText(getString(R.string.order_requested_on)+" "+data.getString("add_date"));

                           else if (data.getString("order_status").equalsIgnoreCase("Accepted"))
                                txt_ordertime.setText(getString(R.string.order_accepted_on)+" "+data.getString("accepted_time"));

                           else if (data.getString("order_status").equalsIgnoreCase("Cancel"))
                                txt_ordertime.setText(getString(R.string.order_cancelled_on)+" "+data.getString("cancel_time"));

                            else if (data.getString("order_status").equalsIgnoreCase("Delivered"))
                                txt_ordertime.setText(getString(R.string.order_deleieverd_on)+" "+data.getString("delivery_time"));

                            JSONArray fooditem_array = jsonObject1.getJSONObject("data").getJSONArray("food_items");

                            mRecycleview.setLayoutManager(new LinearLayoutManager(OrderList.this));
                            orderListAdapter=new OrderListAdapter(fooditem_array,OrderList.this);
                            mRecycleview.setAdapter(orderListAdapter);

                            float total_item_price=0;
                            float total_discount=0;

                            float total_price =0;
                            int total_items =0;

                            float product_price=0;
                            float toppings_price =0;
                            float restro_discount = 0;
                            float coupon_price= 0;
                            for (int i=0;i<fooditem_array.length();i++) {
                                JSONObject food_item_object = fooditem_array.getJSONObject(i);
                                product_price = (float) fooditem_array.getJSONObject(i).getDouble("unit_price");
                                toppings_price = 0;

                                JSONArray toopings  = fooditem_array.getJSONObject(i).getJSONArray("toopings");

                                for (int v =0; v < toopings.length(); v++){
                                    toppings_price =  toppings_price + (float) (toopings.getJSONObject(v).getDouble("topping_price"));
                                }

                                restro_discount =  (restro_discount + ( (product_price * (float) food_item_object.getDouble("discount_percent"))/100 ) );

                                //add in total price
                                total_price = total_price + ((product_price + toppings_price) * fooditem_array.getJSONObject(i).getInt("quantity"));
                                //total items
                                total_items = total_items + fooditem_array.getJSONObject(i).getInt("quantity");

                            }

                            Log.e("total iprice", "Success: "+total_price );

                            txt_itemTotal.setText("€"+String.format(Locale.ENGLISH,"%.2f",total_price));
                            txt_discount.setText("€"+String.format(Locale.ENGLISH,"%.2f",restro_discount));

                            if (!data.getString("promo_code_price").equalsIgnoreCase("null"))
                            coupon_price= Float.valueOf(data.getString("promo_code_price"));
                            float totalDiscount=restro_discount + coupon_price;
                            Log.e("total discount", "Success: "+totalDiscount );
                            txt_total_discount.setText("€"+String.format("%.2f",totalDiscount));
                        }

                        else {

                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")){
                                Intent intent=new Intent(OrderList.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            finish();
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
                finish();
//                UiHelper.showErrorMessage(mSnackView,error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    finish();
//                    UiHelper.showNetworkError(FoodDetailActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }



        });

    }

    private void reOrder(String cart_id){

        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("cart_id",cart_id);
        jsonObject.addProperty("language",language);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOrdersDetail(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {

                    try {
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            JSONObject data = jsonObject1.getJSONObject("data");
                            JSONArray food_items = data.getJSONArray("food_items");

                            /*Build Cart data*/
                            JSONObject cartData = new JSONObject();
                            JSONArray cartDataArray = new JSONArray();
                            JSONObject product_object = new JSONObject();

                            float total_price =0;
                            int total_items =0;

                            float product_price=0;
                            float toppings_price =0;


                            for (int i=0; i<food_items.length(); i++){

                                product_price = (float) food_items.getJSONObject(i).getDouble("unit_price");
                                toppings_price = 0;

                                product_object = new JSONObject();

                                product_object.put("id",food_items.getJSONObject(i).getString("food_id"));
                                product_object.put("price",food_items.getJSONObject(i).getString("unit_price"));
                                product_object.put("product_name",food_items.getJSONObject(i).getString("food_name"));
                                product_object.put("product_discount",food_items.getJSONObject(i).getString("discount_percent"));

                                product_object.put("restro_name",data.getString("restaurant_name"));
                                product_object.put("restro_location",data.getString("restaurant_address"));
                                product_object.put("restro_image",data.getString("restaurant_logo"));
                                product_object.put("restro_id",data.getString("restaurant_id"));
                                product_object.put("restro_status","");



//                                product_object.put("items");
                                JSONArray toppings_array = new JSONArray();
                                JSONArray toopings  = food_items.getJSONObject(i).getJSONArray("toopings");
                                for (int v =0; v < toopings.length(); v++){
                                    JSONObject toppings_object = new JSONObject();
                                    toppings_object.put("id",toopings.getJSONObject(v).getString("topping_id"));
                                    toppings_object.put("name",toopings.getJSONObject(v).getString("topping_name"));
                                    toppings_object.put("price",toopings.getJSONObject(v).getString("topping_price"));

                                    toppings_price =  toppings_price + (float) (toopings.getJSONObject(v).getDouble("topping_price"));

                                    toppings_array.put(toppings_object);
                                }

                                //add in total price
                                total_price = total_price + ((product_price + toppings_price) * food_items.getJSONObject(i).getInt("quantity"));
                                //total items
                                total_items = total_items + food_items.getJSONObject(i).getInt("quantity");

                                // Repeat toppings for quantity build items array
                                JSONArray items_array = new JSONArray();
                                for (int x =0; x < food_items.getJSONObject(i).getInt("quantity"); x++){
                                    items_array.put(toppings_array);
                                }

                                product_object.put("items",items_array);

                                //add to cart
                                cartDataArray.put(product_object);
                            }

                            cartData.put("data",cartDataArray);
                            cartData.put("total_items",total_items);
                            cartData.put("total_price",total_price);

                            Log.e(TAG,"newly Build Cart :" +cartData.toString());
                            BaseApplication.getInstance().getSession().setCartItmes(cartData.toString());

                            //Go to cart Activity
                            Intent intent = new Intent(OrderList.this, ViewCartActivity.class);
                            intent.putExtra("restaurant_id",data.getString("restaurant_id"));
                            BaseApplication.getInstance().getSession().setRestroId(data.getString("restaurant_id"));

                          //  intent.putExtra("coordinate_id",);
                            startActivity(intent);
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

    public void update(float total_price){
    //    txt_itemTotal.setText(String.valueOf(total_price));
    }


}
