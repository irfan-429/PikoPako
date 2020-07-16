package com.pikopako.Fragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.JsonObject;
import com.pikopako.Activity.Apply_Coupon;
import com.pikopako.Activity.Change_Address_Activity;
import com.pikopako.Activity.CheckoutActivity;
import com.pikopako.Activity.LoginActivity;
import com.pikopako.Activity.Make_payment;
import com.pikopako.Activity.SplashActivity;
import com.pikopako.Adapter.ViewCartAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.CustomTimePickerDialog;
import com.pikopako.AppUtill.GPSTracker;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Model.Ingrediants_modal;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class CartFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.recyclevieww)
    RecyclerView mRecycleview;

    @BindView(R.id.hh)
    RelativeLayout hh;

    @BindView(R.id.scroolView)
    RelativeLayout mSnackView;
//    @BindView(R.id.logout_lyt)
//    LinearLayout logout_lyt;

    @BindView(R.id.login_lyt)
    LinearLayout login_lyt;

    @BindView(R.id.layout_applyCoupon)
    LinearLayout layout_applyCoupon;

    @BindView(R.id.btn_proceed)
    Button btn_proceed;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.Txt_coupondetails)
    CustomTextViewBold Txt_coupondetails;

    @BindView(R.id.txt_changeaddress)
    CustomTextViewBold txt_change_address;

    @BindView(R.id.txt_deleivery_address)
    CustomTextViewNormal txt_deleivery_address;

    @BindView(R.id.img_coupn)
    ImageView img_coupn;

    @BindView(R.id.img_coupnclose)
    ImageView img_coupnclose;

    @BindView(R.id.img_restaurant)
    ImageView img_restaurant;

    @BindView(R.id.txt_restroname)
    CustomTextViewBold txt_restroname;

    @BindView(R.id.txt_deleiveryCharges)
    CustomTextViewNormal txt_deleiveryCharges;

    @BindView(R.id.Iv_applyCoupon)
    ImageView Iv_applyCoupon;

    @BindView(R.id.txt_itemTotal)
    CustomTextViewNormal txt_itemTotal;

    @BindView(R.id.txt_toPay)
    CustomTextViewBold txt_toPay;

    @BindView(R.id.txt_restro_discount)
    CustomTextViewNormal txt_restro_discount;

    @BindView(R.id.txt_discount)
    CustomTextViewNormal txt_discount;

    @BindView(R.id.txt_address)
    CustomTextViewNormal txt_address;

    @BindView(R.id.txt_status)
    CustomTextViewBold txt_status;

    @BindView(R.id.txt_discountkey)
    CustomTextViewNormal txt_discountkey;

    @BindView(R.id.txt_cartempty)
    CustomTextViewBold txt_cartempty;

    @BindView(R.id.showcart)
    ScrollView showcart;

    @BindView(R.id.edt_remarks)
    CustomEditTextBold edt_remarks;

    @BindView(R.id.ed_timepicker)
    CustomEditTextBold ed_timepicker;

    ViewCartAdapter productListAdapter;
    ProgressDialog progressDialog;
    private GoogleMap googleMap;

    JSONObject cart;
    JSONArray simlified_products_array = new JSONArray();
    Context context;

    //    float discount=0;
