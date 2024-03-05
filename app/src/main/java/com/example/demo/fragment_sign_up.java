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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_sign_up#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_sign_up extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_sign_up() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_sign_up.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_sign_up newInstance(String param1, String param2) {
        fragment_sign_up fragment = new fragment_sign_up();
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




    EditText et_email, et_username, et_password, et_r_password;
    Button btn_sign_up;
    FirebaseAuth mAuth;
    private String email, username, password, password_repeat;

    private void init_attributes(View view) {
        et_email = view.findViewById(R.id.su_email);
        et_username = view.findViewById(R.id.su_username);
        et_password = view.findViewById(R.id.su_password);
        et_r_password = view.findViewById(R.id.su_r_password);
        btn_sign_up = view.findViewById(R.id.su_btn);
        mAuth = FirebaseAuth.getInstance();

    }

    private void check_edit_text_content() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(password_repeat)) {
            Toast.makeText(getContext(), "Empty Content Found", Toast.LENGTH_SHORT).show();
            return;
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
                username = String.valueOf(et_username.getText());
                email = String.valueOf(et_email.getText());
                password = String.valueOf(et_password.getText());
                password_repeat = String.valueOf(et_r_password.getText());
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
//                                    updateUI(null);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getActivity(), "Failed to register: "+task.getException().getMessage()+"!", Toast.LENGTH_SHORT).show();
                                        Log.w("Pttt", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
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
