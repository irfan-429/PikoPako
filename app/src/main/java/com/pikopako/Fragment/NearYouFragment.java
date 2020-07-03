package com.pikopako.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pikopako.Activity.FilterActivity;
import com.pikopako.Activity.Rating_reviews_Activity;
import com.pikopako.Activity.RestroInfoActivity;
import com.pikopako.Adapter.ProductListAdapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.GPSTracker;
import com.pikopako.AppUtill.GeocoderLocation;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Model.FilterModel;
import com.pikopako.Model.ProductListModel;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;


public class NearYouFragment extends Fragment implements ProductListAdapter.ClickItemEvent {

    @BindView(R.id.recycleview)
    RecyclerView mRecycleview;

    @BindView(R.id.snackView)
    LinearLayout mSnackView;

    //yha productmodel ki jagah ProductListModel
    ArrayList<ProductListModel> productListModelArrayList = new ArrayList<>();
    ProductListModel productListModel;
    ProductListAdapter productListAdapter;
    ProgressDialog progressDialog;
    Double latitude, longitude;
    String address;
    JSONArray selected_category_ids = new JSONArray();
    ArrayList<FilterModel> category_list = new ArrayList<>();
    String convertedIds;
    LatLng saved_latlng;
    String language = "";
    ArrayList<String> permissionToAsk = new ArrayList<>();
    private GoogleMap googleMap;
    private GeocoderLocation addressFromGeocode;
    private LatLng latLng;
    private final String TAG="NearYouFragment=> ";

    public static String mLat = "26.9124336", mLng = "75.78727090000007";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycle_without_toolbar, container, false);
        ButterKnife.bind(this, view);
        mRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));


//        JSONObject profileData = null;
//        try {
//            profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
//
//            if (profileData.has("address")&& !profileData.getString("address").equalsIgnoreCase("null"))
//                address = profileData.getString("address");
//            else
//                address=BaseApplication.getInstance().getSession().getDeliveryAddress();
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(address);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        if (getActivity().getIntent().hasExtra("address")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getIntent().getStringExtra("address"));
            latitude = getActivity().getIntent().getDoubleExtra("latitude", 0);
            longitude = getActivity().getIntent().getDoubleExtra("longitude", 0);
            Log.e(TAG, "lat and long "+latitude+" , "+longitude );

//            latitude = Double.parseDouble(mLat);
//            longitude = Double.parseDouble(mLng);

            BaseApplication.getInstance().getSession().setDeliveryLatitudeSet(String.valueOf(latitude));
            BaseApplication.getInstance().getSession().setDeliveryLongitudeSet(String.valueOf(longitude));
            callApi(makePayload());
        } else {
            //Set previous stored delivery location
            String locationTitle = BaseApplication.getInstance().getSession().getPreviousLocationTitle();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(locationTitle);
            Log.d(TAG, "onCreateView: else " + locationTitle);
            Log.d(TAG, "onCreateView: else " + locationTitle);

            String savedLat = BaseApplication.getInstance().getSession().getDeliveryLatitudeSet();
            String savedLng = BaseApplication.getInstance().getSession().getDeliveryLongitudeSet();

            Log.d(TAG, " pre savedLat: " + savedLat);

            if (savedLat.isEmpty() && savedLng.isEmpty()) {
                GPSTracker gpsTracker=new GPSTracker(getContext());
                if (gpsTracker.canGetLocation()){
                    savedLat= String.valueOf(gpsTracker.getLatitude());
                    savedLng= String.valueOf(gpsTracker.getLongitude());
                }

                Log.d(TAG, " mid savedLat: " + savedLat);

            }

            latitude = Double.parseDouble(savedLat);
            longitude = Double.parseDouble(savedLng);

            Log.d(TAG, " post savedLat: " + savedLat);

//            Log.e(TAG, "stored lat and long " + latitude + " , " + longitude);

            callApi(makePayload());


            //To get current location
            //getLocation();

        }


        return view;
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
            getLocation();
        }
    }

