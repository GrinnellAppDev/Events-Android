package edu.grinnell.appdev.events;

/**
 * Represents an event model. An event object holds the
 * information about any specific event that is posted on the Grinnell Calender
 */
public class Event {
    private String title;
    private String date;
    private String content;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }



}
