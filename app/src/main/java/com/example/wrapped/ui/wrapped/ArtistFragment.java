package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.wrapped.R;
import com.example.wrapped.Spotify;
import org.json.JSONArray;
import org.json.JSONObject;

public class ArtistFragment extends Fragment {

    private TextView topArtistName;
    private TextView topArtistGenres;

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_top_artist, container, false);

        topArtistName = rootView.findViewById(R.id.top_artist_name);
        topArtistGenres = rootView.findViewById(R.id.top_artist_genre);

        displayTopArtist();

        return rootView;
    }

    private void displayTopArtist() {
        JSONObject topArtists = Spotify.getArtists();
        if (topArtists != null) {
            try {
                JSONArray items = topArtists.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topArtist = items.getJSONObject(0);
                    String name = topArtist.getString("name");
                    JSONArray genres = topArtist.getJSONArray("genres");
                    StringBuilder genresList = new StringBuilder();
                    Log.d("Top Artist", name);
                    for (int i = 0; i < genres.length(); i++) {
                        if (i > 0) genresList.append(", ");
                        genresList.append(genres.getString(i));
                    }
                    topArtistName.setText(name);
                    topArtistGenres.setText(genresList.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
