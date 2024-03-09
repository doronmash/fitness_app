package com.example.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.custom_data.CustomData;
import com.example.demo.view_models.ActivityViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ActivityViewModel activityViewModel;

    @SuppressLint("SetTextI18n")
    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);

        TextView tv_sign_up = findViewById(R.id.hm_sign_up);
        TextView tv_sign_in = findViewById(R.id.hm_sign_in);
        TextView tv_name = findViewById(R.id.hm_tv_name);
        TextView tv_logout = findViewById(R.id.hm_tv_logout);
        mAuth = FirebaseAuth.getInstance();

        activityViewModel.getCustomData().observe(this, customData -> {
            // Update UI, perform actions, etc.
            tv_sign_up.setVisibility(View.INVISIBLE);
            tv_sign_in.setVisibility(View.INVISIBLE);
            tv_name.setVisibility(View.VISIBLE);
            tv_logout.setVisibility(View.VISIBLE);

            // Handle data changes in YourActivity
            HashMap<String, String> dataMap = customData.getData();
            String name = dataMap.get("name");
            Log.d("temp", "Name: " + name);
            tv_name.setText("Hello " + name);

        });
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment
                loadFragment(new fragment_sign_up());
            }
        });

        tv_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment
                loadFragment(new fragment_sign_in());
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                tv_sign_up.setVisibility(View.VISIBLE);
                tv_sign_in.setVisibility(View.VISIBLE);
                tv_name.setVisibility(View.INVISIBLE);
                tv_logout.setVisibility(View.INVISIBLE);
                tv_name.setText("");

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(getApplicationContext(), "Logout Succeed",
                            Toast.LENGTH_SHORT).show();
                }

                // Switch to login fragment
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
