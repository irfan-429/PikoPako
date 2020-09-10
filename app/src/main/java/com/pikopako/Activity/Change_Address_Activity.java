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
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class Change_Address_Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GeocoderLocation.AddressRecieved {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rrl)
    RelativeLayout mSnackView;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.setshippinglocation)
    AutoCompleteTextView mEditLocation;

    @BindView(R.id.ed_flatno)
    CustomEditTextBold ed_flatno;

    @BindView(R.id.ed_landmark)
    CustomEditTextBold ed_landmark;

    @BindView(R.id.btnsaveandproceed)
    Button btnsaveandproceed;

    @BindView(R.id.radiobutton_home)
    RadioButton radioButton_home;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.radioButton_work)
    RadioButton radioButton_work;

    private Double latitude;
    private Double longitude;

    private GoogleMap googleMap;
    ProgressDialog progressDialog;

    private LatLng latLng;
    private GeocoderLocation addressFromGeocode;
    String restaurant_id;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

    String home;
    float deleivery_charge, coordinate_id;
    String language = "";
    boolean move = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_address_layout);
        Places.initialize(this, getResources().getString(R.string.google_api_key1)); //init auto complete places API
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        if (getIntent().hasExtra("location")) {
            move = true;
        } else move = false;
        initilizeMap();
        listners();



        Intent intent = getIntent();


        if (intent.hasExtra("location")) {
            mEditLocation.setText(intent.getStringExtra("location"));
            Log.e("TAGG", "onCreate: " + getIntent().getStringExtra("location"));
        }

        restaurant_id = intent.getStringExtra("restaurant_id");
        Log.e("restro id", "onCreate: " + intent.getStringExtra("restaurant_id"));
