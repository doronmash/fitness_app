package com.example.demo;

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
import com.example.demo.view_models.ActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.lifecycle.ViewModelProvider;



public class fragment_sign_up extends Fragment {

    EditText et_email, et_username, et_password, et_r_password;
    Button btn_sign_up;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference db_user_reference;
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
        db_user_reference = database.getReference("users");
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
                                        HashMap<String, String> activityDataMap = new HashMap<>();
                                        activityDataMap.put("userID", user.getUid());
                                        activityDataMap.put("name", name);
                                        activityDataMap.put("email", email);
                                        activityDataMap.put("password", password);


                                        CustomData userData = new CustomData(activityDataMap);
                                        activityViewModel.setCustomData(userData);

                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("userDict", activityDataMap);

                                        Fragment fragment_user_info = new fragment_user_info();
                                        fragment_user_info.setArguments(bundle);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.hm_fragment, fragment_user_info)
                                                .addToBackStack(null)
                                                .commit();
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
}
