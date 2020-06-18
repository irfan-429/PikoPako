package com.pikopako.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CouponCodeModel {

        public int coupenId=0;
        public String coupenCodeNAme="";
        public String coupenPrice="";
        public String copenExpiryDate="";
        public String status="";
        public ArrayList<CouponCodeModel> codeDetailsModelArrayList=null;

        public void getAllCoupenCodeDetails(JSONArray jsonArray)
        {
            codeDetailsModelArrayList=new ArrayList<>();
            codeDetailsModelArrayList.clear();
            try {
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Log.e("model detail coupon ", "getAllCoupenCodeDetails: "+jsonObject.toString() );
                    CouponCodeModel coupenCodeDetailsModel=new CouponCodeModel();
                    coupenCodeDetailsModel.coupenId=jsonObject.has("id")?jsonObject.getInt("id"):0;
                    coupenCodeDetailsModel.coupenCodeNAme=jsonObject.has("coupon_code")?jsonObject.getString("coupon_code"):"";
                    coupenCodeDetailsModel.coupenPrice=jsonObject.has("discount_percentage")?jsonObject.getString("discount_percentage"):"";
                    coupenCodeDetailsModel.status=jsonObject.has("status")?jsonObject.getString("status"):"";
                    coupenCodeDetailsModel.copenExpiryDate=jsonObject.has("expiry_date")?jsonObject.getString("expiry_date"):"";
                    codeDetailsModelArrayList.add(coupenCodeDetailsModel);
                }
                Log.e("Coupen arraylist size ",""+codeDetailsModelArrayList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


}
