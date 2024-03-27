package com.example.demo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_sign_in#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_sign_in extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;




    public static fragment_sign_in newInstance(String param1, String param2) {
        fragment_sign_in fragment = new fragment_sign_in();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    EditText et_email, et_password;
    Button btn_sign_in;
    private String email, password;
    FirebaseAuth mAuth;


    private void init_attributes(View view) {
        et_email = view.findViewById(R.id.si_email);
        et_password = view.findViewById(R.id.si_password);
        btn_sign_in = view.findViewById(R.id.si_btn);
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean check_edit_text_content() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ) {
            Toast.makeText(getActivity(), "Empty Content Found", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }


    private void getUserByID(String userID) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(String.format("/users/%s", userID));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "User retrieved successfully: " + user.toString());
                    } catch (DatabaseException e) {
                        // Handle the exception
                        e.printStackTrace();
                    }

                } else {
                    Log.d(TAG, "User with ID " + userID + " does not exist.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error getting user data: " + databaseError.getMessage());
            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        init_attributes(view);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the second fragment
                email = String.valueOf(et_email.getText());
                password = String.valueOf(et_password.getText());
                if (check_edit_text_content()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(getActivity(), "Log in worked.",
                                                Toast.LENGTH_SHORT).show();

                                        // Retrieve the user data after signing in
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            String userID = currentUser.getUid();
                                            getUserByID(userID);
                                        }
//                                        Log.d("newUser", user.toString());


                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);


//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                    }
                                }
                            });
                }




            }
        });

        return view;
    }
}