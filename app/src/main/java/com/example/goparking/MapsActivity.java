package com.example.goparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.goparking.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private int REQUEST_CODE = 101;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Context mContext;
    private int trackBar_dist_value=500;

    Intent info;

    Button btn_distance, btn_money;
    ImageButton btn_showInfo, btn_charge, btn_motor;
    TextView txv_distance, txv_money;

    SupportMapFragment supportMapFragment;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Marker userLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        //Initialize fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mContext = MapsActivity.this;
        info=getIntent();

        btn_distance = findViewById(R.id.btn_distance);
        btn_money = findViewById(R.id.btn_money);
        btn_showInfo = findViewById(R.id.showInfo);
        btn_charge=findViewById(R.id.charge);
        btn_motor=findViewById(R.id.motor);
        txv_distance =findViewById(R.id.txv_distance);
        txv_money = findViewById(R.id.txv_money);

        btn_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intiDistance(view);
            }
        });

        btn_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMoney(view);
            }
        });

        btn_showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setClass(MapsActivity.this, information.class);
                startActivity(info);
            }
        });

        btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setClass(MapsActivity.this, ChargeMap.class);
                startActivity(info);
            }
        });

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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                startLocationUpdates();
                //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            startLocationUpdates();
            //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(false);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            checkLocationPermission();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (fusedLocationProviderClient != null) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
                setParkingPlace(locationResult.getLastLocation());
            }
        }
    };
    private void setParkingPlace(Location location){
        InfoWindow adapter = new InfoWindow(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        ArrayList<String> parkName = info.getStringArrayListExtra("data_parkName");
        ArrayList<String> carNum = info.getStringArrayListExtra("data_carNum");
        ArrayList<String> tmp_lon = info.getStringArrayListExtra("data_lon");
        ArrayList<String> tmp_lat = info.getStringArrayListExtra("data_lat");

        for(int i=0; i<5; i++){
            Double lat = Double.parseDouble(tmp_lat.get(i));
            Double lon = Double.parseDouble(tmp_lon.get(i));

            LatLng pos = new LatLng(lat, lon);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.park))
                    .title(parkName.get(i))
                    .snippet(carNum.get(i)));
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 18));delete

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(mContext, "marker is click", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmark));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5); //set icon to center
            markerOptions.flat(true);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } else  {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE );
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    //fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    startLocationUpdates();;
                }
            } else {
                // Permission denied
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                checkLocationPermission();
            }
        }
    }

    private void intiDistance(View v){
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_choice_distance, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        SeekBar trackBar_dist=popWindow.getContentView().findViewById(R.id.distanceBar);
        trackBar_dist.setMax(3);//500m~2000m
        trackBar_dist.setProgress(0);

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                trackBar_dist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        trackBar_dist_value=(i+1)*500;
                        int camera_level=i;
                        if (camera_level==0) //500m
                            camera_level=3;
                        else if(camera_level==1) //1000m
                            camera_level=2;
                        else if(camera_level==2)  //1500m
                            camera_level=1;
                        else if(camera_level==3)  //2000m
                            camera_level=0;
                        mMap.animateCamera( CameraUpdateFactory.zoomTo((camera_level+15)),1,null);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(MapsActivity.this, "觸碰SeekBar", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(MapsActivity.this, "?放開SeekBar", Toast.LENGTH_SHORT).show();
                    }
                });

                txv_distance.setText(""+trackBar_dist_value+"m");
                info.putExtra("myDistance",trackBar_dist_value);
                //mMap.animateCamera( CameraUpdateFactory.zoomTo(100.0f ) )
                return false;
            }
        });

        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        popWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    private void initMoney(View v){
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_choice_money, null, false);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        Button twenty=popWindow.getContentView().findViewById(R.id.twenty);
        Button thirty=popWindow.getContentView().findViewById(R.id.thirty);
        Button forty=popWindow.getContentView().findViewById(R.id.forty);
        Button fifty=popWindow.getContentView().findViewById(R.id.fifty);

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                twenty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txv_money.setText("20元/hr");
                    }
                });

                thirty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txv_money.setText("30元/hr");
                    }
                });

                forty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txv_money.setText("40元/hr");
                    }
                });

                fifty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txv_money.setText("50元/hr");
                    }
                });

                //info.putExtra("myFare",20);  /** parameter need modify */
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        popWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

}