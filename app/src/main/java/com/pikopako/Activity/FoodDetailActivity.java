package com.pikopako.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonObject;
import com.pikopako.Adapter.FoodDetailAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class FoodDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.topsnackview)
    AppBarLayout mSnackView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycleviewfood)
    RecyclerView recyclerView;

    @BindView(R.id.tv_add)
    CustomTextViewBold tv_add;

    @BindView(R.id.Iv_food_img)
    ImageView Iv_food_img;

    @BindView(R.id.Txt_food_name)
    CustomTextViewBold Txt_food_name;

    @BindView(R.id.Txt_food_price)
    CustomTextViewBold Txt_food_price;

    @BindView(R.id.txt_ingredients)
    CustomTextViewNormal txt_ingredients;

    @BindView(R.id.txt_description)
    CustomTextViewNormal txt_description;

    @BindView(R.id.add_layout)
    LinearLayout add_layout;

    @BindView(R.id.add_quantity_layout)
    LinearLayout add_quantity_layout;

    @BindView(R.id.snackbar_layout)
    LinearLayout snackbar_layout;

    @BindView(R.id.tv_view_cart)
    CustomTextViewBold tv_view_cart;

    @BindView(R.id.txt_Less)
    CustomTextViewNormal txt_Less;

    @BindView(R.id.txt_max)
    CustomTextViewNormal txt_max;

    @BindView(R.id.txtTotalProductQuantity)
    CustomTextViewBold txtTotalProductQuantity;

    @BindView(R.id.tv_totalprice)
    CustomTextViewBold tv_total_price;

    @BindView(R.id.tv_snckview_quantity)
    CustomTextViewBold tv_snackview_totalQuantity;

    @BindView(R.id.tv_closed)
    CustomTextViewBold tv_closed;
    @BindView(R.id.Iv_heart)
    ImageView Iv_heart;

    @BindView(R.id.Iv_heart_red)
    ImageView Iv_heart_red;

    @BindView(R.id.lyt_make)
    LinearLayout lyt_make;

    @BindView(R.id.lyt_toppings)
    LinearLayout lyt_toppings;

    @BindView(R.id.Iv_opentoppings)
    ImageView Iv_opentoppings;

    @BindView(R.id.Iv_closetoppings)
    ImageView Iv_closetoppings;

    String TAG = "FoodDetailActivity";

    long click_time = 0;
    long delay = 700;
    String food_id;
    ArrayList<Ingrediants_modal> ingrediants_modalArrayList = new ArrayList<>();
    Ingrediants_modal productModel = new Ingrediants_modal();
    int itemQuantity = 1;
    FoodDetailAdapter foodDetailAdapter;
    String id, restaurant_id, coordinate_id;
    /// For Cart Data
    float total_price = 0;
    int total_items = 0;
    float toppings_price = 0;
    JSONObject cartItems = null;
    JSONObject product_Object = null;
    int position = -1;
    ///
    ArrayList<ArrayList<Ingrediants_modal>> List_of_list = new ArrayList<>();
    JSONArray origin_json_array = new JSONArray();
    String restro_image = "";
    String restro_name = "";
    String restro_location = "";
    String restro_status = "";
    String restro_id = "";
    ///
    String language = "";
    boolean clearListOfList = true;
    boolean reCreateListOfList = false;
    private ArrayList<Ingrediants_modal> original_ingrediants_modal_arrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_food_detail);
        ButterKnife.bind(this);
        listners();
        initialize();


        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);

        Intent intent = getIntent();
        //Favorite food intent only if
