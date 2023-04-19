package cnam.smb116.tp8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private GnssStatus.Callback gnssStatusCallback;
    private LocationManager locationManager;
    private ArrayList<Satellite> satellites = new ArrayList<>();
    private ArrayAdapter<Satellite> adapter;
    private TextView gpsStatus, firstFix, longitude, latitude, altitude, accuracy, speed, bearing, time, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Demande des permissions nécéssaires à l'exécution de l'activité
        requestPermissions(new String[] { ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 101);

        // Initialisation des composants
        ListView listView = findViewById(R.id.listSatellites);
        gpsStatus = findViewById(R.id.gpsStatus);
        firstFix = findViewById(R.id.firstFix);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        altitude = findViewById(R.id.altitude);
        accuracy = findViewById(R.id.accuracy);
        speed = findViewById(R.id.speed);
        bearing = findViewById(R.id.bearing);
        time = findViewById(R.id.time);
        address = findViewById(R.id.address);

        // Création de l'adapter pour l'affichage de la liste dans l'IHM
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, satellites);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Création de la callback
        gnssStatusCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                updateSatellites(status);
            }
            @Override
            public void onFirstFix(int ttffMillis) {
                super.onFirstFix(ttffMillis);
                firstFix.setText(ttffMillis + " ms");
            }
            @Override
            public void onStarted() {
                super.onStarted();
                gpsStatus.setText("OK");
            }
            @Override
            public void onStopped() {
                super.onStopped();
                gpsStatus.setText("KO");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // On inscrit la callback et la requête de mise à jour de localisation si on a les autorisations
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, this);
        }
    }

    @Override
    protected void onStop() {
        // On désinscrit la callback
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
        super.onStop();
    }

    // Met à jour l'IHM avec les informations de localisation lorsque celles-ci changent
    @Override
    public void onLocationChanged(Location location) {
        double longi = location.getLongitude();
        double lati = location.getLatitude();
        longitude.setText(Double.toString(longi));
        latitude.setText(Double.toString(lati));
        altitude.setText(Double.toString(location.getAltitude()));
        accuracy.setText(location.getAccuracy() + " m");
        speed.setText(location.getSpeed() + " m/s");
        bearing.setText(location.getBearing() + " °");
        time.setText(new Date(location.getTime()).toString());
        // Récupération de l'adresse via la latitude et la longitude
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.FRANCE);
        try {
            List<Address> adresses = geocoder.getFromLocation(lati, longi, 1);
            if(adresses != null && adresses.size() > 0) {
                String ad = adresses.get(0).getAddressLine(0);
                address.setText(ad.replaceFirst(", ", ",\n"));
            }
        } catch (IOException e) {
            Toast.makeText(this, "Impossible de récupérer l'adresse", Toast.LENGTH_SHORT).show();
        }
    }

    // Fonction de mise à jour de la liste des satellites
    private void updateSatellites(GnssStatus status) {
        satellites = new ArrayList<>();
        // Récupération des informations des satellites depuis le status
        for (int i = 0; i < status.getSatelliteCount(); i++) {
            Satellite sat = new Satellite(status.getAzimuthDegrees(i),
                    status.getElevationDegrees(i),
                    status.getSvid(i),
                    status.getCn0DbHz(i),
                    status.hasAlmanacData(i),
                    status.hasEphemerisData(i));
            satellites.add(sat);
        }
        Toast.makeText(this, satellites.size() + " satellites au total", Toast.LENGTH_SHORT).show();
        // Mise à jour de l'arrayAdapter et de l'IHM
        adapter.clear();
        adapter.addAll(satellites);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}