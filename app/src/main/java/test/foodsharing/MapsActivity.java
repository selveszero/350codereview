package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.MapFragment;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;

// test activity for Google MAps integration

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        double latitude = 39.952583;
        double longitude =  -75.165222;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Philadelphia, PA"));
    }
}
