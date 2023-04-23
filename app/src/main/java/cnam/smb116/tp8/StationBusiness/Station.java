package cnam.smb116.tp8.StationBusiness;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

// La station est rendu parcelable pour être passée dans l'Intent
public class Station {
    String station_id="";
    String name="";
    Double lat= 0.00;
    Double lon= 0.00;
    String capacity="";
    String stationCode="";
    String numBikesAvailable="";
    String numDocksAvailable="";

    public Station(String station_id, String name, Double lat, Double lon, String
            capacity, String stationCode) {
        this.station_id = station_id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.capacity = capacity;
        this.stationCode = stationCode;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getNumBikesAvailable() {
        return numBikesAvailable;
    }

    public void setNumBikesAvailable(String numBikesAvailable) {
        this.numBikesAvailable = numBikesAvailable;
    }

    public String getNumDocksAvailable() {
        return numDocksAvailable;
    }

    public void setNumDocksAvailable(String numDocksAvailable) {
        this.numDocksAvailable = numDocksAvailable;
    }

    // Fonction légèrement modifiée pour également charger la liste des stations à afficher dans l'IHM
    public static boolean loadStations(HashMap<String, Station> hmap_stations, ArrayList<Station> stationsList) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_information.json";
        String jsonStr = sh.makeServiceCall(url);

        if (jsonStr == null) {
            return false;
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject j_data = jsonObj.getJSONObject("data");
            JSONArray stations = j_data.getJSONArray("stations");

            // looping through All stations
            // adding station name to ArrayList for better readability
            for (int i = 0; i < stations.length(); i++) {
                JSONObject c = stations.getJSONObject(i);
                String station_id = c.getString("station_id");
                String name = c.getString("name");
                Double lat = c.getDouble("lat");
                Double lon = c.getDouble("lon");
                String capacity = c.getString("capacity");
                String stationCode = c.getString("stationCode");
                Station s = new Station(station_id, name, lat, lon, capacity, stationCode);
                hmap_stations.put(station_id, s);
                stationsList.add(s);
            }
        } catch (JSONException e) {

        }
        return true;
    }

    // Le toString sert dans l'affichage des éléments de la liste
    @Override
    public String toString() {
        return stationCode + ": " + name;
    }

    public static boolean loadCapacity(HashMap<String, Station> hmap_stations) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_status.json";
        String jsonStr = sh.makeServiceCall(url);

        if (jsonStr == null) {
            return false;
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject j_data = jsonObj.getJSONObject("data");
            JSONArray stations = j_data.getJSONArray("stations");
            // looping through All stations
            for (int i = 0; i < stations.length(); i++) {
                JSONObject c = stations.getJSONObject(i);
                String station_id = c.getString("station_id");
                String numBikesAvailable = c.getString("numBikesAvailable");
                String numDocksAvailable = c.getString("numDocksAvailable");
                Station s =(Station)hmap_stations.get(station_id);
                if(s!=null) {
                    s.setNumBikesAvailable(numBikesAvailable);
                    s.setNumDocksAvailable(numDocksAvailable);
                }
            }
        } catch (final JSONException e) {
        }
        return true;
    }


}
