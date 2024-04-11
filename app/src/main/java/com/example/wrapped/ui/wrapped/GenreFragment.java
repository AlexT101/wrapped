package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wrapped.R;
import com.example.wrapped.Spotify;

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

    private TextView genre1_middle;
    private TextView genre2_middle;
    private TextView genre3_middle;
    private TextView genre4_middle;
    private TextView genre5_middle;


    public GenreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_genre, container, false);

        topGenreTextView = rootView.findViewById(R.id.Top_genre);

        genre1_middle = rootView.findViewById(R.id.genre1_middle);

        genre2_middle = rootView.findViewById(R.id.genre2_middle);

        genre3_middle = rootView.findViewById(R.id.genre3_middle);

        genre4_middle = rootView.findViewById(R.id.genre4_middle);

        genre5_middle = rootView.findViewById(R.id.genre5_middle);

        displayTopGenres();

        return rootView;
    }
    private void displayTopGenres() {
        JSONObject topArtists = Spotify.getArtists();
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

            // Sort genres by count and ensure uniqueness
            List<String> sortedGenres = genreCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Display the top genres in their respective TextViews
            TextView[] genreViews = {topGenreTextView, genre1_middle, genre2_middle, genre3_middle, genre4_middle, genre5_middle};

            for (int i = 0; i < Math.min(sortedGenres.size(), genreViews.length); i++) {
                genreViews[i].setText(sortedGenres.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
