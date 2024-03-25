package com.example.wrapped;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wrapped.ui.wrapped.PlaceholderFragment;
import com.example.wrapped.ui.wrapped.PlaceholderFragment2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WrappedActivity extends AppCompatActivity {

    private boolean fabsVisible;
    private FloatingActionButton[] fabs;
    private TextView[] textViews;
    private int[] offsets;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private StoriesProgressBar progressBar;
    private final int numberOfPages = 5;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapped);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        progressBar = findViewById(R.id.storiesProgress);
        progressBar.setNumberOfSegments(numberOfPages - 1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if(positionOffset > 0) {
                    progressBar.updateProgress(position, positionOffset);
                }
            }
        });

        fabsVisible = false;

        fabs = new FloatingActionButton[]{
                findViewById(R.id.fab_download),
                findViewById(R.id.fab_share)
        };

        textViews = new TextView[]{
                findViewById(R.id.tv_download),
                findViewById(R.id.tv_share)
        };

        offsets = new int[]{
                R.dimen.offset1,
                R.dimen.offset2
        };

        FloatingActionButton fabMain = findViewById(R.id.fab_main);

        fabMain.setOnClickListener(view -> {
            if (fabsVisible) {
                hideFabs();
            } else {
                showFabs();
            }
            fabsVisible = !fabsVisible;
        });

        fabs[0].setOnClickListener(view -> downloadPNG());

        fabs[1].setOnClickListener(view -> shareFriend());
    }
    private void showFabs() {
        for (int i = 0; i < fabs.length; i++) {
            final FloatingActionButton fab = fabs[i];
            final TextView textView = textViews[i];
            float offset = getResources().getDimension(offsets[i]);

            fab.setTranslationY(offset);
            textView.setTranslationY(offset);
            fab.show();
            textView.setVisibility(View.VISIBLE);
            textView.setAlpha(0f);

            fab.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).start();
            textView.animate().translationY(0).alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).start();
        }
    }

    private void hideFabs() {
        for (int i = 0; i < fabs.length; i++) {
            final FloatingActionButton fab = fabs[i];
            final TextView textView = textViews[i];
            float offset = getResources().getDimension(offsets[i]);

            fab.animate().translationY(offset).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).withEndAction(fab::hide).start();
            textView.animate().translationY(offset).alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).withEndAction(() -> textView.setVisibility(View.INVISIBLE)).start();
        }
    }

    private void shareFriend() {

    }

    private void downloadPNG() {
        View storiesProgress = findViewById(R.id.storiesProgress);
        View fabGroup = findViewById(R.id.fabGroup);

        storiesProgress.setVisibility(View.GONE);
        fabGroup.setVisibility(View.GONE);

        final View rootView = findViewById(android.R.id.content);

        rootView.post(() -> {
            Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rootView.draw(canvas);

            storiesProgress.setVisibility(View.VISIBLE);
            fabGroup.setVisibility(View.VISIBLE);

            saveBitmapToFile(bitmap, "wrapped.png");
        });
    }

    private void saveBitmapToFile(Bitmap bitmap, String fileName) {
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(picturesDirectory, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(this, "PNG Downloaded: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving PNG: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == numberOfPages - 1) {
                return new PlaceholderFragment2();
            }
            return new PlaceholderFragment();
        }

        @Override
        public int getItemCount() {
            return numberOfPages;
        }
    }
}
