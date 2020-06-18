package com.pikopako.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pikopako.Activity.Contact_Us;
import com.pikopako.Activity.EditActivity;
import com.pikopako.Activity.LocationOptionActivity;
import com.pikopako.Activity.LoginActivity;
import com.pikopako.Activity.MainActivity;
import com.pikopako.Activity.My_Favourites_list;
import com.pikopako.Activity.My_offers;
import com.pikopako.Activity.My_orders;
import com.pikopako.Activity.UpdatePassword;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.CustomTextViewNormal;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class AccountFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.tvContact)
    CustomTextViewBold tvContact;

    @BindView(R.id.tvEmail)
    CustomTextViewBold tvEmail;

    @BindView(R.id.imageProfile)
    CircularImageView imageProfile;

    @BindView(R.id.tvName)
    CustomTextViewBold tvName;

    @BindView(R.id.snackView)
    LinearLayout mSnackView;

    @BindView(R.id.Txt_edit)
    CustomTextViewBold Txt_edt;


    @BindView(R.id.Txt_my_favourites)
    CustomTextViewNormal Txt_my_favourite;

    @BindView(R.id.Txt_my_orders)
    CustomTextViewNormal Txt_my_orders;

    @BindView(R.id.Txt_offers)
    CustomTextViewNormal Txt_offers;

    @BindView(R.id.Txt_logout)
    Button Txt_logout;

    @BindView(R.id.Txt_change_password)
    CustomTextViewNormal Txt_change_password;

    @BindView(R.id.Txt_contact)
    CustomTextViewNormal Txt_contact;
    ProgressDialog progressDialog;
    String language="";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_my_account, container, false);
        ButterKnife.bind(this, view);
        Txt_edt.setOnClickListener(this);
        Txt_change_password.setOnClickListener(this);
        Txt_my_favourite.setOnClickListener(this);
        Txt_my_orders.setOnClickListener(this);
        Txt_offers.setOnClickListener(this);
        Txt_logout.setOnClickListener(this);
        Txt_contact.setOnClickListener(this);


        //  imageProfile.setImageBitmap(BaseApplication.getInstance().getSession().getUserCustomerProfileImage());
        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.e("TAG", "onStart: " );
//        getUserInfo();
//    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    //    getUserInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch")){
            language="German";
        }
        else
            language="English";
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_filter);
        menu.clear();
    }

    private void getUserInfo() {
        final ProgressDialog progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getUserDetail(BaseApplication.getInstance().getSession().getToken());
        new NetworkController().get(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                progressDialog.dismiss();
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            JSONObject data = jsonObject1.getJSONObject("data");
                            tvContact.setText(data.getString("contact_number"));
                            tvEmail.setText(data.getString("email"));
                            tvName.setText(data.getString("name"));
                            if (isAdded()) {
                                Glide.with(getActivity()).
                                        load(data.getString("profile_image")).
                                        asBitmap().
                                        error(R.drawable.profileicon)
                                        .into(imageProfile);
                            }

                            BaseApplication.getInstance().getSession().setProfileData(String.valueOf(jsonObject1.getJSONObject("data")));
                            Log.e("PRofile Data", "Success: "+BaseApplication.getInstance().getSession().getProfileData() );
                            ((MainActivity)getActivity()).setNavigationHeader();
                            Log.e("json Data user profile", "" + data.toString());


                        } else {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));
                            if (jsonObject1.getString("error_code").equalsIgnoreCase("delete_user")) {
                                BaseApplication.getInstance().getSession().clearSession();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                 startActivity(intent);
                                 getActivity().finish();
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
                UiHelper.showErrorMessage(mSnackView, error);
            }

            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(getActivity(), mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Txt_edit:
                // UiHelper.showErrorMessage(mSnackView,"Under working");
                Intent intent = new Intent(getActivity(), EditActivity.class);

                startActivity(intent);
                break;

            case R.id.Txt_my_favourites:
                Intent inte = new Intent(getActivity(), My_Favourites_list.class);
                startActivity(inte);
                break;
            case R.id.Txt_my_orders:
                Intent nn1 = new Intent(getActivity(), My_orders.class);
                startActivity(nn1);
                break;
            case R.id.Txt_offers:
                Intent nn = new Intent(getActivity(), My_offers.class);
                startActivity(nn);
                break;
            case R.id.Txt_logout:
                logout();
                break;

            case R.id.Txt_change_password:
                Intent intent1 = new Intent(getActivity(), UpdatePassword.class);
                startActivity(intent1);
                break;

            case R.id.Txt_contact:
                Intent in = new Intent(getActivity(), Contact_Us.class);
                startActivity(in);
                break;
        }
    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.Are_u_sure_to_logout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e("tag", "delivery_address :" + BaseApplication.getInstance().getSession().getDeliveryAddress());
                if (!BaseApplication.getInstance().getSession().getDeliveryAddress().equalsIgnoreCase("")) {
                    BaseApplication.getInstance().getSession().clearSession();
                    Intent intent = new Intent(getActivity(), LocationOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    // overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    //finish();

                } else {
                    callToLogout(dialog);
                }
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
    }

    private void callToLogout(final DialogInterface dialogInterface) {
        progressDialog = UiHelper.generateProgressDialog(getActivity(), false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("language",language);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().logout(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(getActivity(), call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    progressDialog.dismiss();
                    try {
                        dialogInterface.dismiss();
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            BaseApplication.getInstance().getSession().clearSession();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                              getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                             getActivity().finishAffinity();
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
                    UiHelper.showNetworkError(getActivity(), mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }
}
