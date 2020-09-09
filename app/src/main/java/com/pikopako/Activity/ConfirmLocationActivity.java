package com.pikopako.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.GPSTracker;
import com.pikopako.AppUtill.GeocoderLocation;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class ConfirmLocationActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback, GeocoderLocation.AddressRecieved {

    @BindView(R.id.btnAddMore)
    Button mBtnAddMore;

    @BindView(R.id.btnConfirmLocation)
    Button btnConfirmLocation;

//    @BindView(R.id.editLocation)
//    CustomEditTextBold mEditLocation;

    @BindView(R.id.editLocation)
    AutoCompleteTextView mEditLocation;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.add_more_lyt)
    LinearLayout add_more_lyt;
/*

    @BindView(R.id.progressBar4)
    ProgressBar progressBar4;
*/


    private Double latitude;
    private Double longitude;

    private boolean confirm = false;
    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    private GoogleMap googleMap;
    ProgressDialog progressDialog;
    private boolean isRegister = false;
    private LatLng latLng;
    private GeocoderLocation addressFromGeocode;
    String profileData;
    String language = "";

    boolean move = false;
    private String TAG = "==ConfirmLoc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_location);
        Places.initialize(this, getResources().getString(R.string.google_api_key1)); //init auto complete places API

        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        // progressBar4.setIndeterminate(true);
        initilizeMap();
        if (getIntent().getExtras() != null) {
            isRegister = getIntent().getBooleanExtra(Constant.IS_SIGNUP, false);
            if (isRegister) {
                add_more_lyt.setVisibility(View.GONE);
            } else {
                add_more_lyt.setVisibility(View.VISIBLE);
            }
        }



        listners();
//        btnConfirmLocation.setClickable(false);
        profileData = BaseApplication.getInstance().getSession().getProfileData();

        //set location
        String savedLat = BaseApplication.getInstance().getSession().getDeliveryLatitudeSet();
        String savedLng = BaseApplication.getInstance().getSession().getDeliveryLongitudeSet();
        String savedLoc=  BaseApplication.getInstance().getSession().getPreviousLocationTitle();
        if ((savedLat!=null && !savedLat.isEmpty()) && (savedLng!=null && !savedLng.isEmpty())){
            mEditLocation.setText(savedLoc);
            latitude=Double.parseDouble(savedLat);
            longitude=Double.parseDouble(savedLng);
        } else setCurrentLoc();
    }

    private void setCurrentLoc() {
        GPSTracker gpsTracker = new GPSTracker(this);
        LatLng coordinate = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        getAddress(coordinate);

    }

    private void initilizeMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        int height = getWindowManager().getDefaultDisplay().getHeight();
        height = height / 3;
        height = height + 100;
        googleMap.setPadding(0, height, 20, 0);

        if (addressFromGeocode == null) {
            addressFromGeocode = new GeocoderLocation();
            addressFromGeocode.recieved = ConfirmLocationActivity.this;
        }
        proceedAfterPermission();

        Log.e(TAG, "onMapReady: ");
    }

    private void proceedAfterPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                GPSTracker gps = new GPSTracker(ConfirmLocationActivity.this);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    getAddress(new LatLng(latitude, longitude));
                    setMap();
                }else Toast.makeText(ConfirmLocationActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        String savedLat = BaseApplication.getInstance().getSession().getDeliveryLatitudeSet();
        String savedLng = BaseApplication.getInstance().getSession().getDeliveryLongitudeSet();
        String savedLoc=  BaseApplication.getInstance().getSession().getPreviousLocationTitle();
        if ((savedLat!=null && !savedLat.isEmpty()) && (savedLng!=null && !savedLng.isEmpty())){
            mEditLocation.setText(savedLoc);
            latitude=Double.parseDouble(savedLat);
            longitude=Double.parseDouble(savedLng);

            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
            googleMap.animateCamera(yourLocation);
            onMarkerDragging();
        }
        else locationget();

    }


    private void setMap() {
//        if (gps.canGetLocation()) {
        LatLng coordinate = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
        googleMap.animateCamera(yourLocation);
        if (googleMap != null) {
            googleMap.clear();
        }
    }

    private void locationget() {
        Log.e(TAG, "locationget: ");
        // Intent mServiceIntent = new Intent(ConfirmLocationActivity.this, LocationService.class);
        //startService(mServiceIntent);





        GPSTracker gpsTracker = new GPSTracker(this);
        LatLng coordinate = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
        googleMap.animateCamera(yourLocation);

        onMarkerDragging();

//        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                proceedAfterPermission();
//                return true;
//            }
//        });


        getAddress(coordinate);
    }

    private void onMarkerDragging() {
        if (googleMap != null) {
            googleMap.clear();
        }
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e(TAG, "onCameraIdle: ");
                LatLng location = googleMap.getCameraPosition().target;
                addressFromGeocode.start(ConfirmLocationActivity.this, location, false);

            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.e(TAG, "onCameraMoveStarted: ");
            }
        });

    }


    private void getAddress(final LatLng latLng) {
        latitude= latLng.latitude;
        longitude=latLng.longitude;


        Log.e(TAG, "getAddress: ");
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses;

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mEditLocation.setText(addresses.get(0).getAddressLine(0).toString());
            this.latLng = latLng;
            //  progressBar4.setIndeterminate(false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mEditLocation.setText("");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            mEditLocation.setText("");
        }
    }

    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.toolbar_title_set_location));
        mBtnAddMore.setOnClickListener(this);
        btnConfirmLocation.setOnClickListener(this);
        mEditLocation.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;
        String location = mEditLocation.getText().toString().trim();
        if (location.isEmpty()) {
            mEditLocation.setError(getString(R.string.valid_msg_location_required));
            valid = false;
        } else if (location.length() < 3) {
            mEditLocation.setError(getString(R.string.valid_msg_location_length_required));
            valid = false;
        } else {
            mEditLocation.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnAddMore:
                intent = new Intent(ConfirmLocationActivity.this, SetDeliveryLocationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.editLocation:
                Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country (UAE) ae
                        .build(this);
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);

//                intent = new Intent(ConfirmLocationActivity.this, CommonSearchActivity.class);
//                startActivityForResult(intent, 10001);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;

            case R.id.btnConfirmLocation:
                if (isRegister) {
                    setDeliveryLocation();
                } else {
                    if (!validate()) {
                        return;
                    }
                    BaseApplication.getInstance().getSession().setDeliveryAddress(mEditLocation.getText().toString());
                    BaseApplication.getInstance().getSession().setDeliveryLatitude(String.valueOf(latitude));
                    BaseApplication.getInstance().getSession().setDeliveryLongitude(String.valueOf(longitude));

                    //save loc
                    BaseApplication.getInstance().getSession().setPreviousLocationTitle(mEditLocation.getText().toString().trim());
                    BaseApplication.getInstance().getSession().setDeliveryLatitudeSet(String.valueOf(latitude));
                    BaseApplication.getInstance().getSession().setDeliveryLongitudeSet(String.valueOf(longitude));


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("latitude", latitude);
                        jsonObject.put("longitude", longitude);
                        jsonObject.put("address", mEditLocation.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject));

                    intent = new Intent(ConfirmLocationActivity.this, DefaultActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.IS_SIGNUP, false);
                    intent.putExtra("address", mEditLocation.getText().toString());
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);

                    Log.e(TAG, "onClick: " + latitude + " -lng:  " + longitude);
                    startActivity(intent);
