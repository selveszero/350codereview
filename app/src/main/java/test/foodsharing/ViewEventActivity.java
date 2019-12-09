package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.MapFragment;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Displays event details when clicked on

public class ViewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Push notification stuff
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "key=" + "AAAAx1Qj1JE:APA91bF2EDDjjLWeAbmWniyOwMT5v0HnLctGlzTjXNnlqmUVpL-fUCwgVphAuwVlM7ly7lcd7OrF2IozU2Rt9jVoXa8LHmOUlKZLWrq1CO31sPpt0qzVJ5vi7KMA8XD96io4tbfB8EH_";
    private String contentType = "application/json";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        final String eventId = getIntent().getStringExtra("eventId");
        String eventOwner = getIntent().getStringExtra("eventOwner");
        String eventName = getIntent().getStringExtra("eventName");
        String eventDate = getIntent().getStringExtra("eventDate");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        String eventFood = getIntent().getStringExtra("eventFood");

        // Push notifications
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());

        // Initialize views
        TextView eventIdTv = findViewById(R.id.event_id);
        eventIdTv.setText(eventId);

        TextView eventOwnerTv = findViewById(R.id.event_owner);
        eventOwnerTv.setText(eventOwner);

        TextView eventNameTv = findViewById(R.id.event_name);
        eventNameTv.setText(eventName);

        TextView eventDateTv = findViewById(R.id.event_date);
        eventDateTv.setText(eventDate);

        TextView eventLocationTv = findViewById(R.id.event_location);
        eventLocationTv.setText(eventLocation);

        TextView eventFoodTv = findViewById(R.id.event_food);
        eventFoodTv.setText(eventFood);

        Button notifyButton = findViewById(R.id.notify_button);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testNotification(eventId);
            }
        });

        if (!eventOwner.equals(getUserEmail())) {
            notifyButton.setVisibility(View.GONE);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().bringToFront();

    }

    // get the user's email
    private String getUserEmail() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        String email = sharedPref.getString("current_user", "");
        return email;
    }

    // display push notification (event details not filled in yet)
    private void testNotification(String eventId) {
        String topic = "/topics/" + eventId;
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            notificationBody.put("title", "Title");
            notificationBody.put("message", "Oh word");
            notification.put("to", topic);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e("Create json error", e.getMessage());
        }

        sendNotification(notification);
    }

    // handles the sending of push notifications
    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewEventActivity.this, "Request error", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }

        };

        requestQueue.add(jsonObjectRequest);

    }

    // handles the creation of the map fragment
    @Override
    public void onMapReady(GoogleMap googleMap) {
        double latitude = 39.952583;
        double longitude =  -75.165222;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Philadelphia, PA"));
    }
}
