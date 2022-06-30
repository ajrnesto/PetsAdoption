package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.petsadoption.Adapters.FavouriteAdapter;
import com.petsadoption.Adapters.PetAdapter;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements FavouriteAdapter.OnFavouriteListener {

    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private DatabaseReference dbFavs = PETS_DB.getReference("user_"+USER.getUid()+"_favourites");
    private ValueEventListener velFavs;

    View view;

    private TextView tvEmpty;

    ArrayList<String> arrFavs;
    FavouriteAdapter favouriteAdapter;
    FavouriteAdapter.OnFavouriteListener onFavouriteListener = this;
    RecyclerView rvFavourites;
    CircularProgressIndicator loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initialize();
        loadRecyclerView();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbFavs.removeEventListener(velFavs);
    }

    private void initialize() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        loadingBar = view.findViewById(R.id.loadingBar);
    }

    private void loadRecyclerView() {
        arrFavs = new ArrayList<>();
        rvFavourites = view.findViewById(R.id.rvFavourites);
        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));

        velFavs = dbFavs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrFavs.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrFavs.add(dataSnapshot.getValue().toString());
                    favouriteAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        favouriteAdapter = new FavouriteAdapter(getContext(), arrFavs, onFavouriteListener);
        rvFavourites.setAdapter(favouriteAdapter);
        favouriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFavouriteClick(int position) {

    }
}