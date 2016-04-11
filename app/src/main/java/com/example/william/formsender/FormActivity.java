package com.example.william.formsender;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    EditText nameTextField;
    EditText priceTextField;
    EditText descriptionText;
    Spinner catSpinner;
    ImageView mapImageView;

    private ProgressDialog mProgressDialog;

    Double lat;
    Double lng;

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

        nameTextField = (EditText) findViewById(R.id.nameTextField);
        priceTextField = (EditText) findViewById(R.id.priceTextField);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
        catSpinner = (Spinner) findViewById(R.id.catSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        catSpinner.setAdapter(adapter);

        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                send(v);
            }
        });

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.sending));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void send(View v) {
        Log.v(TAG, "Send button clicked");
        v.setEnabled(false);

        String sendURL = "http://pacific-castle-63467.herokuapp.com/locations/android";
//        String sendURL = "http://localhost:3000/locations/android";

        String name = nameTextField.getText().toString();
        String price = priceTextField.getText().toString();
        String cat = catSpinner.getSelectedItem().toString();
        String desc = descriptionText.getText().toString();

        JSONObject jsonForm = new JSONObject();

        try {
            jsonForm.put("name", name);
            jsonForm.put("price", price);
            jsonForm.put("category", cat);
            jsonForm.put("description",desc);
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
                    String jsonRes = response.body().string();
                    try {
                        JSONObject res = new JSONObject(jsonRes);
                        if (Objects.equals(res.getString("msg"), "OK")) {
                            String result = "New Location Created";
                            backToMap(result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "There was an error");
                }
            }
        });

    }

    private void backToMap(String result) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("formResult",result);
        startActivity(intent);
    }
}
