package test.foodsharing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

// Fill sin the RecyclerView for all Events

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> data;
    private boolean hideFavoriteButton = false;
    private String email;

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView eventName;
        private Button favoriteButton;
        private TextView dateLabel;

        private final Context context;

        private Event event;

        public EventViewHolder(View v) {
            super(v);
            this.context = v.getContext();
            v.setOnClickListener(this);
            eventName = v.findViewById(R.id.event_name);
            favoriteButton = v.findViewById(R.id.favorite_button);
            dateLabel = v.findViewById(R.id.date_label);

            String dateLabelText = "N/A";
            if (event != null) {
                dateLabelText = event.getEventDate() + "";
            }
            dateLabel.setText(dateLabelText);
        }

        public void setEvent(Event e) {
            this.event = e;
        }

        public void setEventName(String s) {
            eventName.setText(s);
        }

        public void setEventDate(String s) { dateLabel.setText(s); }

        public void hideFavoriteButton() {
            favoriteButton.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewEventActivity.class);
            intent.putExtra("eventId", this.event.getEventId());
            intent.putExtra("eventOwner", this.event.getEventOwner());
            intent.putExtra("eventName", this.event.getEventName());
            intent.putExtra("eventDate", this.event.getEventDate());
            intent.putExtra("eventLocation", this.event.getEventLocation());
            intent.putExtra("eventFood", this.event.getEventFood());
            context.startActivity(intent);
        }

    }

    public EventAdapter(List<Event> data) {
        this.data = data;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        EventViewHolder vh = new EventViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.setEvent(data.get(position));
        holder.setEventDate(data.get(position).getEventDate());
        holder.setEventName(data.get(position).getEventName());

        if (hideFavoriteButton) {
            holder.hideFavoriteButton();
        }

        final int currPosition = position;

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIsFavorite(currPosition);
            }
        });

    }

    // defines functionality for when a user favorites an event
    private void setIsFavorite(int position) {
        Event e = data.get(position);
        String eventId = e.getEventId();

        DataSource ds = DataSource.getInstance();
        String result = ds.favoriteEvent(email, eventId);

        if (result != null) {
            if (result.equals("added")) {
                FirebaseMessaging.getInstance().subscribeToTopic(eventId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Favorited event!";
                                if (!task.isSuccessful()) {
                                    msg = "Error in favoriting";
                                }
                                Log.d("hello", msg);
                            }
                        });
            } else if (result.equals("removed")) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(eventId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Unfavorited event!";
                                if (!task.isSuccessful()) {
                                    msg = "Error in unfavoriting";
                                }
                                Log.d("hello", msg);
                            }
                        });
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setHideFavoriteButton(boolean b) {
        this.hideFavoriteButton = b;
    }

    public void setUserEmail(String email) {
        this.email = email;
    }


}