//    String savedCouponCode;
    float couponDiscount = 0;
    String couponCode = "";

    float product_quantity, product_price, product_discount;

    String address, restaurant_id, coordinate_id;


    String restro_image = "";
    String restro_name = "";
    String restro_location = "";
    String restro_status = "";

    String houseno = "";
    String landmark = "";
    String location = "";
    String address_title = "";

    double latitude, longitude;
    float deleivery_charge, minimum_order_amount, promo_code_id;
    String isPromoApplied = "No";
    float itemTotal, toPay;

    String TAG = "CartFragment";
    String language = "";

    ArrayList<String> permissionToAsk = new ArrayList<>();
    long click_time = 0;
    long delay = 50;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_view_cart, container, false);
        ButterKnife.bind(this, view);
        listners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restro_image = BaseApplication.getInstance().getSession().getRestroImage();
        restro_location = BaseApplication.getInstance().getSession().getRestroLocation();
        restro_name = BaseApplication.getInstance().getSession().getRestroName();
        restro_status = BaseApplication.getInstance().getSession().getRestroStatus();
        restaurant_id = BaseApplication.getInstance().getSession().getRestroId();
        coordinate_id = BaseApplication.getInstance().getSession().getCoordinateId();
        Log.e(TAG, "onCreateView: restro id " + restaurant_id);

        mRecycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleview.setNestedScrollingEnabled(false);
        mRecycleview.setFocusable(false);
        login_lyt.setVisibility(View.VISIBLE);
        btn_proceed.setText(getString(R.string.make_payment));

        if (BaseApplication.getInstance().getSession().getAddress() != null && !BaseApplication.getInstance().getSession().getAddress().equalsIgnoreCase("")) {
            try {
                JSONObject dd = new JSONObject(BaseApplication.getInstance().getSession().getAddress());
                deleivery_charge = (float) dd.getDouble("deleivery_charge");
                txt_deleiveryCharges.setText("€" + String.format(Locale.ENGLISH, "%.2f", deleivery_charge));
                location = dd.getString("location");
                houseno = dd.getString("houseno");
                landmark = dd.getString("landmark");
                address_title = dd.getString("address_title");
                latitude = dd.getDouble("latitude");
                longitude = dd.getDouble("longitude");
                txt_deleivery_address.setText(houseno + " " + landmark + " " + location);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        buildSimplified_JSONArray();
        load_Adapter();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_filter);
        menu.clear();
    }

    private void buildSimplified_JSONArray() {
        Log.e("cart item session", "buildSimplified_JSONArray: " + BaseApplication.getInstance().getSession().getCartItems().toString());
        try {
            if (!BaseApplication.getInstance().getSession().getCartItems().isEmpty()) {
                cart = new JSONObject(BaseApplication.getInstance().getSession().getCartItems());
                //simplified Cart data
                JSONObject simplified_object = new JSONObject();
                simlified_products_array = new JSONArray();

                JSONArray cart_data_array = cart.getJSONArray("data");
                restro_image = cart_data_array.getJSONObject(0).getString("restro_image");
                restro_name = cart_data_array.getJSONObject(0).getString("restro_name");
                restro_location = cart_data_array.getJSONObject(0).getString("restro_location");
                restro_status = cart_data_array.getJSONObject(0).getString("restro_status");
//
                txt_restroname.setText(restro_name);
                txt_address.setText(restro_location);
                txt_status.setText(restro_status);

                Glide.with(getActivity())
                        .load(restro_image)
                        .error(R.drawable.profileicon)
                        .into(img_restaurant);

                //getData();

                //Go Through all individual products in cart
                for (int i = 0; i < cart_data_array.length(); i++) {

                    JSONObject cart_product_object = cart_data_array.getJSONObject(i);

                    //product object
                    JSONObject simplified_product_object = new JSONObject();
                    simplified_product_object.put("product_Name", cart_product_object.getString("product_name"));
                    simplified_product_object.put("product_price", cart_product_object.getString("price"));
                    simplified_product_object.put("product_quantity", 1);

                    if (cart_product_object.getString("product_discount") == null || cart_product_object.getString("product_discount").equalsIgnoreCase("null"))
                        simplified_product_object.put("product_discount", 0);
                    else
                        simplified_product_object.put("product_discount", cart_product_object.getString("product_discount"));
                    simplified_product_object.put("product_id", cart_product_object.getString("id"));
                    simplified_product_object.put("toppings", new JSONArray());

                    //Finding matching toppings
                    JSONArray cart_product_copys_array = cart_product_object.getJSONArray("items");

                    boolean macthed = false;

                    //new Skip array for new product
                    List<Integer> skip_array = new ArrayList<>();
                    //Go through all the items (product copy) to find with matching toppings
                    Log.e(TAG, "for i:" + i);
                    boolean skip = false;

                    //through all array
                    for (int j = 0; j < cart_product_copys_array.length(); j++) {
                        //skip matched positions
                        Log.e(TAG, "skip array for :" + i + " " + skip_array.toString());
                        skip = false;
                        for (int a = 0; a < skip_array.size(); a++) {
                            if (skip_array.get(a) == j) {
                                Log.e(TAG, "skip matched at:" + j + "==" + skip_array.get(a));
                                skip = true;
                                break;
                            }
                        }//skip check loop
                        if (skip) {
                            Log.e(TAG, "skip at :" + j);
                            continue;
                        }

                        Log.e(TAG, "for J:" + j);


                        JSONArray cart_toppings_array = new JSONArray(cart_product_copys_array.getJSONArray(j).toString());
                        simplified_product_object.put("toppings", cart_toppings_array);


                        if (cart_product_copys_array.length() > 1) {

                            for (int x = j + 1; x < cart_product_copys_array.length(); x++) {
                                macthed = false;
                                JSONArray nxt_cart_toppings_array = new JSONArray();
                                if (x < cart_product_copys_array.length())
                                    nxt_cart_toppings_array = cart_product_copys_array.getJSONArray(x);


                                if (cart_toppings_array.length() == nxt_cart_toppings_array.length()) {
                                    for (int k = 0; k < cart_toppings_array.length(); k++) {

                                        if (cart_toppings_array.getJSONObject(k).getInt("id") == nxt_cart_toppings_array.getJSONObject(k).getInt("id")) {
                                            macthed = true;
                                            //add matched position in skip-array
                                            skip_array.add(x);
                                        } else
                                            macthed = false;
                                    }

                                    if (cart_toppings_array.length() == 0) {
                                        macthed = true;
                                        skip_array.add(x);
                                    }

                                    if (macthed)
                                        simplified_product_object.put("product_quantity", simplified_product_object.getInt("product_quantity") + 1);


                                } else {

                                }

                            }//next loop
                            //
                            simlified_products_array.put(new JSONObject(simplified_product_object.toString()));
                            simplified_product_object.put("product_quantity", 1);

                        }//if
                        else {
                            simlified_products_array.put(simplified_product_object);
                            simplified_product_object.put("product_quantity", 1);
                        }

                    }//array edratore
                    //add

                }

                toPay = 0;
                itemTotal = 0;
                float restroDiscount = 0;
                float toppings_price = 0;
                float total_dis = 0;

                try {
                    for (int i = 0; i < simlified_products_array.length(); i++) {
                        product_price = Float.valueOf(simlified_products_array.getJSONObject(i).getString("product_price"));
                        product_quantity = Float.valueOf(simlified_products_array.getJSONObject(i).getString("product_quantity"));
                        product_discount = Float.valueOf(simlified_products_array.getJSONObject(i).getString("product_discount"));

                        restroDiscount = restroDiscount + ((product_discount * product_price) / 100) * product_quantity;
                        Log.e(TAG, "restroDiscount" + restroDiscount);
                        itemTotal = (product_price * product_quantity) + itemTotal;
//                           totaldiscounttt=itemTotal * (product_discount/100);
//                           toPay=itemTotal-restroDiscountounttt;


                        JSONArray itemsArray = simlified_products_array.getJSONObject(i).getJSONArray("toppings");
                        float topin = 0;
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject jsonObject = itemsArray.getJSONObject(j);
                            topin = topin + Float.parseFloat(jsonObject.getString("price"));
                        }

                        toppings_price = toppings_price + (topin * product_quantity);

                    }
                    Log.e(TAG, "toppings_price" + toppings_price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Do some math & update ui


                //check cart for coupon discount
                if (cart.has("couponData")) {
                    couponCode = cart.getJSONObject("couponData").getString("couponCode");
                    couponDiscount = (float) cart.getJSONObject("couponData").getDouble("couponDiscount");
                    //update ui
                    if (couponDiscount == 0) {
                        isPromoApplied = getResources().getString(R.string.no);
                        img_coupn.setVisibility(View.VISIBLE);
                        img_coupnclose.setVisibility(View.GONE);
                        Iv_applyCoupon.setVisibility(View.VISIBLE);

                    } else {
                        isPromoApplied = getResources().getString(R.string.yes);
                        Txt_coupondetails.setText(getString(R.string.your_coupon) + " " + couponCode + " " + getString(R.string.is_appiled_by) + " " + couponDiscount);
                        img_coupn.setVisibility(View.GONE);
                        img_coupnclose.setVisibility(View.VISIBLE);
                        Iv_applyCoupon.setVisibility(View.GONE);
                    }
                }

                Log.e(TAG, "Coupon Discount:" + couponDiscount);

                //Check for delivery address
                if (cart.has("delivery_location")) {
                    JSONObject deliveryData = cart.getJSONObject("delivery_location");

                    location = deliveryData.getString("location");
                    houseno = deliveryData.getString("houseno");
                    landmark = deliveryData.getString("landmark");
                    longitude = deliveryData.getDouble("longitude");
                    latitude = deliveryData.getDouble("latitude");
                    address_title = deliveryData.getString("address_title");
                    deleivery_charge = (float) deliveryData.getDouble("deleivery_charge");

//                    txt_deleivery_address.setText(location);
                    txt_deleivery_address.setText(houseno+ " "+ landmark+" "+ location);
                    txt_deleiveryCharges.setText("€" + String.format(Locale.ENGLISH, "%.2f", deleivery_charge));

                }

                Log.e(TAG, "delivery charges:" + deleivery_charge);

                toPay = (itemTotal + toppings_price + deleivery_charge) - (restroDiscount + couponDiscount);

                Log.e(TAG, "toPay" + toPay);
                txt_restro_discount.setText("€" + String.format(Locale.ENGLISH, "%.2f", restroDiscount));
                txt_discount.setText("€" + String.format(Locale.ENGLISH, "%.2f", restroDiscount + couponDiscount));
                txt_itemTotal.setText("€" + String.format(Locale.ENGLISH, "%.2f", itemTotal + toppings_price));
                txt_toPay.setText("€" + String.format(Locale.ENGLISH, "%.2f", toPay));

                Log.e("TAG", "to pay " + txt_toPay.getText());

                txt_discountkey.setText(getString(R.string.you_have_saved) + " " + "€" + String.format(Locale.ENGLISH, "%.2f", restroDiscount + couponDiscount) + " " + getResources().getString(R.string.on_this_bill));

                //

                Log.e(TAG, "Simplified:==>" + simlified_products_array.toString());

                Log.e(TAG, "cart:" + cart.toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        //update Simplified data
        BaseApplication.getInstance().getSession().setSimplifiedCartData(simlified_products_array.toString());

        //
        if (productListAdapter != null) {
            productListAdapter.updateArrayList(simlified_products_array);
        }

        if (itemTotal == 0) {
            txt_cartempty.setVisibility(View.VISIBLE);
            showcart.setVisibility(View.GONE);
        }
    }


    private void getData() {
        progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("restaurant_id", BaseApplication.getInstance().getSession().getRestroId());
        jsonObject.addProperty("coordinate_id", BaseApplication.getInstance().getSession().getCoordinateId());

        jsonObject.addProperty("timezone", tz.getID());
        jsonObject.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
        jsonObject.addProperty("language", language);
        Log.e("tag", "json object restro id detailitemfrag:- " + jsonObject.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantServices(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        Log.e("restro detailview cart", jsonObject1.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            //   restro_status=jsonObject1.getJSONObject("data").getString("restaurant_status");
                            //     restro_image=jsonObject1.getJSONObject("data").getString("restaurant_logo");
                            //          restro_name=jsonObject1.getJSONObject("data").getString("restaurant_name");
                            //  restro_location=jsonObject1.getJSONObject("data").getString("address");

                            txt_restroname.setText(jsonObject1.getJSONObject("data").getString("restaurant_name"));
                            txt_address.setText(jsonObject1.getJSONObject("data").getString("address"));
                            txt_status.setText(jsonObject1.getJSONObject("data").getString("restaurant_status"));

                            Glide.with(getActivity()).
                                    load(jsonObject1.getJSONObject("data").getString("restaurant_logo"))
                                    .error(R.drawable.profileicon)
                                    .into(img_restaurant);

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
                if (progressDialog != null)
                    progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(getActivity(), mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    private void load_Adapter() {
        productListAdapter = new ViewCartAdapter(simlified_products_array, this, getActivity());
        mRecycleview.setAdapter(productListAdapter);
        productListAdapter.notifyDataSetChanged();
    }

    public void updateText(float Total_price, float productDiscount) {
        Log.e(TAG, "updateText: total_price " + Total_price);

        String fff = txt_discount.getText().toString();
        String[] diss = fff.split("\\€");
        Log.e(TAG, "diss length: " + diss[1]);
        float remaningDis = Float.parseFloat(diss[1]) - productDiscount;

        txt_itemTotal.setText("€" + String.format(Locale.ENGLISH, "%.2f", Total_price));
        txt_discount.setText("€" + String.format(Locale.ENGLISH, "%.2f", remaningDis));
        txt_restro_discount.setText("€" + String.format(Locale.ENGLISH, "%.2f", remaningDis - couponDiscount));
        Log.e(TAG, "product disCount : " + productDiscount);

        txt_toPay.setText("€" + String.format(Locale.ENGLISH, "%.2f", Total_price + deleivery_charge - remaningDis));
        Log.e("TAG", "to pay " + txt_toPay.getText());
        txt_discountkey.setText(getString(R.string.you_have_saved) + " " + "€" + String.format(Locale.ENGLISH, "%.2f", remaningDis) + " " + getResources().getString(R.string.on_this_bill));

        if (Total_price == 0) {
            showcart.setVisibility(View.GONE);
            txt_cartempty.setVisibility(View.VISIBLE);
        }
    }

    private void listners() {


        txt_change_address.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
        layout_applyCoupon.setOnClickListener(this);
        img_coupnclose.setOnClickListener(this);
        ed_timepicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_changeaddress:


                if (BaseApplication.getInstance().getSession().getExit().equalsIgnoreCase("Exit")) {
                    Intent intent = new Intent(getActivity(), Change_Address_Activity.class);
                    try {
                        intent.putExtra("restaurant_id", cart.getJSONArray("data").getJSONObject(0).getString("restro_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("houseno", houseno);
                    intent.putExtra("landmark", landmark);
                    intent.putExtra("title", address_title);
                    intent.putExtra("location", location);
                    Log.e(TAG, "onClick: location" + location);
                    startActivityForResult(intent, 4);
                } else askPermissions();

                try {
                    Log.e("view cart", "onClick: " + cart.getJSONArray("data").getJSONObject(0).getString("restro_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.layout_applyCoupon:
                if (!BaseApplication.getInstance().getSession().isLoggedIn()) {
                    UiHelper.showToast(getActivity(), getString(R.string.pls_login_first_to_apply_copon));
                } else if (Txt_coupondetails.getText().toString().equalsIgnoreCase(getResources().getString(R.string.apply_coupon))) {
                    Intent nnt = new Intent(getActivity(), Apply_Coupon.class);
                    nnt.putExtra("price", txt_itemTotal.getText().toString());
                    startActivityForResult(nnt, 2);
                }
                break;

            case R.id.btn_proceed:

                DateFormat formatter1 = new SimpleDateFormat("dd");

                String datee1 = formatter1.format(new Date(String.valueOf(Calendar.getInstance().getTime())));
                int month1 = Calendar.getInstance().get(Calendar.MONTH);
                String month_name1 = "";
                if (month1 == 0)
                    month_name1 = "Jan";
                else if (month1 == 1)
                    month_name1 = "Feb";
                else if (month1 == 2)
                    month_name1 = "Mar";
                else if (month1 == 3)
                    month_name1 = "Apr";
                else if (month1 == 4)
                    month_name1 = "May";
                else if (month1 == 5)
                    month_name1 = "Jun";
                else if (month1 == 6)
                    month_name1 = "Jul";
                else if (month1 == 7)
                    month_name1 = "Aug";
                else if (month1 == 8)
                    month_name1 = "Sep";
                else if (month1 == 9)
                    month_name1 = "Oct";
                else if (month1 == 10)
                    month_name1 = "Nov";
                else if (month1 == 11)
                    month_name1 = "Dec";


                DateFormat yearformatter1 = new SimpleDateFormat("yyyy");

                String year1 = yearformatter1.format(new Date(String.valueOf(Calendar.getInstance().getTime())));


                String device_date1 = datee1 + " " + month_name1 + " " + year1;
                Log.e(TAG, "Device Date: " + device_date1);

//                String gg1= "";
//                try {
//                    gg1 = InternetTime.substring(5,16);
//                } catch (IndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }
//                Log.e(TAG, "Internet time: "+gg1 );

                //INTERNET DATE
//                if (device_date1.equalsIgnoreCase(BaseApplication.getInstance().getSession().getInternetTime())) {
                    String fff = txt_itemTotal.getText().toString();
                    String[] diss = fff.split("\\€");

                    float t = Float.parseFloat(diss[1]);
                    //     if (btn_proceed.getText() == "MAKE PAYMENT") {

                    Log.e(TAG, "onClick: " + txt_deleivery_address.getText().toString());
                    if (txt_deleivery_address.getText().toString().trim().isEmpty() || txt_deleivery_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.address_is_not_set))) {
                        UiHelper.showToast(getActivity(), getString(R.string.pls_enter_shipping_address));
                    } else if (t < minimum_order_amount) {
                        UiHelper.showToast(getActivity(), getString(R.string.your_order_amount_is_less));
                    } else if (!BaseApplication.getInstance().getSession().isLoggedIn()) {
                        Intent intent1 = new Intent(getActivity(), CheckoutActivity.class);

                        String fffs = txt_discount.getText().toString();
                        Log.e(TAG, "onClick TextTOPAY: " + txt_toPay.getText());
                        Log.e(TAG, "onClick TextTODiscount: " + txt_discount.getText());
                        String toppaayy = txt_toPay.getText().toString();
                        String[] disss = fffs.split("€");
                        String[] sss = toppaayy.split("€");

                        Log.e(TAG, "onClick: " + Float.parseFloat(sss[1]));
                        //  try{
                        intent1.putExtra("paid_amount", Float.parseFloat(sss[1]));
                        intent1.putExtra("discount_amount", Float.parseFloat(disss[1]));


                        intent1.putExtra("delivery_charge", deleivery_charge);
                        intent1.putExtra("is_promo_code_applied", isPromoApplied);
                        intent1.putExtra("house_number", houseno);
                        intent1.putExtra("landmark", landmark);
                        intent1.putExtra("address", location);
                        intent1.putExtra("latitude", latitude);
                        intent1.putExtra("longitude", longitude);
                        intent1.putExtra("restaurant_id", restaurant_id);
                        intent1.putExtra("address_title", address_title);
                        intent1.putExtra("coordinate_id", coordinate_id);
                        intent1.putExtra("promo_code_id", promo_code_id);
                        intent1.putExtra("promo_code_price", couponDiscount);
                        intent1.putExtra("restro_image", restro_image);
                        intent1.putExtra("restro_location", restro_location);
                        intent1.putExtra("restro_name", restro_name);
                        intent1.putExtra("restro_status", restro_status);
                        intent1.putExtra("remarks", edt_remarks.getText().toString());
                        intent1.putExtra("delivery_time", ed_timepicker.getText().toString());
                        intent1.putExtra("cartfragment", false);
//                    }catch (ArrayIndexOutOfBoundsException e){
//                        e.printStackTrace();
//                    }


                        startActivity(intent1);
                    } else {

                        Intent intent1 = new Intent(getActivity(), Make_payment.class);


                        String fffs = txt_discount.getText().toString();
                        String toppaayy = txt_toPay.getText().toString();
                        String[] disss = fffs.split("\\€");
                        String[] sss = toppaayy.split("\\€");


                        intent1.putExtra("paid_amount", Float.parseFloat(sss[1]));
                        intent1.putExtra("discount_amount", Float.parseFloat(disss[1]));
                        intent1.putExtra("delivery_charge", deleivery_charge);
                        intent1.putExtra("is_promo_code_applied", isPromoApplied);
                        intent1.putExtra("house_number", houseno);
                        intent1.putExtra("landmark", landmark);
                        intent1.putExtra("address", location);
                        intent1.putExtra("latitude", latitude);
                        intent1.putExtra("longitude", longitude);
                        intent1.putExtra("restaurant_id", restaurant_id);
                        intent1.putExtra("address_title", address_title);
                        intent1.putExtra("coordinate_id", coordinate_id);
                        intent1.putExtra("promo_code_id", promo_code_id);
                        intent1.putExtra("promo_code_price", couponDiscount);

                        intent1.putExtra("restro_image", restro_image);
                        intent1.putExtra("restro_location", restro_location);
                        intent1.putExtra("restro_name", restro_name);
                        intent1.putExtra("restro_status", restro_status);
                        intent1.putExtra("remarks", edt_remarks.getText().toString());
                        intent1.putExtra("delivery_time", ed_timepicker.getText().toString());
                        Log.e(TAG, "onClick: " + address + latitude + longitude);

                        startActivity(intent1);
                    }
//                } else UiHelper.showToast(getActivity(), getResources().getString(R.string.Please_check_your_time));

                break;
            //  }
            case R.id.img_coupnclose:
                Txt_coupondetails.setText(R.string.apply_coupon);
                img_coupnclose.setVisibility(View.GONE);
                img_coupn.setVisibility(View.VISIBLE);
                Iv_applyCoupon.setVisibility(View.VISIBLE);
//                BaseApplication.getInstance().getSession().setCoupon(0,"");
                try {
                    cart.getJSONObject("couponData").put("couponDiscount", 0);
                    BaseApplication.getInstance().getSession().setCartItmes(cart.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String fffss = txt_discount.getText().toString();
                String toppaayys = txt_toPay.getText().toString();
                String[] disss = fffss.split("\\€");
                String[] sss = toppaayys.split("\\€");

                float dis = Float.parseFloat(disss[1]);
                float toPAy = Float.parseFloat(sss[1]);

                String ddd = "€" + String.format(Locale.ENGLISH, "%.2f", dis - couponDiscount);
                txt_discount.setText(ddd);

                //   txt_discount.setText("€"+String.format("%.2f",dis - couponDiscount));
                txt_toPay.setText("€" + String.format(Locale.ENGLISH, "%.2f", toPAy + couponDiscount));
                txt_discountkey.setText(getString(R.string.you_have_saved) + " " + "€" + String.format(Locale.ENGLISH, "%.2f", dis - couponDiscount) + " " + getResources().getString(R.string.on_this_bill));
                couponDiscount = 0;
                isPromoApplied = getString(R.string.no);
                break;

            case R.id.ed_timepicker:


                DateFormat formatter = new SimpleDateFormat("dd");

                String datee = formatter.format(new Date(String.valueOf(Calendar.getInstance().getTime())));
                int month = Calendar.getInstance().get(Calendar.MONTH);
                String month_name = "";
                if (month == 0)
                    month_name = "Jan";
                else if (month == 1)
                    month_name = "Feb";
                else if (month == 2)
                    month_name = "Mar";
                else if (month == 3)
                    month_name = "Apr";
                else if (month == 4)
                    month_name = "May";
                else if (month == 5)
                    month_name = "Jun";
                else if (month == 6)
                    month_name = "Jul";
                else if (month == 7)
                    month_name = "Aug";
                else if (month == 8)
                    month_name = "Sep";
                else if (month == 9)
                    month_name = "Oct";
                else if (month == 10)
                    month_name = "Nov";
                else if (month == 11)
                    month_name = "Dec";


                DateFormat yearformatter = new SimpleDateFormat("yyyy");

                String year = yearformatter.format(new Date(String.valueOf(Calendar.getInstance().getTime())));


                String device_date = datee + " " + month_name + " " + year;
                Log.e(TAG, "Device Date: " + device_date);
                Log.e(TAG, "Internet Time: " + SplashActivity.InternetDate);


//                String gg= "";
//                try {
//                    gg = InternetTime.substring(5,16);
//                } catch (IndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }
//                Log.e(TAG, "Internet time: "+gg );


                //INTERNET DATE
//                if (device_date.equalsIgnoreCase(BaseApplication.getInstance().getSession().getInternetTime())) {
                    Log.e(TAG, "equals ");
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new CustomTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            callAPiForTime(i, i1);
                        }
                    }, hour, minute, true);

                    mTimePicker.show();
//
//                mTimePicker = new TimePickerDialog(ViewCartActivity.this,android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
//                        callAPiForTime(selectedHour,selectedMinute);
//                      //  ed_timepicker.setText( selectedHour + ":" + selectedMinute);
//                    }
//                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();


//                } else UiHelper.showToast(getActivity(), getResources().getString(R.string.Please_check_your_time));

                break;
        }
    }

    private void callAPiForTime(int time, int mints) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String day = days[currentDay];
        final String sTime = time + ":" + String.valueOf(mints);

        Log.e(TAG, "callAPiForTime: " + day + "timee:" + sTime);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("time", sTime);
        jsonObject.addProperty("day", day);
        jsonObject.addProperty("restaurant_id", restaurant_id);
        //   jsonObject.addProperty("food_id", id);
        //  Log.e("food_id",""+id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().checkRestaurantStatus(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
//
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                            if (jsonObject2.getString("restaurant_status").equalsIgnoreCase("Closed")) {

                                UiHelper.showToast(getActivity(), "Restaurant don't Deleiver this time");
                            } else
                                ed_timepicker.setText(sTime);
                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: ");
        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 110) {
//                Bundle bundle = data.getExtras();
//                if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
//                    final String address = bundle.getString("text");
//                    latitude = Double.valueOf(bundle.getString("latitude"));
//                    longitude = Double.valueOf(bundle.getString("longitude"));
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    if (googleMap != null) {
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                    }
//                    txt_deleivery_address.setText(address);
//                }
//
//            }

            if (requestCode == 2) {
                Log.e(TAG, "onActivityResult: 2");

                Bundle bundle = data.getExtras();
                if (bundle.getString("id") != null && bundle.getString("couponCode") != null && bundle.getString("discount") != null) {

                    promo_code_id = Float.parseFloat(bundle.getString("id"));

                    couponCode = bundle.getString("couponCode");
                    couponDiscount = Float.parseFloat(bundle.getString("discount"));

                    Log.e(TAG, "onActivityResult: couponCode" + couponCode);

                    Txt_coupondetails.setText(getString(R.string.your_coupon) + " " + couponCode + " " + getString(R.string.is_appiled_by) + " " + "€" + couponDiscount);
//                  img_coupn.setImageDrawable(getResources().getDrawable(R.drawable.funnel));
                    img_coupn.setVisibility(View.GONE);
                    img_coupnclose.setVisibility(View.VISIBLE);
                    Iv_applyCoupon.setVisibility(View.GONE);

                    String fff = txt_discount.getText().toString();
                    String toppaayy = txt_toPay.getText().toString();
                    String[] diss = fff.split("\\€");
                    String[] sss = toppaayy.split("\\€");

                    float current_disc = Float.parseFloat(diss[1]);
                    float current_toPay = Float.parseFloat(sss[1]);

                    txt_toPay.setText("€" + String.format(Locale.ENGLISH, "%.2f", current_toPay - couponDiscount));
                    txt_discount.setText("€" + String.format(Locale.ENGLISH, "%.2f", current_disc + couponDiscount));
                    txt_discountkey.setText(getString(R.string.you_have_saved) + " " + "€" + String.format(Locale.ENGLISH, "%.2f", current_disc + couponDiscount) + " " + getResources().getString(R.string.on_this_bill));
                    isPromoApplied = getString(R.string.yes);
                    //Add Applied Coupon data to cart
                    JSONObject couponData = new JSONObject();
                    try {
                        couponData.put("couponCode", couponCode);
                        couponData.put("couponDiscount", couponDiscount);
                        cart.put("couponData", couponData);
                        BaseApplication.getInstance().getSession().setCartItmes(cart.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            if (requestCode == 4) {
                Log.e(TAG, "onActivityResult: 4:  " + data.getStringExtra("location"));
//                Bundle bundle = data.getExtras();
//                if (bundle.getString("location") != null && bundle.getString("houseno")!=null && bundle.getString("landmark")!=null)  {
                location = data.getStringExtra("location");
                houseno = data.getStringExtra("houseno");
                landmark = data.getStringExtra("landmark");
                longitude = data.getDoubleExtra("longitude", 0);
                latitude = data.getDoubleExtra("latitude", 0);
                address_title = data.getStringExtra("address_title");
                deleivery_charge = data.getFloatExtra("deleivery_charge", 0);
                minimum_order_amount = data.getFloatExtra("minimum_order_amount", 0);
//                txt_deleivery_address.setText(location);
                txt_deleivery_address.setText(houseno+ " "+ landmark+" "+ location);

                String fff = txt_toPay.getText().toString();
                String[] diss = fff.split("\\€");
                float current_toPay = Float.parseFloat(diss[1]);

                txt_deleiveryCharges.setText("€" + String.format(Locale.ENGLISH, "%.2f", deleivery_charge));

                try {

                    //Save Delivery location data on cart
                    JSONObject delivery_location_object = new JSONObject();
                    delivery_location_object.put("location", location);
                    delivery_location_object.put("houseno", houseno);
                    delivery_location_object.put("landmark", landmark);
                    delivery_location_object.put("longitude", longitude);
                    delivery_location_object.put("latitude", latitude);
                    delivery_location_object.put("address_title", address_title);
                    delivery_location_object.put("deleivery_charge", deleivery_charge);

                    cart.put("delivery_location", delivery_location_object);
//                    float temp_total = (float)cart.getDouble("total_price")+deleivery_charge;
//                    cart.put("total_price",temp_total);
                    BaseApplication.getInstance().getSession().setCartItmes(cart.toString());
                    BaseApplication.getInstance().getSession().setAddress(String.valueOf(delivery_location_object));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txt_toPay.setText("€" + String.valueOf(deleivery_charge + current_toPay));

                Log.e(TAG, "onActivityResult 4: " + location + "deleiver:-" + deleivery_charge + "houseno:-" + houseno + "landmarek:" + landmark + "longitude:" + longitude + "lati:-" + latitude + "addresstitl:-" + address_title);
            }


            if (requestCode == 201) {
                Log.e(TAG, "onActivityResult 201: " + data.getParcelableArrayListExtra("data").toString());
                ArrayList<Ingrediants_modal> newToppingsList = (ArrayList<Ingrediants_modal>) data.getSerializableExtra("data");
                String product_id = data.getStringExtra("product_id");
                String customize = data.getStringExtra("customize");
                int pos = data.hasExtra("pos") ? data.getIntExtra("pos", -1) : -1;
                addProduct(newToppingsList, product_id, customize, pos);
            }
        }
    }

    private void addProduct(ArrayList<Ingrediants_modal> newToppingsList, String product_id, String customize, int pos) {
        //add new Product with different toppings /*CART data*/

        try {
            //Add product to saved cart data
            Log.e(TAG, "addProduct total_price:  " + cart.getString("total_price"));
            String cartJsonString = BaseApplication.getInstance().getSession().getCartItems();
            cart = new JSONObject(cartJsonString);
            JSONArray cart_data_array = cart.getJSONArray("data");

            //find product to be added
            for (int i = 0; i < cart_data_array.length(); i++) {

                JSONObject cart_product_object = cart_data_array.getJSONObject(i);

                if (cart_product_object.getString("id").equalsIgnoreCase(product_id)) {

                    float newPrice = (float) cart_product_object.getDouble("price");
                    float toppings_price = 0;

                    JSONArray items_array = cart_product_object.getJSONArray("items");

                    JSONArray toppingsArray = new JSONArray();

                    for (int j = 0; j < newToppingsList.size(); j++) {
                        JSONObject toppings_object = new JSONObject();
                        if (newToppingsList.get(j).isIngredient_add) {
                            toppings_price = toppings_price + Float.parseFloat(newToppingsList.get(j).getIngredientPrice());
                            toppings_object.put("id", newToppingsList.get(j).getingreid());
                            toppings_object.put("price", newToppingsList.get(j).getIngredientPrice());
                            toppings_object.put("name", newToppingsList.get(j).getToppingname());
                            toppingsArray.put(toppings_object);
                        }
                    }

                    Log.e(TAG, " toppings price:" + toppings_price);
                    newPrice = newPrice + toppings_price;


                    if (customize.equalsIgnoreCase("true")) {
                        Log.e(TAG, "View Cart Act Replacing product item pos:" + pos);
                        //find and replace old copy
                        if (pos != -1) {
                            float old_Toppings_price = 0;
                            float new_toppings_price = 0;

                            JSONArray oldArray = simlified_products_array.getJSONObject(pos).getJSONArray("toppings");


                            Log.e(TAG, "Customize toppings old toppings" + oldArray.toString());
                            Log.e(TAG, "Customize toppings toppings Array" + toppingsArray.toString());
                            boolean match = false;

                            for (int k = 0; k < items_array.length(); k++) {
                                //fit for matching
                                Log.e(TAG, "existing toppings len:" + toppingsArray.length());
                                if (items_array.getJSONArray(k).length() == toppingsArray.length()) {
                                    match = true;
                                    for (int x = 0; x < toppingsArray.length(); x++) {
                                        for (int z = 0; z < items_array.getJSONArray(k).length(); z++) {
                                            if (!items_array.getJSONArray(k).getJSONObject(z).getString("id").equalsIgnoreCase(toppingsArray.getJSONObject(x).getString("id"))) {
                                                match = false;
                                                break;
                                            }
                                        }
                                    }//matching array double loop
                                } else
                                    match = false;

                                //match found
                                if (match) {
                                    Log.e(TAG, "Customize match found at :" + k);
                                    old_Toppings_price = 0;
                                    for (int y = 0; y < toppingsArray.length(); y++) {
                                        old_Toppings_price = old_Toppings_price + (float) toppingsArray.getJSONObject(y).getDouble("price");
                                    }
                                    items_array.put(k, toppingsArray);
                                    Log.e(TAG, "Toppings Price new old :" + toppings_price + " " + old_Toppings_price);
                                    cart.put("total_price", cart.getDouble("total_price") - (old_Toppings_price - toppings_price));
                                } else {
                                    Log.e(TAG, "View tem");
                                    //and new item copy
                                    old_Toppings_price = 0;
//                                    for (int y = 0; y < toppingsArray.length(); y++) {
//                                        old_Toppings_price = old_Toppings_price + (float) toppingsArray.getJSONObject(y).getDouble("price");
//                                        Log.e(TAG, "LOOP PRICE: "+old_Toppings_price );
//                                    }
                                    items_array.put(pos, toppingsArray);

                                    for (int y = 0; y < items_array.getJSONArray(k).length(); y++) {
                                        old_Toppings_price = old_Toppings_price + (float) items_array.getJSONArray(k).getJSONObject(y).getDouble("price");
                                        Log.e(TAG, "LOOP PRICE: " + old_Toppings_price);
                                    }
                                    Log.e(TAG, "ITEMS Array: " + items_array + "Toppings Array:" + toppingsArray.toString() + "OLD ARRAY:" + oldArray.toString());
                                    Log.e(TAG, "addProduct: " + cart.getDouble("total_price") + " toppingPrice:" + toppings_price + "oldToppin:g" + old_Toppings_price);

                                    float itemsPrice = (float) cart_product_object.getDouble("price") * (float) cart.getDouble("total_items");
                                    Log.e(TAG, "addProduct:ITEMSPRICE: " + itemsPrice);
                                    cart.put("total_price", itemsPrice + toppings_price + old_Toppings_price);

                                }
//
                            }
                            Log.e(TAG, "addProductBAHAR: " + cart.getDouble("total_price") + " toppingPrice:" + toppings_price + "oldToppin:g" + old_Toppings_price);

                        }

                    } else {
                        Log.e(TAG, "View Cart Act adding new product item");
                        //and new item copy
                        items_array.put(toppingsArray);
                        cart.put("total_price", cart.getDouble("total_price") + newPrice);
                        cart.put("total_items", cart.getInt("total_items") + 1);
                    }

//                  cart.put("data",cart_data_array);

                    Log.e(TAG, "cart data on Add:" + cart.toString());
                    BaseApplication.getInstance().getSession().setCartItmes(cart.toString());
                }

            }
            //update cart object
//            saved_cart_data_object.put("total_items",saved_cart_data_object.getInt("total_items")+1);
//            saved_cart_data_object.put("total_price",newPrice);
//            saved_cart_data_object.put("data",cart_product_array);


            //Update simplified
            buildSimplified_JSONArray();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void askPermissions() {
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission) {
            if (ContextCompat.checkSelfPermission(getActivity(), s) == PackageManager.PERMISSION_DENIED) {
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: ");
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions(getActivity(), permissionToAsk.toArray(new String[permissionToAsk.size()]), Constant.requestcodeForPermission);
        } else {
            Log.e("tag", "else access askPermissions: ");
            accessPermission();
        }
    }

    private void accessPermission() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            Log.e("tag", "if can get locationaccessPermission: ");
            Intent intent = new Intent(getActivity(), Change_Address_Activity.class);
            try {
                intent.putExtra("restaurant_id", cart.getJSONArray("data").getJSONObject(0).getString("restro_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("houseno", houseno);
            intent.putExtra("landmark", landmark);
            intent.putExtra("title", address_title);
            intent.putExtra("location", location);
            //   Log.e(TAG, "onClick: restaurant_id" + restaurant_id);
            startActivityForResult(intent, 4);
        } else {
            Log.e("tag", "else cannot get locationaccessPermission: ");
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
            showDialog();
            Log.e("tag", "delivery_address :" + BaseApplication.getInstance().getSession().getDeliveryAddress());

        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.gpsTitle));
        builder.setMessage(getString(R.string.gpsMessage));
        builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Log.e("tag", "delivery_address :" + BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), Change_Address_Activity.class);
                try {
                    intent.putExtra("restaurant_id", cart.getJSONArray("data").getJSONObject(0).getString("restro_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("houseno", houseno);
                intent.putExtra("landmark", landmark);
                intent.putExtra("title", address_title);
                intent.putExtra("location", location);
                startActivityForResult(intent, 4);

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode == Constant.requestcodeForPermission) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: ");
                    allGranted = false;

                }
            }
            if (allGranted) {
                Log.e("tag", "All grantednRequestPermissionsResult: ");
                accessPermission();
            } else {
                //  finish();
                BaseApplication.getInstance().getSession().setExit("Exit");
                Intent intent = new Intent(getActivity(), Change_Address_Activity.class);
                try {
                    intent.putExtra("restaurant_id", cart.getJSONArray("data").getJSONObject(0).getString("restro_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("houseno", houseno);
                intent.putExtra("landmark", landmark);
                intent.putExtra("title", address_title);
                intent.putExtra("location", location);
                startActivityForResult(intent, 4);
            }
        }
    }

}
