package com.petsadoption.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Activities.AuthenticationActivity;
import com.petsadoption.Activities.UserActivity;
import com.petsadoption.Fragments.PetDetailsFragment;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.petViewHolder>{

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage PETS_ST = FirebaseStorage.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static DatabaseReference dbPets;
    private static DatabaseReference dbFavs;
    private static StorageReference stPets;

    static Context context;
    static ArrayList<Pet> arrPets;
    static ArrayList<String> arrFavs;
    private PetAdapter.OnPetListener mOnPetListener;
    String userEmail;

    public PetAdapter(Context context, String userEmail, ArrayList<Pet> arrPets, ArrayList<String> arrFavs, OnPetListener onPetListener) {
        this.context = context;
        this.userEmail = userEmail;
        this.arrPets = arrPets;
        this.arrFavs = arrFavs;
        this.mOnPetListener = onPetListener;
    }

    @NonNull
    @Override
    public PetAdapter.petViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_pet, parent, false);
        return new PetAdapter.petViewHolder(view, mOnPetListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.petViewHolder holder, int position) {
        if (userEmail.toLowerCase().contains("admin")) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnFavourite.setVisibility(View.GONE);
            holder.btnAdopt.setVisibility(View.GONE);
        }
        else {
            holder.btnRemove.setVisibility(View.GONE);
            holder.btnFavourite.setVisibility(View.VISIBLE);
            holder.btnAdopt.setVisibility(View.VISIBLE);
        }
        Pet pet = arrPets.get(position);
        String uid = pet.getUid();
        String name = pet.getName();
        String photo = pet.getPhoto();
        String breed = pet.getBreed();
        String sex = pet.getSex();

        favouriteChecker(holder, uid);

        Picasso.get().load(photo).fit().centerCrop().into(holder.ivPhoto, new Callback() {
            @Override
            public void onSuccess() {
                holder.cvPetPhoto.setVisibility(View.VISIBLE);
                holder.cvPetBasicInfo.setVisibility(View.VISIBLE);
                holder.loading.hide();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        holder.tvName.setText(name);
        holder.tvBreed.setText(breed);
        Utils.renderSex(sex, holder.btnSex);
    }

    private void favouriteChecker(petViewHolder holder, String uid) {
        if (arrFavs.contains(uid)) {
            holder.btnFavourite.setIconResource(R.drawable.heart_24);
            holder.btnFavourite.setIconTintResource(R.color.red_dark);
        }
        else {
            holder.btnFavourite.setIconResource(R.drawable.heart_outline_24);
            holder.btnFavourite.setIconTintResource(R.color.green_olive);
        }
        holder.btnFavourite.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return arrPets.size();
    }

    public static class petViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MaterialCardView cvPetPhoto, cvPetBasicInfo;
        CircularProgressIndicator loading;
        RoundedImageView ivPhoto;
        TextView tvName, tvBreed, tvAge;
        MaterialButton btnSex, btnRemove, btnFavourite, btnAdopt;
        PetAdapter.OnPetListener onPetListener;

        public petViewHolder(@NonNull View itemView, PetAdapter.OnPetListener onPetListener) {
            super(itemView);
            cvPetPhoto = itemView.findViewById(R.id.cvPetPhoto);
            cvPetBasicInfo = itemView.findViewById(R.id.cvPetBasicInfo);
            loading = itemView.findViewById(R.id.loading);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            tvBreed = itemView.findViewById(R.id.tvBreed);
            btnSex = itemView.findViewById(R.id.btnSex);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnFavourite = itemView.findViewById(R.id.btnFavourite);
            btnAdopt = itemView.findViewById(R.id.btnAdopt);
            this.onPetListener = onPetListener;

            btnRemove.setOnClickListener(this);
            btnFavourite.setOnClickListener(this);
            btnAdopt.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onPetListener.onPetClick(position);
            dbPets = PETS_DB.getReference("pets").child(arrPets.get(position).getUid());
            stPets = PETS_ST.getReference("petPhotos").child(arrPets.get(position).getFileName());
            String petName = arrPets.get(position).getName();

            MaterialAlertDialogBuilder dialogLoginRequired = new MaterialAlertDialogBuilder(context);
            dialogLoginRequired.setTitle("Sign in required");
            dialogLoginRequired.setMessage("You need an account to access this feature.");
            dialogLoginRequired.setPositiveButton("Log in", (dialogInterface, i) -> {
                ((Activity)context).startActivity(new Intent(context, AuthenticationActivity.class));
                ((Activity)context).finish();
            });
            dialogLoginRequired.setNeutralButton("Cancel", (dialogInterface, i) -> { });

            if (view == btnRemove) {
                MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(itemView.getContext(), R.style.PetsAdoption_Dialog);
                dialogDelete
                        .setMessage("Remove "+petName+" from this list?")
                        .setNegativeButton("Remove", (dialogInterface, i) -> {
                            stPets.delete()
                                    .addOnSuccessListener(unused -> {
                                        dbPets.removeValue();
                                        Toast.makeText(itemView.getContext(), "Removed "+petName+" from the list", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), petName+" was not removed from the list due to network problems", Toast.LENGTH_SHORT).show());
                });
                dialogDelete.setNeutralButton("Cancel", (dialogInterface, i) -> {
                }).setOnCancelListener(dialogInterface -> {
                }).show();
            }
            else if (view == btnFavourite) {
                if (USER != null) {
                    String petUid = arrPets.get(position).getUid();
                    dbFavs = PETS_DB.getReference("user_"+USER.getUid()+"_favourites").child(arrPets.get(position).getUid());
                    dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) { // if pet was marked as favourite
                                btnFavourite.setIconResource(R.drawable.heart_24);
                                btnFavourite.setIconTintResource(R.color.red_dark);
                                dbFavs.setValue(petUid);
                                Toast.makeText(itemView.getContext(), "Added "+petName+" to Favourites", Toast.LENGTH_SHORT).show();
                            }
                            else { // if pet was not yet marked as favourite
                                btnFavourite.setIconResource(R.drawable.heart_outline_24);
                                btnFavourite.setIconTintResource(R.color.green_olive);
                                dbFavs.removeValue();
                                Toast.makeText(itemView.getContext(), "Removed "+petName+" from Favourites", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    dialogLoginRequired.show();
                }
            }
            else if (view == btnAdopt) {
                Bundle petUid = new Bundle();
                petUid.putString("pet_uid", arrPets.get(position).getUid());
                petUid.putString("pet_photo", arrPets.get(position).getPhoto());
                petUid.putString("pet_name", arrPets.get(position).getName());
                petUid.putString("pet_breed", arrPets.get(position).getBreed());
                petUid.putString("pet_age_years", arrPets.get(position).getAgeYears());
                petUid.putString("pet_age_months", arrPets.get(position).getAgeMonths());
                petUid.putString("pet_sex", arrPets.get(position).getSex());
                petUid.putString("pet_weight", arrPets.get(position).getWeight());
                petUid.putString("pet_location", arrPets.get(position).getLocation());
                petUid.putString("pet_personality", arrPets.get(position).getPersonality());
                petUid.putString("pet_health_notes", arrPets.get(position).getHealthNotes());

                PetDetailsFragment petDetailsFragment = new PetDetailsFragment();
                petDetailsFragment.setArguments(petUid);

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, petDetailsFragment, "DETAILS")
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public interface OnPetListener{
        void onPetClick(int position);
    }
}