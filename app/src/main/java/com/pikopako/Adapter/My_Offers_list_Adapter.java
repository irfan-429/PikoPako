package com.pikopako.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.pikopako.Activity.Apply_Coupon;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.Fragment.RestroInfoServices;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;

public class My_Offers_list_Adapter  extends RecyclerView.Adapter<My_Offers_list_Adapter.ViewHolder> {


    Context mContext;
    JSONArray jsonArray=new JSONArray();
    RestroInfoServices clickItemEvent;

    float price;
    String language="";
    public My_Offers_list_Adapter(Context mContext, JSONArray jsonArray,float price) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;
        this.price=price;

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }

    public My_Offers_list_Adapter(Context mContext, JSONArray jsonArray) {

        this.mContext=mContext;
        this.clickItemEvent=clickItemEvent;
        this.jsonArray=jsonArray;

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }

    @NonNull
    @Override
    public My_Offers_list_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_offers_row,parent,false);
        return new My_Offers_list_Adapter.ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        try {
            JSONObject categoriesobject=jsonArray.getJSONObject(position);
            holder.txt_couponcode.setText(categoriesobject.getString("coupon_code"));
            holder.txt_status.setText(mContext.getString(R.string.minorder)+" - €"+categoriesobject.getString("minimum_order_amount"));
            holder.txt_expiry_Date.setText(categoriesobject.getString("expiry_date"));
            holder.txt_percentage.setText("€"+categoriesobject.getString("discount_percentage")+" "+mContext.getString(R.string.off));


            holder.mSnackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.e("tag", "onClick: "+jsonArray.getJSONObject(position).getString("coupon_code") );
                        applyCoupon(jsonArray.getJSONObject(position).getString("coupon_code"));

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

        CustomTextViewBold txt_couponcode,txt_percentage;
        CustomTextViewNormal txt_expiry_Date,txt_status;


        public ViewHolder(View itemView) {
            super(itemView);
            mSnackView=(RelativeLayout)itemView.findViewById(R.id.topp);

            txt_couponcode=(CustomTextViewBold) itemView.findViewById(R.id.txt_couponcode);
            txt_status=(CustomTextViewNormal) itemView.findViewById(R.id.txt_status);
            txt_expiry_Date=(CustomTextViewNormal)itemView.findViewById(R.id.txt_expiry_Date);
            txt_percentage=(CustomTextViewBold) itemView.findViewById(R.id.txt_percentage);

        }
    }

    public interface ClickItemEvent{
        void onClickItem(String itemId);
    }


    private void applyCoupon(String ed_apply_code) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(mContext, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("coupon", ed_apply_code);
        jsonObject.addProperty("price", price);

        jsonObject.addProperty("language",language);
        Log.e("tag", "json object apply coupn " + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().applyCoupon(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(mContext, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            String id=jsonObject1.getJSONObject("data").getString("id");
                            String couponCode=jsonObject1.getJSONObject("data").getString("coupon_code");
                            String discount_percentage=jsonObject1.getJSONObject("data").getString("discount_percentage");
                            String status=jsonObject1.getJSONObject("data").getString("status");

                            Intent intent=new Intent();
                            intent.putExtra("id",id);
                            intent.putExtra("couponCode",couponCode);
                            intent.putExtra("discount",discount_percentage);
                            ((Apply_Coupon)mContext).setResult(Activity.RESULT_OK,intent);

                            //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e("tag","Coupon Updated Successfully"+id+"coupon Code:-"+couponCode);
                            UiHelper.showToast(mContext,jsonObject1.getString("message"));
                            ((Apply_Coupon)mContext).finish();
                        } else {
                            UiHelper.showToast(mContext, jsonObject1.getString("message"));
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
                UiHelper.showToast(mContext, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showToast(mContext, mContext.getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

}
