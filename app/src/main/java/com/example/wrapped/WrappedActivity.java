package com.example.wrapped;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wrapped.ui.wrapped.PlaceholderFragment;
import com.example.wrapped.ui.wrapped.PlaceholderFragment2;

public class WrappedActivity extends AppCompatActivity {

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

                // Directly update the progress of the current segment based on swipe position
                if(positionOffset > 0) {
                    int currentSegment = position;
                    float progress = positionOffset;

                    progressBar.updateProgress(currentSegment, progress);
                }
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a new Fragment instance here.
            // You can pass data to the fragment using Bundle if needed.
            if (position == numberOfPages - 1) {
                return new PlaceholderFragment2();
            }
            return new PlaceholderFragment();
        }

        @Override
        public int getItemCount() {
            return numberOfPages; // Define the total number of pages
        }
    }
}