//        if (intent.hasExtra("houseno")&& intent.hasExtra("landmark")&& intent.hasExtra("title")){
//            ed_flatno.setText(intent.getStringExtra("houseno"));
//            ed_landmark.setText(intent.getStringExtra("landmark"));
//            String title=intent.getStringExtra("title");
//
//            if (title.equalsIgnoreCase("Home"))
//                radioButton_home.setChecked(true);
//            else
//                radioButton_work.setChecked(true);
//        }

        if (BaseApplication.getInstance().getSession().getAddress() != null && !BaseApplication.getInstance().getSession().getAddress().trim().isEmpty()) {
            try {
                JSONObject dd = new JSONObject(BaseApplication.getInstance().getSession().getAddress());
                ed_landmark.setText(dd.getString("landmark"));
                ed_flatno.setText(dd.getString("houseno"));
                String tit = dd.getString("address_title");

                if (tit.equalsIgnoreCase("Home"))
                    radioButton_home.setChecked(true);
                else
                    radioButton_work.setChecked(true);

                    mEditLocation.setText(dd.getString("location"));


                latitude = dd.getDouble("latitude");
                longitude = dd.getDouble("longitude");
                Log.e("dd", "onCreate: " + dd.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("id restro", "onCreate: " + restaurant_id);


    }

    private void initLocation() {
        //set location
        String savedLat = BaseApplication.getInstance().getSession().getDeliveryLatitudeSet();
        String savedLng = BaseApplication.getInstance().getSession().getDeliveryLongitudeSet();
        String savedLoc = BaseApplication.getInstance().getSession().getPreviousLocationTitle();
        Log.i("==>", " " + savedLoc);
        if ((savedLat != null && !savedLat.isEmpty()) && (savedLng != null && !savedLng.isEmpty())) {
            mEditLocation.setText(savedLoc);
            latitude = Double.parseDouble(savedLat);
            longitude = Double.parseDouble(savedLng);
            setMap();
        } else {
            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                getAddress(new LatLng(latitude, longitude));
                setMap();
            }
        }
    }


    private void initilizeMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        this.googleMap.setMyLocationEnabled(true);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        height = height / 3;
        height = height + 100;
        googleMap.setPadding(0, height, 20, 0);

        if (addressFromGeocode == null) {
            addressFromGeocode = new GeocoderLocation();
            addressFromGeocode.recieved = Change_Address_Activity.this;
        }

        initLocation();


        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng location = googleMap.getCameraPosition().target;
                addressFromGeocode.start(Change_Address_Activity.this, location, false);

            }
        });

        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        this.googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                GPSTracker gps = new GPSTracker(Change_Address_Activity.this);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    getAddress(new LatLng(latitude, longitude));
                    setMap();
                }else Toast.makeText(Change_Address_Activity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    private void setMap() {

        LatLng coordinate = new LatLng(latitude,longitude);
        if (BaseApplication.getInstance().getSession().getAddress() != null && !BaseApplication.getInstance().getSession().getAddress().trim().isEmpty()) {
            try {
                JSONObject dd = new JSONObject(BaseApplication.getInstance().getSession().getAddress());
                if (dd.has("latitude") && dd.getString("latitude") != null) {
                    coordinate = new LatLng(dd.getDouble("latitude"), dd.getDouble("longitude"));
                } else
                    coordinate = new LatLng(latitude, longitude);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            coordinate = new LatLng(latitude, longitude);




        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
        Log.e("TAG", "YOUR LOCATION: " + yourLocation + "Coordinate:" + coordinate);
        googleMap.animateCamera(yourLocation);
        if (googleMap != null) {
            googleMap.clear();
        }

//        Log.e("coordinate 2", "locationget: "+new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()) );
//        coordinate=new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//        latitude=gpsTracker.getLatitude();
//        longitude=gpsTracker.getLongitude();
//        Log.e("coord", "locationget: "+coordinate );
        getAddress(coordinate);
    }


    private void getAddress(final LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses;

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            //   if (addresses.size()>0)
//            if (!getIntent().getStringExtra("location").equalsIgnoreCase("")) {
//
//                mEditLocation.setText(getIntent().getStringExtra("location"));
//            }
//            //   if (getIntent().getStringExtra("location").equalsIgnoreCase("")) {
//            else {
//                mEditLocation.setText(addresses.get(0).getAddressLine(0).toString());
//                Log.e("GET ADDRESS", "getAddress: " + addresses.get(0).getAddressLine(0).toString());
//
//                mEditLocation.setText(addresses.get(0).getAddressLine(0).toString());
//
//            }

            mEditLocation.setText(addresses.get(0).getAddressLine(0));


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
        mEditLocation.setOnClickListener(this);
        btnsaveandproceed.setOnClickListener(this);

    }


    public boolean validate() {
        boolean valid = true;
        String location = mEditLocation.getText().toString().trim();
        String house = ed_flatno.getText().toString().trim();
        String landmark = ed_landmark.getText().toString().trim();

        if (location.isEmpty()) {
//            mEditLocation.requestFocus()
            mEditLocation.setError(getString(R.string.valid_msg_location_required));
            valid = false;
        } else if (house.isEmpty()) {
            ed_flatno.setError(getString(R.string.valid_msg_required));
            valid = false;
        } else if (landmark.isEmpty()) {
            ed_landmark.setError(getString(R.string.valid_msg_required));
            valid = false;
        } else if (location.length() < 3) {
            mEditLocation.setError(getString(R.string.valid_msg_location_length_required));
            valid = false;
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.setshippinglocation:
                Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country (UAE) ae
                        .build(this);
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);

//                    intent = new Intent(Change_Address_Activity.this, CommonSearchActivity.class);
//                    startActivityForResult(intent, 10001);
//                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;

            case R.id.btnsaveandproceed:
//                if (mEditLocation.getText().toString().trim().isEmpty()){
//                        mEditLocation.setError(getString(R.string.please_enter_location));
//                    }

                if (validate()) callApi();

//
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
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    if (googleMap != null) {
//                        move = true;
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                    }
//                    mEditLocation.setText(bundle.getString("text"));
//                }
//
//            }
//        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("==>", "onActivityResult: " + place.getName());
                LatLng latLngOrderDest = place.getLatLng(); //get lat lng of destination place
                latitude = latLngOrderDest.latitude;
                longitude = latLngOrderDest.longitude;
                Log.e("==>", "lat: " + latitude + " lng " + longitude);
                mEditLocation.setText(UiHelper.getAddress(this, latitude, longitude)); //set searched place to location field

//                latitude= 26.9124336;
//                longitude=75.78727090000007;

                LatLng latLng = new LatLng(latitude, longitude);
                if (googleMap != null) {
                    move = true;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }

                Log.i("placesLat", String.valueOf(latitude));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("places", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//                Intent intent1=new Intent();
//                intent1.putExtra("location",mEditLocation.getText().toString());
//                intent1.putExtra("houseno.",ed_flatno.getText().toString());
//                intent1.putExtra("landmark",ed_landmark.getText().toString());
//
//                setResult(4,intent1);
//                finish();
                //  overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                        //    callApi();
                        Log.e("tag", "location move" + longAdd);
                        if (needCamera)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    }
                });
    }

    private void callApi() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);
        jsonObject.addProperty("restaurant_id", restaurant_id);
        jsonObject.addProperty("language", language);

        Log.e("lat,longres", "" + latitude + " long:-" + longitude + " restid:- " + restaurant_id);

        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().checkDeliveryLocation(BaseApplication.getInstance().getSession().getToken(), jsonObject);


        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            Log.e("json objecxt", "Success: " + jsonObject1.toString());

                            deleivery_charge = Float.parseFloat(jsonObject1.getJSONObject("data").getString("delivery_amount"));
                            coordinate_id = Float.parseFloat(jsonObject1.getJSONObject("data").getString("coordinate_id"));
                            String minimum_order_amount = jsonObject1.getJSONObject("data").getString("minimum_order_amount");

                            BaseApplication.getInstance().getSession().setCoordinateId(String.valueOf(coordinate_id));
                            Intent intent1 = new Intent();
                            intent1.putExtra("location", mEditLocation.getText().toString());
                            intent1.putExtra("houseno", ed_flatno.getText().toString());
                            intent1.putExtra("landmark", ed_landmark.getText().toString());
                            intent1.putExtra("longitude", longitude);
                            intent1.putExtra("latitude", latitude);
                            intent1.putExtra("deleivery_charge", deleivery_charge);
                            intent1.putExtra("minimum_order_amount", minimum_order_amount);
                            if (radioButton_home.isChecked())
                                home = String.valueOf(radioButton_home.getText());
                            else
                                home = String.valueOf(radioButton_work.getText());

                            intent1.putExtra("address_title", home);

                            Log.e("intent data", "onClick: " + intent1.toString());
                            setResult(Activity.RESULT_OK, intent1);
                            finish();


                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(Change_Address_Activity.this, LoginActivity.class);
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
}
