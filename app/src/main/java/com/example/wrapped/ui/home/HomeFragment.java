package com.example.wrapped.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wrapped.DuoActivity;
import com.example.wrapped.WrappedActivity;
import com.example.wrapped.databinding.FragmentHomeBinding;
import com.example.wrapped.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton wrappedButton = view.findViewById(R.id.wrapped_button);
        wrappedButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WrappedActivity.class);
            startActivity(intent);
        });

        ImageButton duoButton = view.findViewById(R.id.duo_button);
        duoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DuoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}