//        if (intent.hasExtra("restaurant_id")){
//            restaurant_id=getIntent().getStringExtra("restaurant_id");
//        }
        if (intent.hasExtra("restro_bundle")) {
            Bundle bundle1 = intent.getBundleExtra("restro_bundle");
            restro_image = bundle1.getString("restro_image");
            restro_name = bundle1.getString("restro_name");
            restro_location = bundle1.getString("restro_location");
            restro_status = bundle1.getString("restro_status");
            restro_id = bundle1.getString("restro_id");

            if (intent.hasExtra("restaurant_id"))
                BaseApplication.getInstance().getSession().setRestroId(getIntent().getStringExtra("restaurant_id"));
            else
                BaseApplication.getInstance().getSession().setRestroId(restro_id);
            BaseApplication.getInstance().getSession().setRestroStatus(restro_status);
            BaseApplication.getInstance().getSession().setRestroName(restro_name);
            BaseApplication.getInstance().getSession().setRestroLocation(restro_location);
            BaseApplication.getInstance().getSession().setRestroImage(restro_image);
            Log.e(TAG, "onCreate bundel id: " + bundle1.getString("restro_id"));
        }

        if (restro_status.equalsIgnoreCase("Closed")) {
            tv_closed.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.GONE);
        } else {
            tv_add.setVisibility(View.VISIBLE);
            tv_closed.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra("statusFromFavourite")){

            if (getIntent().getStringExtra("statusFromFavourite").equalsIgnoreCase("Closed")) {
                tv_closed.setVisibility(View.VISIBLE);
                tv_add.setVisibility(View.GONE);
            } else {
                tv_add.setVisibility(View.VISIBLE);
                tv_closed.setVisibility(View.GONE);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        get_CartData();
    }

    private void initialize() {
        id = getIntent().getStringExtra("food_id");
        restaurant_id = getIntent().getStringExtra("restaurant_id");
        coordinate_id = getIntent().getStringExtra("coordinate_id");
        Log.e(TAG, "initialize: " + restaurant_id + "coordinateid" + coordinate_id);
        Log.e(TAG, "inintialize:");
        callApi();
    }

    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //    mTitle.setText("  ");
        //   btn_menu.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_view_cart.setOnClickListener(this);
        txt_Less.setOnClickListener(this);
        txt_max.setOnClickListener(this);
        Iv_heart.setOnClickListener(this);
        Iv_heart_red.setOnClickListener(this);
        lyt_make.setOnClickListener(this);
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

    public void onClickItem(String itemId) {
//        Intent i = new Intent(RestroInfoActivity.this,RestroDetailItemFragment.class);
//        startActivity(i);
//        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.Iv_heart:

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();
                if (!BaseApplication.getInstance().getSession().isLoggedIn()) {
                    UiHelper.showToast(this, getString(R.string.pls_login_first));
                    Iv_heart.setVisibility(View.VISIBLE);
                    Iv_heart_red.setVisibility(View.GONE);
                } else {
                    Iv_heart.setVisibility(View.GONE);
                    Iv_heart_red.setVisibility(View.VISIBLE);
                    add_to_favourite_api();
                }
                break;


            case R.id.lyt_make:


                if (Iv_closetoppings.getVisibility() == View.VISIBLE) {
                    lyt_toppings.setVisibility(View.GONE);
                    Iv_closetoppings.setVisibility(View.GONE);
                    Iv_opentoppings.setVisibility(View.VISIBLE);

                } else {
                    lyt_toppings.setVisibility(View.VISIBLE);
                    Iv_closetoppings.setVisibility(View.VISIBLE);
                    Iv_opentoppings.setVisibility(View.GONE);
                }

                break;
            case R.id.Iv_heart_red:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();

                Iv_heart_red.setVisibility(View.GONE);
                Iv_heart.setVisibility(View.VISIBLE);
                add_to_delete_food();
                break;

            case R.id.tv_add:

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();


                if (cartItems != null) {
                    try {
                        JSONArray dataArray = cartItems.getJSONArray("data");
                        if (dataArray.length() != 0) {

                            if (!restro_id.equalsIgnoreCase(dataArray.getJSONObject(0).getString("restro_id"))) {
                                UiHelper.showToast(this, getString(R.string.you_are_not_allowed_toorder));
                                return;
                            } else if (!restro_id.equalsIgnoreCase(getIntent().getStringExtra("restaurant_id"))) {
                                UiHelper.showToast(this, getString(R.string.you_are_not_allowed_toorder));
                                return;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    BaseApplication.getInstance().getSession().setRestroImage(restro_image);
                    BaseApplication.getInstance().getSession().setRestroLocation(restro_location);
                    BaseApplication.getInstance().getSession().setRestroName(restro_name);
                    BaseApplication.getInstance().getSession().setRestroStatus(restro_status);
                    BaseApplication.getInstance().getSession().setRestroId(restro_id);
                    BaseApplication.getInstance().getSession().setCoordinateId(coordinate_id);

                }

                Intent intent3 = new Intent(this, Choose_topping_Activity.class);
                if (getIntent().hasExtra("food_id")) {
                    intent3.putExtra("food_id", getIntent().getStringExtra("food_id"));
                } else
                    intent3.putExtra("food_id", id);

                intent3.putExtra("customize", "false");
                startActivityForResult(intent3, 225);

//                add_layout.setVisibility(View.GONE);
//                add_quantity_layout.setVisibility(View.VISIBLE);
//                snackbar_layout.setVisibility(View.VISIBLE);
                overridePendingTransition(R.anim.trans_botton_in, R.anim.trans_bottom_out);

                //total price
//                toppings_price = 0;
//                total_price = total_price + Float.parseFloat(productModel.getProductPrice());
//
//                for (int i = 0; i < foodDetailAdapter.adapter_arraylist.size(); i++) {
//                    if (foodDetailAdapter.adapter_arraylist.get(i).isIngredient_add)
//                        toppings_price = toppings_price + Float.parseFloat(foodDetailAdapter.adapter_arraylist.get(i).getIngredientPrice());
//                }
//
//
//                total_price = total_price + toppings_price;
//                Log.e(TAG, "when clicking add total items: " + total_items);
//                total_items++;
//
//                tv_total_price.setText("€"+String.valueOf(total_price));
//
//                //add to List of List
//                List_of_list.clear();
//                List_of_list.add(new ArrayList<>(foodDetailAdapter.adapter_arraylist));
//                set_ingridiants_Adapter();
//
//                tv_snackview_totalQuantity.setText(String.valueOf(total_items)+" "+getString(R.string.item));
//
//                //Update cart data
//                setCartData(true);

                break;

            case R.id.tv_view_cart:
                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();

                clearListOfList = true;
                //clear
                //  BaseApplication.getInstance().getSession().setCartItmes("");

                ArrayList<Ingrediants_modal> object = new ArrayList<Ingrediants_modal>();

                //   List_of_list = (ArrayList<ArrayList<Ingrediants_modal>>) getIntent().getSerializableExtra("ARRAYLIST");

                Intent intent = new Intent(FoodDetailActivity.this, ViewCartActivity.class);


                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) List_of_list);
                Log.e(TAG, "onClick: " + List_of_list.toString());
                intent.putExtra("BUNDLE", args);

                if (getIntent().hasExtra("restaurant_id")) {
                    intent.putExtra("restaurant_id", getIntent().getStringExtra("restaurant_id"));
                } else
                    intent.putExtra("restaurant_id", restaurant_id);
                intent.putExtra("coordinate_id", coordinate_id);
                Log.e(TAG, "onClick: " + restaurant_id);
                startActivity(intent);


                //  Intent intent=new Intent(FoodDetailActivity.this,ViewCartActivity.class);
                // startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;

            case R.id.txt_Less:

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();


                if (txtTotalProductQuantity.getText().toString().equalsIgnoreCase("0")) {
                    snackbar_layout.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.GONE);
                    add_layout.setVisibility(View.VISIBLE);
                }
                Log.e(TAG, "onClick: " + List_of_list.size());

                if (List_of_list.size() > 0) {
                    Log.e(TAG, "onClicked minus: " + List_of_list.size());
                    //remove item price from total price
                    toppings_price = 0;
                    for (int i = 0; i < List_of_list.get(List_of_list.size() - 1).size(); i++) {
                        if (List_of_list.get(List_of_list.size() - 1).get(i).isIngredient_add)
                            toppings_price = Float.parseFloat(List_of_list.get(List_of_list.size() - 1).get(i).getIngredientPrice()) + toppings_price;
                    }

                    total_price = total_price - (toppings_price + Float.parseFloat(productModel.getProductPrice()));

                    //Remove item from list

                    List_of_list.remove(List_of_list.size() - 1);


                    if (List_of_list.size() > 0) {
                        foodDetailAdapter.adapter_arraylist = null;
                        foodDetailAdapter.adapter_arraylist = List_of_list.get(List_of_list.size() - 1);
                        foodDetailAdapter.notifyDataSetChanged();
                    } else {
                        //show add btn

                        foodDetailAdapter.adapter_arraylist = new Ingrediants_modal().parseIngrediants(origin_json_array);
                        foodDetailAdapter.notifyDataSetChanged();
                    }


                    tv_total_price.setText("€" + String.valueOf(total_price));


//                        Log.e("bug", "on remove after onClick: is =>" + foodDetailAdapter.adapter_arraylist.get(0).isIngredient_add);
                    //                 Log.e("bug", "on remove total price :" + total_price + "\ntoppings price :" + toppings_price + "\nproduct price :" + productModel.getProductPrice());


                    txtTotalProductQuantity.setText(String.valueOf(List_of_list.size()));
                    tv_snackview_totalQuantity.setText(txtTotalProductQuantity.getText() + " " + getResources().getString(R.string.item));
                    productModel.setProductQuantity(String.valueOf(List_of_list.size()));

                    total_items--;
                    //Update cart data
                    setCartData(false);
                } else {
                    try {

                        if (product_Object != null) {
                            JSONArray items_array = product_Object.getJSONArray("items");

//                        if (items_array.length()==2){
//                            Log.e("to invisible ", "onClick: " );
//                            snackbar_layout.setVisibility(View.GONE);
//                            add_quantity_layout.setVisibility(View.GONE);
//                            add_layout.setVisibility(View.VISIBLE);
//                        }

                            if (items_array.length() > 0) {
                                Log.e(TAG, ">0: ");
                                JSONArray toppingsArray = items_array.getJSONArray(items_array.length() - 1);

                                int toppings_price = 0;
                                for (int i = 0; i < toppingsArray.length(); i++) {
                                    JSONObject toppings_object = toppingsArray.getJSONObject(i);

                                    toppings_price = toppings_price + toppings_object.getInt("price");

                                }

                                total_price = total_price - (toppings_price + Float.parseFloat(productModel.getProductPrice()));
                                tv_total_price.setText("€" + String.valueOf(total_price));

                                total_items--;
                                //Update cart data
                                setCartData(false);

                                if (total_items == 0) {
                                    BaseApplication.getInstance().getSession().setRestroImage("");
                                    BaseApplication.getInstance().getSession().setRestroLocation("");
                                    BaseApplication.getInstance().getSession().setRestroName("");
                                    BaseApplication.getInstance().getSession().setRestroStatus("");
                                    BaseApplication.getInstance().getSession().setRestroId("");
                                    BaseApplication.getInstance().getSession().setCoordinateId("");
                                }
                            }


                        }
//                    else if (txtTotalProductQuantity.getText().toString().equalsIgnoreCase("1")){
//                            Log.e("elsecndition", "onClick: " );
//                            snackbar_layout.setVisibility(View.GONE);
//                            add_quantity_layout.setVisibility(View.GONE);
//                            add_layout.setVisibility(View.VISIBLE);
//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                tv_snackview_totalQuantity.setText(String.valueOf(total_items) + " " + getResources().getString(R.string.item));
                tv_total_price.setText("€" + String.valueOf(total_price));

                if (total_items == 0) {
                    snackbar_layout.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.GONE);
                    add_layout.setVisibility(View.VISIBLE);
                }

                break;


            case R.id.txt_max:

                if (SystemClock.elapsedRealtime() - click_time < delay)
                    return;
                click_time = SystemClock.elapsedRealtime();

                if (product_Object != null) {

                    try {
                        if (!product_Object.getString("restro_id").equalsIgnoreCase(restro_id)) {

                            Log.e(TAG, "allowed: ");
                            UiHelper.showToast(this, "Not allowed from another restaurant");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "before dialog List of List size: " + List_of_list.size() + "   total items" + total_items);
                //Check Toppings Change
                //   if (check_toppings_change()){
                //       Log.e(TAG,"Toppings did Changed");
                //toppings did changed, show dialog what to do
                addProductDialog();
                //    }

//                // normal add
//                else{
//                    //add to List of List
//                    Log.e(TAG,"Toppings did not change");
//                    List_of_list.add(new ArrayList<>(foodDetailAdapter.adapter_arraylist));
//                  /*Same thing */
//                    //total price
//                    toppings_price = 0;
//                    for (int i = 0; i < foodDetailAdapter.adapter_arraylist.size(); i++) {
//                        if (foodDetailAdapter.adapter_arraylist.get(i).isIngredient_add)
//                            toppings_price = toppings_price + Float.parseFloat(foodDetailAdapter.adapter_arraylist.get(i).getIngredientPrice());
//                    }
//
//                    Log.e(TAG, "on add total price :" + total_price + "\ntoppings price :" + toppings_price + "\nproduct price :" + productModel.getProductPrice());
//
//                    total_price = total_price + toppings_price + Float.parseFloat(productModel.getProductPrice());
//
//                    tv_total_price.setText("€"+String.valueOf(total_price));
//
//                    total_items++;
//                    //Update cart data
//                    setCartData(true);
//                    //to reset ingredients list
//                    set_ingridiants_Adapter();
//                }

//                //to reset ingredients list
//                set_ingridiants_Adapter();
                Log.e(TAG, "List of List size: " + List_of_list.size() + "   total items" + total_items);
//                txtTotalProductQuantity.setText(String.valueOf(List_of_list.size()));
//                tv_snackview_totalQuantity.setText(txtTotalProductQuantity.getText() + " " + getResources().getString(R.string.item));
//                productModel.setProductQuantity(String.valueOf(List_of_list.size()));

//                total_items++;
//                //Update cart data
//                setCartData(true);

                break;

        }
    }

    //Check if toppings change
    private boolean check_toppings_change() {
        /* true if Toppings did Change, or False if topping did not change */
        Log.e(TAG, "Check ing toppings....");
        if (List_of_list.size() > 0) {

            ArrayList<Ingrediants_modal> previouse_list = List_of_list.get(List_of_list.size() - 1);
            ArrayList<Ingrediants_modal> nxt_list = new ArrayList<>(foodDetailAdapter.adapter_arraylist);

            if (previouse_list.size() == nxt_list.size()) {
                for (int i = 0; i < previouse_list.size(); i++) {
                    Log.e(TAG, "booleans =>:" + previouse_list.get(i).isIngredient_add + "==" + nxt_list.get(i).isIngredient_add);
                    if (previouse_list.get(i).isIngredient_add != nxt_list.get(i).isIngredient_add)
                        return true;
                }
            } else
                return true;
        } else
            return false;

        return false;
    }

    private void addProductDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog_customization);
        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
        Button btn_Choose = (Button) dialog.findViewById(R.id.btn_Choose);
        Button btn_repeatLast = (Button) dialog.findViewById(R.id.btnRepeatLast);
        CustomTextViewNormal txt_toppings = (CustomTextViewNormal) dialog.findViewById(R.id.textDial);

        Log.e(TAG, "inside add dialog List of List size: " + List_of_list.size() + "   total items" + total_items);
        //find Old toppings if added
        String oldToppings = "";
        if (List_of_list.size() > 0) {
            ArrayList<Ingrediants_modal> lastList = List_of_list.get(List_of_list.size() - 1);
            for (int k = 0; k < lastList.size(); k++) {
                if (lastList.get(k).isIngredient_add) {
                    if (oldToppings.isEmpty())
                        oldToppings = oldToppings + "" + lastList.get(k).ingredients_name;
                    else
                        oldToppings = oldToppings + "," + lastList.get(k).ingredients_name;
                }
            }
            if (lastList.size() == 0)
                oldToppings = getResources().getString(R.string.without_customization);
        } else
            oldToppings = getResources().getString(R.string.without_customization);

        txt_toppings.setText(oldToppings);

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
                //repeat the top item again
                Log.e(TAG, "onClick: " + List_of_list.size());
                ArrayList<Ingrediants_modal> newList = List_of_list.get(List_of_list.size() - 1);
                List_of_list.add(parse_into_new_list(newList));
                /*Dirty Math*/
                //total price
                toppings_price = 0;
                for (int i = 0; i < newList.size(); i++) {
                    if (newList.get(i).isIngredient_add)
                        toppings_price = toppings_price + Float.parseFloat(newList.get(i).getIngredientPrice());
                }

                Log.e(TAG, "on add total price :" + total_price + "\ntoppings price :" + toppings_price + "\nproduct price :" + productModel.getProductPrice());

                total_price = total_price + toppings_price + Float.parseFloat(productModel.getProductPrice());

                tv_total_price.setText("€" + String.valueOf(total_price));
                total_items++;
                //Update cart data
                setCartData(true);
                //to reset ingredients list
                set_ingridiants_Adapter();

                dialog.dismiss();
            }
        });

        //I'll Choose
        btn_Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent4 = new Intent(FoodDetailActivity.this, Choose_topping_Activity.class);
                if (getIntent().hasExtra("food_id")) {
                    intent4.putExtra("food_id", getIntent().getStringExtra("food_id"));
                } else
                    intent4.putExtra("food_id", id);

                intent4.putExtra("customize", "false");
                startActivityForResult(intent4, 225);

