package com.example.wrapped;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastWrapAdapter extends RecyclerView.Adapter<PastWrapAdapter.PastWrapViewHolder> {
    private List<Wrap> pastWraps;
    private final RecyclerViewListener recyclerViewListener;

    public PastWrapAdapter(List<Wrap> pastWraps, RecyclerViewListener recyclerViewListener) {
        this.pastWraps = pastWraps;
        this.recyclerViewListener = recyclerViewListener;
    }

    @Override
    public PastWrapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_wrapped, parent, false);
        return new PastWrapViewHolder(view, recyclerViewListener);
    }

    @Override
    public void onBindViewHolder(PastWrapViewHolder holder, int position) {
        Wrap pastWrap = pastWraps.get(position);
        holder.textViewDate.setText(pastWrap.getDate());
        if (pastWrap.getTimeline().contains("short")) {
            holder.textViewTimeline.setText("1M");
        } else if (pastWrap.getTimeline().contains("med")) {
            holder.textViewTimeline.setText("6M");
        } else {
            holder.textViewTimeline.setText("1Y");
        }

    }

    @Override
    public int getItemCount() {
        return pastWraps.size();
    }

    static class PastWrapViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewTimeline, textViewLocation;

        public PastWrapViewHolder(View itemView, RecyclerViewListener recyclerViewListener) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.date);
            textViewTimeline = itemView.findViewById(R.id.length);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewListener!= null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewListener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}