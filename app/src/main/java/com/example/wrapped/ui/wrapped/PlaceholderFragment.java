package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wrapped.R;

public class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        TextView textView = rootView.findViewById(R.id.text_view);
        textView.setText("Page goes here");
        return rootView;
    }
}
