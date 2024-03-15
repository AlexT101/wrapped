package com.example.wrapped.ui.wrapped;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wrapped.DuoActivity;
import com.example.wrapped.MainActivity;
import com.example.wrapped.R;
import com.example.wrapped.WrappedActivity;

public class PlaceholderFragment2 extends Fragment {

    public PlaceholderFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_placeholder2, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
    }
}
