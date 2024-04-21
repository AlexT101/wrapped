package com.example.wrapped.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wrapped.MainActivity;
import com.example.wrapped.R;
import com.example.wrapped.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "SettingsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        EditText editText = root.findViewById(R.id.textView5);
        TextView username_top = root.findViewById(R.id.username_top);
        MainActivity mainActivity = (MainActivity) getActivity();
        editText.setText(mainActivity.getUsername());
        username_top.setText(mainActivity.getUsername());
        Button saveButton = root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = root.findViewById(R.id.textView5);
                String uname = editText.getText().toString();
                setUserName(uname,mAuth.getUid().toString(), root);
                username_top.setText(uname);
            }
        });
        Button deleteAccountButton = root.findViewById(R.id.deleteButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    private void setUserName(String username, String uid, View root) {
        FirebaseFirestore.getInstance().collection("users").document(uid).
                update(Collections.singletonMap("username", username)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getActivity().recreate();
                        Toast.makeText(getActivity(), "Updated UserName", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deleteUserData(user.getUid());
                            } else {
                                Log.w(TAG, "Failed to delete user account.", task.getException());
                                Toast.makeText(getActivity(), "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.w(TAG, "No user signed in.");
        }
    }

    private void deleteUserData(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);

        docRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                document.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted.");
                                                Toast.makeText(getActivity(), "User account and data deleted.", Toast.LENGTH_SHORT).show();
                                                // Perform any other necessary actions after successful deletion
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                                Toast.makeText(getActivity(), "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else {
                                Log.d(TAG, "Document does not exist!");
                                // Document does not exist
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getActivity(), "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}