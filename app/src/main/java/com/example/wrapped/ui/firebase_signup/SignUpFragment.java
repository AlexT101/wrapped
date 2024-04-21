package com.example.wrapped.ui.firebase_signup;// SignUpFragment.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.wrapped.MainActivity;
import com.example.wrapped.R;
import com.example.wrapped.ui.firebase.FirebaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button signupButton;
    private Button goLogin;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.signUpEmailEditText);
        passwordEditText = view.findViewById(R.id.signUpPasswordEditText);
        signupButton = view.findViewById(R.id.signup_button);
        firebaseAuth = FirebaseAuth.getInstance();
        goLogin = view.findViewById(R.id.return_to_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEditText == null || TextUtils.isEmpty(emailEditText.getText().toString()) || passwordEditText == null || TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter valid email/password", Toast.LENGTH_SHORT).show();
                } else {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    signUpUser(email, password);
                }
            }
        });

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to signup fragment
                navigateToFirebaseFragment();
            }
        });
    }

    private void signUpUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up successful
                            DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            document.set(Collections.singletonMap("username", email)).
                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Proceed to another fragment or activity
                                            Toast.makeText(getActivity(), "Sign up successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            // Start the activity
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Sign up failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Log.d("SignUpError", "Sign up failed", task.getException());
                            // Sign up failed
                            Toast.makeText(getActivity(), "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToFirebaseFragment() {
        FirebaseFragment firebaseFragment = new FirebaseFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, firebaseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}