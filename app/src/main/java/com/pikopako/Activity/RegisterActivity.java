package com.pikopako.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonObject;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomEditTextBold;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;



public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    @BindView(R.id.editName)
    CustomEditTextBold mEditName;

    @BindView(R.id.editEmail)
    CustomEditTextBold mEditEmail;

    @BindView(R.id.editPass)
    CustomEditTextBold mEditPass;

    @BindView(R.id.editContact)
    CustomEditTextBold mEditContact;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    ProgressDialog progressDialog;

    String language="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        listners();


    }
    private void listners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.toolbar_title_sign_up));
        btnRegister.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;
        String email = mEditEmail.getText().toString().trim();
        String password = mEditPass.getText().toString().trim();
        String phone = mEditContact.getText().toString();
        String personName = mEditName.getText().toString();

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
        if (password.isEmpty()) {
            mEditPass.setError(getResources().getString(R.string.password_required));
            valid = false;
        } else {
            mEditPass.setError(null);
        }
        if (password.length()<6){
            mEditPass.setError(getResources().getString(R.string.password_should_be_atleast));
            valid=false;
        }else {
            mEditPass.setError(null);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                if (!validate()) {
                    return;
                }
                else
                callRegisterEvent();
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void callRegisterEvent() {
//        if (!validate()) {
//            return;
//        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("email", mEditEmail.getText().toString().trim());
        jsonObject.addProperty("password", mEditPass.getText().toString().trim());
        jsonObject.addProperty("contact_number", mEditContact.getText().toString().trim());
        jsonObject.addProperty("name", mEditName.getText().toString().trim());
        jsonObject.addProperty("language",language);
        Log.e("tag", "pare login" + jsonObject.toString());
        callToRegister(jsonObject);
    }
    private void callToRegister(JsonObject jsonObject) {
        progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().register(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject)
            {
                if (jsonObject != null) {
                    try {
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS))
                        {
                            //BaseApplication.getInstance().getSession().setIsLoggedIn();


                           // BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));



                            JsonObject jsonObject4 = new JsonObject();
                            //jsonObject.addProperty("token",BaseApplication.getInstance().getSession().getFCMToken());
                            jsonObject4.addProperty("name", mEditName.getText().toString().trim());
                            jsonObject4.addProperty("email", mEditEmail.getText().toString().trim());
                            jsonObject4.addProperty("phone_number", mEditContact.getText().toString().trim());


                            UiHelper.showToast(RegisterActivity.this,jsonObject1.getString("message"));
                            Constant.isComingFromRegister=true;

                            Intent intent2=new Intent(RegisterActivity.this,OtpVerificationActivity.class);
                            intent2.putExtra("email",mEditEmail.getText().toString().trim());
                            intent2.putExtra("name",jsonObject1.getJSONObject("data").getString("name"));
                            intent2.putExtra("contact_number",jsonObject1.getJSONObject("data").getString("contact_number"));

                            boolean items=false;
                            if (getIntent().hasExtra("cart")){
                                intent2.putExtra("viewcart",items);
                            }
                            if (getIntent().hasExtra("cartfragment")){
                                intent2.putExtra("cartfragment",items);
                            }

                            startActivity(intent2);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                            /*Intent intent=new Intent();
                              if (getIntent().hasExtra("cart")){

                                  boolean items=false;
                                  intent=new Intent(RegisterActivity.this,MainActivity.class);
                                  intent.putExtra("viewcart",items);
                                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                  intent.putExtra(Constant.IS_SIGNUP,true);

                              }
                              else if (getIntent().hasExtra("cartfragment")){
                                  Log.e("Register extra", "Success: ");
                                  boolean items=false;
                                  intent=new Intent(RegisterActivity.this,MainActivity.class);
                                  intent.putExtra("cartfragment",items);
                                  intent.putExtra(Constant.IS_SIGNUP,true);

                              }
                              else
                              {
                                   intent = new Intent(RegisterActivity.this, ConfirmLocationActivity.class);
                                  intent.putExtra(Constant.IS_SIGNUP,true);
                              }

                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/

                        }
                        else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void Error(String error) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView,error);
            }
            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(RegisterActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }




}
