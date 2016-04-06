package com.example.william.formsender;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

public class MainActivity extends AppCompatActivity {

    TextView mMainTitle;
    Button mapButton;
    Button formButton;
    Button signinButton;
    String userId;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = (Button) findViewById(R.id.mapButton);
        formButton = (Button) findViewById(R.id.formButton);
        signinButton = (Button) findViewById(R.id.signinButton);
        mMainTitle = (TextView) findViewById(R.id.mainTitleTextView);

        Intent intent = getIntent();
        if(intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId");
            Log.v(TAG, userId);
        }


        formButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForm();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openSignin();
            }
        });
    }

    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    private void openForm() {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    private void openSignin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

}
