package com.example.wrapped.ui.wrapped;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wrapped.R;
import com.example.wrapped.Wrap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PersonalityFragment extends Fragment {

    private TextView personalityTitleTextView;
    private ImageView personalityImage;

    private TextView personalityText;

    public PersonalityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_personality, container, false);

        personalityTitleTextView = rootView.findViewById(R.id.typeText);

        personalityImage = rootView.findViewById(R.id.type);

        personalityText = rootView.findViewById(R.id.bottomText);

        displayPersonality();

        return rootView;
    }

    private void displayPersonality() {
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

            // Count unique genres
            int uniqueGenreCount = genreCount.size();
            String personalityTitle;
            String personalityLine;

            if (uniqueGenreCount < 4) {
                personalityTitle = "The Replayer";
                personalityLine = "You’re always bopping the same tune,\n" +
                                  "and you take comfort in listening to\n" +
                                  "your same favorites.";
                personalityImage.setBackgroundResource(R.drawable.personality_replayer);


            } else if (uniqueGenreCount < 8) {
                personalityTitle = "The Mood Maven";
                personalityLine = "Your playlists change with your mood.\n" +
                                  " Whether you're feeling happy, or sad,\n" +
                                  "you have the perfect song \n" +
                                  "for every emotion.";
                personalityImage.setBackgroundResource(R.drawable.personality_mood);

            } else if (uniqueGenreCount < 15) {
                personalityTitle = "The Explorer";
                personalityLine = "You're always on the lookout for new \n" +
                                  "sounds and genres. \n" +
                                  "Your curiosity drives you to constantly\n" +
                                  "discover fresh music.";
                personalityImage.setBackgroundResource(R.drawable.personality_explorer);

            } else {
                personalityTitle = "The Eclecticist";
                personalityImage.setBackgroundResource(R.drawable.personality_eclecticist);
                personalityLine = "Your taste knows no bounds.\n" +
                                  "From classical to hip-hop, you\n" +
                                  "appreciate the beauty in\n" +
                                  "all types of music.";
            }
            // Update the personality title in UI
            personalityTitleTextView.setText(personalityTitle);
            personalityText.setText(personalityLine);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
