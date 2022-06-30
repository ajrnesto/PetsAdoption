package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petsadoption.Adapters.MessageAdapter;
import com.petsadoption.Objects.Message;
import com.petsadoption.Objects.User;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class ViewMessageFragment extends Fragment implements MessageAdapter.OnMessageListener {

    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private DatabaseReference dbUser;
    private DatabaseReference dbMessages;
    private DatabaseReference dbUserMessages;

    private ValueEventListener velUserMessages;

    private static final int USER_TYPE_REGULAR = 0;
    private static final int USER_TYPE_ADMIN = 1;

    View view;

    RecyclerView rvChat;
    CircularProgressIndicator loadingBar;
    TextInputEditText etChatBox;
    MaterialButton btnSend;

    ArrayList<Message> arrMessages;
    MessageAdapter messageAdapter;
    MessageAdapter.OnMessageListener onMessageListener = this;

    String chat = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_message, container, false);

        initialize();
        loadUserMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chat = etChatBox.getText().toString().trim();
                if (chat.isEmpty()) {
                    return;
                }
                updateAdminMessageNode();
                updateUserMessageNode();
                etChatBox.getText().clear();
            }

            private void updateAdminMessageNode() {
                if (!USER.getEmail().toLowerCase().contains("admin")) {
                    dbMessages = PETS_DB.getReference("messages").child(requireArguments().getString("user_uid"));
                    dbMessages.child(requireArguments().getString("user_uid")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) { // if node does not exist yet, then build it
                                dbUser = PETS_DB.getReference("user_"+USER.getUid());
                                dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String uid = USER.getUid();
                                        String firstName = snapshot.child("firstName").getValue().toString();
                                        String lastName = snapshot.child("lastName").getValue().toString();
                                        String email = USER.getEmail();
                                        String photoUrl;
                                        if (snapshot.child("photoUrl").exists()) {
                                            photoUrl = snapshot.child("photoUrl").getValue().toString();
                                        }
                                        else {
                                            photoUrl = Utils.getDefaultPhotoUrl();
                                        }
                                        String message = chat;
                                        long timestamp = System.currentTimeMillis();

                                        User newUserMessageNode = new User(uid, firstName, lastName, email, photoUrl, message, timestamp);
                                        dbMessages.setValue(newUserMessageNode);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else { // else, just update the latest message, the timestamp, and the latest photo url
                                dbUser = PETS_DB.getReference("user_"+USER.getUid()).child("photoUrl");
                                dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String photoUrl = snapshot.getValue().toString();
                                        String message = chat;
                                        long timestamp = System.currentTimeMillis();

                                        dbMessages.child("photoUrl").setValue(photoUrl);
                                        dbMessages.child("message").setValue(message);
                                        dbMessages.child("timestamp").setValue(timestamp);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            private void updateUserMessageNode() {
                dbUserMessages = PETS_DB.getReference("user_"+requireArguments().getString("user_uid")+"_messages");
                DatabaseReference newMessage = dbUserMessages.push();
                String uid = newMessage.getKey();
                String message = chat;
                String authorEmail = USER.getEmail();
                long timestamp = System.currentTimeMillis();

                if (getCurrentUserType() == USER_TYPE_ADMIN) {
                    String authorPhotoUrl = Utils.getAdminPhotoUrl();
                    Message newMessageData = new Message(uid, message, authorEmail, authorPhotoUrl, timestamp);
                    newMessage.setValue(newMessageData);
                }
                else {
                    dbUser = PETS_DB.getReference("user_"+USER.getUid()).child("photoUrl");
                    dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String authorPhotoUrl;

                            if (snapshot.exists()) {
                                 authorPhotoUrl = snapshot.getValue().toString();
                            }
                            else {
                                authorPhotoUrl = Utils.getDefaultPhotoUrl();
                            }

                            Message newMessageData = new Message(uid, message, authorEmail, authorPhotoUrl, timestamp);
                            newMessage.setValue(newMessageData);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        dbUserMessages.removeEventListener(velUserMessages);
    }

    private void initialize() {
        loadingBar = view.findViewById(R.id.loadingBar);
        rvChat = view.findViewById(R.id.rvChat);
        etChatBox = view.findViewById(R.id.etChatBox);
        btnSend = view.findViewById(R.id.btnSend);
    }

    private void loadUserMessages() {
        arrMessages = new ArrayList<>();
        rvChat = view.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);

        dbUserMessages = PETS_DB.getReference("user_"+requireArguments().getString("user_uid")+"_messages");
        velUserMessages = dbUserMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrMessages.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    arrMessages.add(message);
                    messageAdapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(arrMessages.size() - 1);
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageAdapter = new MessageAdapter(getContext(), getCurrentUserType(), arrMessages, onMessageListener);
        rvChat.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
    }

    private int getCurrentUserType() {
        if (USER.getEmail().toLowerCase().contains("admin")) {
            return USER_TYPE_ADMIN;
        }
        else {
            return USER_TYPE_REGULAR;
        }
    }

    @Override
    public void onMessageClick(int position) {

    }
}