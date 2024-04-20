package com.example.wrapped.ui.wrapped;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wrapped.R;
import com.example.wrapped.Spotify;
import com.example.wrapped.Wrap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    private TextView top_Genre;
    private ImageView albumCover;

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

        number1_artist = rootView.findViewById(R.id.number1_artist);
        number2_artist = rootView.findViewById(R.id.number2_artist);
        number3_artist = rootView.findViewById(R.id.number3_artist);
        number4_artist = rootView.findViewById(R.id.number4_artist);
        number5_artist = rootView.findViewById(R.id.number5_artist);

        top_Genre = rootView.findViewById(R.id.overall_genre);
        albumCover = rootView.findViewById(R.id.overall_album_cover);

        displayTopItems();

        return rootView;
    }

    private void displayTopItems() {
        JSONObject tracks = Wrap.getCurrent().getTracks();
        displayTopTracks(tracks);
        displayTopArtists(Wrap.getCurrent().getArtists());
        displayAlbumCover(tracks);
        displayTopGenre(tracks);
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

    private void displayTopGenre(JSONObject tracks) {
        try {
            JSONArray items = Wrap.getCurrent().getArtists().getJSONArray("items");
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

            top_Genre.setText(topGenre);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void displayAlbumCover(JSONObject tracks) {
        try {
            JSONArray items = tracks.getJSONArray("items");
            if (items.length() > 0) {
                JSONObject topTrack = items.getJSONObject(0);
                JSONObject album = topTrack.getJSONObject("album");
                JSONArray images = album.getJSONArray("images");
                String imageUrl = images.length() > 0 ? images.getJSONObject(0).getString("url") : null;

                if (imageUrl != null) {
                    new ImageLoadTask(imageUrl, albumCover).execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
        private final String url;
        private final ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
