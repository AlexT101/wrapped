package com.example.wrapped.ui.connect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wrapped.DuoActivity;
import com.example.wrapped.LoadingActivity;
import com.example.wrapped.MainActivity;
import com.example.wrapped.R;
import com.example.wrapped.databinding.FragmentConnectBinding;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectFragment extends Fragment {

    /**
    public static final String CLIENT_ID = "3c72822f2eef4262a6763b428780c09b";
    public static final String REDIRECT_URI = "wrappe://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private TextView tokenTextView;
     */

    private FragmentConnectBinding binding;

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

        //tokenTextView = (TextView) view.findViewById(R.id.name);
        //Button tokenBtn = (Button) view.findViewById(R.id.connectButton);

        //tokenBtn.setOnClickListener((v) -> {
        //    getToken();
        //});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(getActivity(), AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
     */
}