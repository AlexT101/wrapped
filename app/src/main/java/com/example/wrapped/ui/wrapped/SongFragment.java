package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wrapped.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class SongFragment extends Fragment {

    private TextView topTrackName;
    private TextView topTrackArtist;

    public SongFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_top_song, container, false);

        topTrackName = rootView.findViewById(R.id.songTitle);
        topTrackArtist = rootView.findViewById(R.id.songArtist);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String topTracksJson = bundle.getString("top_tracks");
            displayTopTrack(topTracksJson);
        }

        return rootView;
    }

    private void displayTopTrack(String topTracksJson) {
        try {
            JSONObject jsonObject = new JSONObject(topTracksJson);
            JSONArray items = jsonObject.getJSONArray("items");
            if (items.length() > 0) {
                JSONObject topTrack = items.getJSONObject(0);
                String name = topTrack.getString("name");
                JSONArray artists = topTrack.getJSONArray("artists");
                StringBuilder artistNames = new StringBuilder();
                for (int i = 0; i < artists.length(); i++) {
                    if (i > 0) artistNames.append(", ");
                    artistNames.append(artists.getJSONObject(i).getString("name"));
                }

                topTrackName.setText(name);
                topTrackArtist.setText(artistNames.toString());
            }
        } catch (Exception e) {
            Log.e("SongFragment", "Error parsing top tracks data", e);
        }
    }
}
