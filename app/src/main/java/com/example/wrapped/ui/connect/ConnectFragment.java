package com.example.wrapped.ui.connect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wrapped.ProfileListener;
import com.example.wrapped.Spotify;
import com.example.wrapped.R;
import com.example.wrapped.databinding.FragmentConnectBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectFragment extends Fragment implements ProfileListener {

    private FragmentConnectBinding binding;

    private TextView spotify_name;
    private TextView spotify_id;
    private Button confirmConnectedButton;
    private Button disconnectButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ConnectViewModel connectViewModel =
                new ViewModelProvider(this).get(ConnectViewModel.class);

        binding = FragmentConnectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spotify spotifyInstance = new Spotify();

        Button codeBtn = view.findViewById(R.id.connectButton);
        codeBtn.setOnClickListener((v) -> {
            spotifyInstance.loadCode(getActivity());
        });

        Spotify.setProfileListener(this);

        spotify_name = view.findViewById(R.id.spotify_name);
        spotify_id = view.findViewById(R.id.spotify_id);
        confirmConnectedButton = view.findViewById(R.id.confirmConnectedButton);
        disconnectButton = view.findViewById(R.id.disconnectButton);

        disconnectButton.setOnClickListener((v) -> {
            Spotify.setProfile(null);
        });

        onProfileUpdate(Spotify.getProfile());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Spotify.setProfileListener(null);
        binding = null;
    }

    @Override
    public void onProfileUpdate(JSONObject newProfile) {
        if (newProfile != null) {
            try {
                String name = newProfile.getString("display_name");
                String id = newProfile.getString("id");
                if (spotify_name != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spotify_name.setText(name);
                            spotify_id.setText("@" + id);
                            confirmConnectedButton.setText(R.string.connected);
                            confirmConnectedButton.setBackgroundResource(R.drawable.transparent_bg_greenbordered);
                        }
                    });
                }
            } catch (JSONException e) {
                Log.d("ConnectFragment", "Cannot parse JSON");
            }
        } else {
            spotify_name.setText("Name");
            spotify_id.setText("@id");
            confirmConnectedButton.setText(R.string.not_connected);
            confirmConnectedButton.setBackgroundResource(R.drawable.transparent_bg_redbordered);
        }
    }
}