//    private void getLocation() {
//        GPSTracker gpsTracker=new GPSTracker(getActivity());
//        if(gpsTracker.canGetLocation()){
//            Log.e("tag", "if can get locationaccessPermission: " );
//            LatLng coordinate = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//
//            getAddress(coordinate);
//        }
//        else {
//            Log.e("tag", "else cannot get locationaccessPermission: " );
////            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////            startActivity(intent);
//            showDialog();
//            Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());
//
//        }
//    }

    private void getLocation() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());

        Log.e("tag", "if can get locationaccessPermission: ");
        LatLng coordinate = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());



        getAddress(coordinate);

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
                getLocation();
                Log.e("tag", "delivery_address :" + BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void getAddress(final LatLng latLng) {
        Log.e("TAG", "getAddress:Latitude " + latLng.latitude);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        try {
            List<Address> addresses;

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(addresses.get(0).getAddressLine(0).toString());
            Log.e("TAG", "getAddress: " + addresses);
            this.latLng = latLng;
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            BaseApplication.getInstance().getSession().setDeliveryLatitudeSet(String.valueOf(latitude));
            BaseApplication.getInstance().getSession().setDeliveryLongitudeSet(String.valueOf(longitude));
            //  progressBar4.setIndeterminate(false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        }

        callApi(makePayload());

    }

//    @Override
//    public void addressRecieved(final String longAdd, final LatLng latlng, final boolean needCamera) {
//        getActivity().runOnUiThread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        latitude = latlng.latitude;
//                        longitude = latlng.longitude;
//                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(longAdd);
//                            }
//
//                });
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    private JsonObject makePayload() {
        ///
        JsonObject payload = new JsonObject();
        if (BaseApplication.getInstance().getSession().isLoggedIn()) {
            //  try {
//                JSONObject profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
//                Log.e("Address Maintitlebar", profileData.toString());
//
//                if (profileData.has("latitude") && profileData.has("longitude") && !profileData.getString("latitude").equalsIgnoreCase("null") && !profileData.getString("longitude").equalsIgnoreCase("null")){
//                latitude = profileData.getDouble("latitude");
//                longitude = profileData.getDouble("longitude");
//                }
//
//
//                else if (!BaseApplication.getInstance().getSession().getDeliveryLatitude().equalsIgnoreCase("")){
//                    latitude = Double.valueOf(BaseApplication.getInstance().getSession().getDeliveryLatitude());
//                    longitude=Double.valueOf(BaseApplication.getInstance().getSession().getDeliveryLongitude());
//                }
            payload.addProperty("latitude", latitude);
            payload.addProperty("longitude", longitude);
            Log.e("TAG", "latitude in make Payload: " + latitude);
            TimeZone tz = TimeZone.getDefault();
            String timezone = tz.getID();
//            timezone = "Asia/Kolkata";
            payload.addProperty("timezone", timezone);
            payload.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
//            if (BaseApplication.getInstance().getSession().isLoggedIn()) {
//                latitude=  profileData.getDouble("latitude");
//                longitude=profileData.getDouble("longitude");
//                payload.addProperty("latitude",latitude);
//                payload.addProperty("longitude",longitude);
//            }else {
//
//                payload.addProperty("latitude", BaseApplication.getInstance().getSession().getDeliveryLatitude());
//                payload.addProperty("longitude", BaseApplication.getInstance().getSession().getDeliveryLongitude());
//            }


//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            Log.e("else", "makePayload: " + payload.toString());

        } else {
            Log.e("else", "makePayload: " + payload.toString());
            TimeZone tz = TimeZone.getDefault();
            String timezone = tz.getID();
//            timezone = "Asia/Kolkata";
            payload.addProperty("timezone", timezone);
            payload.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));

            //    String lat = String.valueOf(BaseApplication.getInstance().getSession().getDeliveryLatitude());
            //    String longi = String.valueOf(BaseApplication.getInstance().getSession().getDeliveryLongitude());

            payload.addProperty("latitude", latitude);
            payload.addProperty("longitude", longitude);
        }

        Log.e("cndition bahar", "makePayload: " + payload.toString());
//        TimeZone tz = TimeZone.getDefault();
//        payload.addProperty("timezone", tz.getID());
//        payload.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));
//        payload.addProperty("latitude", latitude);
//        payload.addProperty("longitude", longitude);
        Log.e("final ek or third pylod", "makePayload: " + payload.toString());
        convertedIds = "";
        for (int i = 0; i < selected_category_ids.length(); i++) {
            try {
                if (i == 0)
                    convertedIds = selected_category_ids.getString(i);
                else
                    convertedIds = convertedIds + "," + selected_category_ids.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



       /* Gson gson = new Gson();
        String data = gson.toJson(selected_category_ids);
        JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();*/
        //Log.e("TAG", "selected_category_ids jason array: "+jsonArray );


        // if (convertedIds.length()>0)
        payload.add("category_id", new JsonParser().parse(selected_category_ids.toString()).getAsJsonArray());
        payload.addProperty("language", language);
        //
        return payload;
    }

    private void callApi(JsonObject payload) {



        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        Log.e("===Tag", "payload of near you" + payload.toString());

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getRestaurantDetail(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            productListModel = new ProductListModel();
                            productListModel.initialize(jsonObject1);
                            productListModelArrayList = productListModel.arraylist;

                            Log.e("near you fragment", "json object dta" + jsonObject1.getString("data"));
                            //    BaseApplication.getInstance().getSession().set_RestaurantID(jsonObject1.getJSONObject("data").getString("restaurant_id"));

                            init();

                            checkForRatingApi();

                        } else {
                            UiHelper.showToast(getActivity(), jsonObject1.getString("message"));

                            productListModelArrayList.clear();
                            init();
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
                // UiHelper.showErrorMessage(mSnackView, error);
                UiHelper.showToast(getActivity(), "Error! Please try again");
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
//                    UiHelper.showNetworkError(getActivity(), mSnackView);
                    UiHelper.showToast(getActivity(), getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        //  callApi(makePayload());


        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(BaseApplication.getInstance().getSession().getDeliveryAddress());

//
//        JSONObject profileData = null;
//        try {
//            profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
//
//            if (profileData.has("address")&& !profileData.getString("address").equalsIgnoreCase("null"))
//                address = profileData.getString("address");
//            else
//                address=BaseApplication.getInstance().getSession().getDeliveryAddress();
//           // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(address);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void checkForRatingApi() {

        //   final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        //   progressDialog.show();

        JsonObject payload = new JsonObject();
        payload.addProperty("language", language);


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().checkRatingPending(BaseApplication.getInstance().getSession().getToken(), payload);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                //     progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                            if (jsonObject2.length() > 0) {

                                Intent intent = new Intent(getActivity(), Rating_reviews_Activity.class);
                                intent.putExtra("restro_image", jsonObject2.getString("restaurant_logo"));
                                intent.putExtra("restro_name", jsonObject2.getString("restaurant_name"));
                                intent.putExtra("restro_id", jsonObject2.getString("restaurant_id"));
                                intent.putExtra("cart_id", jsonObject2.getString("id"));
                                startActivity(intent);
                            } else
                                return;

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void Error(String error) {
//                if (progressDialog != null)
//                    progressDialog.dismiss();
                // UiHelper.showErrorMessage(mSnackView, error);
                UiHelper.showToast(getActivity(), "Error! Please try again");
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
//                    if (progressDialog != null)
//                        progressDialog.dismiss();
//                    UiHelper.showNetworkError(getActivity(), mSnackView);
                    UiHelper.showToast(getActivity(), getString(R.string.pls_check_your_internet));
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });


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

    private void init() {
        productListAdapter = new ProductListAdapter(getActivity(), productListModelArrayList, NearYouFragment.this);
        mRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleview.setAdapter(productListAdapter);
        //    productListAdapter.notifyDataSetChanged();

    }

    //refresh
    public void refresh(LatLng latLng, String addre) {
        if (latLng == null)
            return;
        double latitudel = latLng.latitude;
        double longitudel = latLng.longitude;

        BaseApplication.getInstance().getSession().setDeliveryLatitudeSet(String.valueOf(latitudel));
        BaseApplication.getInstance().getSession().setDeliveryLongitudeSet(String.valueOf(longitudel));
        BaseApplication.getInstance().getSession().setPreviousLocationTitle(addre);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(addre);
        address = addre;

        saved_latlng = latLng;

        JsonObject payload = new JsonObject();

//            try {
//                JSONObject profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
//                Log.e("Address Maintitlebar", profileData.toString());
        payload.addProperty("latitude", latitudel);
        payload.addProperty("longitude", longitudel);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        TimeZone tz = TimeZone.getDefault();
        payload.addProperty("timezone", tz.getID());
        payload.addProperty("time", String.valueOf(Calendar.getInstance().getTime()));

        // convertedIds=selected_category_ids.toString().replace("[","").replace("]","").replace(",", ",");
        convertedIds = "";
        for (int i = 0; i < selected_category_ids.length(); i++) {
            try {
                if (i == 0)
                    convertedIds = selected_category_ids.getString(i);
                else
                    convertedIds = convertedIds + "," + selected_category_ids.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //    if (convertedIds.length()>0)
        //payload.addProperty("category_id", convertedIds);
        payload.add("category_id", new JsonParser().parse(selected_category_ids.toString()).getAsJsonArray());
        Log.e("refresh", "payload of near you" + payload.toString());
        callApi(payload);
    }


    @Override
    public void onClickItem(String itemId, String position) {
        Log.e("tagOnclickItem", " " + itemId + "  " + position);
        Intent i = new Intent(getActivity(), RestroInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", productListModelArrayList);
        bundle.putString("position", position);
        i.putExtras(bundle);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//        Fragment restrofragment=new RestroDetailItemFragment();
//        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
//        FragmentTransaction transaction=fragmentManager.beginTransaction();
//        transaction.replace(R.id.frame,restrofragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                Intent nn = new Intent(getContext(), FilterActivity.class);
                startActivityForResult(nn, 443);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 443:
                    try {
                        selected_category_ids = new JSONArray(data.getStringExtra("id"));

                        Log.e("selected ids", "onActivityResult: " + selected_category_ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // callApi(makePayload());
                    Log.e("saved lat lng", "onActivityResult: " + saved_latlng);
                    if (saved_latlng != null)
                        refresh(saved_latlng, address);
                    else
                        callApi(makePayload());

                    break;
            }

        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        permissionToAsk.clear();
//        if (requestCode==Constant.requestcodeForPermission){
//            boolean allGranted=true;
//            for (int i=0;i<grantResults.length;i++){
//                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
//                    Log.e("tag", "Permission denied onRequestPermissionsResult: " );
//                    allGranted = false;
//
//                }
//            }
//            if (allGranted){
//                Log.e("tag", "All grantednRequestPermissionsResult: " );
//                getLocation();
//            }
//            else {
//                //  finish();
//                BaseApplication.getInstance().getSession().setExit("Exit");
//
//            }
//        }
//    }


}