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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        displayTopArtists();
    }

    public void fetchPlayingSong() {
        JSONObject topTracks = Wrap.getCurrent().getTracks();
        if (topTracks != null) {
            try {
                JSONArray items = topTracks.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topTrack = items.getJSONObject(0);
                    JSONObject album = topTrack.getJSONObject("album");
                    String uri = album.getString("uri");
                    Log.d("uri", uri);

                    Spotify.onStart();
                    // Assuming uri is obtained from some source
                    Spotify.connected(uri);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayTopArtists() {
        JSONObject topArtists = Wrap.getCurrent().getArtists();
        if (topArtists != null) {
            try {
                JSONArray items = topArtists.getJSONArray("items");

                if (items.length() > 0) {
                    JSONObject artist1 = items.getJSONObject(0);
                    updateArtistImage(artist1, R.id.artist_1);

                    if (items.length() > 1) {
                        JSONObject artist2 = items.getJSONObject(1);
                        updateArtistImage(artist2, R.id.artist_2);
                    }
                    if (items.length() > 2) {
                        JSONObject artist3 = items.getJSONObject(2);
                        updateArtistImage(artist3, R.id.artist_3);
                    }
                    if (items.length() > 3) {
                        JSONObject artist4 = items.getJSONObject(3);
                        updateArtistImage(artist4, R.id.artist_4);
                    }
                    if (items.length() > 4) {
                        JSONObject artist5 = items.getJSONObject(4);
                        updateArtistImage(artist5, R.id.artist_5);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateArtistImage(JSONObject artist, int imageViewId) {
        try {
            JSONArray images = artist.getJSONArray("images");
            String imageUrl = images.length() > 0 ? images.getJSONObject(0).getString("url") : null;
            ImageView artistImageView = getView().findViewById(imageViewId);
            if (imageUrl != null) {
                new ImageLoadTask(imageUrl, artistImageView).execute();
            }
        } catch (JSONException e) {
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
