package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wrapped.R;
import com.example.wrapped.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class IntroFragment extends Fragment {

    public IntroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_intro, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchPlayingSong();
    }

    private void fetchPlayingSong() {
        Spotify.fetchActiveDevice(deviceIds -> {
            if (!deviceIds.isEmpty()) {
                String activeDeviceId = deviceIds.get(0); // Assuming we use the first active device

                JSONObject topTracks = Spotify.getTracks();
                if (topTracks != null) {
                    try {
                        JSONArray items = topTracks.getJSONArray("items");
                        if (items.length() > 0) {
                            JSONObject topTrack = items.getJSONObject(0);
                            String uri = topTrack.getString("uri");  // Assuming 'uri' is directly under the track object
                            Log.d("uri", uri);

                            String requestBody = "{\n" +
                                    "    \"context_uri\": \"" + uri + "\",\n" +
                                    "    \"offset\": {\"position\": 0},\n" +  // Assuming you want to start at the first track
                                    "    \"position_ms\": 0\n" +
                                    "}";
                            Spotify.playSong(requestBody, activeDeviceId);  // Adjusted to pass device ID
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("Spotify", "No active devices found.");
            }
        });
    }

}
