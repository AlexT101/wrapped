package com.example.wrapped;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StoriesProgressBar extends View {
    private Paint paint = new Paint();
    private Paint gapPaint = new Paint();
    private int numberOfSegments = 5; // Default number of segments
    private int currentSegment = 0; // The current segment being viewed
    private float segmentCompletedPercentage = 0f;

    public StoriesProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoriesProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStrokeWidth(8);
        paint.setColor(0xFFFFFFFF); // Set the desired color for the filled segments
        gapPaint.setStrokeWidth(8);
        gapPaint.setColor(0xFF303030); // Darker color for the unfilled segments
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int segmentWidth = width / numberOfSegments;

        // Define the gap width and adjust the segment width to account for gaps
        int gapWidth = 12; // Width of the gap between segments
        segmentWidth = (width - (gapWidth * (numberOfSegments - 1))) / numberOfSegments;

        for (int i = 0; i < numberOfSegments; i++) {
            // Calculate left and right positions accounting for gaps
            int left = i * (segmentWidth + gapWidth) + getPaddingLeft();
            int right = left + segmentWidth;

            // Draw the segment background (gap)
            canvas.drawRect(left, getPaddingTop(), right, getPaddingTop() + height, gapPaint);

            // Only fill the segment if it's completed or currently being filled
            if (i < currentSegment || (i == currentSegment && segmentCompletedPercentage > 0)) {
                paint.setAlpha(255);
                if (i == currentSegment) {
                    right = (int) (left + segmentWidth * segmentCompletedPercentage);
                }
                canvas.drawRect(left, getPaddingTop(), right, getPaddingTop() + height, paint);
            }
        }
    }

    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        invalidate(); // Redraw the view with the new number of segments
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
        invalidate(); // Redraw the view to reflect the current segment progress
    }

    public void updateProgress(int currentSegment, float progress) {
        this.currentSegment = currentSegment;
        this.segmentCompletedPercentage = progress;
        invalidate(); // Force redraw with the updated progress
    }

}
