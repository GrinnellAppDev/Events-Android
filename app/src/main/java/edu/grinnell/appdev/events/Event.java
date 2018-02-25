package edu.grinnell.appdev.events;

/**
 * Created by ScutisorexThori on 2/15/18.
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
