package com.pikopako.Adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pikopako.Activity.Choose_topping_Activity;
import com.pikopako.Activity.ViewCartActivity;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.CartFragment;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ViewCartAdapter extends RecyclerView.Adapter<ViewCartAdapter.ViewHolder> {


    Context mContext;
    JSONArray simplified_product_array;
    Ingrediants_modal ingrediants_modal;
    ViewCartActivity clickItemEvent;
    int itemQuantity = 1;
    int maxItemQuantity = 10;
    ViewCartActivity viewCartActivity;
    CartFragment cartFragmentobject;
    FragmentActivity cartFragment;
    float totalPrice;
    String food_id;

    long click_time = 0;
    long delay = 700;

    boolean ActivityCAll = false;

    String TAG = "ViewCartAdapter";

    //View Cart Activity
    public ViewCartAdapter(JSONArray simplified_product_array, ViewCartActivity mContext, ViewCartActivity clickItemEvent) {
        this.simplified_product_array = simplified_product_array;
        this.mContext = mContext;
        this.clickItemEvent = clickItemEvent;
        viewCartActivity = (ViewCartActivity) mContext;

        ActivityCAll = true;
    }

    //Frag
    public ViewCartAdapter(JSONArray simlified_products_array, CartFragment context, FragmentActivity activity) {
        this.simplified_product_array = simlified_products_array;
        mContext = activity;
        this.cartFragment = activity;
        cartFragmentobject = context;

        ActivityCAll = false;
    }

    public void updateArrayList(JSONArray simplified_product_array) {
        Log.e(TAG, "Cart Adapter ArrayList Update :" + simplified_product_array.toString());
        this.simplified_product_array = simplified_product_array;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_view_row, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        try {

            JSONObject jsonObject = new JSONObject(String.valueOf(simplified_product_array.getJSONObject(position)));

            holder.txt_foodname.setText(jsonObject.getString("product_Name"));

            holder.txtTotalProductQuantity.setText(jsonObject.getString("product_quantity"));

            food_id = jsonObject.getString("product_id");
            //
            String toppings = mContext.getString(R.string.without_customization);
            float product_price = Float.parseFloat(jsonObject.getString("product_price"));
            for (int i = 0; i < jsonObject.getJSONArray("toppings").length(); i++) {
                JSONObject toppings_object = jsonObject.getJSONArray("toppings").getJSONObject(i);

                if (i != 0)
                    toppings = toppings + "," + toppings_object.getString("name");
                else
                    toppings = toppings_object.getString("name");

                product_price = (product_price + Float.parseFloat(toppings_object.getString("price")));

            }
            holder.txt_food_toppings.setText(toppings);

            totalPrice = Float.parseFloat(String.valueOf(product_price * jsonObject.getInt("product_quantity")));
            holder.txtTotalProductPrice.setText("€" + String.format(Locale.ENGLISH, "%.2f", totalPrice));
            if (!toppings.equalsIgnoreCase(mContext.getString(R.string.without_customization))) {
                holder.lyt_customized.setVisibility(View.VISIBLE);
                holder.lyt_withoutcustomize.setVisibility(View.GONE);
                holder.lyt_custom.setVisibility(View.VISIBLE);
            } else {
                holder.lyt_customized.setVisibility(View.GONE);
                holder.lyt_custom.setVisibility(View.VISIBLE);
                holder.lyt_withoutcustomize.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.txt_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SystemClock.elapsedRealtime()-click_time < delay)
//                    return;
//                click_time=SystemClock.elapsedRealtime();
                //  add_product_copy(position);

                if (BaseApplication.getInstance().getSession().getIsToppingAvble()) {
                    addProductDialog(position);
                } else add_product_copy(position);


//
//                if (BaseApplication.getInstance().getSession().isLoggedIn())
//                    MainActivity.setCartCount();
//                else
//                    ((DefaultActivity)mContext).setCartCount();
            }
        });

        holder.txt_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SystemClock.elapsedRealtime()-click_time < delay)
//                    return;
//                click_time=SystemClock.elapsedRealtime();
                remove_product(position);
