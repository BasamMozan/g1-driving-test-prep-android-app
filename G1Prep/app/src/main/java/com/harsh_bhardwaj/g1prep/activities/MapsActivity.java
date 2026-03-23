package com.harsh_bhardwaj.g1prep.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.harsh_bhardwaj.g1prep.R;
import com.harsh_bhardwaj.g1prep.databinding.ActivityMapsBinding;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView backArrowImageView;
    private ActivityMapsBinding binding;
    private LatLng userLocation, ottawa, toronto;
    HashMap<String, LatLng> testCentresInOttawa;
    HashMap<String, LatLng> testCentresInToronto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ottawa = new LatLng(45.4083428d, -75.6631765d);
        toronto = new LatLng(43.6532d, -79.3832d);
        updateHashMaps();

        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        backArrowImageView = findViewById(R.id.backArrowIcon);
        backArrowImageView.setOnClickListener(view -> {
            vibrator.vibrate(30);
            onBackPressed();
        });
        userLocation = ottawa;
    }

    private void placeMarkers() {
        HashMap<String, LatLng> curMap = new HashMap<>();

        if (userLocation == ottawa){
            curMap = testCentresInOttawa;
        }
        if(userLocation == toronto){
            curMap = testCentresInToronto;
        }
        for (Map.Entry mapElement : curMap.entrySet()) {
            LatLng latLng = (LatLng) mapElement.getValue();
            String centreName = (String) mapElement.getKey();
            mMap.addMarker(new MarkerOptions().position(latLng).title(centreName));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        placeMarkers();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11));
    }


    private void updateHashMaps(){
        testCentresInOttawa = new HashMap<>();
        testCentresInToronto = new HashMap<>();

        testCentresInOttawa.put("Temporary Drive Test", new LatLng(45.36792499257798d, -75.66295307296882d));
        testCentresInOttawa.put("Ottawa Driving School Inc", new LatLng(45.40168318277344, -75.63411396339808));
        testCentresInOttawa.put("Drive Test", new LatLng(45.4729912745818, -75.58879536264405));
        testCentresInOttawa.put("Ottawa Driving School", new LatLng(45.456617658782896, -75.64235370898972));
        testCentresInOttawa.put("DriveTest", new LatLng(45.403611613330334, -75.64510029085359));
        testCentresInOttawa.put("Drive Test 2", new LatLng(45.149458173865284, -76.1339918626243));

        testCentresInToronto.put("DriveTest", new LatLng(43.76065065165586, -79.47487431022122));
        testCentresInToronto.put("Community Driving School", new LatLng(43.76560964299253, -79.36295109442905));
        testCentresInToronto.put("DriveTest - Toronto Metro East", new LatLng(43.75221942300084, -79.31076603675908));
        testCentresInToronto.put("Scarborough Driving", new LatLng(43.76114656928731, -79.26407414305437));
        testCentresInToronto.put("Global Driving Academy", new LatLng(43.685719917772225, -79.44534855390796));
        testCentresInToronto.put("Etobicoke Driving School", new LatLng(43.632566572289775, -79.5222528494216));
        testCentresInToronto.put("DriveTest Brampton", new LatLng(43.68869908590553, -79.71520023370138));
        testCentresInToronto.put("Apna Toronto", new LatLng(43.60187969593514, -79.65049038888601));
        testCentresInToronto.put("Learn Safe Driving School", new LatLng(43.695203800916175, -79.34860414116886));
    }
}