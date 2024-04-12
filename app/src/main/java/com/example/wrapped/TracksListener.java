package com.example.wrapped;

import org.json.JSONObject;

public interface TracksListener {
    void onTracksUpdate(JSONObject newTracks);
}