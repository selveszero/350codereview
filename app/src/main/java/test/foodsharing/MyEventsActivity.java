package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Displays user owned events - very similar to EventsActivity

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        recyclerView = findViewById(R.id.my_events_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<Event> events = getMyEvents();

        mAdapter = new EventAdapter(events);
        ((EventAdapter) mAdapter).setHideFavoriteButton(true);
        ((EventAdapter) mAdapter).setUserEmail(getUserEmail());
        recyclerView.setAdapter(mAdapter);

    }

    private String getUserEmail() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        String email = sharedPref.getString("current_user", "");
        return email;
    }

    private List<Event> getMyEvents() {

        DataSource ds = DataSource.getInstance();
        String events = ds.getMyEvents(getUserEmail());

        JSONObject jsonObject = null;
        ArrayList<Event> eventsList = new ArrayList<Event>();

        try {
            jsonObject = new JSONObject(events);
            JSONArray array = jsonObject.getJSONArray("events");

            for (int i = 0; i < array.length(); i++) {
                String id = array.getJSONObject(i).getString("_id");
                String eventOwner = array.getJSONObject(i).getString("eventOwner");
                String name = array.getJSONObject(i).getString("name");
                String date = array.getJSONObject(i).getString("date");
                String location = array.getJSONObject(i).getString("location");
                String food = array.getJSONObject(i).getString("food");
                Event e = new Event(id, eventOwner, name, date, location, food);
                eventsList.add(i, e);
            }

        } catch (Exception e) {
            Log.d("error", e.toString());
        }

        return eventsList;
    }
}
