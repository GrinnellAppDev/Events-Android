package edu.grinnell.appdev.events;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents an event model. An event object holds the
 * information about any specific event that is posted on the Grinnell Calender. It implements
 * parcelable so that Event list can be passed to a fragment using a bundle
 */
public class Event implements Parcelable{
    private String title;
    private String content;
    private String location;
    private Date startTime;
    private Date endTime;
    private Long startTimeMillis;
    private Long endTimeMillis;
    private String submitter;
    private String submitterEmail;
    private String organizer;
    private String id;
    private int isDivider;

    public Event(String title, String content, String location,
                 Long startTime, Long endTime, String submitter, String submitterEmail,
                 String organizer, String id, int isDivider) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.startTimeMillis = startTime;
        this.endTimeMillis = endTime;
        this.organizer = organizer;
        this.submitter = submitter;
        this.submitterEmail = submitterEmail;
        this.id = id;
        this.isDivider = isDivider;
    }

    //To be read in the same order that the parcel was written
    protected Event(Parcel in) {
        title = in.readString();
        content = in.readString();
        location = in.readString();
        submitter = in.readString();
        submitterEmail = in.readString();
        organizer = in.readString();
        id = in.readString();
        startTimeMillis = in.readLong();
        endTimeMillis = in.readLong();
        isDivider = in.readInt();
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

    public void setEmail(String email) {this.submitterEmail = email;}

    public void setOrganizer(String organizer) {this.organizer = organizer;}

    public void setID(String id) {this.id = id;}

    public void setSubmitter (String submitter){ this.submitter = submitter;}

    public Date getStartTime() {return this.startTime;}

    public Date getEndTime() {return this.endTime;}

    public Long getStartTimeNew() {return this.startTimeMillis;}

    public Long getEndTimeNew() {return this.endTimeMillis;}

    public String getTitle() {return title;}

    public String getContent() {return content;}

    public String getLocation() { return location; }

    public String getEmail() { return submitterEmail; }

    public String getOrganizer() { return organizer; }

    public String getSubmitter() { return submitter; }

    public String getId() {return id;}

    public int getIsDivider() {return isDivider;}

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(location);
        dest.writeString(submitter);
        dest.writeString(submitterEmail);
        dest.writeString(organizer);
        dest.writeString(id);
        dest.writeLong(startTimeMillis);
        dest.writeLong(endTimeMillis);
        dest.writeInt(isDivider);
    }
}
