package cargo.com.testing.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cargo.com.testing.R;
import cargo.com.testing.Service.DirectionsJSONParser;

/**
 * Created by Vcreativedevelopers on 05-02-2017.
 */
public class CargoBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    static GoogleMap map;
    //Linearlayout
    @Bind(R.id.linearlayout_cargo_location_details)
    LinearLayout locationdetails;
    @Bind(R.id.linearlayout_cargo_section)
    LinearLayout cargosection;
    @Bind(R.id.cargo_onclick)
    LinearLayout cargo_onclick;
    @Bind(R.id.location_onclick)
    LinearLayout location_onclick;
    @Bind(R.id.volumetric_input_details)
    LinearLayout volumetric_input_details;
    @Bind(R.id.map_fargment)
    LinearLayout map_fargment;
    //Initilaize Imageview
    @Bind(R.id.pickup_location_marker)
    ImageView pickup_location_marker;
    @Bind(R.id.drop_location_marker)
    ImageView drop_location_marker;
    //Initialize for CheckBox
    @Bind(R.id.checkBox_Volumetric)
    CheckBox volumetric_enable;
    //Initialize edittext
    @Bind(R.id.input_pickuplocation)
    EditText input_pickuplocation;
    @Bind(R.id.input_drop_location)
    EditText input_drop_location;
    public static int ispicklocation = 0;
    public static String address_value = "";
    public static double P_latitude = 0.0;
    public static double P_longitude = 0.0;
    public static double D_latitude = 0.0;
    public static double D_longitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_demo);
        ButterKnife.bind(this);
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);
        InitializeForOnclicks();
        InitializeForCheckBox();
        InitializeForLocations();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ispicklocation == 1){
            System.out.println("vml check the adress  "+address_value);
            ispicklocation = 0;
            input_pickuplocation.setText(address_value);
            if(P_latitude!=0.0&&P_longitude!=0.0){
                plotmarkerinmap(P_latitude,P_longitude);
            }
        }else if(ispicklocation == 2){
            ispicklocation = 0;
            input_drop_location.setText(address_value);
            if(D_latitude!=0.0&&D_longitude!=0.0){
                plotmarkerinmap(D_latitude,D_longitude);
            }
        }

    }

    private void plotmarkerinmap(double latitde,double longitude) {
        MarkerOptions marking = new MarkerOptions();
        map.clear();
        if(P_latitude!=0.0 && P_longitude!=0.0 && D_latitude!=0.0 && D_longitude!=0.0){
            final LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(P_latitude,P_longitude)).include(new LatLng(D_latitude, D_longitude)).build();
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(P_latitude, P_longitude))
                    .anchor(0.5f, 0.5f));
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(D_latitude, D_longitude))
                    .anchor(0.5f, 0.5f));

            LatLng pickup = new LatLng(P_latitude,P_longitude);
            LatLng drop = new LatLng(D_latitude,D_longitude);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(pickup, drop);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }else{
            final LatLng latlng = new LatLng(latitde,longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17f));
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitde, longitude))
                    .anchor(0.5f, 0.5f));
        }
    }

    private void InitializeForLocations() {
        pickup_location_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ispicklocation = 1;
                Intent i = new Intent(CargoBookingActivity.this,GetLocation.class);
                startActivity(i);
            }
        });

        drop_location_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ispicklocation = 2;
                Intent i = new Intent(CargoBookingActivity.this,GetLocation.class);
                startActivity(i);
            }
        });
    }

    private void InitializeForCheckBox() {
        volumetric_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   volumetric_input_details.setVisibility(View.VISIBLE);
               }else{
                   volumetric_input_details.setVisibility(View.GONE);
               }
            }
        });
    }

    private void InitializeForOnclicks() {
        cargosection.setVisibility(View.GONE);
        locationdetails.setVisibility(View.VISIBLE
        );
        cargo_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearlayoutcolor(cargo_onclick);
                cargosection.setVisibility(View.VISIBLE);
                locationdetails.setVisibility(View.GONE);
            }
        });

        location_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearlayoutcolor(location_onclick);
                locationdetails.setVisibility(View.VISIBLE);
                cargosection.setVisibility(View.GONE);
            }
        });
    }

    private void clearlayoutcolor(LinearLayout Clicklayout) {
        cargo_onclick.setBackgroundColor(getResources().getColor(R.color.white));
        location_onclick.setBackgroundColor(getResources().getColor(R.color.white));
        Clicklayout.setBackgroundColor(getResources().getColor(R.color.LoginAccent));
        if(Clicklayout == cargo_onclick){
            map_fargment.setVisibility(View.GONE);
        }else {
            map_fargment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                //clear data in the fields
                return true;
            case R.id.save:
                HitServer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void HitServer() {
    }
    //Direction $ JSON Url
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
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

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(getApplicationContext());

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        //static String distance = "50";
        //static String duration = "";
        private Context context;
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        public ParserTask(Context context) {
            this.context = context;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();


            System.out.println("vml check the resut value"+result);
            if(result.size()<1){
                Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

//                    if(j==0){    // Get distance from the list
//                        distance = (String)point.get("distance");
//                        continue;
//                    }else if(j==1){ // Get duration from the list
//                        duration = (String)point.get("duration");
//                        continue;
//                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }
            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
}
