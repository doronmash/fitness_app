package com.example.demo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.demo.models.User;
import com.example.demo.view_models.ActivityViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class fragment_user_info extends Fragment {

    DatePicker datePicker;
    NumberPicker weightPicker;
    Button btn_next;
    HashMap<String, String> userDataDict;
    private ActivityViewModel activityViewModel;
    FirebaseDatabase database_new;
    DatabaseReference db_user_reference_new;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private User createNewUser() {
        String userID = userDataDict.get("userID");
        String name = userDataDict.get("name");
        String email = userDataDict.get("email");
        String password = userDataDict.get("password");
        int weight = weightPicker.getValue();
        int selectedDay = datePicker.getDayOfMonth();
        int selectedMonth = datePicker.getMonth();
        int selectedYear = datePicker.getYear();

        // Create a Calendar instance and set the selected date
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay);

        // Convert Calendar to Date object
        Date dateOfBirth = calendar.getTime();

        User newUser = new User(userID, name, email, password, weight, dateOfBirth);

        return newUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        activityViewModel = new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        userDataDict = activityViewModel.getCustomData().getValue().getData();

        datePicker = view.findViewById(R.id.ui_dp_datePicker);
        btn_next = view.findViewById(R.id.ui_btn_next);
        weightPicker = view.findViewById(R.id.ui_np_weight);
        database_new = FirebaseDatabase.getInstance();
        db_user_reference_new = database_new.getReference("users");

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseApp.initializeApp(getContext());
                // Handle data changes in YourActivity

                User newUser = createNewUser();

                // Check if the new user email is unique
                db_user_reference_new.orderByChild("email").equalTo(newUser.getEmail())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // User email already exists, handle the case (e.g., show an error message)
                                    Log.d("FirebaseExample", "User email already exists.");
                                    Toast.makeText(getActivity(), "User email already exists.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // User email is unique, add the new user
                                    db_user_reference_new.child(newUser.getUserID()).setValue(newUser);
                                    Log.d("FirebaseExample", "New user added successfully.");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("FirebaseExample", "Error checking for existing user email: " + databaseError.getMessage());
                            }
                        });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the range for year picker (adjust as needed)
        weightPicker.setMinValue(25);
        weightPicker.setMaxValue(150);
    }
}
