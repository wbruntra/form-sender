package com.example.william.formsender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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

public class FormActivity extends AppCompatActivity {

    EditText nameTextField;
    EditText priceTextField;
    Spinner catSpinner;
    ImageView mapImageView;

    Button sendButton;

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");


    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);

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
    }

    public void send(View v) {
        Log.v(TAG, "Send button clicked");
        String sendURL = "http://localhost:3000/";

        String name = nameTextField.getText().toString();
        String price = priceTextField.getText().toString();
        String cat = catSpinner.getSelectedItem().toString();

        JSONObject jsonForm = new JSONObject();

        try {
            jsonForm.put("name", name);
            jsonForm.put("price", price);
            jsonForm.put("category", cat);
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
