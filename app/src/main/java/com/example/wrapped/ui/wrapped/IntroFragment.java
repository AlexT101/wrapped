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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IntroFragment extends Fragment {

    public IntroFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_intro, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assuming you already have the access token and the ID of the top track
        String accessToken = "";
        String topTrackId = "";

        try {
            accessToken = Spotify.getToken();
            // Assuming getTracks() returns a JSONObject of the top track which has an "id" field
            JSONObject topTracks = Spotify.getTracks();
            if (topTracks != null && topTracks.has("items")) {
                JSONArray items = topTracks.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topTrack = items.getJSONObject(0);
                    topTrackId = topTrack.getString("id");
                }
            }
        } catch (JSONException e) {
            // Handle exception related to JSON parsing
            e.printStackTrace();
        } catch (Exception e) {
            // Handle any other exceptions
            e.printStackTrace();
        }

        fetchPlayingSong();
    }

    private void fetchPlayingSong() {
        JSONObject topTracks = Spotify.getTracks();
        if (topTracks != null) {
            try {
                JSONArray items = topTracks.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topTrack = items.getJSONObject(0);
                    JSONObject album = topTrack.getJSONObject("album");
                    String uri = album.getString("uri");
                    Log.d("uri", uri);

                    String requestBody = "{\n" +
                            "    \"context_uri\": \"" + uri + "\",\n" +
                            "    \"position_ms\": 0\n" +
                            "}";
                    Spotify.playSong(requestBody);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void transferPlaybackAndStartSong(String accessToken, String deviceId, String trackId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.spotify.com/v1/me/player/play?device_id=" + deviceId;

        String jsonPayload = "{\"uris\":[\"spotify:track:" + trackId + "\"]}";
        RequestBody body = RequestBody.create(
                jsonPayload,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Playback transfer was successful
                    Log.d("IntroFragment", "Playback started on device with ID: " + deviceId);
                } else {
                    // Handle errors
                    Log.e("IntroFragment", "Failed to start playback: " + response.code() + " - " + response.message());
                }
            }
        });
    }

    // Custom consumer interface for handling device IDs
    public interface DeviceIdConsumer {
        void accept(List<String> deviceIds);
    }
}