//                add_layout.setVisibility(View.GONE);
//                add_quantity_layout.setVisibility(View.VISIBLE);
//                snackbar_layout.setVisibility(View.VISIBLE);
                overridePendingTransition(R.anim.trans_botton_in, R.anim.trans_bottom_out);


                //add new Updated
//                 List_of_list.add(new ArrayList<>(foodDetailAdapter.adapter_arraylist));
//                /*Dirty math*/
//                //total price
//                toppings_price = 0;
//
//                for (int i = 0; i < foodDetailAdapter.adapter_arraylist.size(); i++) {
//                    if (foodDetailAdapter.adapter_arraylist.get(i).isIngredient_add)
//                        toppings_price = toppings_price + Float.parseFloat(foodDetailAdapter.adapter_arraylist.get(i).getIngredientPrice());
//                }
//
//                Log.e(TAG, "on add total price :" + total_price + "\ntoppings price :" + toppings_price + "\nproduct price :" + productModel.getProductPrice());
//
//                total_price = total_price + toppings_price + Float.parseFloat(productModel.getProductPrice());
//
//                tv_total_price.setText("€"+String.valueOf(total_price));
//                total_items++;
//                //Update cart data
//                setCartData(true);
//                //to reset ingredients list
//                set_ingridiants_Adapter();

                dialog.dismiss();
            }
        });
    }

    private void add_to_delete_food() {
        // final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        //   progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("food_item_id", id);
        jsonObject.addProperty("status", 1);
        jsonObject.addProperty("language", language);
        Log.e(TAG, "add delete id" + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addFavourite(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                //    progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e(TAG, "food added to delete");
                            UiHelper.showToast(FoodDetailActivity.this, jsonObject1.getString("message"));

                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void Error(String error) {
                //   if (progressDialog != null)
                //       progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    //     if (progressDialog != null)
                    //         progressDialog.dismiss();
                    UiHelper.showNetworkError(FoodDetailActivity.this, mSnackView);
                }
                Log.e(TAG, "isConnected : " + isConnected);
            }
        });
    }

    private void add_to_favourite_api() {

        //  final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        //   progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("food_item_id", id);
        jsonObject.addProperty("status", 0);
        jsonObject.addProperty("language", language);
        Log.e(TAG, "json payload favourite id" + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().addFavourite(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                //   progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e(TAG, "food added to favourite");
                            UiHelper.showToast(FoodDetailActivity.this, jsonObject1.getString("message"));

                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(FoodDetailActivity.this, LoginActivity.class);
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
                //if (progressDialog != null)
                //       progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    //     if (progressDialog != null)
                    //        progressDialog.dismiss();
                    UiHelper.showNetworkError(FoodDetailActivity.this, mSnackView);
                }
                Log.e(TAG, "isConnected : " + isConnected);
            }
        });
    }

    void set_ingridiants_Adapter() {

//        Log.e(TAG, "set adapter before :=>" + foodDetailAdapter.adapter_arraylist.get(0).isIngredient_add);
        foodDetailAdapter.adapter_arraylist.clear();
//        foodDetailAdapter.adapter_arraylist = new Ingrediants_modal().parseIngrediants(origin_json_array);
        if (List_of_list.size() > 0)
            foodDetailAdapter.adapter_arraylist = parse_into_new_list(List_of_list.get(List_of_list.size() - 1));
        else
            foodDetailAdapter.adapter_arraylist = new Ingrediants_modal().parseIngrediants(origin_json_array);

        foodDetailAdapter.notifyDataSetChanged();
        //  Log.e(TAG, "set adapter after:=>" + foodDetailAdapter.adapter_arraylist.get(0).isIngredient_add);
        Log.e(TAG, "set adapter :=>" + foodDetailAdapter.adapter_arraylist.toString());
        Log.e(TAG, "listOfList :=>" + List_of_list.toString());
    }

    private ArrayList<Ingrediants_modal> parse_into_new_list(ArrayList<Ingrediants_modal> modalArrayList) {
        ArrayList<Ingrediants_modal> newOne = new ArrayList<>();
        Ingrediants_modal ingrediants_modal;
        for (int i = 0; i < modalArrayList.size(); i++) {
            ingrediants_modal = new Ingrediants_modal();
            ingrediants_modal.isIngredient_add = modalArrayList.get(i).isIngredient_add;
            ingrediants_modal.discount = modalArrayList.get(i).discount;
            ingrediants_modal.ingredients_name = modalArrayList.get(i).ingredients_name;
            ingrediants_modal.ingredients_id = modalArrayList.get(i).ingredients_id;
            ingrediants_modal.ingredients_price = modalArrayList.get(i).ingredients_price;

            newOne.add(ingrediants_modal);
        }

        return newOne;
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

            UiHelper.showToast(FoodDetailActivity.this, "Can not less the quantity");
        }
        return itemQuantity;
    }

    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        if (getIntent().hasExtra("food_id")) {
            jsonObject.addProperty("food_id", getIntent().getStringExtra("food_id"));
        } else
            jsonObject.addProperty("food_id", id);
        jsonObject.addProperty("language", language);
        Log.e(TAG, "" + id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getIngredients(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        Log.e(TAG, "Success: " + jsonObject1.getString("status"));
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            Log.e(TAG, "Success: " + jsonObject1.getString("status"));

                            JSONObject data = jsonObject1.getJSONObject("data");

                            Txt_food_name.setText(data.getString("food_name"));
                            productModel.setFoodName(data.getString("food_name"));
                            productModel.setfoodid(data.getString("food_id"));
                            Txt_food_price.setText("€" + data.getString("price"));
                            productModel.setDiscount(data.getString("discount"));

                            Log.e(TAG, "Success: " + data.getString("discount"));

                            if (data.getString("is_favourite").equalsIgnoreCase("0")) {
                                Iv_heart.setVisibility(View.VISIBLE);
                                Iv_heart_red.setVisibility(View.GONE);
                            } else if (data.getString("is_favourite").equalsIgnoreCase("1")) {
                                Iv_heart_red.setVisibility(View.VISIBLE);
                                Iv_heart.setVisibility(View.GONE);
                            }

                            //   total_price = Float.parseFloat(data.getString("price"));


//                              food_id=data.getString("food_id");
                            Glide.with(FoodDetailActivity.this).
                                    load(data.getString("food_image"))
                                    .error(R.drawable.profileicon)
                                    .into(Iv_food_img);
//                            Glide.with(FoodDetailActivity.this).load(data.getString("food_image"))
//
//
//                                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE).dontAnimate().placeholder(R.drawable.restro).into(Iv_food_img);

                            txt_description.setText(data.getString("description"));
                            txt_ingredients.setText(data.getString("ingredients"));
                            Log.e(TAG, "data" + data.toString());


                            productModel.setProductPrice(data.getString("price"));

                            JSONArray toppingarray = data.getJSONArray("food_toppings");
                            origin_json_array = toppingarray;
                            Ingrediants_modal ingrediants_modal = new Ingrediants_modal();
                            ingrediants_modalArrayList = ingrediants_modal.parseIngrediants(toppingarray);

                            Log.e(TAG, "Api call size:." + original_ingrediants_modal_arrayList.size());
                            foodDetailAdapter = new FoodDetailAdapter(FoodDetailActivity.this, ingrediants_modalArrayList);
                            recyclerView.setAdapter(foodDetailAdapter);

                            //For List of List
                            List_of_list.clear();
                            List_of_list.add(ingrediants_modalArrayList);
                            clearListOfList = false;
                            reCreateListOfList = true;
                            //get cart items
                            get_CartData();

                        } else if (jsonObject1.getString("error_code").equalsIgnoreCase("delete_user")) {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            Intent intent = new Intent(FoodDetailActivity.this, LoginActivity.class);
                            startActivity(intent);

                        }

                        //    }


                    } catch (JSONException e) {
                        Log.e(TAG, "error:catch");
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void Error(String error) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                finish();
                Log.e(TAG, "error:");
//                UiHelper.showErrorMessage(mSnackView,error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
//                    UiHelper.showNetworkError(FoodDetailActivity.this,mSnackView);
                }
                Log.e(TAG, "isConnected : " + isConnected);
            }

        });

    }

    //Get Cart orders
    void get_CartData() {

//        if(clearListOfList)
//           List_of_list.clear();
//        else
//           clearListOfList = true;

        Log.e(TAG, "Gathering Cart item data.....");
        String cartdata = BaseApplication.getInstance().getSession().getCartItems();
        Log.e(TAG, "Gathered Cart item data:=>" + cartdata);


        Log.e(TAG, "Cart is  empty");


        if (!cartdata.isEmpty() && cartdata != null) {
            Log.e(TAG, "Cart is not empty");

            try {

                JSONObject cartItemsObject = new JSONObject(cartdata);
                //global for later use
                cartItems = new JSONObject(cartdata);

                snackbar_layout.setVisibility(View.VISIBLE);

                total_price = Float.parseFloat(cartItemsObject.getString("total_price"));
                total_items = cartItemsObject.getInt("total_items");
                if (total_items == 0) {
                    snackbar_layout.setVisibility(View.GONE);
                    add_layout.setVisibility(View.VISIBLE);
                    add_quantity_layout.setVisibility(View.GONE);
                }

                Log.e(TAG, "when getting data: " + total_items);
                tv_snackview_totalQuantity.setText(total_items + " " + getResources().getString(R.string.item));
                tv_total_price.setText("€" + String.valueOf(total_price));

                //product detail
                JSONArray all_items_array = cartItemsObject.getJSONArray("data");

                product_Object = null;
                for (int i = 0; i < all_items_array.length(); i++) {

                    Log.e(TAG, "get_CartData: " + id);

                    if (id.equalsIgnoreCase(all_items_array.getJSONObject(i).getString("id"))) {

                        //hold object for later use
                        product_Object = all_items_array.getJSONObject(i);
                        position = i;

                        JSONArray items_array = product_Object.getJSONArray("items");

                        if (items_array.length() > 0) {
                            Log.e("if", "get_CartData: " + items_array.length());
                            add_layout.setVisibility(View.GONE);
                            add_quantity_layout.setVisibility(View.VISIBLE);
                            txtTotalProductQuantity.setText(String.valueOf(items_array.length()));

                            //Recreate List of List
                            if (reCreateListOfList) {
                                Log.e(TAG, "get Cart Data Recreate List of List");
                                reCreateListOfList = false;
                                for (int j = 0; j < items_array.length(); j++) {
                                    JSONArray toppingsArray = items_array.getJSONArray(j);
                                    for (int x = 0; x < toppingsArray.length(); x++) {
                                        //List of List
                                        for (int y = 0; y < List_of_list.size(); y++) {

                                            for (int z = 0; z < List_of_list.get(y).size(); z++) {

                                                if (List_of_list.get(y).get(z).ingredients_id.equalsIgnoreCase(toppingsArray.getJSONObject(x).getString("id"))) {
                                                    List_of_list.get(y).get(z).isIngredient_add = true;
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }


                        break;
                    }
                }

                Log.e(TAG, "old Cart item data :=>" + cartdata.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //set Cart data
    void setCartData(boolean newAdded) {

        try {
            //if cart is empty
            if (cartItems == null) {
                Log.e(TAG, "Current cart is null *cartItems*");

                JSONObject cartItemObject = new JSONObject();
                cartItemObject.put("total_price", total_price);
                cartItemObject.put("total_items", total_items);

                // its first time make new product object

                JSONArray items_in_Cart_Array = new JSONArray();

                //product
                JSONObject product_JsonObject = new JSONObject();

                product_JsonObject.put("id", id);
                product_JsonObject.put("product_name", productModel.getfoodName());
                product_JsonObject.put("price", productModel.getProductPrice());
                product_JsonObject.put("product_discount", productModel.getDiscount());

                product_JsonObject.put("restro_image", restro_image);
                product_JsonObject.put("restro_name", restro_name);
                product_JsonObject.put("restro_location", restro_location);
                product_JsonObject.put("restro_status", restro_status);
                product_JsonObject.put("restro_id", restro_id);


                Log.e(TAG, "setCartData: " + product_JsonObject.toString());

                JSONArray items_Array = new JSONArray();
                JSONArray toppings_Array = new JSONArray();

                Log.e(TAG, "List of List" + List_of_list.size());

                for (int i = 0; i < List_of_list.size(); i++) {

                    for (int j = 0; j < List_of_list.get(i).size(); j++) {

                        if (List_of_list.get(i).get(j).isIngredient_add) {
                            JSONObject toppings_Object = new JSONObject();
                            toppings_Object.put("id", List_of_list.get(i).get(j).getingreid());
                            toppings_Object.put("price", List_of_list.get(i).get(j).getIngredientPrice());
                            toppings_Object.put("name", List_of_list.get(i).get(j).getToppingname());
                            // adding toppings in toppings array
                            toppings_Array.put(toppings_Object);
                        }

                    }
                    //
                    items_Array.put(toppings_Array);
                    product_JsonObject.put("items", items_Array);

                }

                //product into cart item list
                items_in_Cart_Array.put(product_JsonObject);

                //items in cart json
                cartItemObject.put("data", items_in_Cart_Array);


                Log.e(TAG, "item In cart json :" + cartItemObject.toString());

                //initialize variables
                cartItems = cartItemObject;
                product_Object = product_JsonObject;
                position = 0;


            }
            //if item in cart already exist
            else {
                Log.e(TAG, "Current cart is not null *cartItems*");
                //update total price and total items


                cartItems.put("total_price", total_price);
                cartItems.put("total_items", total_items);


                //new Item is added in cart
                if (newAdded) {


                    JSONArray items_in_Cart_Array = new JSONArray();
                    items_in_Cart_Array = cartItems.getJSONArray("data");


                    //Building product data
                    JSONObject product_ObjectJson = new JSONObject();
                    // product is added first time
                    if (product_Object == null) {

                        Log.e(TAG, "Adding Completely new Product");

                        product_ObjectJson.put("id", id);
                        product_ObjectJson.put("product_name", productModel.getfoodName());
                        product_ObjectJson.put("price", productModel.getProductPrice());
                        product_ObjectJson.put("product_discount", productModel.getDiscount());

                        product_ObjectJson.put("restro_image", restro_image);
                        product_ObjectJson.put("restro_name", restro_name);
                        product_ObjectJson.put("restro_location", restro_location);
                        product_ObjectJson.put("restro_status", restro_status);
                        product_ObjectJson.put("restro_id", restro_id);

                        JSONArray items_Array = new JSONArray();

                        //gather  toppings data
                        JSONArray toppings_Array = new JSONArray();
                        int pos = List_of_list.size() - 1;
                        for (int j = 0; j < List_of_list.get(pos).size(); j++) {

                            if (List_of_list.get(pos).get(j).isIngredient_add) {
                                JSONObject toppings_Object = new JSONObject();
                                toppings_Object.put("id", List_of_list.get(pos).get(j).getingreid());
                                toppings_Object.put("price", List_of_list.get(pos).get(j).getIngredientPrice());
                                toppings_Object.put("name", List_of_list.get(pos).get(j).getToppingname());
                                // adding toppings in toppings array
                                toppings_Array.put(toppings_Object);
                            }
                        }
                        //add toppings
                        items_Array.put(toppings_Array);
                        //add items in product
                        product_ObjectJson.put("items", items_Array);

                        // completely new product is added
                        items_in_Cart_Array.put(product_ObjectJson);
                        //update product object
                        product_Object = product_ObjectJson;
                        position = items_in_Cart_Array.length() - 1;

                    }

                    //Product already added just update items array
                    else {

                        Log.e(TAG, "Adding copy of same product");


                        product_ObjectJson = product_Object;

                        JSONArray items_Array = product_ObjectJson.getJSONArray("items");
                        //gather  toppings data
                        JSONArray toppings_Array = new JSONArray();

                        int pos = List_of_list.size() - 1;
                        for (int j = 0; j < List_of_list.get(pos).size(); j++) {

                            if (List_of_list.get(pos).get(j).isIngredient_add) {
                                JSONObject toppings_Object = new JSONObject();
                                toppings_Object.put("id", List_of_list.get(pos).get(j).getingreid());
                                toppings_Object.put("price", List_of_list.get(pos).get(j).getIngredientPrice());
                                toppings_Object.put("name", List_of_list.get(pos).get(j).getToppingname());
                                // adding toppings in toppings array
                                toppings_Array.put(toppings_Object);
                            }
                        }
                        //add toppings
                        items_Array.put(toppings_Array);
                        //add items in product
                        product_ObjectJson.put("items", items_Array);

                        //update already added product in item_in_cart_array
                        items_in_Cart_Array.put(position, product_ObjectJson);
                        //update product object
                        product_Object = product_ObjectJson;
                    }

                    // update items_in_cart_array in cart_item jsonObject
                    cartItems.put("data", items_in_Cart_Array);
                    //save update cartItem json
                    Log.e(TAG, "item added new cart:=>" + cartItems.toString());


                }
                // item is removed from cart
                else {


                    Log.e(TAG, "removing product from cart");

                    JSONArray items_in_Cart_Array = new JSONArray();
                    items_in_Cart_Array = cartItems.getJSONArray("data");

                    JSONObject product_ObjectJson = new JSONObject();
                    product_ObjectJson = product_Object;


                    JSONArray items_array = product_ObjectJson.getJSONArray("items");
                    JSONArray update_items_array = new JSONArray();

                    if (items_array.length() > 1) {
                        Log.e(TAG, "removing copy of the product");
                        //copy all item from items_array into new update_items_array, leaving last item
                        for (int i = 0; i < items_array.length() - 1; i++) {
                            update_items_array.put(items_array.get(i));
                        }

                        //product updated
                        product_ObjectJson.put("items", update_items_array);
                        // update product in items_in_cart_array
                        items_in_Cart_Array.put(position, product_ObjectJson);
                        //update product object
                        product_Object = product_ObjectJson;
                        //update cart_item json
                        cartItems.put("data", items_in_Cart_Array);

                    }
                    // current product only have one copy so remove complete product
                    else {
                        Log.e(TAG, "removing complete product");
                        JSONArray update_items_in_cart_array = new JSONArray();
                        for (int i = 0; i < items_in_Cart_Array.length(); i++) {
                            if (i != position)
                                update_items_in_cart_array.put(items_in_Cart_Array.get(i));
                        }
                        //update cart_item Json
                        cartItems.put("data", update_items_in_cart_array);
                        //update product object
                        product_Object = null;
                    }

                    Log.e(TAG, "item removed new cart:=>" + cartItems.toString());

                }

            }
//            Log.e(TAG, "item arry length: " +product_Object.getJSONArray("items").length());
            //   Log.e(TAG, "cart item length: "+cartItems.getString("total_items") );
            //update ui
            if (product_Object != null)
                txtTotalProductQuantity.setText(String.valueOf(product_Object.getJSONArray("items").length()));

            else
                txtTotalProductQuantity.setText("0");
            tv_total_price.setText("€" + cartItems.getString("total_price"));
            tv_snackview_totalQuantity.setText(cartItems.getString("total_items") + " " + getResources().getString(R.string.item));
//            txtTotalProductQuantity.setText(cartItems.getString("total_items"));
            // save cart data
            Log.e(TAG, "setCartData List size : " + cartItems.toString());
            BaseApplication.getInstance().getSession().setCartItmes(cartItems.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 225) {


                clearListOfList = false;
                Log.e(TAG, "onActivityResult: checking ");

                Log.e(TAG, "onActivityResult 225: " + data.getParcelableArrayListExtra("data").toString());
                ArrayList<Ingrediants_modal> newToppingsList = (ArrayList<Ingrediants_modal>) data.getSerializableExtra("data");
                String product_id = data.getStringExtra("product_id");
                String customize = data.getStringExtra("customize");
                int pos = data.hasExtra("pos") ? data.getIntExtra("pos", -1) : -1;

                //    addProduct(newToppingsList, product_id, customize, pos);
                add_layout.setVisibility(View.GONE);
                add_quantity_layout.setVisibility(View.VISIBLE);
                snackbar_layout.setVisibility(View.VISIBLE);
                toppings_price = 0;
                total_price = total_price + Float.parseFloat(productModel.getProductPrice());

                Log.e(TAG, "onActivityResult: " + List_of_list.size());
                //  List_of_list.add(parse_into_new_list(newToppingsList));

                for (int i = 0; i < newToppingsList.size(); i++) {
                    if (newToppingsList.get(i).isIngredient_add)
                        toppings_price = toppings_price + Float.parseFloat(newToppingsList.get(i).getIngredientPrice());
                }


                total_price = total_price + toppings_price;
                Log.e(TAG, "when clicking add total items: " + total_items);
                total_items++;

                tv_total_price.setText("€" + String.valueOf(total_price));

                //add to List of List
                List_of_list.clear();
                List_of_list.add(new ArrayList<>(newToppingsList));

                set_ingridiants_Adapter();

                tv_snackview_totalQuantity.setText(String.valueOf(total_items) + " " + getString(R.string.item));

                //Update cart data
                setCartData(true);

                Log.e(TAG, "onResult List of list sixe:" + List_of_list.size());
            }
        }
    }

}//class

