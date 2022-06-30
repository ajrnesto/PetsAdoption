package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petsadoption.Adapters.UserAdapter;
import com.petsadoption.Objects.User;
import com.petsadoption.R;

import java.util.ArrayList;

public class MessagingUsersFragment extends Fragment implements UserAdapter.OnUserListener {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private final DatabaseReference dbUsersChattingToAdmin = PETS_DB.getReference("messages");
    private ValueEventListener velUsersChattingToAdmin;

    View view;

    RecyclerView rvUsers;
    CircularProgressIndicator loadingBar;

    ArrayList<User> arrUsers;
    UserAdapter userAdapter;
    UserAdapter.OnUserListener onUserListener = this;

    @Override
    public void onResume() {
        super.onResume();
        loadMessages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messaging_users, container, false);

        initialize();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbUsersChattingToAdmin.removeEventListener(velUsersChattingToAdmin);
    }

    private void initialize() {
        loadingBar = view.findViewById(R.id.loadingBar);
        rvUsers = view.findViewById(R.id.rvUsers);
    }

    private void loadMessages() {
        arrUsers = new ArrayList<>();
        rvUsers.setHasFixedSize(true);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        velUsersChattingToAdmin = dbUsersChattingToAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    arrUsers.add(user);
                    userAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userAdapter = new UserAdapter(getContext(), arrUsers, onUserListener);
        rvUsers.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick(int position) {
        String userUid = arrUsers.get(position).getUid();

        DatabaseReference dr = PETS_DB.getReference("user_"+userUid).child("firstName");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.getValue().toString();
                TextView tvActivityTitle = requireActivity().findViewById(R.id.tvActivityTitle);
                tvActivityTitle.setText(firstName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("user_uid", userUid);

        ViewMessageFragment viewMessageFragment = new ViewMessageFragment();
        viewMessageFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                .replace(R.id.frameLayout, viewMessageFragment, "VIEW_MESSAGE")
                .addToBackStack(null)
                .commit();
    }
}