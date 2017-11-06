package com.mobileapps.walkbuddy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.mobileapps.walkbuddy.walkbuddy.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;


public class RecordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private LocationManager manager;
    private LocationListener locationListener;
    public CharSequence name;
    public double lat;
    public double lng;
    public ArrayList<Double> verticesLat;
    public ArrayList<Double> verticesLng;


    private boolean mRequestingLocationUpdates;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Button stopButton;
    Button cancelButton;
    TextView timerText;
    Handler customHandler = new Handler();
    long startTime = 0, timeInMillis = 0;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMillis = SystemClock.uptimeMillis() - startTime;
            int secs = (int) timeInMillis/1000;
            int mins = secs/60;
            secs %= 60;
            String timer = String.format(Locale.getDefault(), "%02d", mins) + ":" + String.format(Locale.getDefault(), "%02d",secs);
            timerText.setText(timer);
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle extra = getIntent().getBundleExtra("data");
        name = extra.getCharSequence("name","error");
        lat = extra.getDouble("lat",0);
        lng = extra.getDouble("long",0);

        // Timer
        timerText = findViewById(R.id.timerValue);

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(RecordActivity.this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mRequestingLocationUpdates = true;
        }

        verticesLat = new ArrayList<Double>();
        verticesLng = new ArrayList<Double>();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                verticesLat.add(location.getLatitude());
                verticesLng.add(location.getLongitude());
            }

        };
        stopButton =  (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customHandler.removeCallbacks(updateTimerThread);
                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                Bundle extra = new Bundle();
                extra.putCharSequence("name",name);
                extra.putSerializable("userLat",verticesLat);
                extra.putSerializable("userLng",verticesLng);
                extra.putDouble("destLat",lat);
                extra.putDouble("destLng",lng);
                extra.putLong("timeInMillis", timeInMillis);
                intent.putExtra("data",extra);
                startActivity(intent);
                finish();
            }
        });

        cancelButton = findViewById(R.id.btn_cancel_route);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, locationListener);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
