package com.petsadoption.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Objects.Message;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageViewHolder>{

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets, dbMessages;

    private static final int USER_TYPE_REGULAR = 0;
    private static final int USER_TYPE_ADMIN = 1;

    Context context;
    int currentUserType;
    ArrayList<Message> arrMessages;
    private MessageAdapter.OnMessageListener mOnMessageListener;

    public MessageAdapter(Context context, int currentUserType, ArrayList<Message> arrMessages, MessageAdapter.OnMessageListener onMessageListener) {
        this.context = context;
        this.currentUserType = currentUserType;
        this.arrMessages = arrMessages;
        this.mOnMessageListener = onMessageListener;
    }

    @NonNull
    @Override
    public MessageAdapter.messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_message, parent, false);
        return new MessageAdapter.messageViewHolder(view, mOnMessageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.messageViewHolder holder, int position) {
        Message message = arrMessages.get(position);

        String uid = message.getUid();
        String messageContent = message.getMessage();
        String authorEmail = message.getAuthorEmail();
        String authorPhotoUrl = message.getAuthorPhotoUrl();
        long timestamp = message.getTimestamp();
        int authorUserType = getAuthorUserType(authorEmail);

        if (authorUserType == currentUserType) { // if user is the message author
            showFirstPerson(holder, uid, messageContent, authorEmail, authorPhotoUrl, timestamp);
            hideSecondPerson(holder);
        }
        else { // if user is NOT the message author
            showSecondPerson(holder, uid, messageContent, authorEmail, authorPhotoUrl, timestamp);
            hideFirstPerson(holder);
        }
    }

    private void showFirstPerson(messageViewHolder holder, String uid, String messageContent, String authorEmail, String authorPhotoUrl, long timestamp) {
        holder.cvFirstPerson.setVisibility(View.VISIBLE);
        holder.imgFirstPerson.setVisibility(View.VISIBLE);

        holder.tvFirstPersonMessage.setText(messageContent);
        if (getAuthorUserType(authorEmail) == USER_TYPE_ADMIN) {
            Picasso.get().load(Utils.getAdminPhotoUrl()).fit().centerCrop().into(holder.imgFirstPerson);
        }
        else {
            Picasso.get().load(authorPhotoUrl).fit().centerCrop().into(holder.imgFirstPerson);
        }
    }

    private void hideFirstPerson(messageViewHolder holder) {
        holder.cvFirstPerson.setVisibility(View.INVISIBLE);
        holder.imgFirstPerson.setVisibility(View.INVISIBLE);
    }

    private void showSecondPerson(messageViewHolder holder, String uid, String messageContent, String authorEmail, String authorPhotoUrl, long timestamp) {
        holder.cvSecondPerson.setVisibility(View.VISIBLE);
        holder.imgSecondPerson.setVisibility(View.VISIBLE);

        holder.tvSecondPersonMessage.setText(messageContent);
        if (getAuthorUserType(authorEmail) == USER_TYPE_ADMIN) {
            Picasso.get().load(Utils.getAdminPhotoUrl()).fit().centerCrop().into(holder.imgSecondPerson);
        }
        else {
            Picasso.get().load(authorPhotoUrl).fit().centerCrop().into(holder.imgSecondPerson);
        }
    }

    private void hideSecondPerson(messageViewHolder holder) {
        holder.cvSecondPerson.setVisibility(View.INVISIBLE);
        holder.imgSecondPerson.setVisibility(View.INVISIBLE);
    }

    private int getAuthorUserType(String authorEmail) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
            }
        }, 500);
        if (authorEmail.toLowerCase().contains("admin")) {
            return USER_TYPE_ADMIN;
        }
        else {
            return USER_TYPE_REGULAR;
        }
    }

    @Override
    public int getItemCount() {
        return arrMessages.size();
    }

    public class messageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RoundedImageView imgFirstPerson, imgSecondPerson;
        MaterialCardView cvFirstPerson, cvSecondPerson;
        TextView tvFirstPersonMessage, tvSecondPersonMessage;
        MessageAdapter.OnMessageListener onMessageListener;

        public messageViewHolder(@NonNull View itemView, MessageAdapter.OnMessageListener onMessageListener) {
            super(itemView);
            imgFirstPerson = itemView.findViewById(R.id.imgFirstPerson);
            cvFirstPerson = itemView.findViewById(R.id.cvFirstPerson);
            tvFirstPersonMessage = itemView.findViewById(R.id.tvFirstPersonMessage);

            imgSecondPerson = itemView.findViewById(R.id.imgSecondPerson);
            cvSecondPerson = itemView.findViewById(R.id.cvSecondPerson);
            tvSecondPersonMessage = itemView.findViewById(R.id.tvSecondPersonMessage);

            this.onMessageListener = onMessageListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMessageListener.onMessageClick(getAdapterPosition());
        }
    }

    public interface OnMessageListener{
        void onMessageClick(int position);
    }
}
