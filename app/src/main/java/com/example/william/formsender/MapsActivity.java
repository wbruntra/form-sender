package com.example.william.formsender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapLongClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final String TAG = MapsActivity.class.getSimpleName();

    private boolean mPermissionDenied = false;
    private boolean buttonClicked;

    public String userId;
    public JSONArray recentLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        if(intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId");
            Log.v(TAG, userId);
        } else {
            userId = "none";
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void retrieveLocations() {
        String whatsGoodURL = "http://localhost:3000/locations/json";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(whatsGoodURL)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Call failed!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonString = response.body().string();
                    try {
                        JSONArray entries = new JSONArray(jsonString);
                        recentLocations = entries;
                        populateMap(recentLocations);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "There was an error");
                }
            }
        });
    }

    private void populateMap(JSONArray entries) throws JSONException {
        for(int n=0;n< entries.length(); n++) {
            JSONObject entry = entries.getJSONObject(n);
            Double lat = entry.getDouble("lat");
            Double lng = entry.getDouble("lng");
            final String placeName = entry.getString("name");
            final LatLng placeGPS = new LatLng(lat, lng);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.addMarker(new MarkerOptions().position(placeGPS).title(placeName));
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buttonClicked = false;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapLongClickListener(this);
        enableMyLocation();
        retrieveLocations();

        LatLng cdmx = new LatLng(19.41, -99.16);
        float zoomLevel = (float) 13.0;
//        mMap.addMarker(new MarkerOptions().position(cdmx).title("Marker in Mexico"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cdmx, zoomLevel));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // (the camera animates to the user's current position).
                float zoomLevel = (float) 17.0;
                if (buttonClicked) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                    buttonClicked = false;
                }
            }
        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        float zoomLevel = (float) 15.0;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        buttonClicked = true;
        return false;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Double lat = point.latitude;
        Double lng = point.longitude;

        if (!Objects.equals(userId, "none")) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra("clickedLat", lat);
            intent.putExtra("clickedLng", lng);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            Log.v(TAG,"Click from anonymous user");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
