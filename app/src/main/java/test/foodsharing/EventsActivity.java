package test.foodsharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView welcomeText;
    private Button logoutButton;
    private Button favoritesButton;
    private Button myEventsButton;

    private Event[] events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText(getUser());

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        favoritesButton = findViewById(R.id.favorites_button);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFavorites();
            }
        });

        myEventsButton = findViewById(R.id.my_events_button);
        myEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMyEvents();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set adapter for recyclerview
        List<Event> events = getEvents();

        // sort events by date created
        events.sort(new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                if (event.getEventDateObject().compareTo(t1.getEventDateObject()) < 0) {
                    return -1;
                } else if (event.getEventDateObject().compareTo(t1.getEventDateObject()) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });


        mAdapter = new EventAdapter(events);
        ((EventAdapter) mAdapter).setUserEmail(getUser());
        recyclerView.setAdapter(mAdapter);

    }

    // get list of all events from the database
    private List<Event> getEvents() {
        DataSource ds = DataSource.getInstance();
        String events = ds.getEvents();

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

                if (e.getEventDateObject() != null && e.getEventDateObject().compareTo(new Date()) >= 1) {
                    eventsList.add(e);
                }
            }

        } catch (Exception e) {
            Log.d("error", e.toString());
        }

        return eventsList;
    }

    // go to edit profile activity
    public void gotoEditProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    // get the logged in user
    private String getUser() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        return sharedPref.getString("current_user", "");
    }

    // destroy the current session and log out user
    private void logout() {
        SharedPreferences sharedPref = this.getSharedPreferences("shared", Context.MODE_PRIVATE);
        sharedPref.edit().remove("current_user").apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    // go to favorites activity
    private void goToFavorites() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    // go to events owned by the user
    private void goToMyEvents() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        startActivity(intent);
    }

    // for testing
    private Event[] getFavorites() {
        int count = 0;
        for (int i = 0; i < events.length; i++) {
            if (events[i].getIsFavorite()) {
                count++;
            }
        }
        Event[] newArr = new Event[count];

        int currIndex = 0;
        for (int i = 0; i < events.length; i++) {
            if (events[i].getIsFavorite()) {
                newArr[currIndex] = events[i];
                currIndex++;
            }
        }

        return newArr;

    }
}
