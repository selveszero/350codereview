package test.foodsharing;

import android.util.Log;

import java.net.URL;

// makes the http requests to the server to retrieve data

public class DataSource {

    private static DataSource instance = new DataSource();

    private DataSource() {}

    public static DataSource getInstance() {
        return instance;
    }

    // get the password of the user
    public String getPassword(String email) {
        try {
            URL url = new URL("http://10.0.2.2:3000/api?email=" + email);
            AccessWebTask accessWebTask = new AccessWebTask(1);
            accessWebTask.execute(url);
            String password = accessWebTask.get();
            return password;
        } catch (Exception e) {
            Log.d("Data source exception", e.toString());
            return null;
        }
    }

    // get all approved events
    public String getEvents() {
        try {
            URL url = new URL("http://10.0.2.2:3000/getAllEvents");
            AccessWebTask accessWebTask = new AccessWebTask(3);
            accessWebTask.execute(url);
            String events = accessWebTask.get();
            return events;
        } catch (Exception e) {
            Log.d("Data source exception", e.toString());
            return null;
        }
    }

    // get all events owned by the user
    public String getMyEvents(String id) {
        try {
            URL url = new URL("http://10.0.2.2:3000/getMyEvents?id=" + id);
            AccessWebTask accessWebTask = new AccessWebTask(3);
            accessWebTask.execute(url);
            String events = accessWebTask.get();
            return events;
        } catch (Exception e) {
            Log.d("Data source exception", e.toString());
            return null;
        }
    }

    // create a new account
    public void createAccount(String name, String email, String password, String pwConfirm, String school) {
        try {
            URL url = new URL("http://10.0.2.2:3000/create");
            AccessWebTask accessWebTask = new AccessWebTask(2, name, email, password, pwConfirm, school);
            accessWebTask.execute(url);
        } catch (Exception e) {
            Log.d("Create account data source exception", e.toString());
        }
    }

    // add an event to list of favorites
    public String favoriteEvent(String email, String eventId) {
        try {
            URL url = new URL("http://10.0.2.2:3000/favoriteEvent");
            AccessWebTask accessWebTask = new AccessWebTask(4, email, eventId);
            accessWebTask.execute(url);
            String result = accessWebTask.get();
            Log.d("hello", result);
            return result;
        } catch (Exception e) {
            Log.d("Favorite event data source exception", e.toString());
            return null;
        }
    }

    // get all favorited events
    public String getFavoriteEvents(String email) {
        try {
            URL url = new URL("http://10.0.2.2:3000/getFavoriteEvents?email=" + email);
            AccessWebTask accessWebTask = new AccessWebTask(5, email);
            accessWebTask.execute(url);
            String events = accessWebTask.get();
            return events;
        } catch (Exception e) {
            Log.d("Get favorites data source exception", e.toString());
            return null;
        }
    }

}
