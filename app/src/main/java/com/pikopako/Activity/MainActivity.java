package com.pikopako.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pikopako.Adapter.My_Offers_list_Adapter;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppDelegate.NetworkController;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.AccountFragment;
import com.pikopako.Fragment.CartFragment;
import com.pikopako.Fragment.ExploreFragment;
import com.pikopako.Fragment.NearYouFragment;
import com.pikopako.Fragment.ProgressDialog;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;


public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tool)
    CoordinatorLayout mSnackView;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private GoogleMap googleMap;
    private Double latitude;
    private Double longitude;
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = "";
    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private boolean isLogout=false;
    CustomTextViewBold waterForAfrica;
    String language="";
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

    NearYouFragment nearYouFragment;
    TextView txt_cart_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Places.initialize(this, getResources().getString(R.string.google_api_key1)); //init auto complete places API
        ButterKnife.bind(this);

        if (Locale.getDefault().getDisplayLanguage().toString().equalsIgnoreCase("Deutsch"))
            language = "German";
        else language = "English";

        setSupportActionBar(toolbar);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.ld_activityScreenTitles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getTotalDonatedWater(); //api call

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    UiHelper.applyFontToMenuItem(this,subMenuItem);
                }
            }
            //the method we have create in activity
            UiHelper.applyFontToMenuItem(this,mi);
        }
        setUpNavigationView();
        setNavigationHeader();
        loadHomeFragment();


        if (getIntent().hasExtra("viewcart")){

           Intent intent=new Intent(MainActivity.this,ViewCartActivity.class);
                       startActivity(intent);
          // finish();
        }
       else if (getIntent().hasExtra("cartfragment")){
                navItemIndex=1;
                CURRENT_TAG=Constant.POS_CART;
            getSupportActionBar().setTitle(getString(R.string.POS_TOTAL_CART));
                    loadHomeFragment();
        }

       else if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = Constant.POS_DASHBOARD;
            String address=BaseApplication.getInstance().getSession().getDeliveryAddress();
            getSupportActionBar().setTitle(address);
            loadHomeFragment();
        }



            if (navItemIndex == 0) {
            Log.e("clicked","toolbar log ");
//                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
////                        UiHelper.showToast(MainActivity.this, "Clicked");
//                        Intent nn=new Intent(MainActivity.this,FilterActivity.class);
//                       // startActivityForResult(nn,44);
//                        startActivity(nn);
//                        return false;
//                    }
//                });

            }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onclick","setnavigationonclick main activity");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    return;
                } else

                    drawer.openDrawer(GravityCompat.START);
                 overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        try {
            JSONObject profileData = new JSONObject(BaseApplication.getInstance().getSession().getProfileData());
            Log.e("Address Maintitlebar",profileData.toString());

         //   getSupportActionBar().setTitle(profileData.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public  void setNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        Log.e("set navigation", "setNavigationHeader: " );
        try {
            JSONObject jsonObject = new JSONObject(BaseApplication.getInstance().getSession().getProfileData().toString());
            CircularImageView userImage = (CircularImageView) view.findViewById(R.id.img_profile);
            CustomTextViewBold name = (CustomTextViewBold) view.findViewById(R.id.name);
            CustomTextViewBold website = (CustomTextViewBold) view.findViewById(R.id.website);
             waterForAfrica = (CustomTextViewBold) view.findViewById(R.id.donatedWaterForAfrica);
            name.setText(jsonObject.getString("name"));
            Log.e("TAG", "setNavigationHeader: "+jsonObject.toString() );
            website.setText(jsonObject.getString("email"));

            String imagUrl = (jsonObject.has("profile_image") ? jsonObject.getString("profile_image") : "");
            Glide.with(this).load(imagUrl).error(R.drawable.profileicon).into(userImage);

            //  String imagUrl = (jsonObject.has("restaurant_logo") ? jsonObject.getString("restaurant_logo") : "");
          //  Glide.with(this).load(jsonObject.getString("profile_image")).error(R.drawable.avtar_image).into(userImage);
            ImageView imgCloseDrawer = (ImageView) view.findViewById(R.id.btnClose);
            imgCloseDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer != null)
                        drawer.closeDrawers();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_pos_dashboard:
                        isLogout=false;
                        navItemIndex = 0;
                        CURRENT_TAG = Constant.POS_DASHBOARD;
                        String address=BaseApplication.getInstance().getSession().getDeliveryAddress();
                       //getSupportActionBar().setTitle(address);
                        break;
                    case R.id.menu_pos_cart:
                        isLogout=false;
                        navItemIndex = 1;
                        CURRENT_TAG = Constant.POS_CART;
                        getSupportActionBar().setTitle(getString(R.string.POS_TOTAL_CART));
                        break;
                    case R.id.menu_pos_explorer:
                        isLogout=false;
                        navItemIndex = 2;
                        CURRENT_TAG = Constant.POS_EXPLORER;
                        getSupportActionBar().setTitle(getString(R.string.POS_EXPLORE));
                        break;
                    case R.id.menu_pos_account:
                        isLogout=false;
                        navItemIndex = 3;
                        CURRENT_TAG = Constant.POS_ACCOUNT;
                        getSupportActionBar().setTitle(getString(R.string.POS_ACCOUNT));
                        break;
                    case R.id.menu_pos_logout:
                        isLogout=true;
                        logout();
                        break;
                    default:
                        navItemIndex = 0;

                }
                if(!isLogout){
                    loadHomeFragment();
                }

                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)  MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)  MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        };

        //Setting the actionbarToggle to drawer layout

        Drawable drawable = ResourcesCompat.getDrawable(getResources(),   R.mipmap.menu_icon, this.getTheme());
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        actionBarDrawerToggle.setHomeAsUpIndicator(drawable);
        drawer.setDrawerListener(actionBarDrawerToggle);


        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("tag", "onResume: " );
        setNavigationHeader();
        setCartCount();
     }

    public void setCartCount() {

        txt_cart_count=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.menu_pos_cart));


        txt_cart_count.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        txt_cart_count.setTypeface(null, Typeface.BOLD);
        txt_cart_count.setTextColor(getResources().getColor(R.color.white));

        String carts =BaseApplication.getInstance().getSession().getCartItems();
        try {
            if (carts!=null && !carts.equalsIgnoreCase("")) {
                JSONObject cart = new JSONObject(carts);
                Log.e("TAG", "Cart Items: "+cart.getString("total_items") );
                if (cart.getString("total_items").equalsIgnoreCase("0"))
                    txt_cart_count.setVisibility(View.GONE);
                else {
                    txt_cart_count.setVisibility(View.VISIBLE);
                    txt_cart_count.setText(cart.getString("total_items"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK){

            if (requestCode==110){
                Bundle bundle=data.getExtras();
                if (bundle.getString("latitude")!=null && bundle.getString("longitude")!=null){
                    final String address=bundle.getString("text");
                    Log.e("TAG", "entered Address: "+address );
                   /* if (!address.equalsIgnoreCase("")){
                        BaseApplication.getInstance().getSession().setAddress();
                    }*/
                    latitude = Double.valueOf(bundle.getString("latitude"));
                    longitude = Double.valueOf(bundle.getString("longitude"));
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (nearYouFragment!=null)
                        nearYouFragment.refresh(latLng,address);

                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    }

                  //  getSupportActionBar().setTitle("adrressss  ");
                }

            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e("==>", "onActivityResult: " + place.getName());
//                mEditLocation.setText(String.valueOf(place.getName())); //set searched place to location field
                LatLng latLngOrderDest = place.getLatLng(); //get lat lng of destination place
                latitude = latLngOrderDest.latitude;
                longitude = latLngOrderDest.longitude;
                Log.e("==>", "lat: " + latitude + " lng " + longitude);
                LatLng latLng = new LatLng(latitude, longitude);

                if (nearYouFragment != null)
                    nearYouFragment.refresh(latLng, place.getName());

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

//        if (resultCode==Activity.RESULT_OK){
//            if (requestCode==44){
//                Bundle bundle=data.getExtras();
//
//            }
//        }
    }
    private void loadHomeFragment() {
        selectNavMenu();


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        // refresh toolbar menu
        invalidateOptionsMenu();
    }



    private Fragment getHomeFragment() {
        switch (navItemIndex) {


            case 0:
                nearYouFragment = new NearYouFragment();
                setCartCount();
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                                .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country
                                .build(MainActivity.this);
                        startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);


                        //    UiHelper.showToast(MainActivity.this, "Toolbar Clicked");
//                        Intent intent = new Intent(MainActivity.this, CommonSearchActivit.class);
//                        startActivityForResult(intent, 110);
                    }
                });
                return nearYouFragment;
            case 1:
                CartFragment cartFragment = new CartFragment();
                setCartCount();
                    toolbar.setOnClickListener(null);
                return cartFragment;
            case 2:
                ExploreFragment exploreFragment = new ExploreFragment();
                setCartCount();
                toolbar.setOnClickListener(null);
                return exploreFragment;

            case 3:
                AccountFragment accountFragment = new AccountFragment();
                setCartCount();
                toolbar.setOnClickListener(null);
                return accountFragment;
            default:
                return new NearYouFragment();
        }
    }


    private void setToolbarTitle() {
        if (navItemIndex==0){
            String address=BaseApplication.getInstance().getSession().getDeliveryAddress();
       //     getSupportActionBar().setTitle(address);
                Log.e("Address Maintitlebar",address);
            return;
        }
      //  else
       // getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }
    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = Constant.POS_DASHBOARD;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.Are_u_sure_to_logout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callToLogout(dialog);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // this.menu = menu;
        getMenuInflater().inflate(R.menu.filter_restorents_menu, menu);
        return true;
    }


    private void callToLogout(final DialogInterface dialogInterface) {
        final ProgressDialog progressDialog= UiHelper.generateProgressDialog(this,false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language",language);
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().logout(BaseApplication.getInstance().getSession().getToken(),jsonObject);
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    progressDialog.dismiss();
                    try {
                        dialogInterface.dismiss();
                        JSONObject jsonObject1=new JSONObject(jsonObject.toString());
                        if(jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)){
                            BaseApplication.getInstance().getSession().clearSession();
                            UiHelper.showToast(MainActivity.this, jsonObject1.getString("message"));
                            Intent intent=new Intent(MainActivity.this,LocationOptionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finishAffinity();
                        }
                        else if (jsonObject1.getString("message").equalsIgnoreCase("Session expired.")) {
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));

                                BaseApplication.getInstance().getSession().clearSession();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finishAffinity();

                        } else{
                            UiHelper.showErrorMessage(mSnackView, jsonObject1.getString("message"));

                            BaseApplication.getInstance().getSession().clearSession();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finishAffinity();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else
                    Log.e("TAG", "Success else: " );
            }
            @Override
            public void Error(String error) {
                Log.e("Tag", "error : " + error);

                if(progressDialog!=null)
                    progressDialog.dismiss();
                UiHelper.showErrorMessage(mSnackView,error);
            }
            @Override
            public void isConnected(boolean isConnected) {
                if (!isConnected) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    UiHelper.showNetworkError(MainActivity.this,mSnackView);
                }
                Log.e("Tag", "isConnected : " + isConnected);
            }
        });

    }

    private void getTotalDonatedWater() {
        Call<JsonObject> call = BaseApplication.getInstance().getApiClient().getTotalDonatedWater(BaseApplication.getInstance().getSession().getToken());
        new NetworkController().post(this, call, new NetworkController.APIHandler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void Success(Object jsonObject) {
                if (jsonObject != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                        if (jsonObject1.getString("status").equalsIgnoreCase(Constant.SUCCESS)) {
                            waterForAfrica.setText(getString(R.string.donated_water_for_africa)+ jsonObject1.getJSONObject("data").getString("total_donated_water")+"L");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void Error(String error) {
            }

            @Override
            public void isConnected(boolean isConnected) {
            }


        });

    }


}