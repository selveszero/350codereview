package test.foodsharing;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

// Event object with all event details stored
public class Event implements Serializable {

    private String id;
    private String eventOwner;
    private String eventName;
    private String date;
    private String location;
    private String typeOfFood;
    private Date eventDate;
    private boolean isFavorite = false;

    public Event(String id, String eventOwner, String eventName, String date, String location, String typeOfFood) {
        this.id = id;
        this.eventOwner = eventOwner;
        this.eventName = eventName;
        this.date = date;
        this.location = location;
        this.typeOfFood = typeOfFood;

        convertToDate(this.date);
    }

    // convert input string into a Date object
    private void convertToDate(String d) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String dateInString = d;

        try {

            Date date = formatter.parse(dateInString);
            this.eventDate = date;

        } catch (ParseException e) {
            this.eventDate = null;
        }
    }

    public String getEventId() { return this.id; }

    public String getEventOwner() { return this.eventOwner; }

    public String getEventName() {
        return this.eventName;
    }

    public String getEventDate() { return this.date; }

    public String getEventLocation() { return this.location; }

    public String getEventFood() { return this.typeOfFood; }

    public void setIsFavorite(boolean f) {
        this.isFavorite = f;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public Date getEventDateObject() { return this.eventDate; }

}
