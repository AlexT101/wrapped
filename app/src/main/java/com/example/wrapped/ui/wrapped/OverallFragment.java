package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wrapped.R;
import com.example.wrapped.Spotify;

import org.json.JSONArray;
import org.json.JSONObject;

public class OverallFragment extends Fragment {
    private TextView number1_song;
    private TextView number2_song;
    private TextView number3_song;
    private TextView number4_song;
    private TextView number5_song;

    // Top artists TextViews
    private TextView number1_artist;
    private TextView number2_artist;
    private TextView number3_artist;
    private TextView number4_artist;
    private TextView number5_artist;

    public OverallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_overall, container, false);

        number1_song = rootView.findViewById(R.id.number1_song);
        number2_song = rootView.findViewById(R.id.number2_song);
        number3_song = rootView.findViewById(R.id.number3_song);
        number4_song = rootView.findViewById(R.id.number4_song);
        number5_song = rootView.findViewById(R.id.number5_song);

        // Initialize TextViews for artists
        number1_artist = rootView.findViewById(R.id.number1_artist);
        number2_artist = rootView.findViewById(R.id.number2_artist);
        number3_artist = rootView.findViewById(R.id.number3_artist);
        number4_artist = rootView.findViewById(R.id.number4_artist);
        number5_artist = rootView.findViewById(R.id.number5_artist);

        displayTopItems();

        return rootView;
    }
    private void displayTopItems() {
        displayTopTracks(Spotify.getTracks());
        displayTopArtists(Spotify.getArtists());
    }

    private void displayTopTracks(JSONObject tracks) {
        try {
            JSONArray items = tracks.getJSONArray("items");
            TextView[] songViews = {number1_song, number2_song, number3_song, number4_song, number5_song};

            for (int i = 0; i < Math.min(items.length(), songViews.length); i++) {
                JSONObject track = items.getJSONObject(i);
                String trackName = track.getString("name");
                songViews[i].setText(trackName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTopArtists(JSONObject artists) {
        try {
            JSONArray items = artists.getJSONArray("items");
            TextView[] artistViews = {number1_artist, number2_artist, number3_artist, number4_artist, number5_artist};

            for (int i = 0; i < Math.min(items.length(), artistViews.length); i++) {
                JSONObject artist = items.getJSONObject(i);
                String artistName = artist.getString("name");
                artistViews[i].setText(artistName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
