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
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArtistFragment extends Fragment {

    private TextView topArtistName;
    private TextView topArtistGenres;
    private ImageView topArtistImage;

    public ArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_top_artist, container, false);

        topArtistName = rootView.findViewById(R.id.top_artist_name);
        topArtistGenres = rootView.findViewById(R.id.top_artist_genre);
        topArtistImage = rootView.findViewById(R.id.top_artist_profile);

        displayTopArtist();

        return rootView;
    }

    private void displayTopArtist() {
        JSONObject topArtists = Wrap.getCurrent().getArtists();
        if (topArtists != null) {
            try {
                JSONArray items = topArtists.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topArtist = items.getJSONObject(0);
                    String name = topArtist.getString("name");
                    JSONArray genres = topArtist.getJSONArray("genres");
                    JSONArray images = topArtist.getJSONArray("images");
                    String imageUrl = images.length() > 0 ? images.getJSONObject(0).getString("url") : null;
                    StringBuilder genresList = new StringBuilder();
                    Log.d("Top Artist", name);
                    for (int i = 0; i < genres.length(); i++) {
                        if (i > 0) genresList.append(", ");
                        genresList.append(genres.getString(i));
                    }
                    topArtistName.setText(name);
                    topArtistGenres.setText(genresList.toString());
                    if (imageUrl != null) {
                        new ImageLoadTask(imageUrl, topArtistImage).execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
