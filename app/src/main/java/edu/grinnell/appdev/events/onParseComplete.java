package edu.grinnell.appdev.events;


import java.util.List;


/**
 * Provides methods to implement that is used by the Parser class
 * to notify the UI thread once the XML string has been parsed.
 */
public interface onParseComplete {
    void onParseComplete(List<Event> eventList);
    void onParseFail(String failMsg);
}
