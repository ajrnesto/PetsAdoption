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
import com.petsadoption.Objects.Application;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.applicationViewHolder>{

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbPets, dbApplications;

    static Context context;
    static ArrayList<Application> arrApplications;
    ArrayList<Pet> arrPets = new ArrayList<>();
    private ApplicationAdapter.OnApplicationListener mOnApplicationListener;

    public ApplicationAdapter(Context context, ArrayList<Application> arrApplications, ApplicationAdapter.OnApplicationListener onApplicationListener) {
        this.context = context;
        this.arrApplications = arrApplications;
        this.mOnApplicationListener = onApplicationListener;
    }

    @NonNull
    @Override
    public ApplicationAdapter.applicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_application, parent, false);
        return new ApplicationAdapter.applicationViewHolder(view, mOnApplicationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationAdapter.applicationViewHolder holder, int position) {
        Application application = arrApplications.get(position);
        String uid = application.getUid();
        long timestamp = application.getTimestamp();
        int status = application.getStatus();
        String firstName = application.getFirstName();
        String lastName = application.getLastName();
        String email = application.getEmail();
        String mobile = application.getMobile();
        String address = application.getAddress();
        String petUid = application.getPetUid();

        dbPets = PETS_DB.getReference("pets").child(petUid);
        dbPets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    setStatusToUnavailable(uid);
                    holder.tvStatus.setText("Pet uavailable");
                }
                else {
                    holder.tvStatus.setText(Utils.getStatus(status));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm aa");
                holder.tvTimestamp.setText(sdf.format(timestamp));
                holder.tvApplicantName.setText(firstName+ " " +lastName);
                holder.tvEmail.setText(email);
                holder.tvMobile.setText(mobile);
                holder.tvAddress.setText(address);

                holder.tvPetName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                holder.tvPetBreed.setText(Objects.requireNonNull(snapshot.child("breed").getValue()).toString());
            }

            private void setStatusToUnavailable(String uid) {
                dbApplications = PETS_DB.getReference("applications").child(uid);
                dbApplications.child("status").setValue(-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrApplications.size();
    }

    public class applicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTimestamp, tvStatus, tvApplicantName, tvEmail, tvMobile, tvAddress, tvPetName, tvPetBreed;
        ApplicationAdapter.OnApplicationListener onApplicationListener;

        public applicationViewHolder(@NonNull View itemView, ApplicationAdapter.OnApplicationListener onApplicationListener) {
            super(itemView);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvApplicantName = itemView.findViewById(R.id.tvApplicantName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetBreed = itemView.findViewById(R.id.tvPetBreed);

            this.onApplicationListener = onApplicationListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onApplicationListener.onApplicationClick(getAdapterPosition());
        }
    }

    public interface OnApplicationListener{
        void onApplicationClick(int position);
    }
}