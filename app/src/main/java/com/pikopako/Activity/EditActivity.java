package com.pikopako.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.CustomTextInputLayout;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.ImagePicker;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class EditActivity extends BaseActivity implements View.OnClickListener {
    public final static String[] permissionForProfile = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PICK_IMAGE_ID = 10;
    private static final int requestcode_Camera_Permission = 11;
    @BindView(R.id.snackView)
    LinearLayout mSnackView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;
    @BindView(R.id.textinput_name)
    CustomTextInputLayout textinput_name;

    //    @BindView(R.id.editshippingaddress)
//    CustomEditTextBold editshippingaddress;
    @BindView(R.id.textinput_phoneno)
    CustomTextInputLayout textinput_phoneno;
    @BindView(R.id.textinput_addres)
    CustomTextInputLayout getTextinput_address;
    @BindView(R.id.editname)
    CustomEditTextBold editname;
    @BindView(R.id.editaddress)
    CustomEditTextBold editaddress;
    @BindView(R.id.editcontactno)
    CustomEditTextBold editcontactno;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.imageProfile)
    CircularImageView profile_img;
    @BindView(R.id.imageView)
    CircularImageView imageView;
    Bitmap bm;
    byte[] profile_img_EncodeByte = null;
    ArrayList<String> permissionToAsk = new ArrayList<>();
    String language = "";
    private GoogleMap googleMap;
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);


    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_account);
        Places.initialize(this, getResources().getString(R.string.google_api_key1)); //init auto complete places API
        ButterKnife.bind(this);
        setActionBarTitle();

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")) {
            language = "German";
        } else
            language = "English";

        calltoData();
    }

    private void calltoData() {
        try {
            JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
            Log.e("tag profiledata", jsonObject.toString());

            if (jsonObject.getString("latitude")!=null && !jsonObject.getString("latitude").equalsIgnoreCase("") && !jsonObject.getString("latitude").equalsIgnoreCase("null")) {
                latitude = jsonObject.getDouble("latitude");
                longitude = jsonObject.getDouble("longitude");
            }
            editname.setText(jsonObject.getString("name"));
            if (!jsonObject.getString("address").equalsIgnoreCase("null"))
            editaddress.setText(jsonObject.getString("address"));
            editcontactno.setText(jsonObject.getString("contact_number"));
            //  editshippingaddress.setText(jsonObject.getString("shipping_address"));

//

            if (!jsonObject.getString("profile_image").equalsIgnoreCase("")) {
                Glide.with(this).
                        load(jsonObject.getString("profile_image")).
                        into(imageView);
            } else {

                imageView.setImageDrawable(getResources().getDrawable(R.drawable.profileicon));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.toolbar_title_manage_profile));

        editaddress.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        profile_img.setOnClickListener(this);
        imageView.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editaddress:
                Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country (UAE) ae
                        .build(this);
                startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);

