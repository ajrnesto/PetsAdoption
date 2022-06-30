package com.petsadoption.Adapters;

import android.content.Context;
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
import com.petsadoption.Fragments.PetDetailsFragment;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.favouriteViewHolder>{

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static DatabaseReference DB_FAVS = PETS_DB.getReference("user_"+USER.getUid()+"_favourites");
    private static DatabaseReference dbPets;

    static Context context;
    static ArrayList<String> arrFavourites;
    ArrayList<Pet> arrPets = new ArrayList<>();
    private FavouriteAdapter.OnFavouriteListener mOnFavouriteListener;

    public FavouriteAdapter(Context context, ArrayList<String> arrFavourites, FavouriteAdapter.OnFavouriteListener onFavouriteListener) {
        this.context = context;
        this.arrFavourites = arrFavourites;
        this.mOnFavouriteListener = onFavouriteListener;
    }

    @NonNull
    @Override
    public FavouriteAdapter.favouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_pet, parent, false);
        return new FavouriteAdapter.favouriteViewHolder(view, mOnFavouriteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.favouriteViewHolder holder, int position) {
        String petUid = arrFavourites.get(position);

        holder.btnFavourite.setIconResource(R.drawable.heart_24);
        holder.btnFavourite.setIconTintResource(R.color.red_dark);

        dbPets = PETS_DB.getReference("pets").child(petUid);
        dbPets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);
                arrPets.add(pet);

                String name = pet.getName();
                String photo = pet.getPhoto();
                String breed = pet.getBreed();
                String sex = pet.getSex();

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrFavourites.size();
    }

    public class favouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MaterialCardView cvPetPhoto, cvPetBasicInfo;
        CircularProgressIndicator loading;
        RoundedImageView ivPhoto;
        TextView tvName, tvBreed, tvAge;
        MaterialButton btnSex, btnRemove, btnFavourite, btnAdopt;
        FavouriteAdapter.OnFavouriteListener onFavouriteListener;

        public favouriteViewHolder(@NonNull View itemView, FavouriteAdapter.OnFavouriteListener onFavouriteListener) {
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
            this.onFavouriteListener = onFavouriteListener;

            btnFavourite.setOnClickListener(this);
            btnAdopt.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (view == btnFavourite) {
                DB_FAVS = PETS_DB.getReference("user_"+USER.getUid()+"_favourites").child(arrPets.get(position).getUid());
                DB_FAVS.removeValue();
                btnFavourite.setIconResource(R.drawable.heart_outline_24);
                btnFavourite.setIconTintResource(R.color.green_olive);
                arrFavourites.remove(position);
                Toast.makeText(itemView.getContext(), "Removed "+arrPets.get(position).getName()+" from Favourites", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
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

    public interface OnFavouriteListener{
        void onFavouriteClick(int position);
    }
}