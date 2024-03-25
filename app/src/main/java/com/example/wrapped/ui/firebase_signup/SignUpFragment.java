package com.example.wrapped.ui.firebase_signup;// SignUpFragment.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
                            Toast.makeText(getActivity(), "Sign up successful", Toast.LENGTH_SHORT).show();
                            // Proceed to another fragment or activity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            // Start the activity
                            startActivity(intent);
                        } else {
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