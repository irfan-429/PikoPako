package com.pikopako.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.pikopako.AppUtill.ScrollGoogleMap;
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


public class SetDeliveryLocationActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback, GeocoderLocation.AddressRecieved {

    @BindView(R.id.scroolView)
    ScrollView mScroolView;

    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    @BindView(R.id.editLocation)
    CustomEditTextBold mEditLocation;

    @BindView(R.id.editName)
    CustomEditTextBold mEditName;

    @BindView(R.id.editContact)
    CustomEditTextBold mEditContact;

    @BindView(R.id.editEmail)
    CustomEditTextBold mEditEmail;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);


    private Double latitude;
    private Double longitude;
    private GoogleMap googleMap;
    private LatLng latLng;
    private GeocoderLocation addressFromGeocode;
    String language = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        Places.initialize(this, getResources().getString(R.string.google_api_key1)); //init auto complete places API
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";
        initilizeMap();
        listners();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void initilizeMap() {

        final ScrollGoogleMap mMap = ((ScrollGoogleMap) getFragmentManager().findFragmentById(R.id.map));
        mMap.getMapAsync(this);
        mMap.setListener(new ScrollGoogleMap.OnTouchListener() {
            @Override
            public void onTouch() {
                mScroolView.requestDisallowInterceptTouchEvent(true);
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (addressFromGeocode == null) {
            addressFromGeocode = new GeocoderLocation();
            addressFromGeocode.recieved = SetDeliveryLocationActivity.this;
        }
        proceedAfterPermission();
    }

    private void proceedAfterPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        googleMap.setMyLocationEnabled(true);
        locationget();
        //We've got the permission, now we can proceed further
        Log.e("aa", "" + "We got the LocationActivity Permission");
    }

    private void locationget() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            LatLng coordinate = new LatLng(gps.getLatitude(), gps.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 18);
            googleMap.animateCamera(yourLocation);
            if (googleMap != null) {
                googleMap.clear();
            }
            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    LatLng location = googleMap.getCameraPosition().target;
                    addressFromGeocode.start(SetDeliveryLocationActivity.this, location, false);

                }
            });

            googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {

                }
            });

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    proceedAfterPermission();
                    return true;
                }
            });

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });

            getAddress(coordinate);
        } else {
            showDialog();
        }
    }

    private void getAddress(final LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses;

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mEditLocation.setText(addresses.get(0).getAddressLine(0).toString());
            this.latLng = latLng;
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
        btnRegister.setOnClickListener(this);
        mEditLocation.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;
        String email = mEditEmail.getText().toString().trim();
        String location = mEditLocation.getText().toString().trim();
        String phone = mEditContact.getText().toString();
        String personName = mEditName.getText().toString();

        if (location.isEmpty()) {
            mEditLocation.setError(getString(R.string.valid_msg_location_required));
            valid = false;
        } else if (location.length() < 3) {
            mEditLocation.setError(getString(R.string.valid_msg_location_length_required));
            valid = false;
        } else {
            mEditLocation.setError(null);
        }
        if (personName.isEmpty()) {
            mEditName.setError(getString(R.string.valid_msg_full_name_required));
            valid = false;
        } else if (personName.length() < 3) {
            mEditName.setError(getString(R.string.valid_msg_full_name));
            valid = false;
        } else {
            mEditName.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditEmail.setError(getResources().getString(R.string.valid_msg_email));
            valid = false;
        } else {
            mEditEmail.setError(null);
        }

        if (phone.isEmpty()) {
            mEditContact.setError(getString(R.string.valid_msg_phone_number_required));
            valid = false;

        } else if (phone.isEmpty() || phone.length() < 7 || phone.length() > 16) {
            mEditContact.setError(getString(R.string.valid_msg_phone));
            valid = false;

        } else {
            mEditContact.setError(null);
        }


        return valid;
    }

    @Override
    protected void onPause() {
        super.onPause();
        UiHelper.hideSoftKeyboard1(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                callRegisterWithoutEvent();
                break;
            case R.id.editLocation:
                Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country (UAE) ae
                        .build(SetDeliveryLocationActivity.this);
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);

//                Intent intent = new Intent(SetDeliveryLocationActivity.this, CommonSearchActivit.class);
//                startActivityForResult(intent, 10001);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10001) {
                Bundle bundle = data.getExtras();
                Log.e("tag", "text" + bundle.getString("text"));
                Log.e("tag", "latitude" + bundle.getString("latitude"));
                Log.e("tag", "longitude" + bundle.getString("longitude"));
                if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
                    final String address = bundle.getString("text");
                    latitude = Double.valueOf(bundle.getString("latitude"));
                    longitude = Double.valueOf(bundle.getString("longitude"));
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    }
                    mEditLocation.setText(address);
                }


            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("==>", "onActivityResult: " + place.getName());
                mEditLocation.setText(String.valueOf(place.getName())); //set searched place to location field
                LatLng latLngOrderDest = place.getLatLng(); //get lat lng of destination place
                latitude = latLngOrderDest.latitude;
                longitude = latLngOrderDest.longitude;

                LatLng latLng = new LatLng(latitude, longitude);
                if (googleMap != null) {
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

    private void callRegisterWithoutEvent() {
        if (!validate()) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("name", mEditName.getText().toString().trim());
        jsonObject.addProperty("email", mEditEmail.getText().toString().trim());
        jsonObject.addProperty("contact_number", mEditContact.getText().toString().trim());
        jsonObject.addProperty("address", mEditLocation.getText().toString().trim());
        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);
        jsonObject.addProperty("language", language);
        Log.e("tag", "pare login" + jsonObject.toString());
        callRegisterWithout(jsonObject);
    }

    private void callRegisterWithout(JsonObject jsonObject) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().register_without_password(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            BaseApplication.getInstance().getSession().setIsLoggedIn();
                            BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));
                            afterLoginDialog(jsonObject1.getString("message"));
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
                    UiHelper.showNetworkError(SetDeliveryLocationActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }

    private void afterLoginDialog(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton(getResources().getString(R.string.login_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent2 = new Intent(SetDeliveryLocationActivity.this, OtpVerificationActivity.class);
                intent2.putExtra("email", mEditEmail.getText().toString().trim());
                intent2.putExtra("name", mEditName.getText().toString().trim());
                intent2.putExtra("contact_number", mEditContact.getText().toString().trim());
                intent2.putExtra("SetLocation", true);

//                boolean items=false;
//                if (getIntent().hasExtra("cart")){
//                    intent2.putExtra("viewcart",items);
//                }
//                if (getIntent().hasExtra("cartfragment")){
//                    intent2.putExtra("cartfragment",items);
//                }

                startActivity(intent2);
                finish();
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void addressRecieved(final String longAdd, final LatLng latlng, final boolean needCamera) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        latitude = latlng.latitude;
                        longitude = latlng.longitude;
                        mEditLocation.setText(longAdd);
                        Log.e("tag", "location move" + longAdd);
                        if (needCamera)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    }
                });
    }


}