//                if (BaseApplication.getInstance().getSession().isLoggedIn())
//                    ((MainActivity)mContext).setCartCount();
//                else
//                    ((DefaultActivity)mContext).setCartCount();

            }
        });

        holder.txt_customized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SystemClock.elapsedRealtime()-click_time < delay)
//                    return;
//                click_time=SystemClock.elapsedRealtime();
                //Update Toppings
                UpdateToppings(position);
            }
        });

        holder.txt_customiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SystemClock.elapsedRealtime()-click_time < delay)
//                    return;
//                click_time=SystemClock.elapsedRealtime();
                //Update Toppings
                UpdateToppings(position);
            }
        });

    }

    private void UpdateToppings(int position) {
        try {
            Intent iin = new Intent(mContext, Choose_topping_Activity.class);
            iin.putExtra("food_id", simplified_product_array.getJSONObject(position).getString("product_id"));
            iin.putExtra("toppings_array", simplified_product_array.getJSONObject(position).getJSONArray("toppings").toString());
            iin.putExtra("customize", "true");
            iin.putExtra("pos", position);

            if (ActivityCAll)
                ((ViewCartActivity) mContext).startActivityForResult(iin, 201);

            else
                cartFragmentobject.startActivityForResult(iin, 201);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addProductDialog(final int position) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog_customization);


        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
        Button btn_Choose = (Button) dialog.findViewById(R.id.btn_Choose);
        Button btn_repeatLast = (Button) dialog.findViewById(R.id.btnRepeatLast);


        //Cancel
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        //Repeat current product
        btn_repeatLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_product_copy(position);
                dialog.dismiss();
            }
        });

        //I'll Choose
        btn_Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent iin = new Intent(mContext, Choose_topping_Activity.class);
                    iin.putExtra("food_id", simplified_product_array.getJSONObject(position).getString("product_id"));
                    //    iin.putExtra("customize","true");
                    if (ActivityCAll)
                        ((ViewCartActivity) mContext).startActivityForResult(iin, 201);
                    else
                        cartFragmentobject.startActivityForResult(iin, 201);

                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return simplified_product_array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewNormal txt_less, txt_max, txt_food_toppings, txt_customized, txt_customiz;
        CustomTextViewBold txtTotalProductQuantity, txtTotalProductPrice, txt_foodname;
        LinearLayout lyt_customized, lyt_custom, lyt_withoutcustomize;

        public ViewHolder(View itemView) {
            super(itemView);
            lyt_custom = (LinearLayout) itemView.findViewById(R.id.lyt_custom);
            lyt_customized = (LinearLayout) itemView.findViewById(R.id.lyt_customized);
            lyt_withoutcustomize = (LinearLayout) itemView.findViewById(R.id.lyt_withoutcustomize);
            txt_less = (CustomTextViewNormal) itemView.findViewById(R.id.txt_Less);
            txt_max = (CustomTextViewNormal) itemView.findViewById(R.id.txt_max);
            txtTotalProductQuantity = (CustomTextViewBold) itemView.findViewById(R.id.txtTotalProductQuantity);
            txtTotalProductPrice = (CustomTextViewBold) itemView.findViewById(R.id.txtTotalProductPrice);
            txt_foodname = (CustomTextViewBold) itemView.findViewById(R.id.txt_foodname);
            txt_food_toppings = (CustomTextViewNormal) itemView.findViewById(R.id.txt_food_toppings);
            txt_customized = (CustomTextViewNormal) itemView.findViewById(R.id.txt_customized);
            txt_customiz = (CustomTextViewNormal) itemView.findViewById(R.id.txt_customiz);
        }
    }

    public interface ClickItemEvent {
        void onClickItem(String itemId);
    }


    private int getMaxItemQuantity() {
        if (itemQuantity >= 1) {
            itemQuantity++;

        }
        return itemQuantity;
    }

    private int getMinItemQuantity() {
        if (itemQuantity > 0) {
            itemQuantity--;

        } else if (itemQuantity == 0) {

            UiHelper.showToast(mContext, "Can not less the quantity");
        }
        return itemQuantity;
    }

    //Add copy of the product
    void add_product_copy(int position) {

        try {

            //Add product to saved cart data

            String saved_cart_data = BaseApplication.getInstance().getSession().getCartItems();
            JSONObject saved_cart_data_object = new JSONObject(saved_cart_data);
            JSONArray cart_product_array = new JSONArray(saved_cart_data_object.getJSONArray("data").toString());

            float product_Dis = 0;
            float newPrice = Float.parseFloat(saved_cart_data_object.getString("total_price"));
            float dis_rate = (float) simplified_product_array.getJSONObject(position).getDouble("product_discount");
            //find product to be added
            for (int i = 0; i < cart_product_array.length(); i++) {

                JSONObject cart_product_object = cart_product_array.getJSONObject(i);

                if (cart_product_object.getInt("id") == simplified_product_array.getJSONObject(position).getInt("product_id")) {
                    //calulate product price

                    float toppings_price_simplefied = 0;

                    JSONArray items_array = cart_product_object.getJSONArray("items");
//                    JSONArray toppings_array = items_array.getJSONArray(items_array.length()-1);

                    JSONArray simplified_items_array = simplified_product_array.getJSONObject(position).getJSONArray("toppings");
                    for (int k = 0; k < simplified_items_array.length(); k++) {
                        toppings_price_simplefied = (float) (toppings_price_simplefied + simplified_items_array.getJSONObject(k).optDouble("price", 0));
                    }

//                    for (int j=0; j < toppings_array.length(); j++) {
//                        item_price = item_price + Float.parseFloat(toppings_array.getJSONObject(j).getString("price"));
//                    }

                    //get Discount price by rate
                    product_Dis = (dis_rate * Float.parseFloat(cart_product_object.getString("price"))) / 100;
//                    Log.e(TAG," toppings price by Cart:"+item_price);
                    Log.e(TAG, " toppings price by Simplified:" + toppings_price_simplefied);

                    newPrice = newPrice + (Float.parseFloat(cart_product_object.getString("price")) + toppings_price_simplefied);

                    //and new item copy
                    items_array.put(simplified_product_array.getJSONObject(position).getJSONArray("toppings"));

                    if (!ActivityCAll) {
                        cartFragmentobject.updateText(newPrice, product_Dis * -1);
                    } else {
                        Log.e(TAG, "view cart adapter update text: " + newPrice);
                        viewCartActivity.updateText(newPrice, product_Dis * -1);
                    }
                }

            }
            //update cart object
            saved_cart_data_object.put("total_items", saved_cart_data_object.getInt("total_items") + 1);
            saved_cart_data_object.put("total_price", newPrice);
            saved_cart_data_object.put("data", cart_product_array);
            Log.e(TAG, "cart data on Add:" + saved_cart_data_object.toString());
            BaseApplication.getInstance().getSession().setCartItmes(saved_cart_data_object.toString());
//            if (BaseApplication.getInstance().getSession().isLoggedIn())
//                ((MainActivity)mContext).setCartCount();
//            else
//                ((DefaultActivity)mContext).setCartCount();
            //remove local or Simplified product copy
            int q = simplified_product_array.getJSONObject(position).getInt("product_quantity");
            simplified_product_array.getJSONObject(position).put("product_quantity", q + 1);

            //update Simplified data
            BaseApplication.getInstance().getSession().setSimplifiedCartData(simplified_product_array.toString());


            //update ui
            notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Remove product
    @TargetApi(Build.VERSION_CODES.KITKAT)
    void remove_product(int position) {
        Log.e(TAG, "position of product which is removing: " + position);
        float product_Dis = 0;
        try {

            //Remove product from saved cart data

            String saved_cart_data = BaseApplication.getInstance().getSession().getCartItems();
            JSONObject saved_cart_data_object = new JSONObject(saved_cart_data);
            JSONArray cart_product_array = new JSONArray(saved_cart_data_object.getJSONArray("data").toString());

            Log.e(TAG, "Saved Cart Data object: " + saved_cart_data_object.toString());
            float total_price = Float.parseFloat(saved_cart_data_object.getString("total_price"));
            float newPrice = total_price;
            float dis_rate = (float) simplified_product_array.getJSONObject(position).getDouble("product_discount");

            //find product to be removed
            for (int i = 0; i < cart_product_array.length(); i++) {

                JSONObject cart_product_object = cart_product_array.getJSONObject(i);

                if (cart_product_object.getInt("id") == simplified_product_array.getJSONObject(position).getInt("product_id")) {
                    //calulate product price
                    float item_price = 0;
                    JSONArray items_array = cart_product_object.getJSONArray("items");

                    Log.e(TAG, "remove_product:ITEMARAY " + items_array);
                    //Find toppings array/pos to be removed
                    int posToRemoved = -1;
                    JSONArray simplifiedToppings_array = simplified_product_array.getJSONObject(position).getJSONArray("toppings");
                    Log.e(TAG, "simplified toping array length: " + simplifiedToppings_array.length());
                    for (int x = 0; x < items_array.length(); x++) {
                        //Lock for empty array in items_array.
                        if (simplifiedToppings_array.length() == 0) {
                            if (items_array.getJSONArray(x).length() == 0) {
                                posToRemoved = x;
                                break;
                            }
                        }
                        // if not empty find who is
                        else {
                            for (int z = 0; z < simplifiedToppings_array.length(); z++) {
                                if (posToRemoved != -1)
                                    break;
                                for (int y = 0; y < items_array.getJSONArray(x).length(); y++) {

                                    if (simplifiedToppings_array.getJSONObject(z).getString("id").equalsIgnoreCase(items_array.getJSONArray(x).getJSONObject(y).getString("id"))) {
                                        posToRemoved = x;
                                        break;
                                    }
                                }
                            }
                        }

                        if (posToRemoved != -1)
                            break;
                    }

                    Log.e(TAG, "PosToRemoved :" + posToRemoved);

                    JSONArray toppings_array = items_array.getJSONArray(posToRemoved/*items_array.length()-1*/);

                    Log.e(TAG, "remove_product:TOPPINGAray " + toppings_array);
                    for (int j = 0; j < toppings_array.length(); j++) {
                        item_price = item_price + Float.parseFloat(toppings_array.getJSONObject(j).getString("price"));
                    }

                    Log.e(TAG, "Totla:" + total_price + " item price:" + item_price + " cart object price:" + cart_product_object.getString("price"));

                    //get Discount price by rate
                    product_Dis = (dis_rate * Float.parseFloat(cart_product_object.getString("price"))) / 100;

                    newPrice = total_price - (Float.parseFloat(cart_product_object.getString("price")) + item_price);
                    Log.e(TAG, "new pice when calculated: " + newPrice);
                    //if  have only single copy then remove whole product
                    if (cart_product_object.getJSONArray("items").length() == 1) {
                        cart_product_array.remove(i);
                        //     newPrice=0;
                    }
                    //remove top item copy
                    else {
                        cart_product_object.getJSONArray("items").remove(posToRemoved/*cart_product_object.getJSONArray("items").length()-1*/);
                        Log.e(TAG, "item remove :" + cart_product_object.getJSONArray("items").length());
                    }
                    break;
                }

            }
            //update cart object
            saved_cart_data_object.put("total_items", saved_cart_data_object.getInt("total_items") - 1);
            saved_cart_data_object.put("total_price", newPrice);
            saved_cart_data_object.put("data", cart_product_array);
            Log.e(TAG, "cart data on remove:" + saved_cart_data_object.toString());
            BaseApplication.getInstance().getSession().setCartItmes(saved_cart_data_object.toString());

//            if (BaseApplication.getInstance().getSession().isLoggedIn())
//            ((MainActivity)mContext).setCartCount();
//            else
//            ((DefaultActivity)mContext).setCartCount();

            //remove local or Simplified product copy
            int q = simplified_product_array.getJSONObject(position).getInt("product_quantity");
            if (q == 1)
                simplified_product_array.remove(position);
            else
                simplified_product_array.getJSONObject(position).put("product_quantity", q - 1);

            //update Simplified data
            BaseApplication.getInstance().getSession().setSimplifiedCartData(simplified_product_array.toString());

            Log.e(TAG, "new price when removing: " + newPrice + "Discount:" + product_Dis);
            //update ui
            notifyDataSetChanged();
            if (cartFragmentobject != null) {
                cartFragmentobject.updateText(newPrice, product_Dis);
            } else {
                viewCartActivity.updateText(newPrice, product_Dis);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}