//                    finish();
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 10001) {
//                Bundle bundle = data.getExtras();
//                Log.e("tag", "text" + bundle.getString("text"));
//                Log.e("tag", "latitude" + bundle.getString("latitude"));
//                Log.e("tag", "longitude" + bundle.getString("longitude"));
//                if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
//                    final String address = bundle.getString("text");
//                    latitude = Double.valueOf(bundle.getString("latitude"));
//                    longitude = Double.valueOf(bundle.getString("longitude"));
//                    BaseApplication.getInstance().getSession().setDeliveryLatitude(String.valueOf(latitude));
//                    BaseApplication.getInstance().getSession().setDeliveryLongitude(String.valueOf(longitude));
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    if (googleMap != null) {
//                        move=true;
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                    }
//                        mEditLocation.setText(bundle.getString("text"));
//
//                }
//               btnConfirmLocation.setClickable(true);
//            }
//        }else

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("==>", "onActivityResult: " + place.getName());
                LatLng latLngOrderDest = place.getLatLng(); //get lat lng of destination place
                latitude = latLngOrderDest.latitude;
                longitude = latLngOrderDest.longitude;
                Log.e("==>", "lat: " + latitude + " lng " + longitude);
//                latitude= 26.9124336;
//                longitude=75.78727090000007;
                mEditLocation.setText(UiHelper.getAddress(this, latitude, longitude)); //set searched place to location field


                BaseApplication.getInstance().getSession().setDeliveryLatitude(String.valueOf(latitude));
                BaseApplication.getInstance().getSession().setDeliveryLongitude(String.valueOf(longitude));
                LatLng latLng = new LatLng(latitude, longitude);
                if (googleMap != null) {
                    move = true;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }

            }
            btnConfirmLocation.setClickable(true);

            Log.i("placesLat", String.valueOf(latitude));
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e("places", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

        UiHelper.hideSoftKeyboard1(this);

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

    private void setDeliveryLocation() {
        if (!validate()) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("address", mEditLocation.getText().toString().trim());
        jsonObject.addProperty("latitude", latitude.toString());
        jsonObject.addProperty("longitude", longitude.toString());
        jsonObject.addProperty("language", language);
        Log.e("tag", "pare set location" + jsonObject.toString());

        setDeliveryLocationApi(jsonObject);

    }

    private void setDeliveryLocationApi(JsonObject jsonObject) {
        progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().setDeliveryAddress(BaseApplication.getInstance().getSession().getToken(), jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            Intent intent = new Intent(ConfirmLocationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.IS_SIGNUP, true);
                            JSONObject jsonObject2 = new JSONObject(profileData);
                            jsonObject2.put("latitude", latitude);
                            jsonObject2.put("longitude", longitude);
                            jsonObject2.put("address", mEditLocation.getText().toString().trim());
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject2));

                            //save loc
                            BaseApplication.getInstance().getSession().setPreviousLocationTitle(mEditLocation.getText().toString().trim());
                            BaseApplication.getInstance().getSession().setDeliveryLatitudeSet(String.valueOf(latitude));
                            BaseApplication.getInstance().getSession().setDeliveryLongitudeSet(String.valueOf(longitude));

                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
                    UiHelper.showNetworkError(ConfirmLocationActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    @Override
    public void addressRecieved(final String longAdd, final LatLng latlng, final boolean needCamera) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        latitude = latlng.latitude;
                        longitude = latlng.longitude;
                        if (!move) {
                            mEditLocation.setText(longAdd);
                        } else
                            move = false;
                        Log.e("tag", "location move" + longAdd + latitude + longitude);
                        if (needCamera)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        btnConfirmLocation.setClickable(true);
                    }
                });
    }


}