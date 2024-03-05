package com.example.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

public class HomePage extends AppCompatActivity {

    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);


        TextView sign_up = findViewById(R.id.hm_sign_up);
        TextView sign_in = findViewById(R.id.hm_sign_in);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment
                loadFragment(new fragment_sign_up());
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment
                loadFragment(new fragment_sign_in());
            }
        });

        // Initially load the first fragment
        loadFragment(new fragment_sign_in());
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.hm_fragment, fragment)
                .addToBackStack(null)  // Optional: Add to back stack for fragment navigation
                .commit();
    }
}
