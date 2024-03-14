package com.example.wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DuoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duo);

        Button loginButton = findViewById(R.id.duo_return_button);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(DuoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
