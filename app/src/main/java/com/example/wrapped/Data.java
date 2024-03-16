package com.example.wrapped;

public class Data {

    public static final String CLIENT_ID = "3c72822f2eef4262a6763b428780c09b";
    public static final String REDIRECT_URI = "wrapped://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private static String spotifyCode;
    private static String spotifyToken;

    public static String getSpotifyCode() {
        return spotifyCode;
    }

    public static void setSpotifyCode(String val) {
        spotifyCode = val;
    }

    public static String getSpotifyToken() {
        return spotifyToken;
    }

    public static void setSpotifyToken(String val) {
        spotifyToken = val;
    }
}
