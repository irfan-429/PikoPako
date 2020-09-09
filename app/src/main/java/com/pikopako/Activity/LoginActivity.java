package com.pikopako.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.snackView)
    RelativeLayout mSnackView;

    @BindView(R.id.editEmail)
    CustomEditTextBold mEditEmail;

    @BindView(R.id.editPass)
    CustomEditTextBold mEditPass;

    @BindView(R.id.view_forgot)
    LinearLayout mForgotPassword;

    @BindView(R.id.btnLogin)
    Button mBtnLogin;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    @BindView(R.id.txt_signup)
    CustomTextViewBold txt_signup;

    @BindView(R.id.layout_signup)
    LinearLayout layout_signup;

    private String isEmailVerified;

    String language = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        listners();
        Log.e("language", "onCreate: " + Locale.getDefault().getDisplayLanguage().toString());



        if (getIntent().hasExtra("cart") || getIntent().hasExtra("cartfragment")) {
            layout_signup.setVisibility(View.VISIBLE);
        } else
            layout_signup.setVisibility(View.GONE);
    }

    private void listners() {
        mTitle.setText(getString(R.string.toolbar_title_login_in));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mBtnLogin.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        txt_signup.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;
        String email = mEditEmail.getText().toString().trim();
        String password = mEditPass.getText().toString().trim();
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

        return valid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_forgot:
                forgotPasswordView();
                break;
            case R.id.btnLogin:
                callLoginEvent();
                break;
            case R.id.txt_signup:
                boolean item = false;
                Intent nntt = new Intent(this, RegisterActivity.class);
                if (getIntent().hasExtra("cart"))
                    nntt.putExtra("cart", item);
                else
                    nntt.putExtra("cartfragment", item);
                startActivity(nntt);
        }
    }

    private void forgotPasswordView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("ok", null);
        builder.setNegativeButton(R.string.cancel, null);
        LinearLayout layout = new LinearLayout(this);
        TextView title = new TextView(this);
        final EditText editText = new EditText(this);

        title.setText(R.string.string_forgot_your_password);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        title.setTextColor(getResources().getColor(R.color.black));
        editText.setSingleLine();
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        editText.setHint(R.string.string_enter_your_email);
        title.setPadding(0, 0, 0, 5);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(title);
        layout.addView(editText);
        layout.setPadding(50, 40, 50, 10);
        builder.setView(layout);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setIcon(R.mipmap.pikopako_logo);
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setTextColor(getResources().getColor(R.color.black));
                Button nagative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nagative.setTextColor(getResources().getColor(R.color.black));
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String email = editText.getText().toString();
                        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            UiHelper.showErrorMessage(mSnackView, getString(R.string.valid_msg_email));
                            return;
                        } else {
                            callToForgot(dialog, email);
                        }

                    }
                });
                nagative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.dismiss();

                        Log.e("", "hide keyboards");
                    }
                });
            }
        });
        mAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        mAlertDialog.show();
    }

    private void callToForgot(final DialogInterface dialogInterface, final String email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        Log.e("tag", "callToForgot: " + language);
        jsonObject.addProperty("language", language);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().fotgotPassword(jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        dialogInterface.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
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
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    UiHelper.showNetworkError(LoginActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

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

    private void callLoginEvent() {
        if (!validate()) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", BaseApplication.getInstance().getSession().getFCMToken());
        jsonObject.addProperty("device_token", BaseApplication.getInstance().getSession().getDeviceToken());
        jsonObject.addProperty("device_type", Constant.DIVICE_TYPE);
        jsonObject.addProperty("email", mEditEmail.getText().toString().trim());
        jsonObject.addProperty("password", mEditPass.getText().toString().trim());
        Log.e("tag", "callLoginEvent: " + language);
        jsonObject.addProperty("language", language);
        Log.e("tag", "pare login" + jsonObject.toString());
        callToLogin(jsonObject);

    }

    private void callToLogin(JsonObject jsonObject) {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().login(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        Log.e("login ", "jsonObject1: " + jsonObject1);

                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            //BaseApplication.getInstance().getSession().setIsLoggedIn();
                            BaseApplication.getInstance().getSession().setToken(jsonObject1.getJSONObject("data").getString("token"));
                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));
                            Intent intent = new Intent();

                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");




//                            Log.e("TAG", "latitude succes me: "+jsonObject2.getString("latitude") );
                            if (jsonObject2.getString("latitude").equalsIgnoreCase("null") && BaseApplication.getInstance().getSession().getDeliveryLatitude().isEmpty()) {
                                intent = new Intent(LoginActivity.this, ConfirmLocationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constant.IS_SIGNUP, true);
                            } else {
                                //save profile loc data
                                BaseApplication.getInstance().getSession().setProfileLat(jsonObject2.getString("latitude"));
                                BaseApplication.getInstance().getSession().setProfileLng(jsonObject2.getString("longitude"));
                                BaseApplication.getInstance().getSession().setProfileLoc(jsonObject2.getString("address"));
                            }


                            BaseApplication.getInstance().getSession().setIsLoggedIn();

                            if (getIntent().hasExtra("cart")) {
                                boolean items = false;
                                Log.e("TAG", "Login ViewCart ");
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("viewcart", items);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constant.IS_SIGNUP, true);

                            } else if (getIntent().hasExtra("cartfragment")) {
                                Log.e("login extra", "Success: ");
                                boolean items = false;
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("cartfragment", items);
                                intent.putExtra(Constant.IS_SIGNUP, true);
                            } else {
                                Log.e("TAG", "Login Simple ");
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constant.IS_SIGNUP, true);
                            }

                            startActivity(intent);
                            finish();
                            Log.e("tag", "data:-" + jsonObject1.getJSONObject("data"));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                        } else if (jsonObject1.getString("error_code").equalsIgnoreCase("otp_screen")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle(getString(R.string.app_name));
                            builder.setMessage(getResources().getString(R.string.your_account_is_not_verified));
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                                    Constant.isComingFromLogin = true;
                                    intent.putExtra("email", mEditEmail.getText().toString().trim());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


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
                    UiHelper.showNetworkError(LoginActivity.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }
}
