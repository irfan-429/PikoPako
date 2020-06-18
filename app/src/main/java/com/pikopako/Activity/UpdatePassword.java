package com.pikopako.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class UpdatePassword extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.toplinear)
    LinearLayout mSnackView;

    @BindView(R.id.ed_old_password)
    CustomEditTextBold Edit_oldpass;

    @BindView(R.id.ed_new_password)
    CustomEditTextBold Edit_newpass;

    @BindView(R.id.ed_cnfrm_password)
    CustomEditTextBold Edit_confrmpass;

    @BindView(R.id.btnUpdate)
    Button btnUpdate;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    String language="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
        ButterKnife.bind(this);
        setActionBarTitle();
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }



    private void setActionBarTitle() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.update_password));

     //   Edit_oldpass.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
      //  Edit_newpass.setOnClickListener(this);
      //  Edit_confrmpass.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUpdate:
                if(Edit_oldpass.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this,getString(R.string.pls_enter_old_pass));
                }else if(Edit_oldpass.getText().toString().trim().length()<6) {
                    UiHelper.showToast(this, getString(R.string.old_pass_should_be_minimum));
                }else if(Edit_newpass.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this,getString(R.string.pls_enter_new_pass));
                }else if(Edit_newpass.getText().toString().trim().length()<6) {
                    UiHelper.showToast(this, getString(R.string.new_pass_should_be_minimum));
                }else if(Edit_confrmpass.getText().toString().trim().equalsIgnoreCase("")) {
                    UiHelper.showToast(this,getString(R.string.pls_enter_confirm_pass));
                }else if(Edit_confrmpass.getText().toString().trim().length()<6) {
                    UiHelper.showToast(this, getString(R.string.confirm_password_should_be_minimum));
                }else if (!Edit_newpass.getText().toString().trim().equals(Edit_confrmpass.getText().toString().trim())) {
                    UiHelper.showToast(this, getString(R.string.new_password_and_confirm_notmatch));
                }
                else {
                    updatePassword(Edit_confrmpass.getText().toString().trim(),Edit_oldpass.getText().toString().trim());
                }
                break;
        }
    }


    private void updatePassword(String new_password,String old_password) {

        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("current_password", old_password);
        jsonObject.addProperty("password", new_password);
        jsonObject.addProperty("language",language);
        Log.e("tag", "json object edit profile" + jsonObject.toString());


        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().changePassword(BaseApplication.getInstance().getSession().getToken(), jsonObject);

        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {

                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {

                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                   //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                            Log.e("tag","password Updated Successfully");
                            UiHelper.showToast(UpdatePassword.this,jsonObject1.getString("message"));
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
                    UiHelper.showNetworkError(UpdatePassword.this, mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });
    }




}
