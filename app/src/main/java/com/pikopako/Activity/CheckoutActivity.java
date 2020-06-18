package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnlogin)
    Button btnlogin;

    @BindView(R.id.btn_checkoutasguest)
    Button btn_checkout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    String language="";
    String restro_image,restro_location,restro_name,restro_status,restaurant_id,remarks,delivery_time;
    String houseno,landmark,location,address_title,coordinate_id,user_name,user_contact,user_email,user_type;
    double latitude,longitude;
    float deleivery_charge,promo_code_id,promo_code_price,discount_amount,paid_amount;
    String isPromoApplied="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
        setActionBarTitle();
        getData();
    }
    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.checkout));

        btn_checkout.setOnClickListener(this);
        btnlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnlogin:
                Intent intent1 = new Intent(this, LoginActivity.class);
                boolean item = false;
                Log.e("TAG", "onClick Intent value: "+getIntent().getStringExtra("cartfragment") );
                if (getIntent().hasExtra("cartfragment")) {
                    intent1.putExtra("cartfragment", false);
                    Log.e("TAG", "onClick: Cart fragment " );
                }
                else {
                    Log.e("TAG", "onClick: Cart Activity " );
                    intent1.putExtra("cart", item);
                }
                startActivity(intent1);
                break;

            case R.id.btn_checkoutasguest:
                Intent intent=new Intent(this,Checkout_Information.class);


                intent.putExtra("paid_amount", paid_amount);
                intent.putExtra("discount_amount", discount_amount);
                intent.putExtra("delivery_charge", deleivery_charge);
                intent.putExtra("is_promo_code_applied", isPromoApplied);
                intent.putExtra("house_number", houseno);
                intent.putExtra("landmark", landmark);
                intent.putExtra("address", location);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("restaurant_id", restaurant_id);
                intent.putExtra("address_title", address_title);
                intent.putExtra("coordinate_id", coordinate_id);
                intent.putExtra("promo_code_id", promo_code_id);
                intent.putExtra("promo_code_price", promo_code_price);
                intent.putExtra("restro_image", restro_image);
                intent.putExtra("restro_location", restro_location);
                intent.putExtra("restro_name", restro_name);
                intent.putExtra("restro_status", restro_status);
                intent.putExtra("remarks",remarks);
                intent.putExtra("delivery_time",delivery_time);
                Log.e("tag", "senddetail: "+restro_image+"  ff"+paid_amount );
                startActivity(intent);
        }
    }

    private void getData(){

        Intent intent=getIntent();

        houseno=intent.getStringExtra("house_number");
        landmark=intent.getStringExtra("landmark");
        location=intent.getStringExtra("address");
        paid_amount= intent.getFloatExtra("paid_amount",0);
        coordinate_id=intent.getStringExtra("coordinate_id");
        discount_amount= intent.getFloatExtra("discount_amount",0);
        address_title=intent.getStringExtra("address_title");

        promo_code_id=intent.getFloatExtra("promo_code_id",0);
        promo_code_price=intent.getFloatExtra("promo_code_price",0);

        latitude=intent.getDoubleExtra("latitude",0);
        longitude=intent.getDoubleExtra("longitude",0);
        deleivery_charge=intent.getFloatExtra("delivery_charge",0);
        isPromoApplied=intent.getStringExtra("is_promo_code_applied");
        restro_image=intent.getStringExtra("restro_image");
        restro_location=intent.getStringExtra("restro_location");
        restro_name=intent.getStringExtra("restro_name");
        restro_status=intent.getStringExtra("restro_status");
        restaurant_id=intent.getStringExtra("restaurant_id");
        remarks=intent.getStringExtra("remarks");
        delivery_time=intent.getStringExtra("delivery_time");
        Log.e("tag", "getData: "+houseno+" latitude"+latitude+"deleivrychrg"+deleivery_charge );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
