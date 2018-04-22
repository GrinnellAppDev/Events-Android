package edu.grinnell.appdev.events;

import java.util.Date;

/**
 * Represents an event model. An event object holds the
 * information about any specific event that is posted on the Grinnell Calender
 */
public class Event {
    private String title;
    private String content;
    private String location;
    private Date startTime;
    private Date endTime;


    public void setTitle(String title) {this.title = title;}

    public void setStartTime(Date startTime) {this.startTime = startTime;}

    public void setEndTime(Date endTime) {this.endTime = endTime;}

    public void setContent(String content) {this.content = content;}

    public void setLocation(String location) { this.location = location; }

    public Date getStartTime() {return this.startTime;}

    public Date getEndTime() {return this.endTime;}

    public String getTitle() {return title;}

    public String getContent() {return content;}

    public String getLocation() { return location; }

}
