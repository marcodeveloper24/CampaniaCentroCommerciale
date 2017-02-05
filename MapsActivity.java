package com.developer.marcocicala.centrocampania;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    static final int RAGGIO = 6371;
    static final double PIGRECO = Math.PI;
    private static final String MYPREF= "prefmaps";
    private static final String PREFKEY= "preffila";
    private static final String PREFKEYLA= "prefla";
    private static final String PREFKEYLO= "preflo";
    private boolean isReady;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LocationManager locationManager;

    AllCordinates cordinates;
    ImageView imageFile;
    Button btnNewPosition;
    Button btnDeletePosition;

    boolean isGpsEnable = false;
    boolean isNetworkEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        isReady = false;
        imageFile = (ImageView) findViewById(R.id.imageFile);




        getPrefereces(imageFile);

        btnNewPosition = (Button) findViewById(R.id.btnNewPosition);
        btnNewPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGPSEnable(getApplicationContext())){
                    isReady =  true;
                    getPosition();
                } else {
                    showSettingsAlert();
                }

            }
        });
        btnDeletePosition = (Button) findViewById(R.id.btnDeletePosition);
        btnDeletePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreferences();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPrefereces(imageFile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getPosition(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(false);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

        LatLng coordinates = getPreferencesCoordinates();
        if (coordinates != null){
            double la = coordinates.latitude;
            double lo = coordinates.longitude;
            if (la == 0 || lo == 0){

            } else {
                mapPosition(la, lo);
            }
        } else {

        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (isReady) {
            cordinates = new AllCordinates();
            imageFile = (ImageView) findViewById(R.id.imageFile);

            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("La tua auto");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            savePreferencesCoordinates(latLng.latitude, latLng.longitude);


            double d1 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF1_latitudine(), cordinates.getF1_longitudine());
            double d2 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF2_latitudine(), cordinates.getF2_longitudine());
            double d3 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF3_latitudine(), cordinates.getF3_longitudine());
            double d4 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF4_latitudine(), cordinates.getF4_longitudine());
            double d5 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF5_latitudine(), cordinates.getF5_longitudine());
            double d6 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF6_latitudine(), cordinates.getF6_longitudine());
            double d7 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF7_latitudine(), cordinates.getF7_longitudine());
            double d8 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF8_latitudine(), cordinates.getF8_longitudine());
            double d9 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF9_latitudine(), cordinates.getF9_longitudine());
            double d10 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF10_latitudine(), cordinates.getF10_longitudine());
            double d11 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF11_latitudine(), cordinates.getF11_longitudine());
            double d12 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF12_latitudine(), cordinates.getF12_longitudine());
            double d13 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF13_latitudine(), cordinates.getF13_longitudine());
            double d14 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF14_latitudine(), cordinates.getF14_longitudine());
            double d15 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF15_latitudine(), cordinates.getF15_longitudine());
            double d16 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF16_latitudine(), cordinates.getF16_longitudine());
            double d17 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF17_latitudine(), cordinates.getF17_longitudine());
            double d18 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF18_latitudine(), cordinates.getF18_longitudine());
            double d19 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF19_latitudine(), cordinates.getF19_longitudine());
            double d21 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF21_latitudine(), cordinates.getF21_longitudine());
            double d22 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF22_latitudine(), cordinates.getF22_longitudine());
            double d23 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF23_latitudine(), cordinates.getF23_longitudine());
            double d24 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF24_latitudine(), cordinates.getF24_longitudine());
            double d25 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF25_latitudine(), cordinates.getF25_longitudine());
            double d26 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF26_latitudine(), cordinates.getF26_longitudine());
            double d27 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF27_latitudine(), cordinates.getF27_longitudine());
            double d28 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF28_latitudine(), cordinates.getF28_longitudine());
            double d29 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF29_latitudine(), cordinates.getF29_longitudine());
            double d30 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF30_latitudine(), cordinates.getF30_longitudine());
            double d31 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF31_latitudine(), cordinates.getF31_longitudine());
            double d32 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF32_latitudine(), cordinates.getF32_longitudine());
            double d33 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF33_latitudine(), cordinates.getF33_longitudine());
            double d34 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF34_latitudine(), cordinates.getF34_longitudine());
            double d35 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF35_latitudine(), cordinates.getF35_longitudine());
            double d36 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF36_latitudine(), cordinates.getF36_longitudine());
            double d37 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF37_latitudine(), cordinates.getF37_longitudine());
            double d38 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF38_latitudine(), cordinates.getF38_longitudine());
            double d39 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF39_latitudine(), cordinates.getF39_longitudine());
            double d40 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF40_latitudine(), cordinates.getF40_longitudine());
            double d41 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF41_latitudine(), cordinates.getF41_longitudine());
            double d42 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF42_latitudine(), cordinates.getF42_longitudine());
            double d43 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF43_latitudine(), cordinates.getF43_longitudine());
            double d44 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF44_latitudine(), cordinates.getF44_longitudine());
            double d45 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF45_latitudine(), cordinates.getF45_longitudine());
            double d46 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF46_latitudine(), cordinates.getF46_longitudine());
            double d47 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF47_latitudine(), cordinates.getF47_longitudine());
            double d48 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF48_latitudine(), cordinates.getF48_longitudine());
            double d49 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF49_latitudine(), cordinates.getF49_longitudine());
            double d50 = getDistance(latLng.latitude, latLng.longitude, cordinates.getF50_latitudine(), cordinates.getF50_longitudine());


            double[] distances = {d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14, d15, d16, d17, d18, d19, d21, d22, d23, d24, d25, d26, d27, d28, d29, d30, d31, d32, d33, d34, d35, d36, d37, d38, d39, d40, d41, d42, d43, d44, d45, d46, d47, d48, d49, d50};
            double minDistance = getMin(distances);

            DecimalFormat df1 = new DecimalFormat("00.000");
            double d = Double.parseDouble(df1.format(minDistance).replace(",", "."));


            if (minDistance > 245) {
                showsAlert();
            } else if (minDistance == d1) {
                imageFile.setImageResource(R.drawable.nord_1);
                savePreferences(1);
            } else if (minDistance == d2) {
                imageFile.setImageResource(R.drawable.nord_2);
                savePreferences(2);
            } else if (minDistance == d3) {
                imageFile.setImageResource(R.drawable.nord_3);
                savePreferences(3);
            } else if (minDistance == d4) {
                imageFile.setImageResource(R.drawable.nord_4);
                savePreferences(4);
            } else if (minDistance == d5) {
                imageFile.setImageResource(R.drawable.nord_5);
                savePreferences(5);
            } else if (minDistance == d6) {
                imageFile.setImageResource(R.drawable.nord_6);
                savePreferences(6);
            } else if (minDistance == d7) {
                imageFile.setImageResource(R.drawable.nord_7);
                savePreferences(7);
            } else if (minDistance == d8) {
                imageFile.setImageResource(R.drawable.nord_8);
                savePreferences(8);
            } else if (minDistance == d9) {
                imageFile.setImageResource(R.drawable.nord_9);
                savePreferences(9);
            } else if (minDistance == d10) {
                imageFile.setImageResource(R.drawable.nord_10);
                savePreferences(10);
            } else if (minDistance == d11) {
                imageFile.setImageResource(R.drawable.nord_11);
                savePreferences(11);
            } else if (minDistance == d12) {
                imageFile.setImageResource(R.drawable.nord_12);
                savePreferences(12);
            } else if (minDistance == d13) {
                imageFile.setImageResource(R.drawable.nord_13);
                savePreferences(13);
            } else if (minDistance == d14) {
                imageFile.setImageResource(R.drawable.nord_14);
                savePreferences(14);
            } else if (minDistance == d15) {
                imageFile.setImageResource(R.drawable.nord_15);
                savePreferences(15);
            } else if (minDistance == d16) {
                imageFile.setImageResource(R.drawable.nord_16);
                savePreferences(16);
            } else if (minDistance == d17) {
                imageFile.setImageResource(R.drawable.nord_17);
                savePreferences(17);
            } else if (minDistance == d18) {
                showAlertNoFila();
                savePreferences(18);
            } else if (minDistance == d19) {
                showAlertNoFila();
                savePreferences(19);
            } else if (minDistance == d21) {
                imageFile.setImageResource(R.drawable.nord_18);
                savePreferences(21);
            } else if (minDistance == d22) {
                imageFile.setImageResource(R.drawable.nord_19);
                savePreferences(22);
            } else if (minDistance == d23) {
                imageFile.setImageResource(R.drawable.nord_20);
                savePreferences(13);
            } else if (minDistance == d24) {
                imageFile.setImageResource(R.drawable.nord_21);
                savePreferences(24);
            } else if (minDistance == d25) {
                imageFile.setImageResource(R.drawable.sud_1);
                savePreferences(25);
            } else if (minDistance == d26) {
                imageFile.setImageResource(R.drawable.sud_2);
                savePreferences(26);
            } else if (minDistance == d27) {
                imageFile.setImageResource(R.drawable.sud_3);
                savePreferences(27);
            } else if (minDistance == d28) {
                imageFile.setImageResource(R.drawable.sud_4);
                savePreferences(28);
            } else if (minDistance == d29) {
                showAlertNoFila();
                savePreferences(29);
            } else if (minDistance == d30) {
                showAlertNoFila();
                savePreferences(30);
            } else if (minDistance == d31) {
                imageFile.setImageResource(R.drawable.sud_5);
                savePreferences(31);
            } else if (minDistance == d32) {
                imageFile.setImageResource(R.drawable.sud_6);
                savePreferences(32);
            } else if (minDistance == d33) {
                imageFile.setImageResource(R.drawable.sud_7);
                savePreferences(33);
            } else if (minDistance == d34) {
                imageFile.setImageResource(R.drawable.sud_8);
                savePreferences(34);
            } else if (minDistance == d35) {
                imageFile.setImageResource(R.drawable.sud_9);
                savePreferences(35);
            } else if (minDistance == d36) {
                imageFile.setImageResource(R.drawable.sud_10);
                savePreferences(36);
            } else if (minDistance == d37) {
                imageFile.setImageResource(R.drawable.sud_11);
                savePreferences(37);
            } else if (minDistance == d38) {
                imageFile.setImageResource(R.drawable.sud_12);
                savePreferences(38);
            } else if (minDistance == d39) {
                imageFile.setImageResource(R.drawable.sud_13);
                savePreferences(39);
            } else if (minDistance == d40) {
                imageFile.setImageResource(R.drawable.sud_14);
                savePreferences(40);
            } else if (minDistance == d41) {
                imageFile.setImageResource(R.drawable.sud_15);
                savePreferences(41);
            } else if (minDistance == d42) {
                imageFile.setImageResource(R.drawable.sud_16);
                savePreferences(42);
            } else if (minDistance == d43) {
                imageFile.setImageResource(R.drawable.sud_17);
                savePreferences(43);
            } else if (minDistance == d44) {
                imageFile.setImageResource(R.drawable.sud_18);
                savePreferences(44);
            } else if (minDistance == d45) {
                imageFile.setImageResource(R.drawable.sud_19);
                savePreferences(45);
            } else if (minDistance == d46) {
                imageFile.setImageResource(R.drawable.sud_20);
                savePreferences(46);
            } else if (minDistance == d47) {
                imageFile.setImageResource(R.drawable.sud_21);
                savePreferences(47);
            } else if (minDistance == d48) {
                imageFile.setImageResource(R.drawable.sud_22);
                savePreferences(48);
            } else if (minDistance == d49) {
                imageFile.setImageResource(R.drawable.sud_23);
                savePreferences(49);
            }

            //stop location updates
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        } else {

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    private double getDistance(double myLat, double myLon, double cLatitude, double cLongitude) {
        double cRadLat = (cLatitude * PIGRECO) / 180;
        double cRadLon = (cLongitude * PIGRECO) / 180;

        double myRadLat = (myLat * PIGRECO) / 180;
        double myRadLon = (myLon * PIGRECO) / 180;

        double distance = ((RAGGIO * Math.acos(Math.sin(myRadLat) * Math.sin(cRadLat) +
                Math.cos(myRadLat) * Math.cos(cRadLat) * Math.cos((myRadLon - cRadLon))))) * 1000;

        return distance;

    }

    public double getMin(double[] distances) {
        double min = distances[0];
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < min) {
                min = distances[i];
            }
        }
        return min;
    }

    public void showsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Campania Centro Commerciale");

        // Setting Dialog Message
        alertDialog.setMessage("Ops! Nessuna fila trovata!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showAlertNoFila(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Campania Centro Commerciale");

        // Setting Dialog Message
        alertDialog.setMessage("Questa fila non ha un numero o colore identificativo!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void savePreferences(int fila){
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFKEY,fila);
        editor.commit();
    }

    public void getPrefereces(ImageView imageFile){
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        int fila = sharedPreferences.getInt(PREFKEY,0);
        switch (fila){
            case 0:
                break;
            case 1:
                imageFile.setImageResource(R.drawable.nord_1);
                break;
            case 2:
                imageFile.setImageResource(R.drawable.nord_2);
                break;
            case 3:
                imageFile.setImageResource(R.drawable.nord_3);
                break;
            case 4:
                imageFile.setImageResource(R.drawable.nord_4);
                break;
            case 5:
                imageFile.setImageResource(R.drawable.nord_5);
                break;
            case 6:
                imageFile.setImageResource(R.drawable.nord_6);
                break;
            case 7:
                imageFile.setImageResource(R.drawable.nord_7);
                break;
            case 8:
                imageFile.setImageResource(R.drawable.nord_8);
                break;
            case 9:
                imageFile.setImageResource(R.drawable.nord_9);
                break;
            case 10:
                imageFile.setImageResource(R.drawable.nord_10);
                break;
            case 11:
                imageFile.setImageResource(R.drawable.nord_11);
                break;
            case 12:
                imageFile.setImageResource(R.drawable.nord_12);
                break;
            case 13:
                imageFile.setImageResource(R.drawable.nord_13);
                break;
            case 14:
                imageFile.setImageResource(R.drawable.nord_14);
                break;
            case 15:
                imageFile.setImageResource(R.drawable.nord_15);
                break;
            case 16:
                imageFile.setImageResource(R.drawable.nord_16);
                break;
            case 17:
                imageFile.setImageResource(R.drawable.nord_17);
                break;
            case 18:
                showAlertNoFila();
                break;
            case 19:
                showAlertNoFila();
                break;
            case 21:
                imageFile.setImageResource(R.drawable.nord_18);
                break;
            case 22:
                imageFile.setImageResource(R.drawable.nord_19);
                break;
            case 23:
                imageFile.setImageResource(R.drawable.nord_20);
                break;
            case 24:
                imageFile.setImageResource(R.drawable.nord_21);
                break;
            case 25:
                imageFile.setImageResource(R.drawable.sud_1);
                break;
            case 26:
                imageFile.setImageResource(R.drawable.sud_2);
                break;
            case 27:
                imageFile.setImageResource(R.drawable.sud_3);
                break;
            case 28:
                imageFile.setImageResource(R.drawable.sud_4);
                break;
            case 29:
                showAlertNoFila();
                break;
            case 30:
                showAlertNoFila();
                break;
            case 31:
                imageFile.setImageResource(R.drawable.sud_5);
                break;
            case 32:
                imageFile.setImageResource(R.drawable.sud_6);
                break;
            case 33:
                imageFile.setImageResource(R.drawable.sud_7);
                break;
            case 34:
                imageFile.setImageResource(R.drawable.sud_8);
                break;
            case 35:
                imageFile.setImageResource(R.drawable.sud_9);
                break;
            case 36:
                imageFile.setImageResource(R.drawable.sud_10);
                break;
            case 37:
                imageFile.setImageResource(R.drawable.sud_11);
                break;
            case 38:
                imageFile.setImageResource(R.drawable.sud_12);
                break;
            case 39:
                imageFile.setImageResource(R.drawable.sud_13);
                break;
            case 40:
                imageFile.setImageResource(R.drawable.sud_14);
                break;
            case 41:
                imageFile.setImageResource(R.drawable.sud_15);
                break;
            case 42:
                imageFile.setImageResource(R.drawable.sud_16);
                break;
            case 43:
                imageFile.setImageResource(R.drawable.sud_17);
                break;
            case 44:
                imageFile.setImageResource(R.drawable.sud_18);
                break;
            case 45:
                imageFile.setImageResource(R.drawable.sud_19);
                break;
            case 46:
                imageFile.setImageResource(R.drawable.sud_20);
                break;
            case 47:
                imageFile.setImageResource(R.drawable.sud_21);
                break;
            case 48:
                imageFile.setImageResource(R.drawable.sud_22);
                break;
            case 49:
                imageFile.setImageResource(R.drawable.sud_23);
                break;

        }
    }

    public void mapPosition(double la, double lo){
        //Place current location marker
        LatLng latLng = new LatLng(la, lo);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("La tua auto");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void savePreferencesCoordinates(double la, double lo){
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String sla = String.valueOf(la);
        String slo = String.valueOf(lo);
        editor.putString(PREFKEYLA,sla);
        editor.putString(PREFKEYLO,slo);
        editor.commit();
    }

    public LatLng getPreferencesCoordinates(){
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        String sla = sharedPreferences.getString(PREFKEYLA,null);
        String slo = sharedPreferences.getString(PREFKEYLO,null);
        if (sla != null || slo != null){
            double la = Double.parseDouble(sla);
            double lo = Double.parseDouble(slo);
            LatLng coo = new LatLng(la,lo);

            return coo;
        } else {
            return null;
        }



    }

    public void clearPreferences(){
        imageFile.setImageBitmap(null);
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        mMap.clear();
    }

    private boolean isGPSEnable(Context context){
        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);

        isGpsEnable = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable){
            return true;
        } else {
            return false;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Campania Centro Commerciale");

        // Setting Dialog Message
        alertDialog.setMessage("GPS non è abilitato. Attivarlo ora?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapsActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
