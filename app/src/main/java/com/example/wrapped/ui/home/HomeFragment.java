package com.example.wrapped.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wrapped.DuoActivity;
import com.example.wrapped.LoadingActivity;
import com.example.wrapped.RecyclerViewListener;
import com.example.wrapped.PastWrapsActivity;
import com.example.wrapped.PastWrapAdapter;
import com.example.wrapped.databinding.FragmentHomeBinding;
import com.example.wrapped.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerViewListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private PastWrapAdapter adapter;
    private List<PastWrapsActivity> PastWrapList;

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
            Intent intent = new Intent(getActivity(), LoadingActivity.class);
            startActivity(intent);
        });

        ImageButton duoButton = view.findViewById(R.id.duo_button);
        duoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DuoActivity.class);
            startActivity(intent);
        });

        TextView textView = view.findViewById(R.id.see_all);
        textView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_history));


        recyclerView = view.findViewById(R.id.recyclerView);
        //findViewById(R.id.recyclerView);

        PastWrapList = new ArrayList<>();
        PastWrapList.add(new PastWrapsActivity("3/24/24", "6M"));
        PastWrapList.add(new PastWrapsActivity("12/24/24", "1M"));

        adapter = new PastWrapAdapter(PastWrapList, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), LoadingActivity.class);
        startActivity(intent);
    }
}