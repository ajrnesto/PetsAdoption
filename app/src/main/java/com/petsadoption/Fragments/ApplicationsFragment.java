package com.petsadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.petsadoption.Adapters.ApplicationAdapter;
import com.petsadoption.Adapters.FavouriteAdapter;
import com.petsadoption.Objects.Application;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;

import java.util.ArrayList;
import java.util.Objects;

public class ApplicationsFragment extends Fragment implements ApplicationAdapter.OnApplicationListener {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbApplications = PETS_DB.getReference("applications");
    private Query qryApplications;
    private ValueEventListener velApplications;

    View view;

    private TextView tvEmpty;
    ArrayList<Application> arrApplications;
    ApplicationAdapter applicationAdapter;
    ApplicationAdapter.OnApplicationListener onApplicationListener = this;
    RecyclerView rvApplications;
    CircularProgressIndicator loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_applications, container, false);

        initialize();
        loadApplications();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        qryApplications.removeEventListener(velApplications);
    }

    private void initialize() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        loadingBar = view.findViewById(R.id.loadingBar);
    }

    private void loadApplications() {
        arrApplications = new ArrayList<>();
        rvApplications = view.findViewById(R.id.rvApplications);
        rvApplications.setHasFixedSize(true);
        rvApplications.setLayoutManager(new LinearLayoutManager(getContext()));

        if (USER.getEmail().toLowerCase().contains("admin")){
            qryApplications = dbApplications.orderByChild("status");
        }
        else{
            qryApplications = dbApplications.orderByChild("applicantUid").equalTo(USER.getUid());
        }

        velApplications = qryApplications.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrApplications.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Application application = dataSnapshot.getValue(Application.class);
                    arrApplications.add(application);
                    applicationAdapter.notifyDataSetChanged();
                }

                loadingBar.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        applicationAdapter = new ApplicationAdapter(getContext(), arrApplications, onApplicationListener);
        rvApplications.setAdapter(applicationAdapter);
        applicationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onApplicationClick(int position) {
        Bundle application = new Bundle();
        application.putString("application_uid", arrApplications.get(position).getUid());
        application.putString("application_applicant_uid", arrApplications.get(position).getApplicantUid());
        application.putString("application_pet_uid", arrApplications.get(position).getPetUid());
        application.putString("application_first_name", arrApplications.get(position).getFirstName());
        application.putString("application_last_name", arrApplications.get(position).getLastName());
        application.putString("application_email", arrApplications.get(position).getEmail());
        application.putString("application_mobile", arrApplications.get(position).getMobile());
        application.putString("application_address", arrApplications.get(position).getAddress());
        application.putString("application_is_pet_owner", arrApplications.get(position).getIsPetOwner());
        application.putString("application_is_home_owner", arrApplications.get(position).getIsHomeOwner());
        application.putString("application_has_yard", arrApplications.get(position).getHasYard());
        application.putString("application_has_children", arrApplications.get(position).getHasChildren());
        application.putString("application_pet_names", arrApplications.get(position).getPetNames());
        application.putString("application_pet_breeds", arrApplications.get(position).getPetBreeds());
        application.putString("application_pet_disposition", arrApplications.get(position).getPetDisposition());
        application.putString("application_vet_mobile", arrApplications.get(position).getVetMobile());
        application.putString("application_is_yard_fenced", arrApplications.get(position).getIsYardFenced());
        application.putString("application_children_ages", arrApplications.get(position).getChildrenAges());
        application.putInt("application_status", arrApplications.get(position).getStatus());
        application.putLong("application_timestamp", arrApplications.get(position).getTimestamp());

        ViewApplicationFragment viewApplicationFragment = new ViewApplicationFragment();
        viewApplicationFragment.setArguments(application);

        ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                .replace(R.id.frameLayout, viewApplicationFragment, "VIEW_APPLICATION")
                .addToBackStack(null)
                .commit();
    }
}