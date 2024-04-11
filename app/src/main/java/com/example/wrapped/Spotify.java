package com.example.wrapped;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;

public class Spotify {

    public static Spotify instance = new Spotify();

    public static final String CLIENT_ID = "3c72822f2eef4262a6763b428780c09b";
    public static final String REDIRECT_URI = "wrapped://auth";
    public static final String BASE64_CREDENTIALS = "M2M3MjgyMmYyZWVmNDI2MmE2NzYzYjQyODc4MGMwOWI6MDM1N2E5NmM4ZWRmNGIyOTk3NWIyMjhmMjM1NGIzOGQ=";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private static String code;
    private static String token;
    private static JSONObject profile;

    private static ProfileListener profileListener;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    public static String getCode() {
        return code;
    }

    public static void setCode(String val) {
        code = val;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String val) {
        token = val;
    }

    public static JSONObject getProfile() {
        return profile;
    }

    public static void setProfile(JSONObject newProfile) {
        profile = newProfile;
        notifyProfileUpdated();
    }

    public void loadCode(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(contextActivity, AUTH_CODE_REQUEST_CODE, request);
    }

    public void loadToken(Activity contextActivity) {
        if (code == null) {
            Log.d("SPOTIFY:HTTP", "You need to get a code first!");
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", "wrapped://auth")
                .build();

        final Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + BASE64_CREDENTIALS)
                .post(body)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to fetch token: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String val = response.body().string();
                contextActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResponse = new JSONObject(val);
                            String accessToken = jsonResponse.getString("access_token");
                            setToken(accessToken);
                            instance.loadProfile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void loadProfile() {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    setProfile(new JSONObject(response.body().string()));
                    Log.d("SPOTIFY", profile.toString(3));
                } catch (JSONException e) {
                    Log.d("SPOTIFY:JSON", "Failed to parse data: " + e);
                }
            }
        });
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read", "user-read-recently-played" })
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

    public static void setProfileListener(ProfileListener listener) {
        profileListener = listener;
    }

    private static void notifyProfileUpdated() {
        if (profileListener != null) {
            profileListener.onProfileUpdate(profile);
        }
    }

    public void fetchTopItems(Activity contextActivity, String itemType, final TopItemsListener listener) {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        String endpointUrl = "https://api.spotify.com/v1/me/top/" + itemType; // itemType can be "tracks" or "artists"

        final Request request = new Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to fetch top items: " + e);
                contextActivity.runOnUiThread(() -> listener.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonResponse = response.body().string();
                contextActivity.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        listener.onSuccess(jsonObject);
                    } catch (JSONException e) {
                        Log.d("SPOTIFY:JSON", "Failed to parse top items JSON: " + e);
                        listener.onFailure(e);
                    }
                });
            }
        });
    }

    public interface TopItemsListener {
        void onSuccess(JSONObject topItems);
        void onFailure(Exception e);
    }
}
