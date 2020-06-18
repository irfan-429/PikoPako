package com.pikopako.AppUtill;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.pikopako.Activity.BaseActivity;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GeocoderLocation {
    private static final String TAG = "GetAddressFromGeocode";
    public AddressRecieved recieved;
    private String shortAdd = "", longAdd = "No Address Found";

    public GeocoderLocation() {
    }

    public void start(BaseActivity context, final LatLng latLng, final boolean needCamera) {
        try {

            Call<JsonObject> call= BaseApplication.getInstance().getApiClient1().geocode(latLng.latitude + "," + latLng.longitude,
                    context.getResources().getString(R.string.google_api_key1));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e("tag","onResponse geo call"+call);
                    if (response.isSuccessful()) {
                        callHome(response.body().toString(), latLng, needCamera);
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("tag","onFailure geo call"+call);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callHome(String responseString, LatLng latLng, boolean needCamera) {
        try {
            JSONObject mainOb = new JSONObject(responseString);
            doJsonWork(mainOb, latLng, needCamera);
        } catch (Exception e) {
            e.printStackTrace();
            shortAdd = longAdd = Constant.ADD_ERR;
        }

    }
    private void doJsonWork(JSONObject mainOb, LatLng latLng, boolean needCamera) {

        try {

            if (!mainOb.isNull("status")) {
                if (mainOb.getString("status").equals("OK")) {
                    JSONObject parentObject = mainOb.getJSONArray("results").getJSONObject(0);
                    JSONArray addComp = parentObject.getJSONArray("address_components");

                    for (int i = 1; i < addComp.length(); i++) {

                        if (i < 3) {
                            shortAdd += addComp.getJSONObject(i).getString("long_name");
                        } else {
                            break;
                        }

                    }

                    longAdd = parentObject.getString("formatted_address");
                    recieved.addressRecieved(longAdd, latLng, needCamera);
                } else if (mainOb.getString("status").equals("ZERO_RESULTS")) {
                    longAdd = Constant.ADD_ERR;
                    recieved.addressRecieved(longAdd, latLng, needCamera);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            shortAdd = longAdd = Constant.ADD_ERR;
        }
    }

    public String getShortAddress() {
        return shortAdd;
    }

    public String getLongAddress() {
       // Log.i(TAG, longAdd);
        return longAdd;
    }

    public interface AddressRecieved {
        public void addressRecieved(String longAdd, LatLng latlng, boolean needCamera);
    }

}