//                Intent intent = new Intent(EditActivity.this, CommonSearchActivit.class);
//                startActivityForResult(intent, 110);
                break;

            case R.id.btnUpdate:
                if (!validate()) {
                    return;
                }
                    try {
                        JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
                        Log.e("json user id", "" + jsonObject.toString());
                      if (profile_img_EncodeByte!=null) {
                          int customer_id = jsonObject.getInt("user_id");
                          //  if (bm !=null)
                          updateUserProfileImage(customer_id, bm);
                          //   UpdateApi();
                      }else {
                          UpdateApi();

                      }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                break;

            case R.id.imageProfile:
                getProfilePic();
                break;
            //   Intent chooseImageIntent = ImagePicker.getPickImageIntent(EditActivity.this);
            //   startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            //    chooseImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            case R.id.imageView:
                getProfilePic();
                break;
        }
    }

    public boolean validate(){
        boolean valid = true;
        String name = editname.getText().toString().trim();
        String contact = editcontactno.getText().toString().trim();
        String address = editaddress.getText().toString().trim();

        if (name.isEmpty()) {
            editname.setError(getResources().getString(R.string.pls_enter_name));
            valid = false;
        }else {
            editname.setError(null);
        }
        if (contact.isEmpty()) {
            editcontactno.setError(getResources().getString(R.string.pls_enter_contact_no));
            valid = false;
        } else {
            editcontactno.setError(null);
        }
        if (address.isEmpty()){
            editaddress.setError(getResources().getString(R.string.pls_enter_address));
            valid = false;
        }
        else {
            editaddress.setError(null);
        }
        return valid;
    }

    private void getProfilePic() {

        permissionToAsk.clear();
        for (String s : permissionForProfile) {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED)
                permissionToAsk.add(s);
        }
        if (!permissionToAsk.isEmpty())
            ActivityCompat.requestPermissions(this, permissionToAsk.toArray(new String[permissionToAsk.size()]), requestcode_Camera_Permission);
        else {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
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
//                    editaddress.setText(address);
//                }
//
//            }
//
//
//        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("==>", "onActivityResult: " + place.getName());
                LatLng latLngOrderDest = place.getLatLng(); //get lat lng of destination place
                latitude = latLngOrderDest.latitude;
                longitude = latLngOrderDest.longitude;
                Log.e("==>", "lat: " + latitude + " lng " + longitude);

                String address= UiHelper.getAddress(this, latitude, longitude);
                editaddress.setText(address); //set searched place to location field

                //save to pref
                BaseApplication.getInstance().getSession().setProfileLat(String.valueOf(latitude));
                BaseApplication.getInstance().getSession().setProfileLng(String.valueOf(longitude));
                BaseApplication.getInstance().getSession().setProfileLoc(address);

                LatLng latLng = new LatLng(latitude, longitude);
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }

            }

            Log.i("placesLat", String.valueOf(latitude));
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e("places", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }


        if (requestCode == PICK_IMAGE_ID) {
            Log.e("EditActivity", "onActivityResult: " + data);

            bm = ImagePicker.getImageFromResult(this, resultCode, data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            profile_img_EncodeByte = baos.toByteArray();
            byte[] bitmapdata = baos.toByteArray();
            String image_str = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
            byte[] encodeByte = Base64.decode(image_str, Base64.DEFAULT);

            bm = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //  profile_img.setImageBitmap(bm);


            imageView.setImageBitmap(bm);

//            if (bm != null) {
//                bm = Bitmap.createScaledBitmap(bm, 300, 300, true);
//                profile_img.setImageBitmap(bm);
//                Log.e("EditActivity", "onActivityResult bm: "+bm );
//              //  updateUserProfileImage(BaseApplication.getInstance().getSession().getID(),bm);
//            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode == requestcode_Camera_Permission) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    allGranted = false;
            }
            if (allGranted) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        }
    }

    private void updateUserProfileImage(int customer_id, Bitmap bm) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(EditActivity.this, false);
        progressDialog.show();
//        String imgString = UiHelper.convertBase64String(bm);
//        StringBuilder sb = new StringBuilder();
//        sb.append("data:image/jpeg;base64,");
//        sb.append(imgString);


//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        profile_img_EncodeByte = byteArrayOutputStream.toByteArray();
//
//        // System.out.println("String buffer is " + sb.toString() + " " + sb.length());

        //  JsonObject jsonObject = new JsonObject();
        // jsonObject.addProperty("user_id", String.valueOf(customer_id));
        //  jsonObject.addProperty("profile_image", sb.toString());


        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("user_id", String.valueOf(customer_id));
        builder.addFormDataPart("language", language);
        builder.addFormDataPart("profile_image", "Profile_image.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), profile_img_EncodeByte));
        Log.e("profile byte ", "" + builder.toString());

        final RequestBody requestBody = builder.build();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().UpdateUserProfileImage(BaseApplication.getInstance().getSession().getToken(), requestBody);
        new NetworkController().get(EditActivity.this, call, new NetworkController.APIHandler() {

            @Override
            public void Success(Object jsonObject) {
                if (progressDialog != null)
                    progressDialog.dismiss();

                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            String customerImage = jsonObject1.has("profile_image") ? jsonObject1.getString("profile_image") : "";
                            BaseApplication.getInstance().getSession().setCustomerProfileImage(customerImage);


                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));

                            Log.e("tagprofileimage", "profile_image sucess" + BaseApplication.getInstance().getSession().getProfileData());
                            UpdateApi();
                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                                Intent intent = new Intent(EditActivity.this, LoginActivity.class);
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
                Log.e("error", "error profileimage api");
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {

                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(EditActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);


            }

        });
    }


    private void UpdateApi() {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", editname.getText().toString().trim());
        jsonObject.addProperty("contact_number", editcontactno.getText().toString().trim());
        jsonObject.addProperty("address", editaddress.getText().toString().trim());
        //  jsonObject.addProperty("shipping_address", editshippingaddress.getText().toString().trim());
        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);
        jsonObject.addProperty("language", language);
        Log.e("tag", "json object edit profile" + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().editProfile(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
//                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra(Constant.IS_SIGNUP, true);
//                            startActivity(intent);
                            //     finish();
                            //   overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e("tag", "Profile Updated Successfully" + BaseApplication.getInstance().getSession().getProfileData());
                            UiHelper.showToast(EditActivity.this, jsonObject1.getString("message"));
                            finish();
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
                    UiHelper.showNetworkError(EditActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }


}