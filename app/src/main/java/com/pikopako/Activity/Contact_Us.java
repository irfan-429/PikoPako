package com.pikopako.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Contact_Us extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
        @BindView(R.id.toplinear)
        LinearLayout mSnackView;

        @BindView(R.id.btnUpdatecontact)
        Button btnUpdate;

        @BindView(R.id.edt_name)
        CustomEditTextBold edt_name;

        @BindView(R.id.edt_email)
        CustomEditTextBold edt_email;

        @BindView(R.id.edt_contact)
        CustomEditTextBold edt_contact;

        @BindView(R.id.edt_subject)
        CustomEditTextBold edt_subject;

        @BindView(R.id.edt_message)
        EditText edt_message;

        @BindView(R.id.toolbar)
        Toolbar toolbar;

        @BindView(R.id.tvTitle)
        CustomTextViewBold mTitle;

        @BindView(R.id.spinner)
        Spinner spinner;

        @BindView(R.id.general_layout)
        LinearLayout general_layout;

        @BindView(R.id.order_layout)
        LinearLayout orderLayout;

        @BindView(R.id.edt_order_id)
        CustomEditTextBold edt_order_id;

        @BindView(R.id.edt_message_orderid)
        EditText edt_message_orderid;
        String item;

        int orderid = -1;
        String language="";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.contact_us);
                ButterKnife.bind(this);
                setActionBarTitle();

                if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
                        language="German";
                }
                else
                        language="English";

                List<String> categories = new ArrayList<String>();
                categories.add(getString(R.string.general));
                categories.add(getString(R.string.cancel_order));
                categories.add(getString(R.string.order_not_received));


                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);
        }


        private void setActionBarTitle() {
                setSupportActionBar(toolbar);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("");
                mTitle.setText(getString(R.string.contact));
                btnUpdate.setOnClickListener(this);
                spinner.setOnItemSelectedListener(this);
                edt_order_id.setOnClickListener(this);

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
                        case R.id.btnUpdatecontact:

                                if (item.equalsIgnoreCase(getString(R.string.spinner_title))) {


                                        if (edt_name.getText().toString().trim().equalsIgnoreCase("")) {
                                                UiHelper.showToast(this, getString(R.string.pls_enter_name));
                                        } else if (edt_name.getText().toString().trim().length() < 3) {
                                                UiHelper.showToast(this, getString(R.string.name_should_3minimum_chrcters));
                                        } else if (edt_name.getText().toString().trim().length() > 35) {
                                                UiHelper.showToast(this, getString(R.string.name_cannot_exceed));
                                        } else if (edt_email.getText().toString().trim().equalsIgnoreCase("")) {
                                                UiHelper.showToast(this, getString(R.string.pls_enter_email));
                                        } else if (!UiHelper.isValidEmail(edt_email.getText().toString().trim())) {
                                                UiHelper.showToast(this, getString(R.string.pls_provide_valid_email));
                                        } else if (edt_contact.getText().toString().trim().equalsIgnoreCase("")) {
                                                UiHelper.showToast(this, getString(R.string.pls_enter_contact_no));
                                        } else if (edt_subject.getText().toString().trim().equalsIgnoreCase("")) {
                                                UiHelper.showToast(this, getString(R.string.pls_entr_subject));
                                        } else if (edt_message.getText().toString().trim().equalsIgnoreCase("")) {
                                                UiHelper.showToast(this, getString(R.string.pls_entr_mesage));
                                        } else
                                                senddetail();

                                } else if (item.equalsIgnoreCase(getString(R.string.cancel_order))) {
                                        callApi_cancelOrder();
                                }

                                else if (item.equalsIgnoreCase(getString(R.string.order_not_received))){
                                        callApi_Notreceived();
                                }
                                break;


                        case R.id.edt_order_id:
                                Intent intent = new Intent(this, OrderIdActivity.class);
                                intent.putExtra("order_id", orderid);
                                startActivityForResult(intent, 225);

                }
        }

        private void callApi_Notreceived() {

                String ddd = edt_order_id.getText().toString().trim();
                String[] sss = ddd.split("PIKOPAKO#");

                String order_id=String.valueOf(sss[1]);

                Log.e("TAG", "callApi_cancelOrder: "+order_id );
                String message = edt_message_orderid.getText().toString().trim();

                final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
                progressDialog.show();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("order_id", order_id);
                jsonObject.addProperty("message", message);
                jsonObject.addProperty("query_type", "Order_Not_Received");
                jsonObject.addProperty("language",language);
                Log.e("tag", "json object edit profile" + jsonObject.toString());


                Call<JsonObject> call = BaseApplication.getInstance().getApiClient().contact_us(BaseApplication.getInstance().getSession().getToken(), jsonObject);

                new NetworkController().post(this, call, new NetworkController.APIHandler() {
                        @Override
                        public void Success(Object jsonObject) {

                                progressDialog.dismiss();
                                if (jsonObject != null) {
                                        try {

                                                JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                                if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                                                        //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                                                        Log.e("cancel order", "contatc send Successfully");
                                                        UiHelper.showToast(Contact_Us.this, jsonObject1.getString("message"));
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
                                        UiHelper.showNetworkError(Contact_Us.this, mSnackView);
                                }
                                Log.e("Tag", "isConnected : " + isConnected);
                        }
                });
        }
        private void callApi_cancelOrder() {

                String ddd = edt_order_id.getText().toString().trim();
                String[] sss = ddd.split("PIKOPAKO#");

                String order_id=String.valueOf(sss[1]);

                Log.e("TAG", "callApi_cancelOrder: "+order_id );

                String message = edt_message_orderid.getText().toString().trim();

                final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
                progressDialog.show();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("order_id", order_id);
                jsonObject.addProperty("message", message);
                jsonObject.addProperty("query_type", "Cancel_Order");
                jsonObject.addProperty("language",language);
                Log.e("tag", "json object edit profile" + jsonObject.toString());


                Call<JsonObject> call = BaseApplication.getInstance().getApiClient().contact_us(BaseApplication.getInstance().getSession().getToken(), jsonObject);

                new NetworkController().post(this, call, new NetworkController.APIHandler() {
                        @Override
                        public void Success(Object jsonObject) {

                                progressDialog.dismiss();
                                if (jsonObject != null) {
                                        try {

                                                JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                                if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                                                        //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                                                        Log.e("cancel order", "contatc send Successfully");
                                                        UiHelper.showToast(Contact_Us.this, jsonObject1.getString("message"));
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
                                        UiHelper.showNetworkError(Contact_Us.this, mSnackView);
                                }
                                Log.e("Tag", "isConnected : " + isConnected);
                        }
                });
        }

        private void senddetail() {

                String name = edt_name.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String contact = edt_contact.getText().toString().trim();
                String subject = edt_subject.getText().toString().trim();
                String message = edt_message.getText().toString().trim();

                final ProgressDialog progressDialog = UiHelper.generateProgressDialog(this, false);
                progressDialog.show();

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("name", name);
                jsonObject.addProperty("email", email);
                jsonObject.addProperty("contact_number", contact);
                jsonObject.addProperty("subject", subject);
                jsonObject.addProperty("message", message);
                jsonObject.addProperty("query_type", item);
                jsonObject.addProperty("language",language);
                Log.e("tag", "json object edit profile" + jsonObject.toString());


                Call<JsonObject> call = BaseApplication.getInstance().getApiClient().contact_us(BaseApplication.getInstance().getSession().getToken(), jsonObject);

                new NetworkController().post(this, call, new NetworkController.APIHandler() {
                        @Override
                        public void Success(Object jsonObject) {

                                progressDialog.dismiss();
                                if (jsonObject != null) {
                                        try {

                                                JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                                if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {

                                                        //         BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data").toString()));
                                                        Log.e("tag", "contatc send Successfully");
                                                        UiHelper.showToast(Contact_Us.this, jsonObject1.getString("message"));
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
                                        UiHelper.showNetworkError(Contact_Us.this, mSnackView);
                                }
                                Log.e("Tag", "isConnected : " + isConnected);
                        }
                });
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                item = parent.getItemAtPosition(i).toString();

                if (item.equalsIgnoreCase(getString(R.string.cancel_order))) {

                        orderLayout.setVisibility(View.VISIBLE);
                        general_layout.setVisibility(View.GONE);




                } else if (item.equalsIgnoreCase(getString(R.string.spinner_title))) {
                        orderLayout.setVisibility(View.GONE);
                        general_layout.setVisibility(View.VISIBLE);

                }else if (item.equalsIgnoreCase(getString(R.string.order_not_received))) {
                        orderLayout.setVisibility(View.VISIBLE);
                        general_layout.setVisibility(View.GONE);
                }

                // Showing selected spinner item
               // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == 225) {


                                Bundle bundle = data.getExtras();

                                        Log.e("225 result", "onActivityResult: "+bundle.getInt("order_id"));
                                      //  orderid = data.getIntExtra("order_id", -1);
                                        orderid=bundle.getInt("order_id");
                                        edt_order_id.setText("PIKOPAKO#"+String.valueOf(orderid));

                        }
                }
        }
}