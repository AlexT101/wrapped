package com.example.wrapped.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wrapped.DataChangeListener;
import com.example.wrapped.DuoActivity;
import com.example.wrapped.LoadingActivity;
import com.example.wrapped.RecyclerViewListener;
import com.example.wrapped.PastWrapsActivity;
import com.example.wrapped.PastWrapAdapter;
import com.example.wrapped.Wrap;
import com.example.wrapped.databinding.FragmentHomeBinding;
import com.example.wrapped.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerViewListener, DataChangeListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    public static PastWrapAdapter adapter;
    private List<PastWrapsActivity> PastWrapList;
    private RadioGroup timeSpanGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        timeSpanGroup = root.findViewById(R.id.radioGroup);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton wrappedButton = view.findViewById(R.id.wrapped_button);
        wrappedButton.setOnClickListener(v -> {

            String selectedTimeSpan = getSelectedTimeSpan();

            Intent intent = new Intent(getActivity(), LoadingActivity.class);

            intent.putExtra("SELECTED_TIME_SPAN", selectedTimeSpan);

            startActivity(intent);
        });

        ImageButton duoButton = view.findViewById(R.id.duo_button);
        duoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DuoActivity.class);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        //findViewById(R.id.recyclerView);

        adapter = new PastWrapAdapter(Wrap.getAll(), this);
        recyclerView.setAdapter(adapter);
        Wrap.setDataChangeListener(this);
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

    private String getSelectedTimeSpan() {
        int selectedId = timeSpanGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "medium_term"; // default if no selection
        }
        View radioButton = timeSpanGroup.findViewById(selectedId);
        int index = timeSpanGroup.indexOfChild(radioButton);

        switch (index) {
            case 0: return "short_term"; // Assuming this is the order of your radio buttons
            case 1: return "medium_term";
            case 2: return "long_term";
            default: return "medium_term"; // Default value
        }
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
    }
}


