package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PetDetailsFragment extends Fragment {

    View view;
    MaterialCardView cvPetInformation;
    RoundedImageView imgPet;
    TextView tvMoreInformation, tvName, tvBreed, tvAge, tvWeight, tvPersonality, tvLocation, tvHealthNotes;
    MaterialButton btnSex, btnAdopt;

    String uid, photo, name, breed, ageYears, ageMonths, sex, weight, location, personality, healthNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pet_details, container, false);
        initialize();
        loadPetInformation();

        btnAdopt.setOnClickListener(view -> {
            Bundle petUid = new Bundle();
            petUid.putString("pet_uid", uid);

            AdoptionFormFragment adoptionFormFragment = new AdoptionFormFragment();
            adoptionFormFragment.setArguments(petUid);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                    .replace(R.id.frameLayout, adoptionFormFragment, "ADOPTION_FORM")
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void initialize() {
        cvPetInformation = view.findViewById(R.id.cvBasicInformation);
        imgPet = view.findViewById(R.id.imgPet);
        tvName = view.findViewById(R.id.tvName);
        tvBreed = view.findViewById(R.id.tvBreed);
        tvAge = view.findViewById(R.id.tvAge);
        tvMoreInformation = view.findViewById(R.id.tvMoreInformation);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvPersonality = view.findViewById(R.id.tvPersonality);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvHealthNotes = view.findViewById(R.id.tvHealthNotes);
        btnSex = view.findViewById(R.id.btnSex);
        btnAdopt = view.findViewById(R.id.btnAdopt);
    }

    private void loadPetInformation() {
        uid = getArguments().getString("pet_uid");
        photo = getArguments().getString("pet_photo");
        name = getArguments().getString("pet_name");
        breed = getArguments().getString("pet_breed");
        ageYears = getArguments().getString("pet_age_years");
        ageMonths = getArguments().getString("pet_age_months");
        sex = getArguments().getString("pet_sex");
        weight = getArguments().getString("pet_weight");
        location = getArguments().getString("pet_location");
        personality = getArguments().getString("pet_personality");
        healthNotes = getArguments().getString("pet_health_notes");

        Picasso.get().load(photo).fit().centerCrop().into(imgPet, new Callback() {
            @Override
            public void onSuccess() {
                cvPetInformation.setVisibility(View.VISIBLE);
                btnAdopt.setVisibility(View.VISIBLE);

                tvName.setText(name);
                tvBreed.setText(breed);
                tvAge.setText(Utils.ageBuilder(ageYears, ageMonths));
                Utils.renderSex(sex, btnSex);
                if (weight == null || weight.isEmpty()) {
                    tvWeight.setVisibility(View.GONE);
                }
                else {
                    tvWeight.setText("Weight: "+weight+" kg");
                    if (tvMoreInformation.getVisibility() == View.GONE) {
                        tvMoreInformation.setVisibility(View.VISIBLE);
                    }
                }
                if (personality == null || personality.isEmpty()) {
                    tvPersonality.setVisibility(View.GONE);
                }
                else {
                    tvPersonality.setText("Personality: "+personality);
                    if (tvMoreInformation.getVisibility() == View.GONE) {
                        tvMoreInformation.setVisibility(View.VISIBLE);
                    }
                }
                if (location == null || location.isEmpty()) {
                    tvLocation.setVisibility(View.GONE);
                }
                else {
                    tvLocation.setText("Location: "+location);
                    if (tvMoreInformation.getVisibility() == View.GONE) {
                        tvMoreInformation.setVisibility(View.VISIBLE);
                    }
                }
                if (healthNotes == null || healthNotes.isEmpty()) {
                    tvHealthNotes.setVisibility(View.GONE);
                }
                else {
                    tvHealthNotes.setText("Health Notes: "+healthNotes);
                    if (tvMoreInformation.getVisibility() == View.GONE) {
                        tvMoreInformation.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                requireActivity().onBackPressed();
            }
        });
    }
}