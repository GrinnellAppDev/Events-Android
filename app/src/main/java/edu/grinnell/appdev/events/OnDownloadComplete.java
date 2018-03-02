package edu.grinnell.appdev.events;

/**
 * Provides methods to implement that is used by the Downloader class
 * to notify the UI thread once the XML file has been downloaded.
 */
public interface OnDownloadComplete {
    void onDownloadComplete(String result);
    void onDownloadFail(String failMsg);
}
