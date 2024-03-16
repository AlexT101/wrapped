package com.example.wrapped;

import org.json.JSONObject;

public interface ProfileListener {
    void onProfileUpdate(JSONObject newProfile);
}