package cargo.com.testing.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import cargo.com.testing.R;
import cargo.com.testing.Service.GPSTracker;
import cargo.com.testing.Service.LocationAddress;

/**
 * Created by Vcreativedevelopers on 05-02-2017.
 */
public class GetLocation extends AppCompatActivity implements OnMapReadyCallback {
    String locationAddress;
    public static GoogleMap maps;
    public static LatLng lastlatlng;
    public static double latitude;
    public static double longitude;
    CameraUpdate cameraUpdate = null;
    @Bind(R.id.okay)
    Button okay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_location_map);
        ButterKnife.bind(this);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude = lastlatlng.latitude;
                longitude = lastlatlng.longitude;
                if(CargoBookingActivity.ispicklocation == 1){
                    CargoBookingActivity.P_latitude = latitude;
                    CargoBookingActivity.P_longitude = longitude;
                }else if(CargoBookingActivity.ispicklocation == 2){
                    CargoBookingActivity.D_latitude = latitude;
                    CargoBookingActivity.D_longitude = longitude;
                }
                LocationConvet();
            }
        });
    }

    private void LocationConvet() {
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude,
                getApplicationContext(), new GeocoderHandler());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;
        final GPSTracker gps = new GPSTracker(GetLocation.this);
        final double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        System.out.println("current location "+latitude +longitude);
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
        maps.setMyLocationEnabled(true);
        maps.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location loc = gps.getLocation();
                if (loc != null) {
                    LatLng latLang = new LatLng(loc.getLatitude(), loc
                            .getLongitude());
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 17);
                    maps.animateCamera(cameraUpdate);

                }else{
                    //location not null
                }
                return false;
            }
        });
        final LatLng latlng = new LatLng(latitude,longitude);

         maps.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,16f));
         maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
             @Override
             public void onMapClick(LatLng latLng) {
                 MarkerOptions marks = new MarkerOptions();

                 marks.position(latLng);

                 marks.title("Pick up Location");

                 System.out.println("vml check the lat and long ");
                 maps.clear();
                 maps.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                 lastlatlng = latLng;
                 maps.addMarker(marks);
             }
         });
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            CargoBookingActivity.address_value = "";
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = "null";
            }
            CargoBookingActivity.address_value  = locationAddress;
            System.out.println("vml check the DDRESS   "+locationAddress);
            finish();
        }
    }
}
