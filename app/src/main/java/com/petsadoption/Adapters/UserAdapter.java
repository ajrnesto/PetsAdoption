package com.petsadoption.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Objects.User;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userViewHolder>{

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets, dbUsers;

    Context context;
    static ArrayList<User> arrUsers;
    ArrayList<Pet> arrPets = new ArrayList<>();
    private UserAdapter.OnUserListener mOnUserListener;

    public UserAdapter(Context context, ArrayList<User> arrUsers, UserAdapter.OnUserListener onUserListener) {
        this.context = context;
        this.arrUsers = arrUsers;
        this.mOnUserListener = onUserListener;
    }

    @NonNull
    @Override
    public UserAdapter.userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_user, parent, false);
        return new UserAdapter.userViewHolder(view, mOnUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.userViewHolder holder, int position) {
        User user = arrUsers.get(position);
        String userName = user.getFirstName();
        String message = user.getMessage();
        String photoUrl = user.getPhotoUrl();
        long timestamp = user.getTimestamp();

        Picasso.get().load(photoUrl).fit().centerCrop().into(holder.imgUserPhoto, new Callback() {
            @Override
            public void onSuccess() {
                holder.tvUserName.setText(userName);
                holder.tvLastMessage.setText(message);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy - hh:mm aa");
                holder.tvTimestamp.setText(sdf.format(timestamp));
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RoundedImageView imgUserPhoto;
        TextView tvUserName, tvLastMessage, tvTimestamp;
        UserAdapter.OnUserListener onUserListener;

        public userViewHolder(@NonNull View itemView, UserAdapter.OnUserListener onUserListener) {
            super(itemView);
            imgUserPhoto = itemView.findViewById(R.id.imgUserPhoto);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

            this.onUserListener = onUserListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onUserListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserListener{
        void onUserClick(int position);
    }
}
