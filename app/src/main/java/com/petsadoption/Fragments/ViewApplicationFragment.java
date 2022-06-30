package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class ViewApplicationFragment extends Fragment {

    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private DatabaseReference dbUserPhotoUrl;
    private DatabaseReference dbPets, dbStatus;

    View view;
    MaterialCardView cvPetInformation, cvApplicantInformation;
    RoundedImageView imgPetPhoto, imgUserPhoto;
    TextView tvTimestamp, tvStatus;
    TextInputLayout tilStatus;
    TextView tvPetName, tvPetSex, tvPetAge, tvPetWeight, tvPetLocation, tvPetPersonality, tvPetHealthNotes;
    TextView tvApplicantName, tvApplicantEmail, tvApplicantMobile, tvApplicantAddress,
            tvApplicantIsPetOwner, tvApplicantPetNames, tvApplicantPetBreeds, tvApplicantPetDisposition,
            tvApplicantVetMobile, tvApplicantIsHomeOwner, tvApplicantHasYard, tvApplicantIsYardFenced,
            tvApplicantHasChildren, tvApplicantChildrenAges;

    String application_uid, application_applicant_uid, application_pet_uid, application_first_name,
            application_last_name, application_email, application_mobile, application_address,
            application_is_pet_owner, application_is_home_owner, application_has_yard, application_has_children,
            application_pet_names, application_pet_breeds, application_pet_disposition, application_vet_mobile,
            application_is_yard_fenced, application_children_ages;
    int application_status;
    long application_timestamp;

    String[] statuses;
    ArrayAdapter<String> adapterStatuses;
    private AutoCompleteTextView menuStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_application, container, false);
        initialize();
        retrieveData();
        initializeSpinner();
        loadTimestampAndStatus();
        loadPetInformation();
        loadApplicantInformation();
        return view;
    }

    private void initialize() {
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        tilStatus = view.findViewById(R.id.tilStatus);
        tvStatus = view.findViewById(R.id.tvStatus);

        // pet information
        cvPetInformation = view.findViewById(R.id.cvPetInformation);
        tvPetName = view.findViewById(R.id.tvPetName);
        tvPetSex = view.findViewById(R.id.tvPetSex);
        tvPetAge = view.findViewById(R.id.tvPetAge);
        tvPetWeight = view.findViewById(R.id.tvPetWeight);
        tvPetLocation = view.findViewById(R.id.tvPetLocation);
        tvPetPersonality = view.findViewById(R.id.tvPetPersonality);
        tvPetHealthNotes = view.findViewById(R.id.tvPetHealthNotes);
        imgPetPhoto = view.findViewById(R.id.imgPetPhoto);

        // applicant information
        cvApplicantInformation = view.findViewById(R.id.cvApplicantInformation);
        tvApplicantName = view.findViewById(R.id.tvApplicantName);
        tvApplicantEmail = view.findViewById(R.id.tvApplicantEmail);
        tvApplicantMobile = view.findViewById(R.id.tvApplicantMobile);
        tvApplicantAddress = view.findViewById(R.id.tvApplicantAddress);
        tvApplicantIsPetOwner = view.findViewById(R.id.tvApplicantIsPetOwner);
        tvApplicantPetNames = view.findViewById(R.id.tvApplicantPetNames);
        tvApplicantPetBreeds = view.findViewById(R.id.tvApplicantPetBreeds);
        tvApplicantPetDisposition = view.findViewById(R.id.tvApplicantPetDisposition);
        tvApplicantVetMobile = view.findViewById(R.id.tvApplicantVetMobile);
        tvApplicantIsHomeOwner = view.findViewById(R.id.tvApplicantIsHomeOwner);
        tvApplicantHasYard = view.findViewById(R.id.tvApplicantHasYard);
        tvApplicantIsYardFenced = view.findViewById(R.id.tvApplicantIsYardFenced);
        tvApplicantHasChildren = view.findViewById(R.id.tvApplicantHasChildren);
        tvApplicantChildrenAges = view.findViewById(R.id.tvApplicantChildrenAges);
        imgUserPhoto = view.findViewById(R.id.imgUserPhoto);
    }

    private void retrieveData() {
        application_uid = requireArguments().getString("application_uid");
        application_applicant_uid = requireArguments().getString("application_applicant_uid");
        application_pet_uid = requireArguments().getString("application_pet_uid");
        application_first_name = requireArguments().getString("application_first_name");
        application_last_name = requireArguments().getString("application_last_name");
        application_email = requireArguments().getString("application_email");
        application_mobile = requireArguments().getString("application_mobile");
        application_address = requireArguments().getString("application_address");
        application_is_pet_owner = requireArguments().getString("application_is_pet_owner");
        application_is_home_owner = requireArguments().getString("application_is_home_owner");
        application_has_yard = requireArguments().getString("application_has_yard");
        application_has_children = requireArguments().getString("application_has_children");
        application_pet_names = requireArguments().getString("application_pet_names");
        application_pet_breeds = requireArguments().getString("application_pet_breeds");
        application_pet_disposition = requireArguments().getString("application_pet_disposition");
        application_vet_mobile = requireArguments().getString("application_vet_mobile");
        application_is_yard_fenced = requireArguments().getString("application_is_yard_fenced");
        application_children_ages = requireArguments().getString("application_children_ages");
        application_status = requireArguments().getInt("application_status");
        application_timestamp = requireArguments().getLong("application_timestamp");
    }

    private void initializeSpinner() {
        statuses = new String[] {"Pending", "For Interview", "Accepted", "Rejected", "Pet Unavailable"};
        adapterStatuses = new ArrayAdapter<>(getContext(), R.layout.list_item, statuses);
        menuStatus = view.findViewById(R.id.menuStatus);
        menuStatus.setAdapter(adapterStatuses);

        menuStatus.setText(statuses[application_status], false);

        dbStatus = PETS_DB.getReference("applications").child(application_uid).child("status");
        menuStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            dbStatus.setValue(i);
        });
    }

    private void loadTimestampAndStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm aa");
        tvTimestamp.setText(sdf.format(application_timestamp));

        if (USER.getEmail().toLowerCase().contains("admin")) {
            tvStatus.setVisibility(View.GONE);
            tilStatus.setVisibility(View.VISIBLE);
        }
        tvStatus.setText(Utils.getStatus(application_status));

        dbStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvStatus.setText(Utils.getStatus(Integer.parseInt(String.valueOf(snapshot.getValue()))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPetInformation() {
        dbPets = PETS_DB.getReference("pets").child(application_pet_uid);
        dbPets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);

                Picasso.get().load(Objects.requireNonNull(pet).getPhoto()).fit().centerCrop().into(imgPetPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        tvPetName.setText(Objects.requireNonNull(pet).getName());
                        tvPetSex.setText(pet.getSex());
                        tvPetAge.setText(Utils.ageBuilder(pet.getAgeYears(), pet.getAgeMonths()));
                        tvPetWeight.setText(pet.getWeight());
                        tvPetLocation.setText(pet.getLocation());
                        tvPetPersonality.setText(pet.getPersonality());
                        tvPetHealthNotes.setText(pet.getHealthNotes());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadApplicantInformation() {
        tvApplicantName.setText(application_first_name+ " " +application_last_name);
        tvApplicantEmail.setText(application_email);
        tvApplicantMobile.setText(application_mobile);
        tvApplicantAddress.setText(application_address);
        tvApplicantIsPetOwner.setText(application_is_pet_owner);
        tvApplicantPetNames.setText(application_pet_names);
        tvApplicantPetBreeds.setText(application_pet_breeds);
        tvApplicantPetDisposition.setText(application_pet_disposition);
        tvApplicantVetMobile.setText(application_vet_mobile);
        tvApplicantIsHomeOwner.setText(application_is_home_owner);
        tvApplicantHasYard.setText(application_has_yard);
        tvApplicantIsYardFenced.setText(application_is_yard_fenced);
        tvApplicantHasChildren.setText(application_has_children);
        tvApplicantChildrenAges.setText(application_children_ages);

        dbUserPhotoUrl = PETS_DB.getReference("user_"+application_applicant_uid).child("photoUrl");
        dbUserPhotoUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Picasso.get().load(snapshot.getValue().toString()).fit().centerCrop().into(imgUserPhoto);
                }
                else {
                    Picasso.get().load(Utils.getDefaultPhotoUrl()).fit().centerCrop().into(imgUserPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}