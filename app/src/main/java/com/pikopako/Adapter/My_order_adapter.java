package com.pikopako.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.pikopako.Activity.OrderList;
import com.pikopako.Activity.ViewCartActivity;
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

import retrofit2.Call;

public class My_order_adapter extends RecyclerView.Adapter<My_order_adapter.ViewHolder> {


    private static final String TAG = "My_order_adapter";
    Context mContext;
    JSONArray jsonArray=new JSONArray();
    ClickItemEvent clickItemEvent;
    String cart_id;

    public My_order_adapter(Context mContext, JSONArray jsonArray,ClickItemEvent clickItemEvent) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_layout,parent,false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        try {
            JSONObject categoriesobject=jsonArray.getJSONObject(position);


            Log.e("tag", "onBindViewHolder: "+categoriesobject.getString("address") );

            Glide.with(mContext).
                    load(categoriesobject.getString("restaurant_logo"))
                    .error(R.drawable.profileicon)
                    .into(holder.img_restaurant);



            holder.txt_restroname.setText(categoriesobject.getString("restaurant_name"));
            holder.txt_address.setText(categoriesobject.getString("address"));
            holder.txt_orderid.setText("PIKOPAKO#"+categoriesobject.getString("id"));
            holder.txt_charges.setText("€"+categoriesobject.getString("paid_amount"));
            holder.txt_paymentmode.setText(mContext.getString(R.string.paid_via)+" "+categoriesobject.getString("payment_method"));
            holder.txt_status.setText(categoriesobject.getString("order_status"));
            holder.txt_items.setText(categoriesobject.getString("food_items"));

            Log.e(TAG, "onBindViewHolder:id: "+categoriesobject.getString("id") +"Restro id"+categoriesobject.getString("restaurant_id") );
            BaseApplication.getInstance().getSession().setRestroId("");
            BaseApplication.getInstance().getSession().setRestroId(categoriesobject.getString("restaurant_id"));
//
            holder.mSnackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, OrderList.class);
                    try {
                        i.putExtra("cart_id",jsonArray.getJSONObject(position).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("cart", "onClick: "+cart_id );
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("data", productListModelArrayList);
//        bundle.putString("position", position);
//        i.putExtras(bundle);
                   mContext.startActivity(i);
                  //  mContext.getApplicationContext().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

            //reorder
            holder.btn_reorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

               try {
                    //get Old cart data if exist
                 String oldCart = BaseApplication.getInstance().getSession().getCartItems();
                 if (!oldCart.isEmpty()){

                         JSONObject oldCartData = new JSONObject(oldCart);

                         if (oldCartData.getInt("total_items") <= 0)
                             callApi(jsonArray.getJSONObject(position).getString("id"));
                         else{

                      //       UiHelper.showToast(mContext,"Already have item in cart, can't reorder !");

                             final Dialog dialog = new Dialog(mContext);
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
                                     try {
                                         callApi(jsonArray.getJSONObject(position).getString("id"));
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                     dialog.dismiss();
                                 }
                             });




                         }


                  }
                 else
                     callApi(jsonArray.getJSONObject(position).getString("id"));

                 } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mSnackView;
        ImageView img_restaurant;
        CustomTextViewBold txt_restroname,txt_orderid,txt_charges,txt_paymentmode,txt_status;
        CustomTextViewNormal txt_address,txt_items;
        Button btn_reorder;

        public ViewHolder(View itemView) {
            super(itemView);
            mSnackView=(RelativeLayout)itemView.findViewById(R.id.rrrl);
            img_restaurant=(ImageView)itemView.findViewById(R.id.img_restaurant);
            txt_restroname=(CustomTextViewBold)itemView.findViewById(R.id.txt_restroname);
            txt_address=(CustomTextViewNormal)itemView.findViewById(R.id.txt_address);
            txt_orderid=(CustomTextViewBold)itemView.findViewById(R.id.txt_orderid);
            txt_charges=(CustomTextViewBold)itemView.findViewById(R.id.txt_charges);
            txt_paymentmode=(CustomTextViewBold)itemView.findViewById(R.id.txt_paymentmode);
            txt_status=(CustomTextViewBold)itemView.findViewById(R.id.txt_status);
            txt_items=(CustomTextViewNormal)itemView.findViewById(R.id.txt_items);
            btn_reorder=(Button)itemView.findViewById(R.id.btn_reorder);
        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }


    private void callApi(String cart_id){

        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(mContext,false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("cart_id",cart_id);

        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getOrdersDetail(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(mContext, call, new NetworkController.APIHandler() {
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
                                int quantity=1;
                                if (!food_items.getJSONObject(i).getString("quantity").equalsIgnoreCase("null") && food_items.getJSONObject(i).getString("quantity") !=null){
                                quantity=food_items.getJSONObject(i).getInt("quantity");
                                }
                                //add in total price
                                total_price = total_price + ((product_price + toppings_price) * quantity);
                                //total items
                                total_items = total_items + quantity;

                               // Repeat toppings for quantity build items array
                                JSONArray items_array = new JSONArray();
                                for (int x =0; x < quantity; x++){
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
                            Intent intent = new Intent(mContext, ViewCartActivity.class);
                            intent.putExtra("restaurant_id",data.getString("restaurant_id"));
                            intent.putExtra("coordinate_id","-1");
                            mContext.startActivity(intent);
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





}
