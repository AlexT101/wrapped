package com.example.wrapped.ui.wrapped;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wrapped.R;

public class GenreFragment extends Fragment {

    public GenreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wrapped_genre, container, false);
        return rootView;
    }
}
