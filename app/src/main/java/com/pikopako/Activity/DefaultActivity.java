package com.pikopako.Activity;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.Fragment.AccountFragment;
import com.pikopako.Fragment.CartFragment;
import com.pikopako.Fragment.ExploreFragment;
import com.pikopako.Fragment.NearYouFragment;
import com.pikopako.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class DefaultActivity  extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.snackView)
    CoordinatorLayout mSnackView;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    public static int navItemIndex = 0;
    public static String CURRENT_TAG = "";
    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private boolean isLogout=false;
    private GoogleMap googleMap;
    private Double latitude;
    private Double longitude;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);


    NearYouFragment nearYouFragment;
    TextView txt_cart_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.ld_activityScreenTitles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = Constant.POS_DASHBOARD;
            String address=BaseApplication.getInstance().getSession().getDeliveryAddress();
            getSupportActionBar().setTitle(address);
            loadHomeFragment();
        }


//        if (navItemIndex==0) {
//            Log.e("tag","Default activity onclick");
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        UiHelper.showToast(DefaultActivity.this,"Clicked");
//                        return false;
//                    }
//                    });
//
//
//
//            toolbar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    UiHelper.showToast(DefaultActivity.this, "Toolbar Clicked");
//                    Intent intent = new Intent(DefaultActivity.this, CommonSearchActivity.class);
//
//                    startActivityForResult(intent, 110);
//
//
//                }
//            });
//        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                    return;
                } else
                    drawer.openDrawer(GravityCompat.START);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 110) {
                Bundle bundle = data.getExtras();
                if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
                    final String address = bundle.getString("text");
                    Log.e("TAG", "onActivityResult: "+address );
                    latitude = Double.valueOf(bundle.getString("latitude"));
                    longitude = Double.valueOf(bundle.getString("longitude"));
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (nearYouFragment != null)
                        nearYouFragment.refresh(latLng,address);

                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    }

                   // getSupportActionBar().setTitle(address);
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        getSupportActionBar().setTitle(address);
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
                        if(BaseApplication.getInstance().getSession().isLoggedIn()){
                            navItemIndex = 3;
                            CURRENT_TAG = Constant.POS_ACCOUNT;
                            getSupportActionBar().setTitle(getString(R.string.POS_ACCOUNT));
                        }
                        else {
                            loginDialog();
                        }
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
//                InputMethodManager inputMethodManager = (InputMethodManager)  DefaultActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(DefaultActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)  DefaultActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(DefaultActivity.this.getCurrentFocus().getWindowToken(), 0);
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

    private void loadHomeFragment() {
        selectNavMenu();
      //  setToolbarTitle();
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
                        //    UiHelper.showToast(MainActivity.this, "Toolbar Clicked");
//                        Intent intent = new Intent(DefaultActivity.this, CommonSearchActivity.class);
//                        startActivityForResult(intent, 110);

//                        Intent intent1 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                                .setCountry(UiHelper.COUNTRY_RESTRICTION) //restriction on specific country (UAE) ae
//                                .build(DefaultActivity.this);
//                        startActivityForResult(intent1, AUTOCOMPLETE_REQUEST_CODE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this.menu = menu;
        getMenuInflater().inflate(R.menu.filter_restorents_menu, menu);
        return true;
    }


    private void setToolbarTitle() {
        if (navItemIndex==0){
            String address=BaseApplication.getInstance().getSession().getDeliveryAddress();
            getSupportActionBar().setTitle(address);

            return;
        }
      //  getSupportActionBar().setTitle(activityTitles[navItemIndex]);
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

    private void loginDialog() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getResources().getString(R.string.please_login_first_msg));
        builder.setPositiveButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            //   BaseApplication.getInstance().getSession().clearSession();
                Intent intent=new Intent(DefaultActivity.this,LocationOptionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}