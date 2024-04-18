package com.example.wrapped;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.wrapped.PastWraps;
import com.example.wrapped.R;

public class PastWrapAdapter extends RecyclerView.Adapter<PastWrapAdapter.PastWrapViewHolder> {
    private List<PastWraps> pastWraps;

    public PastWrapAdapter(List<PastWraps> pastWraps) {
        this.pastWraps = pastWraps;
    }

    @Override
    public PastWrapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_wrapped, parent, false);
        return new PastWrapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PastWrapViewHolder holder, int position) {
        PastWraps pastWrap = pastWraps.get(position);
        holder.textViewDate.setText(pastWrap.getDate());
        holder.textViewTimeline.setText(pastWrap.getTimeline());
    }

    @Override
    public int getItemCount() {
        return pastWraps.size();
    }

    static class PastWrapViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewTimeline, textViewLocation;

        public PastWrapViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.date);
            textViewTimeline = itemView.findViewById(R.id.length);
        }
    }
}