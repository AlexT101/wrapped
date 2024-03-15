package com.example.wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingActivity extends Activity {

    private ProgressBar progressBar;
    private TextView loadingTextView;
    private ImageView checkMark;
    final int transitionDuration = 200;

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
            loadingTextView.setText("Your Wrapped is Ready");
            loadingTextView.setAlpha(0f);
            loadingTextView.animate().alpha(1f).setDuration(transitionDuration).withEndAction(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(LoadingActivity.this, WrappedActivity.class);
                startActivity(intent);
                finish();
            }, 800)).start();
        }).start();
    }



    private void spotifyAPICall() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            /**
             * REPLACE THIS WITH ACTUAL SPOTIFY API CALL AND REMOVE DELAY
             */

            checkAnimation();
        }, 3000);

    }
}
