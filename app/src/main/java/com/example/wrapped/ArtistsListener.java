package com.example.wrapped;

import org.json.JSONObject;

public interface ArtistsListener {
    void onArtistsUpdate(JSONObject newArtists);
}