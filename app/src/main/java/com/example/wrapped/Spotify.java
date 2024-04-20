package com.example.wrapped;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.lang.ref.Reference;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
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
    private static String refresh;
    private static String token;
    private static String user;
    private static JSONObject profile;

    private static class User {
        private String code;
        private User(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    private static JSONObject tracks;

    private static JSONObject artists;

    private static ProfileListener profileListener;

    private static TracksListener tracksListener;

    private static ArtistsListener artistsListener;

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static String getUser() {
        return user;
    }
    public static void setUser(String val) {
        user = val;
    }

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

    public static String getRefresh() {
        return refresh;
    }

    public static void setRefresh(String user, String val) {
        refresh = val;
        if (user != null) {
            pushUserIntoFirestore(user, refresh);
            Log.d("API CALL", "Code successfully saved: " + refresh);
        }
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
            Log.d("API CALL", "You need to get a code first!");
            return;
        } else {
            Log.d("API CALL", "Attempting loadToken() with code: " + code);
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

        Call mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("API CALL", "Failed to fetch token: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String val = response.body().string();
                contextActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResponse = new JSONObject(val);
                            Log.d("API CALL", jsonResponse.toString());
                            if (jsonResponse.has("access_token")) {
                                String accessToken = jsonResponse.getString("access_token");
                                Log.d("API CALL", "Fetched access token: " + accessToken);
                                setToken(accessToken);
                                if (jsonResponse.has("refresh_token")) {
                                    String refresh = jsonResponse.getString("refresh_token");
                                    Log.d("API CALL", "Fetched refresh token: " + refresh);
                                    setRefresh(user, refresh);
                                }
                                instance.loadProfile();
                            } else {
                                Log.d("API CALL", "Could not fetch access token");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void loadToken(String refreshToken, Activity contextActivity) {
        if (refreshToken == null) {
            Log.d("API CALL", "You need to get a refresh token first!");
            return;
        } else {
            Log.d("API CALL", "Attempting loadToken() with refresh token: " + refreshToken);
        }

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .add("client_id", CLIENT_ID)
                .build();

        final Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + BASE64_CREDENTIALS)
                .post(body)
                .build();

        Call mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("API CALL", "Failed to fetch token: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String val = response.body().string();
                contextActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResponse = new JSONObject(val);
                            Log.d("API CALL", jsonResponse.toString());
                            if (jsonResponse.has("access_token")) {
                                String accessToken = jsonResponse.getString("access_token");
                                Log.d("API CALL", "Fetched access token: " + accessToken);
                                setToken(accessToken);
                                if (jsonResponse.has("refresh_token")) {
                                    String refresh = jsonResponse.getString("refresh_token");
                                    Log.d("API CALL", "Fetched refresh token: " + refresh);
                                    setRefresh(user, refresh);
                                }
                                instance.loadProfile();
                            } else {
                                Log.d("API CALL", "Could not fetch access token");
                            }
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

        Call mCall = mOkHttpClient.newCall(request);

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

    public static void setProfileListener(ProfileListener listener) {
        profileListener = listener;
    }

    private static void notifyProfileUpdated() {
        if (profileListener != null) {
            profileListener.onProfileUpdate(profile);
        }
    }

    private static void pushUserIntoFirestore(String uid, String code) {
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

    public static void getUserFromFirestore(Activity contextActivity) {
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String temp = documentSnapshot.getString("code");
                                setRefresh(user, temp);
                                Log.d("API CALL", "Refresh token retrieved: " + temp);
                                instance.loadToken(temp, contextActivity);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    private static void pushWrappedIntoFirestore(JSONObject wrapped, String time, String type) {
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user).collection("old_wrapped").document(LocalDate.now().getMonth().toString())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                pushHelper(documentSnapshot, time, wrapped.toString(), type,"update");
                            } else {
                                pushHelper(documentSnapshot, time, wrapped.toString(), type,"set");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }
    private static void pushHelper(DocumentSnapshot documentSnapshot, String time, String wrapped, String wrapped_type, String action) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(time+"_"+wrapped_type,wrapped);
        if(action.equals("update")) {
            documentSnapshot.getReference().
                    update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("hello");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        } else if (action.equals("set")) {
            documentSnapshot.getReference().
                    set(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("hello");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

    }

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

    public void fetchTopTracks(String timeRange) {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        String endpointUrl = "https://api.spotify.com/v1/me/top/tracks?time_range=" + timeRange;
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
                    JSONObject newWrapped = new JSONObject(response.body().string());
                    setTracks(newWrapped);
                    pushWrappedIntoFirestore(newWrapped, timeRange, "tracks");
                    Log.d("SPOTIFY", tracks.toString(3));
                } catch (JSONException e) {
                    Log.d("SPOTIFY:JSON", "Failed to parse top tracks JSON: " + e);
                }
            }
        });
    }


    public void fetchTopArtists(String timeRange) {
        if (token == null) {
            Log.d("SPOTIFY:HTTP", "You need to get an access token first!");
            return;
        }

        String endpointUrl = "https://api.spotify.com/v1/me/top/artists?time_range=" + timeRange;

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
                    JSONObject newWrapped = new JSONObject(response.body().string());
                    setArtists(newWrapped);
                    pushWrappedIntoFirestore(newWrapped, timeRange, "artists");
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
