package com.example.william.formsender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    EditText nameTextField;
    EditText priceTextField;
    Spinner catSpinner;
    ImageView mapImageView;

    Double lat;
    Double lng;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;


//    protected String mLatitudeLabel;
//    protected String mLongitudeLabel;
//    protected TextView mLatitudeText;
//    protected TextView mLongitudeText;

    Button sendButton;
    String userId;

    User mUser;

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");


    public static final String TAG = FormActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);

        mUser = (User) getApplicationContext();

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("clickedLat", 19);
        lng = intent.getDoubleExtra("clickedLng", -99);
        userId = mUser.getUserId();

        Log.v(TAG, "Clicked from "+lat+","+lng);

//        mLatitudeLabel = getResources().getString(R.string.latitude_label);
//        mLongitudeLabel = getResources().getString(R.string.longitude_label);
//        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
//        mLongitudeText = (TextView) findViewById((R.id.longitude_text));

        nameTextField = (EditText) findViewById(R.id.nameTextField);
        priceTextField = (EditText) findViewById(R.id.priceTextField);
        catSpinner = (Spinner) findViewById(R.id.catSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        catSpinner.setAdapter(adapter);

        sendButton = (Button) findViewById(R.id.sendButton);

//        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//
//            String address = "http://localhost:3000/";
//            String locationString = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
//
////            lat = mLastLocation.getLatitude();
////            lng = mLastLocation.getLongitude();
//
//            Log.v(TAG, locationString);
//
////            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
////                    mLastLocation.getLatitude()));
////            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
////                    mLastLocation.getLongitude()));
//        } else {
//            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void send(View v) {
        Log.v(TAG, "Send button clicked");
        String sendURL = "http://localhost:3000/locations/android";

        String name = nameTextField.getText().toString();
        String price = priceTextField.getText().toString();
        String cat = catSpinner.getSelectedItem().toString();

        JSONObject jsonForm = new JSONObject();

        try {
            jsonForm.put("name", name);
            jsonForm.put("price", price);
            jsonForm.put("category", cat);
            jsonForm.put("lat",lat);
            jsonForm.put("lng",lng);
            jsonForm.put("creatorId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(sendURL)
                .post(RequestBody.create(MEDIA_TYPE_JSON, jsonForm.toString()))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.v(TAG, response.body().string());
                } else {
                    Log.i(TAG, "There was an error");
                }
            }
        });

    }
}
