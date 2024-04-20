package com.example.wrapped;

public class PastWrapsActivity {
    private String date;
    private String timeline;

    public PastWrapsActivity(String date, String timeline) {
        this.date = date;
        this.timeline = timeline;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }
}
