package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
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

        // Get the user's active devices
        String finalAccessToken = accessToken;
        String finalTopTrackId = topTrackId;
        fetchActiveDevice(accessToken, new DeviceIdConsumer() {
            @Override
            public void accept(List<String> deviceIds) {
                // If there are active devices, transfer playback to the first one and start playback
                if (!deviceIds.isEmpty()) {
                    transferPlaybackAndStartSong(finalAccessToken, deviceIds.get(0), finalTopTrackId);
                }
            }
        });
    }

    private void fetchActiveDevice(String accessToken, DeviceIdConsumer onDevicesFetched) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.spotify.com/v1/me/player/devices";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray devicesArray = jsonObject.getJSONArray("devices");
                    List<String> deviceIds = new ArrayList<>();

                    for (int i = 0; i < devicesArray.length(); i++) {
                        JSONObject deviceObject = devicesArray.getJSONObject(i);
                        if (deviceObject.getBoolean("is_active")) {
                            deviceIds.add(deviceObject.getString("id"));
                            break; // Break after finding the first active device
                        }
                    }

                    if (!deviceIds.isEmpty()) {
                        onDevicesFetched.accept(deviceIds);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                // Handle the response
            }
        });
    }

    // Custom consumer interface for handling device IDs
    public interface DeviceIdConsumer {
        void accept(List<String> deviceIds);
    }
}
