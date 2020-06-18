package com.pikopako.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.Activity.OrderList;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    OrderList orderList1;
    Context mContext;
    JSONArray jsonArray;
    RestroInfoServices clickItemEvent;
    String id ="";

    public OrderListAdapter(JSONArray jsonArray,OrderList mContext) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;
        orderList1=(OrderList)mContext;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_layout_row,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        try {
            JSONObject categoriesobject=jsonArray.getJSONObject(position);
            JSONArray toppingArray=categoriesobject.getJSONArray("toopings");


        if (toppingArray.length()>0) {
            String topping_name = "";
            float product_price=0;
            float toppings_price =0;
            float total_product_price=0;
            for (int i = 0; i < toppingArray.length(); i++) {

                if (topping_name.isEmpty())
                topping_name = topping_name+toppingArray.getJSONObject(i).getString("topping_name");
                else
                topping_name = topping_name+","+toppingArray.getJSONObject(i).getString("topping_name");

                toppings_price =  toppings_price + (float) (toppingArray.getJSONObject(i).getDouble("topping_price"));


//                holder.txt_foodName.setText(categoriesobject.getString("food_name") + "(" + toppingArray.getJSONObject(i).getString("topping_name")+ ")" + " x " + categoriesobject.getString("quantity"));
//                holder.txt_food_price.setText(categoriesobject.getString("price"));
            }
            product_price= Float.parseFloat(categoriesobject.getString("unit_price"));
            String foodWithToppings = categoriesobject.getString("food_name")+"("+topping_name+")"+" X "+categoriesobject.getString("quantity");

            holder.txt_foodName.setText(foodWithToppings);
            total_product_price=(toppings_price+product_price) * categoriesobject.getInt("quantity");
            holder.txt_food_price.setText(String.valueOf(total_product_price));

        }
        else {

            holder.txt_foodName.setText(categoriesobject.getString("food_name") + " x " + categoriesobject.getString("quantity"));

            holder.txt_food_price.setText("€"+categoriesobject.getString("price"));



        }

        float totalprice=0;
        totalprice=totalprice+Float.parseFloat(categoriesobject.getString("price"));

            orderList1.update(totalprice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        RelativeLayout mSnackView;
            CustomTextViewNormal txt_foodName,txt_food_price;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_foodName=(CustomTextViewNormal)itemView.findViewById(R.id.txt_foodName);
            txt_food_price=(CustomTextViewNormal)itemView.findViewById(R.id.txt_food_price);

        }
    }

}


