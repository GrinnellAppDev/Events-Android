package edu.grinnell.appdev.events;

import android.support.design.widget.BottomNavigationView;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an event model. An event object holds the
 * information about any specific event that is posted on the Grinnell Calender
 */
public class Event implements Serializable{
    private String title;
    private String content;
    private String location;
    private Date startTime;
    private Date endTime;
    private String email;
    private String organizer;
    private String id;

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

    public String getTitle() {return title;}

    public String getContent() {return content;}

    public String getLocation() { return location; }

    public String getEmail() { return email; }

    public String getOrganizer() { return organizer; }

    public String getId() {return id;}
}
