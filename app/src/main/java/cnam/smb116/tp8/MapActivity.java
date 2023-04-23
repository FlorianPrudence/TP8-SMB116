package cnam.smb116.tp8;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import cnam.smb116.tp8.StationBusiness.Station;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private HashMap<String, Station> hmap_stations = new HashMap<>();
    private ArrayList<Station> stations = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        populateStationMap();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void populateStationMap() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Chargement des stations et des disponibilité
                if(Station.loadStations(hmap_stations, stations) && Station.loadCapacity(hmap_stations) && mMap != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Affichage des stations sur la carte
                            for (int i = 0; i < stations.size(); i++) {
                                LatLng station_pos = new LatLng(stations.get(i).getLat(), stations.get(i).getLon());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(station_pos);
                                markerOptions.title(stations.get(i).getName());
                                markerOptions.snippet(stations.get(i).getNumBikesAvailable() + " vélos disponibles");
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_icon));
                                markers.add(mMap.addMarker(markerOptions));
                            }
                        }
                    });
                }
            }
        });
        t.start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}