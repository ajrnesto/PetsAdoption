package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petsadoption.Adapters.PetAdapter;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;

import java.util.ArrayList;

public class AdoptFragment extends Fragment implements PetAdapter.OnPetListener {

    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage PETS_ST = FirebaseStorage.getInstance();
    private DatabaseReference dbPets = PETS_DB.getReference("pets");
    private DatabaseReference dbFavs = PETS_DB.getReference("user_"+USER.getUid()+"_favourites");
    private StorageReference stPets = PETS_ST.getReference("petPhotos");
    private Query qryPets;
    private ValueEventListener velPets, velFavs;

    View view;

    private TextView tvEmpty;
    private ChipGroup cgAnimals;

    ArrayList<Pet> arrPets;
    ArrayList<String> arrFavs;
    PetAdapter petAdapter;
    PetAdapter.OnPetListener onPetListener = this;
    RecyclerView rvPets;
    CircularProgressIndicator loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adopt, container, false);

        initialize();
        cgAnimals.setOnCheckedStateChangeListener((group, checkedIds) -> {
            loadRecyclerView(checkedIds.get(0));
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbPets.removeEventListener(velPets);
    }

    private void initialize() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        cgAnimals = view.findViewById(R.id.cgAnimals);
        loadingBar = view.findViewById(R.id.loadingBar);

        loadRecyclerView(R.id.chipDog);
    }

    private void loadRecyclerView(int checkedId) {
        arrPets = new ArrayList<>();
        arrFavs = new ArrayList<>();
        rvPets = view.findViewById(R.id.rvPets);
        rvPets.setHasFixedSize(true);
        rvPets.setLayoutManager(new LinearLayoutManager(getContext()));

        if (checkedId == R.id.chipDog){
            qryPets = dbPets.orderByChild("animal").equalTo("Dog");
        }
        else if (checkedId == R.id.chipCat){
            qryPets = dbPets.orderByChild("animal").equalTo("Cat");
        }
        else if (checkedId == R.id.chipRabbit){
            qryPets = dbPets.orderByChild("animal").equalTo("Rabbit");
        }
        else if (checkedId == R.id.chipBird){
            qryPets = dbPets.orderByChild("animal").equalTo("Bird");
        }

        velFavs = dbFavs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrFavs.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        velPets = qryPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                }
                arrPets.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    arrPets.add(pet);
                    petAdapter.notifyDataSetChanged();
                }
                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        petAdapter = new PetAdapter(getContext(), USER.getEmail(), arrPets, arrFavs, onPetListener);
        rvPets.setAdapter(petAdapter);
        petAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPetClick(int position) {

    }
}