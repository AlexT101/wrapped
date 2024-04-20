package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wrapped.R;
import com.example.wrapped.Spotify;
import com.example.wrapped.Wrap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenreFragment extends Fragment {

    private TextView topGenreTextView;


    public GenreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_genre, container, false);

        topGenreTextView = rootView.findViewById(R.id.Top_genre);

        displayTopGenre();

        return rootView;
    }
    private void displayTopGenre() {
        JSONObject topArtists = Wrap.getCurrent().getArtists();  // Get top artists data
        if (topArtists == null) {
            return;
        }

        try {
            JSONArray items = topArtists.getJSONArray("items");
            Map<String, Integer> genreCount = new HashMap<>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject artist = items.getJSONObject(i);
                JSONArray genres = artist.getJSONArray("genres");

                for (int j = 0; j < genres.length(); j++) {
                    String genre = genres.getString(j);
                    genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                }
            }

            // Find the most common genre
            String topGenre = Collections.max(genreCount.entrySet(), Map.Entry.comparingByValue()).getKey();

            topGenreTextView.setText(topGenre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
