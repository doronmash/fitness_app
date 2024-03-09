package com.example.demo;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demo.custom_data.CustomData;
import com.example.demo.models.User;
import com.example.demo.view_models.ActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.lifecycle.ViewModelProvider;



public class fragment_sign_up extends Fragment {

    EditText et_email, et_username, et_password, et_r_password;
    Button btn_sign_up;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    private ActivityViewModel activityViewModel;

    private String email, name, password, password_repeat;

    private void init_attributes(View view) {
        et_email = view.findViewById(R.id.su_email);
        et_username = view.findViewById(R.id.su_username);
        et_password = view.findViewById(R.id.su_password);
        et_r_password = view.findViewById(R.id.su_r_password);
        btn_sign_up = view.findViewById(R.id.su_btn);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

    }

    private void check_edit_text_content() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(password_repeat)) {
            Toast.makeText(getContext(), "Empty Content Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        init_attributes(view);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseApp.initializeApp(getContext());

                // Switch to the second fragment
                name = String.valueOf(et_username.getText());
                email = String.valueOf(et_email.getText());
                password = String.valueOf(et_password.getText());
                password_repeat = String.valueOf(et_r_password.getText());
                activityViewModel = new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

                check_edit_text_content();
                if (password.equals(password_repeat)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Pttt", "createUserWithEmail:success");
                                        Toast.makeText(getContext(), "user created.",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        HashMap<String, String> dataMap = new HashMap<>();
                                        dataMap.put("name", name);
                                        CustomData userData = new CustomData(dataMap);
                                        activityViewModel.setCustomData(userData);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getActivity(), "Failed to register: "+task.getException().getMessage()+"!", Toast.LENGTH_SHORT).show();
                                        Log.w("Pttt", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getActivity(), "Passwords not match.\n User creation failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


















//    @Override
//    public View onCreateView1(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
//        FirebaseApp.initializeApp(getContext());
//
//        init_attributes(view);
//
//        // Initialize Firebase only once (you don't need to initialize it again in onClick)
//
//        btn_sign_up.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Switch to the second fragment
//
//                name = String.valueOf(et_username.getText());
//                email = String.valueOf(et_email.getText());
//                password = String.valueOf(et_password.getText());
//                password_repeat = String.valueOf(et_r_password.getText());
//                check_edit_text_content();
//
//                if (password.equals(password_repeat)) {
//                    final User newUser = new User(name, email, password);
//
//                    // Check if the new user email is unique
//                    reference.orderByChild("email").equalTo(newUser.getEmail())
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        // User email already exists, handle the case (e.g., show an error message)
//                                        Log.d("FirebaseExample", "User email already exists.");
//                                        Toast.makeText(getActivity(), "User email already exists.", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        // User email is unique, add the new user
//                                        String newUserId = reference.push().getKey(); // Generate a unique key for the new user
//                                        reference.child(newUserId).setValue(newUser);
//
//                                        Log.d("FirebaseExample", "New user added successfully.");
//
//                                        // Add code to switch to the second fragment here if needed
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    Log.e("FirebaseExample", "Error checking for existing user email: " + databaseError.getMessage());
//                                }
//                            });
//                } else {
//                    Toast.makeText(getActivity(), "Passwords do not match. User creation failed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        return view;
//    }



}
