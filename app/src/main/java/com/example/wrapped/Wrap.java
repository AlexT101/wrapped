package com.example.wrapped;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wrapped.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wrap {

    private static DataChangeListener listener;

    public static void setDataChangeListener(DataChangeListener listener) {
        Wrap.listener = listener;
    }

    public static void dataChanged() {
        if (listener != null) {
            listener.onDataChanged();
        }
    }

    private static Wrap currentWrap;
    private static ArrayList<Wrap> allWraps = new ArrayList<>();

    public static Wrap getCurrent() {
        return currentWrap;
    }

    public static void setCurrent(Wrap newWrap) {
        currentWrap = newWrap;
    }

    //FIREBASE SYNC FUNCTIONS---------------------------------------------------------

    //Call this function when the user logs in to load all wraps from firebase
    private static String[] months = new String[]{"APRIL"};
    public static void loadFromFirebase(String user) {
        for (String month : months) {
            FirebaseFirestore.getInstance().collection("users").document(user).collection("old_wrapped").document(month)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                Wrap short_term_wrap = new Wrap("short_term");
                                Wrap mid_term_wrap = new Wrap("medium_term");
                                Wrap long_term_wrap = new Wrap("long_term");

                                short_term_wrap.setDate(month);
                                mid_term_wrap.setDate(month);
                                long_term_wrap.setDate(month);

                                for (String fieldName : data.keySet()) {
                                    if (fieldName.contains("short_term")) {
                                        if (fieldName.contains("artists")) {
                                            try {
                                                short_term_wrap.setArtists(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else if (fieldName.contains("tracks")) {
                                            try {
                                                short_term_wrap.setTracks(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    } else if (fieldName.contains("medium_term")) {
                                        if (fieldName.contains("artists")) {
                                            try {
                                                mid_term_wrap.setArtists(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else if (fieldName.contains("tracks")) {
                                            try {
                                                mid_term_wrap.setTracks(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    } else if (fieldName.contains("long_term")) {
                                        if (fieldName.contains("artists")) {
                                            try {
                                                long_term_wrap.setArtists(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else if (fieldName.contains("tracks")) {
                                            try {
                                                long_term_wrap.setTracks(new JSONObject(documentSnapshot.getString(fieldName)));
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }
                                if (short_term_wrap.getArtists() != null && short_term_wrap.getTracks() != null) {
                                    add(short_term_wrap);
                                }
                                if (mid_term_wrap.getArtists() != null && mid_term_wrap.getTracks() != null) {
                                    add(mid_term_wrap);
                                }
                                if (long_term_wrap.getArtists() != null && long_term_wrap.getTracks() != null) {
                                    add(long_term_wrap);
                                }
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

    //Finish writing this function to add a Wrap to Firebase
    public static void addToFirebase(Wrap wrap){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid().toString()).collection("old_wrapped").document(LocalDate.now().getMonth().toString())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (wrap.tracks !=null || wrap.artists !=null) {
                                if (documentSnapshot.exists()) {
                                    pushHelper(documentSnapshot, wrap.timeSpan, wrap.tracks.toString(), "tracks","update");
                                    pushHelper(documentSnapshot, wrap.timeSpan, wrap.artists.toString(), "artists","update");
                                } else {
                                    pushHelper(documentSnapshot, wrap.timeSpan, wrap.tracks.toString(), "tracks","set");
                                    pushHelper(documentSnapshot, wrap.timeSpan, wrap.artists.toString(), "artists","set");
                                }
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
    //--------------------------------------------------------------------------------

    public static ArrayList<Wrap> getAll() {
        return allWraps;
    }

    public static Wrap get(int index) {
        return allWraps.get(index);
    }

    public static void add(Wrap newWrap) {
        allWraps.add(newWrap);
        Wrap.dataChanged();
    }

    public static Wrap createWrap(String timeSpan) {
        Wrap newWrap = new Wrap(timeSpan);
        setCurrent(newWrap);
        add(newWrap);
        return newWrap;
    }

    private String timeSpan;
    private String date;
    private JSONObject tracks;
    private JSONObject artists;

    public Wrap (String time) {
        timeSpan = time;
        date = getCurrentDate();
    }

    public Wrap(String time, String newDate) {
        timeSpan = time;
        date = newDate;
    }

    public String getTimeline() {
        return timeSpan;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String time) {
        timeSpan = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String newDate) {
        date = newDate;
    }

    public JSONObject getTracks() {
        return tracks;
    }

    public void setTracks(JSONObject newTracks) {
        tracks = newTracks;
    }

    public JSONObject getArtists(){return artists;}

    public void setArtists(JSONObject newArtists) {
        artists = newArtists;
    }

    public static String getCurrentDate() {
        return LocalDate.now().getMonth().toString();
    }
}
