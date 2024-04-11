package com.example.wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

public class LoadingActivity extends Activity {

    final int loadingDuration = 2000;
    final int transitionDuration = 200;
    final int readyDuration = 400;

    private ProgressBar progressBar;
    private TextView loadingTextView;
    private ImageView checkMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        checkMark = findViewById(R.id.checkMark);

        spotifyAPICall();
    }

    private void checkAnimation() {
        progressBar.animate().alpha(0f).setDuration(transitionDuration).withEndAction(() -> {
            progressBar.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loadingTextView.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.checkMark);
            loadingTextView.setLayoutParams(params);
            checkMark.setVisibility(View.VISIBLE);
            checkMark.setAlpha(0f);
            checkMark.animate().alpha(1f).setDuration(transitionDuration).start();
        }).start();

        loadingTextView.animate().alpha(0f).setDuration(transitionDuration).withEndAction(() -> {
            loadingTextView.setText(R.string.loading_finished);
            loadingTextView.setAlpha(0f);
            loadingTextView.animate().alpha(1f).setDuration(transitionDuration).withEndAction(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(LoadingActivity.this, WrappedActivity.class);
                startActivity(intent);
                finish();
            }, readyDuration)).start();
        }).start();
    }



    private void spotifyAPICall() {
        Spotify.instance.fetchTopTracks();
        checkAnimation();
    }
}

