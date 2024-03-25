package com.example.wrapped.ui.firebase_signup;// SignUpFragment.java
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wrapped.MainActivity;
import com.example.wrapped.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button signupButton;
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

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                signUpUser(email, password);
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
}