package com.mobileapps.walkbuddy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    TextView TEST1;
    TextView TEST2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Bundle extra = getIntent().getExtras();
        name = extra.getCharSequence("name","error");
        lat = extra.getDouble("lat",0);
        lng = extra.getDouble("long",0);


        TEST1 = (TextView) findViewById(R.id.TEST1);
        TEST2 = (TextView) findViewById(R.id.TEST2);

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
                TEST1.setText("LANGITUDE: " + location.getLatitude());
                TEST2.setText("LONGITUDE: " + location.getLongitude());

            }

        };
        stopButton =  (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                Bundle extra = new Bundle();
                extra.putCharSequence("name",name);
                extra.putSerializable("userLat",verticesLat);
                extra.putSerializable("userLng",verticesLng);
                extra.putDouble("destLat",lat);
                extra.putDouble("destLng",lng);
                intent.putExtra("data",extra);
                startActivity(intent);
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
