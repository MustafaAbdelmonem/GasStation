package com.example.enmustafa.gas;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    GoogleMap mGoogleMap=null;
    private GoogleApiClient mGoogleApiClient;

    Button btnFind;
    Button btnFind2;

    double mLatitude=0;
    double mLongitude=0;
    Circle circle;


    List<HashMap<String, String>> PP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Getting LocationManager object from System Service LOCATION_SERVICE
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation(provider);

       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0,this);

        if(location!=null){
            onLocationChanged(location);

        }
        btnFind = (Button) findViewById(R.id.button);
        btnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "location=" + mLatitude + "," + mLongitude, Toast.LENGTH_SHORT).show();
                String type = "gas_station";
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                sb.append("location=" + mLatitude + "," + mLongitude);
                sb.append("&radius=3000");
                sb.append("&types=" + type);
                sb.append("&sensor=true");
                sb.append("&key=AIzaSyBh74096JLCV-ixi9S9lijKSxacjqXRHRg");
                // Creating a new non-ui thread task to download json data
                PlacesTask placesTask = new PlacesTask();
                // Invokes the "doInBackground()" method of the class PlaceTask
                placesTask.execute(sb.toString());


            }

        });

        btnFind2 = (Button) findViewById(R.id.button2);
        btnFind2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), listplaces.class);
                intent.putExtra("map", (Serializable) PP);
                startActivity(intent);
            }
        });

    }
//
//    private void initMap() {
//
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);
//
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        // Creating a criteria object to retrieve provider
//        Criteria criteria = new Criteria();
//
//        // Getting the name of the best provider
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        // Getting Current Location From GPS
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        if(location!=null){
//            onLocationChanged(location);
//        }
//
//        locationManager.requestLocationUpdates(provider, 20000, 0,this);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                Intent intent = new Intent(getApplicationContext(), Complaint.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        LatLng latLng = new LatLng(mLatitude, mLongitude);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mGoogleMap.addMarker( markerOptions);
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 13);
            mGoogleMap.animateCamera(update);
        circle = drawCircle(latLng);


        // Linking Marker id and place reference


//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//       if(location == null){
//            Toast.makeText(this, "Cant get current location", Toast.LENGTH_LONG).show();
//        } else {
//
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
//            mGoogleMap.animateCamera(update);
//        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }



    private Circle drawCircle(LatLng latLng){

        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(3000)
                .fillColor(Color.argb(20, 50, 0, 255))
                .strokeColor(Color.BLUE)
                .strokeWidth(3);

        return mGoogleMap.addCircle(options);
    }






    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
    String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
               // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
               // Connecting to url
            urlConnection.connect();
                // Reading data from url
            iStream = urlConnection.getInputStream();
               BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while( ( line = br.readLine())!= null){
                    sb.append(line);
                    }
                data = sb.toString();
                br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
        iStream.close();
        urlConnection.disconnect();
        }
    return data;
        }

    LocationRequest mLocationRequest;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;
 // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                    data = downloadUrl(url[0]);

                    }catch(Exception e){
                    Log.d("Background Task",e.toString());
                }
                return data;
        }
        // Executed after the complete execution of doInBackground() method
         @Override
        protected void onPostExecute(String result){
                ParserTask parserTask = new ParserTask();

                // Start parsing the Google places in JSON format
                // Invokes the "doInBackground()" method of the class ParseTask
                parserTask.execute(result);


            }

}

/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
                JSONObject jObject;

                // Invoked by execute() method of this object
                @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */

                places = placeJsonParser.parse(jObject);
                PP=places;
                }catch(Exception e){
                Log.d("Exception",e.toString());
                }
            return places;
            }

                // Executed after the complete execution of doInBackground() method
                @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
          // mGoogleMap.clear();


            for(int i=0;i<list.size();i++){

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name + " : " + vicinity);
              //  markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icomap));

                // Placing a marker on the touched position
                mGoogleMap.addMarker(markerOptions);


                }
            }

        }



}
