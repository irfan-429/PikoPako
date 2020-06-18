package com.pikopako.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pikopako.AppDelegate.BaseApplication;
import com.pikopako.AppUtill.Constant;
import com.pikopako.AppUtill.GPSTracker;
import com.pikopako.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LocationOptionActivity extends BaseActivity {

    @BindView(R.id.view_signin)
    LinearLayout view_signin;

    @BindView(R.id.btnLocation)
    Button btnLocation;

    @BindView(R.id.tvCreateAccount)
    Button tvCreateAccount;

    ArrayList<String> permissionToAsk=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_location);
        ButterKnife.bind(this);



        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   askPermissions();
                if (BaseApplication.getInstance().getSession().getExit().equalsIgnoreCase("Exit")){


                Intent intent= new Intent(LocationOptionActivity.this, ConfirmLocationActivity.class);
                intent.putExtra(Constant.IS_SIGNUP,false);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
            else askPermissions();

            }
        });
        view_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BaseApplication.getInstance().getSession().getExit().equalsIgnoreCase("Exit")){


                    Intent intent= new Intent(LocationOptionActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
                else askPermissionsForSignin();



            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (BaseApplication.getInstance().getSession().getExit().equalsIgnoreCase("Exit")){


                    Intent intent= new Intent(LocationOptionActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
                else askPermissionsForCreate();

            }
        });
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
                Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();
                Intent intent= new Intent(LocationOptionActivity.this, ConfirmLocationActivity.class);
                intent.putExtra(Constant.IS_SIGNUP,false);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void showDialogForSignin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gpsTitle));
        builder.setMessage(getString(R.string.gpsMessage));
        builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();
                Intent intent= new Intent(LocationOptionActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDialogForCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gpsTitle));
        builder.setMessage(getString(R.string.gpsMessage));
        builder.setPositiveButton(getResources().getString(R.string.settingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exitButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseApplication.getInstance().getSession().setExit("Exit");
                dialog.dismiss();
                Intent intent= new Intent(LocationOptionActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);



            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    private void askPermissions(){
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission)
        {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED){
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: " );
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions((Activity) this, permissionToAsk.toArray(new String[permissionToAsk.size()]), Constant.requestcodeForPermission);
        } else{
            Log.e("tag", "else access askPermissions: " );
            accessPermission();
        }
    }

    private void askPermissionsForSignin(){
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission)
        {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED){
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: " );
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions((Activity) this, permissionToAsk.toArray(new String[permissionToAsk.size()]), 105);
        } else{
            Log.e("tag", "else access askPermissions: " );
            accessPermissionForSignin();
        }
    }

    private void askPermissionsForCreate(){
        permissionToAsk.clear();
        for (String s : Constant.askForLocationPermission)
        {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED){
                permissionToAsk.add(s);
                Log.e("tag", "if askPermissions: " );
            }

        }
        if (!permissionToAsk.isEmpty()) {
            Log.e("tag", "if empty askPermissions: ");
            ActivityCompat.requestPermissions((Activity) this, permissionToAsk.toArray(new String[permissionToAsk.size()]), 106);
        } else{
            Log.e("tag", "else access askPermissions: " );
            accessPermissionForCreate();
        }
    }


    private void accessPermission() {
        GPSTracker gpsTracker=new GPSTracker(LocationOptionActivity.this);
        if(gpsTracker.canGetLocation()){
            Log.e("tag", "if can get locationaccessPermission: " );
            Intent intent= new Intent(LocationOptionActivity.this, ConfirmLocationActivity.class);
            intent.putExtra(Constant.IS_SIGNUP,false);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }
        else {
            Log.e("tag", "else cannot get locationaccessPermission: " );
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
            showDialog();
            Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

        }
    }

    private void accessPermissionForSignin() {
        GPSTracker gpsTracker=new GPSTracker(LocationOptionActivity.this);
        if(gpsTracker.canGetLocation()){

            Intent intent= new Intent(LocationOptionActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        }
        else {
            Log.e("tag", "else cannot get locationaccessPermission: " );
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
            showDialogForSignin();
            Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

        }
    }

    private void accessPermissionForCreate() {
        GPSTracker gpsTracker=new GPSTracker(LocationOptionActivity.this);
        if(gpsTracker.canGetLocation()){

            Intent intent= new Intent(LocationOptionActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        }
        else {
            Log.e("tag", "else cannot get locationaccessPermission: " );
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
            showDialogForCreate();
            Log.e("tag","delivery_address :"+ BaseApplication.getInstance().getSession().getDeliveryAddress());

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToAsk.clear();
        if (requestCode==Constant.requestcodeForPermission){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: " );
                    allGranted = false;

                }
            }
            if (allGranted){
                Log.e("tag", "All grantednRequestPermissionsResult: " );
                accessPermission();
            }
            else {
              //  finish();
                BaseApplication.getInstance().getSession().setExit("Exit");
                Intent intent= new Intent(LocationOptionActivity.this, ConfirmLocationActivity.class);
                intent.putExtra(Constant.IS_SIGNUP,false);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        }

        if (requestCode==105){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: " );
                    allGranted = false;

                }
            }
            if (allGranted){
                Log.e("tag", "All grantednRequestPermissionsResult: " );
                accessPermissionForSignin();
            }
            else {
                //  finish();
                BaseApplication.getInstance().getSession().setExit("Exit");
                Intent intent= new Intent(LocationOptionActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        }

        if (requestCode==106){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
                    Log.e("tag", "Permission denied onRequestPermissionsResult: " );
                    allGranted = false;

                }
            }
            if (allGranted){
                Log.e("tag", "All grantednRequestPermissionsResult: " );
                accessPermissionForCreate();
            }
            else {
                //  finish();
                BaseApplication.getInstance().getSession().setExit("Exit");
                Intent intent= new Intent(LocationOptionActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        }
    }
}
