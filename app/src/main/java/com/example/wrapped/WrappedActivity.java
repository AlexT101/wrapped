package com.example.wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WrappedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapped);

        Button loginButton = findViewById(R.id.return_button);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WrappedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
