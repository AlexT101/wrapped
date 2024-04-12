package com.example.wrapped;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;

import java.util.Map;

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

    private static JSONObject tracks;

    private static JSONObject artists;

    private static ProfileListener profileListener;

    private static TracksListener tracksListener;

    private static ArtistsListener artistsListener;

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    private static class User {
        private String code;
        private User(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }


    public static String getCode() {
        return code;
    }

    public static void setCode(String user, String val) {
        code = val;
        pushDataIntoFirestore(user, code);
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

    public static JSONObject getTracks() {return tracks;}

    public static void setTracks(JSONObject newTracks) {
        tracks = newTracks;
        notifyTracksUpdated();
    }

    public static JSONObject getArtists(){return artists;}

    public static void setArtists(JSONObject newArtists) {
        artists = newArtists;
        notifyArtistsUpdated();
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
                .setScopes(new String[] { "user-read-email", "user-top-read", "user-read-recently-played", "user-modify-playback-state" })
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

    private static void pushDataIntoFirestore(String uid, String code) {
        CollectionReference collection = FirebaseFirestore.getInstance().collection("users");
        DocumentReference document = collection.document(uid);
        document.set(new User(code))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    private static void getDataFromFirestore(String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String code = documentSnapshot.getString("code");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    public static void setTracksListener(TracksListener tracks) {
        tracksListener = tracks;
    }

    private static void notifyTracksUpdated() {
        if (tracksListener != null) {
            tracksListener.onTracksUpdate(tracks);
        }
    }

    public static void setArtistsListener(ArtistsListener artists) {
        artistsListener = artists;
    }

    private static void notifyArtistsUpdated() {
        if (artistsListener != null) {
            artistsListener.onArtistsUpdate(artists);
        }
    }

    public void fetchTopTracks() {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        String endpointUrl = "https://api.spotify.com/v1/me/top/tracks";
        Request request = new Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = mOkHttpClient.newCall(request);  // Use a local variable instead of mCall

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to fetch top tracks: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    setTracks(new JSONObject(response.body().string()));
                    Log.d("SPOTIFY", tracks.toString(3));
                } catch (JSONException e) {
                    Log.d("SPOTIFY:JSON", "Failed to parse top tracks JSON: " + e);
                }
            }
        });
    }


    public void fetchTopArtists() {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        String endpointUrl = "https://api.spotify.com/v1/me/top/artists";

        final Request request = new Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to fetch top tracks: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    setArtists(new JSONObject(response.body().string()));
                    Log.d("SPOTIFY", artists.toString(3));
                } catch (JSONException e) {
                    Log.d("SPOTIFY:JSON", "Failed to parse top tracks JSON: " + e);
                }
            }
        });
    }

    public static void play(String uri) {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get a token first!");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("context_uri", uri);
        jsonBody.put("position_ms", 0);
        String jsonPayload = gson.toJson(jsonBody);

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonPayload, MEDIA_TYPE_JSON);

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/play")
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("SPOTIFY:HTTP", "Failed to start playback: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("SPOTIFY:HTTP", "Playback started successfully.");
                } else {
                    Log.e("SPOTIFY:HTTP", "Failed to start playback: " + response.code());
                }
            }
        });
    }
}
