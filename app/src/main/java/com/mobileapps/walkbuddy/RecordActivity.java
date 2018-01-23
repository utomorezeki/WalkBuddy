package com.mobileapps.walkbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.Locale;


public class RecordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private LocationManager manager;
    private LocationListener locationListener;
    public double lat;
    public double lng;
    public ArrayList<Double> verticesLat;
    public ArrayList<Double> verticesLng;
    public ArrayList<Double> poiLat;
    public ArrayList<Double> poiLng;
    public boolean checkConnection;

    private boolean mRequestingLocationUpdates;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Button stopButton;
    Button cancelButton;
    Button poiButton;
    TextView timerText;
    Handler customHandler = new Handler();
    long startTime = 0, timeInMillis = 0;
    private double distanceTolerance = 0.0006;

    PowerManager pm;
    PowerManager.WakeLock wl;

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

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Recording needs Wake Lock permission", Toast.LENGTH_SHORT).show();
            finish();
        }
        pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        Bundle extra = getIntent().getBundleExtra("data");
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
        checkConnection = false;
        createLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mRequestingLocationUpdates = true;
        }

        verticesLat = new ArrayList<Double>();
        verticesLng = new ArrayList<Double>();

        poiLat = new ArrayList<Double>();
        poiLng = new ArrayList<Double>();

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
                int size = verticesLat.size();
                if(size > 0) {
                    if(getDistance(verticesLat.get(size-1), verticesLng.get(size-1)) <= distanceTolerance) {
                        wl.release();
                        customHandler.removeCallbacks(updateTimerThread);
                        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                        Bundle extra = new Bundle();
                        extra.putSerializable("userLat", verticesLat);
                        extra.putSerializable("userLng", verticesLng);
                        extra.putSerializable("poiLat", poiLat);
                        extra.putSerializable("poiLng", poiLng);
                        extra.putDouble("destLat", lat);
                        extra.putDouble("destLng", lng);
                        extra.putLong("timeInMillis", timeInMillis);
                        intent.putExtra("data", extra);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RecordActivity.this, "You are not at your destination yet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelButton = findViewById(R.id.btn_cancel_route);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wl.release();
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                finish();
            }
        });
        poiButton = findViewById(R.id.poiButton);
        poiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if(checkConnection){
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        poiLat.add(mLastLocation.getLatitude());
                        poiLng.add(mLastLocation.getLongitude());
                        Toast.makeText(RecordActivity.this, "POI " + poiLat.size() +" added to route", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RecordActivity.this, "Lost connection. POI not added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(lat-x,2.0) + Math.pow(lng-y,2.0));
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            verticesLat.add(mLastLocation.getLatitude());
            verticesLng.add(mLastLocation.getLongitude());
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, locationListener);
            checkConnection = true;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
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
        checkConnection = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        checkConnection = false;
    }

    @Override
    public void onBackPressed() {
        wl.release();
        Intent intent = new Intent(RecordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if(wl.isHeld())
            wl.release();
        super.onDestroy();
    }
}
