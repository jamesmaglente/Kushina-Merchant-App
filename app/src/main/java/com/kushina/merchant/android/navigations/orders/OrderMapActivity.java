package com.kushina.merchant.android.navigations.orders;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.Globals;

public class OrderMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private Double latitude;
    private Double longitide;
    private String fullAddress;
    private float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    private Globals mGlobals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_map);

        mGlobals = new Globals(this);

        if(getIntent().getStringExtra("latitude") != null && getIntent().getStringExtra("longitude") != null) {
            latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
            longitide = Double.valueOf(getIntent().getStringExtra("longitude"));
            fullAddress = getIntent().getStringExtra("full_address");
        }else{
            mGlobals.showErrorMessage("There is no available marker.", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    if(result){
                        return;
                    }
                }
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if(latitude != null && longitide != null) {
            moveCamera(new LatLng(latitude, longitide), DEFAULT_ZOOM, fullAddress);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                    .draggable(true);

        mMap.addMarker(options).showInfoWindow();
    }

}
