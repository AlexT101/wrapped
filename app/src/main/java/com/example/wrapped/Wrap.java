package com.example.wrapped;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Wrap {

    private static Wrap currentWrap;
    private static ArrayList<Wrap> allWraps = new ArrayList<>();

    public static Wrap getCurrent() {
        return currentWrap;
    }

    public static void setCurrent(Wrap newWrap) {
        currentWrap = newWrap;
    }

    //FIREBASE SYNC FUNCTIONS---------------------------------------------------------

    //Call this function when the user logs in to load all wraps from firebase
    public static void loadFromFirebase(ArrayList<Wrap> all) {
        allWraps = all;
    }

    //Finish writing this function to add a Wrap to Firebase
    public static void addToFirebase(Wrap wrap) {

    }
    //--------------------------------------------------------------------------------

    public static Wrap get(int index) {
        return allWraps.get(index);
    }

    public static void add(Wrap newWrap) {
        allWraps.add(newWrap);
    }

    public static Wrap createWrap(String timeSpan) {
        Wrap newWrap = new Wrap(timeSpan);
        setCurrent(newWrap);
        add(newWrap);
        addToFirebase(newWrap);
        return newWrap;
    }

    private String timeSpan;
    private String date;
    private JSONObject tracks;
    private JSONObject artists;

    public Wrap(String time) {
        timeSpan = time;
        date = getCurrentDate();
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String time) {
        timeSpan = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String newDate) {
        date = newDate;
    }

    public JSONObject getTracks() {
        return tracks;
    }

    public void setTracks(JSONObject newTracks) {
        tracks = newTracks;
    }

    public JSONObject getArtists(){return artists;}

    public void setArtists(JSONObject newArtists) {
        artists = newArtists;
    }

    public static String getCurrentDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        return date.format(formatter);
    }
}
