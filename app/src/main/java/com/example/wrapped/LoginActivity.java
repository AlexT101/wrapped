package com.example.wrapped;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wrapped.ui.firebase.FirebaseFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        navigateToFirebaseFragment();
    }

    private void navigateToFirebaseFragment() {
        FirebaseFragment firebaseFragment = new FirebaseFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firebaseFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
