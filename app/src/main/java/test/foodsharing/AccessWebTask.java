package test.foodsharing;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class AccessWebTask extends AsyncTask<URL, String, String> {

    private int method;
    private String name;
    private String email;
    private String password;
    private String pwConfirm;
    private String school;
    private String eventId;

    public AccessWebTask(int i) {
        super();
        this.method = i;
    }


    public AccessWebTask(int i, String n, String e, String p, String pc, String s) {
        super();
        this.method = i;
        this.name = n;
        this.email = e;
        this.password = p;
        this.pwConfirm = pc;
        this.school = s;
    }

    public AccessWebTask(int i, String email, String eventId) {
        super();
        this.method = i;
        this.email = email;
        this.eventId = eventId;
    }

    public AccessWebTask(int i, String email) {
        super();
        this.method = i;
        this.email = email;
    }


    @Override
    protected String doInBackground(URL... urls) {
        try {
            // return the password of the user
            if (method == 1) {
                URL url = urls[0];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();

                JSONObject jo = new JSONObject(msg);
                String password = jo.getString("password");
                return password;
            }
            else if (method == 2) {

                // creates a new account

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", this.name)
                        .appendQueryParameter("email", this.email)
                        .appendQueryParameter("password", this.password)
                        .appendQueryParameter("passwordConfirm", this.pwConfirm)
                        .appendQueryParameter("school", this.school);
                String data = builder.build().getEncodedQuery();

                URL url = urls[0];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                String result = sb.toString();
                Log.d("hello", "Response from php = " + result);
                //Response = new JSONObjec
                conn.disconnect();
            }
            else if (method == 3) {

                // gets all of the events

                URL url = urls[0];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();

                JSONObject jo = new JSONObject(msg);
                String events = jo.toString();
                return events;
            }
            else if (method == 4) {

                // get all events owned by the user

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", this.email)
                        .appendQueryParameter("event_id", this.eventId);
                String data = builder.build().getEncodedQuery();

                URL url = urls[0];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                JSONObject jo = new JSONObject(sb.toString());
                String result = jo.getString("result");
                conn.disconnect();

                return result;

            }
            else if (method == 5) {

                // get all favorited events (by the user)

                URL url = urls[0];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();

                JSONObject jo = new JSONObject(msg);
                String favorites = jo.toString();
                return favorites;
            }
        } catch (Exception e) {
            Log.d("Web task exception", e.toString());
            return null;
        }

        return null;

    }
}
