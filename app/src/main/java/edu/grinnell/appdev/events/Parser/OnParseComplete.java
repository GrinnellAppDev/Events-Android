package edu.grinnell.appdev.events.Parser;


import java.util.List;

import edu.grinnell.appdev.events.Model.Event;


/**
 * Provides methods to implement that is used by the Parser class
 * to notify the UI thread once the XML string has been parsed.
 */
public interface OnParseComplete {
    void onParseComplete(List<Event> eventList);
    void onParseFail(String failMsg);
}
