package com.pikopako.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.pikopako.Adapter.PlaceHistoryAdapter;
import com.pikopako.AppUtill.CustomAutoCompleteTextView;
import com.pikopako.AppUtill.CustomTextViewBold;
import com.pikopako.AppUtill.UiHelper;
import com.pikopako.LocalStorage.DatabaseHandler;
import com.pikopako.Model.AddressModel;
import com.pikopako.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class CommonSearchActivity extends BaseActivity implements PlaceHistoryAdapter.OnSelectAddress, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "CommonSearchActivity";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD2h4Q1l4Q2ENW-l5ouOEW1uRFbZuAtP9c";

    @BindView(R.id.search_box)
    CustomAutoCompleteTextView search_box;

    @BindView(R.id.placeRecycleview)
    RecyclerView placeRecycleview;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvTitle)
    CustomTextViewBold mTitle;

    ArrayList<AddressModel> resultList = null;
    PlaceHistoryAdapter placeHistoryAdapter;
    private ArrayList<AddressModel> addressList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    String latitude, longitude, address, locationId;
    private String locLatLng;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place myPlace = places.get(0);
            LatLng latlangObj = myPlace.getLatLng();
            latitude = String.valueOf(latlangObj.latitude);
            longitude = String.valueOf(latlangObj.longitude);
            Log.e("latitude:", "" + latitude);
            Log.v("longitude:", "" + longitude);
            new DownloadFilesTask().execute();
            Intent intent = new Intent();
            intent.putExtra("text", address);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            setResult(Activity.RESULT_OK, intent);
            CommonSearchActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_search);
        ButterKnife.bind(this);
        setActionBar();
        placeRecycleview.setLayoutManager(new LinearLayoutManager(this));
        search_box.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UiHelper.hideSoftKeyboard1(CommonSearchActivity.this);
                AddressModel addressModel = resultList.get(position);
                address = addressModel.getAddress();
                locationId = addressModel.getLocationId();
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, locationId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        notifyAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UiHelper.hideSoftKeyboard1(this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void notifyAdapter() {
        final DatabaseHandler db = new DatabaseHandler(this);
        List<AddressModel> addressModels = db.getAllAddress();
        for (AddressModel cn : addressModels) {
            AddressModel addressModel = new AddressModel();
            addressModel.setAddress(cn.getAddress());
            addressModel.setLatitude(cn.getLatitude());
            addressModel.setLongitude(cn.getLongitude());
            addressModel.setLocationId(cn.getLocationId());
            ;
            addressList.add(addressModel);
        }
        placeHistoryAdapter = new PlaceHistoryAdapter(this, addressList, CommonSearchActivity.this);
        placeRecycleview.setAdapter(placeHistoryAdapter);
    }

    @Override
    public void selectAddress(AddressModel address) {
        Intent intent = new Intent();
        intent.putExtra("text", address.getAddress());
        intent.putExtra("latitude", address.getLatitude());
        intent.putExtra("longitude", address.getLongitude());
        setResult(Activity.RESULT_OK, intent);
        CommonSearchActivity.this.finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class DownloadFilesTask extends AsyncTask<String, String, Boolean> {
        DatabaseHandler db;

        public DownloadFilesTask() {
            db = new DatabaseHandler(CommonSearchActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean exist = false;

            List<AddressModel> addressModels = db.getAllAddress();
            for (AddressModel cn : addressModels) {
                Log.e("aa", "doInBackground" + cn.getLocationId().toString());
                Log.e("aa", "doInBackground" + locationId.toString());
                if (cn.getLocationId().equalsIgnoreCase(locationId)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                db.addSAddress(new AddressModel(locationId, address, latitude, longitude));
            }
            return null;
        }
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_icon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mTitle.setText(getString(R.string.toolbar_title_set_location));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                /*Intent intent = new Intent();
                intent.putExtra("text", "");
                intent.putExtra("latitude", "");
                intent.putExtra("longitude", "");
                setResult(Activity.RESULT_OK, intent);
                CommonSearchActivity.this.finish();*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList autocomplete(String input) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(UiHelper.PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + getString(R.string.google_api_key1));
            //sb.append("&types=(regions)");
      //      sb.append("&components=country:de|country:in|country:us");
           // sb.append("&components=country:in");
            sb.append("&components=country:DE");
        //    sb.append("&components=geocode");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.e("place_data", "" + jsonObj.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject jsonObject = predsJsonArray.getJSONObject(i);
                AddressModel addressModel = new AddressModel();
                addressModel.setAddress(jsonObject.getString("description"));
                addressModel.setLocationId(jsonObject.getString("place_id"));
                resultList.add(addressModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<AddressModel> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int searchadrsrow) {
            super(context, searchadrsrow);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            //AddressModel addressModel=resultList.get(position).getAddress();
            return resultList.get(position).getAddress();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}


