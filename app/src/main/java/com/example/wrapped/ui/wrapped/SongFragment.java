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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SongFragment extends Fragment {

    private TextView topTrackName;
    private TextView topTrackArtist;
    private ImageView topTrackAlbumImage;

    public SongFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_top_song, container, false);

        topTrackName = rootView.findViewById(R.id.songTitle);
        topTrackArtist = rootView.findViewById(R.id.songArtist);
        topTrackAlbumImage = rootView.findViewById(R.id.songCover);



        displayTopTrack();
        return rootView;
    }



    private void displayTopTrack() {
        JSONObject topTracks = Spotify.getTracks();
        if (topTracks != null) {
            try {
                JSONArray items = topTracks.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject topTrack = items.getJSONObject(0);
                    String name = topTrack.getString("name");
                    JSONObject album = topTrack.getJSONObject("album");
                    JSONArray images = album.getJSONArray("images");
                    String imageUrl = images.length() > 0 ? images.getJSONObject(0).getString("url") : null;
                    //Log.d("Top Track", name);
                    JSONArray artists = topTrack.getJSONArray("artists");
                    StringBuilder artistNames = new StringBuilder();
                    for (int i = 0; i < artists.length(); i++) {
                        if (i > 0) artistNames.append(", ");
                        artistNames.append(artists.getJSONObject(i).getString("name"));
                    }

                    topTrackName.setText(name);
                    topTrackArtist.setText(artistNames.toString());
                    if (imageUrl != null) {
                        new ImageLoadTask(imageUrl, topTrackAlbumImage).execute();
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
