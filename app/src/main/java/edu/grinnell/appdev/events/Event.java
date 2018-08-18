package edu.grinnell.appdev.events;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.BottomNavigationView;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an event model. An event object holds the
 * information about any specific event that is posted on the Grinnell Calender
 */
public class Event implements Parcelable{
    private String title;
    private String content;
    private String location;
    private Date startTime;
    private Date endTime;
    private Long startTimeMillis;
    private Long endTimeNewMillis;
    private String email;
    private String organizer;
    private String id;

    public Event(String title, String content, String location,
                 Long startTime, Long endTime, String email,
                 String organizer, String id) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.startTimeMillis = startTime;
        this.endTimeNewMillis = endTime;
        this.email = email;
        this.id = id;
    }

    protected Event(Parcel in) {
        title = in.readString();
        content = in.readString();
        location = in.readString();
        email = in.readString();
        organizer = in.readString();
        id = in.readString();
        startTimeMillis = in.readLong();
        endTimeNewMillis = in.readLong();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public void setTitle(String title) {this.title = title;}

    public void setStartTime(Date startTime) {this.startTime = startTime;}

    public void setEndTime(Date endTime) {this.endTime = endTime;}

    public void setContent(String content) {this.content = content;}

    public void setLocation(String location) { this.location = location; }

    public void setEmail(String email) {this.email = email;}

    public void setOrganizer(String organizer) {this.organizer = organizer;}

    public void setID(String id) {this.id = id;}

    public Date getStartTime() {return this.startTime;}

    public Date getEndTime() {return this.endTime;}

    public Long getStartTimeNew() {return this.startTimeMillis;}

    public Long getEndTimeNew() {return this.endTimeNewMillis;}

    public String getTitle() {return title;}

    public String getContent() {return content;}

    public String getLocation() { return location; }

    public String getEmail() { return email; }

    public String getOrganizer() { return organizer; }

    public String getId() {return id;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(location);
        dest.writeString(email);
        dest.writeString(organizer);
        dest.writeString(id);
        dest.writeLong(startTimeMillis);
        dest.writeLong(endTimeNewMillis);
    }
}
