package com.petsadoption.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Fragments.AdoptFragment;
import com.petsadoption.Fragments.AdoptionFormFragment;
import com.petsadoption.Fragments.ApplicationsFragment;
import com.petsadoption.Fragments.FavoritesFragment;
import com.petsadoption.Fragments.PetDetailsFragment;
import com.petsadoption.Fragments.MessagingUsersFragment;
import com.petsadoption.Fragments.ProfileFragment;
import com.petsadoption.Fragments.ViewApplicationFragment;
import com.petsadoption.Fragments.ViewMessageFragment;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private FirebaseAuth AUTH;
    private FirebaseUser USER;
    private DatabaseReference refUser;

    MaterialCardView cvActionBar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    public static NavigationView navView;
    View headerView;
    MaterialToolbar toolbar;
    MenuItem miAdopt, miFavorites, miApplications, miMessages;
    TextView tvUserName, tvUserEmail, tvActivityTitle;
    MaterialButton btnSignOut, btnSignin, btnActionBar;
    RoundedImageView imgUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();

        initializeMenuDrawer();
        initalizeActionBar();

        Fragment fragment = new AdoptFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(() -> {
            PetDetailsFragment petDetailsFragment = (PetDetailsFragment) getSupportFragmentManager().findFragmentByTag("DETAILS");
            AdoptionFormFragment adoptionFormFragment = (AdoptionFormFragment) getSupportFragmentManager().findFragmentByTag("ADOPTION_FORM");
            ViewApplicationFragment viewApplicationFragment = (ViewApplicationFragment) getSupportFragmentManager().findFragmentByTag("VIEW_APPLICATION");
            ViewMessageFragment viewMessageFragment = (ViewMessageFragment) getSupportFragmentManager().findFragmentByTag("VIEW_MESSAGE");

            if (petDetailsFragment != null && petDetailsFragment.isVisible()) {
                tvActivityTitle.setText("Pet Information");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (adoptionFormFragment != null && adoptionFormFragment.isVisible()) {
                tvActivityTitle.setText("Adoption Form");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (viewApplicationFragment != null && viewApplicationFragment.isVisible()) {
                tvActivityTitle.setText("Application Details");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else if (viewMessageFragment != null && viewMessageFragment.isVisible()) {
                tvActivityTitle.setText("Chat with us");
                cvActionBar.setVisibility(View.VISIBLE);
            }
            else {
                cvActionBar.setVisibility(View.GONE);
            }
        });

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    private void initalizeActionBar() {
        btnActionBar = findViewById(R.id.btnActionBar);
        cvActionBar = findViewById(R.id.cvActionBar);
        tvActivityTitle = findViewById(R.id.tvActivityTitle);

        btnActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializeMenuDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);
        headerView = navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        btnSignOut = headerView.findViewById(R.id.btnSignOut);
        btnSignin = headerView.findViewById(R.id.btnSignin);
        imgUserPhoto = headerView.findViewById(R.id.imgUserPhoto);
        miAdopt = findViewById(R.id.miAdopt);
        miFavorites = findViewById(R.id.miFavorites);
        miApplications = findViewById(R.id.miApplications);
        miMessages = findViewById(R.id.miMessages);

        if (USER != null) {
            Toast.makeText(this, "email: "+USER.getEmail(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Welcome to PetsAdoption!", Toast.LENGTH_SHORT).show();
        }

        // header
        if (USER != null) {
            refUser = PETS_DB.getReference("user_"+USER.getUid());
            refUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                    String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                    tvUserName.setText(firstName + " " + lastName);

                    if (snapshot.child("photoUrl").exists()) {
                        Picasso.get().load(Objects.requireNonNull(snapshot.child("photoUrl").getValue()).toString()).fit().centerCrop().into(imgUserPhoto);
                    }
                    else {
                        Picasso.get().load(Utils.getDefaultPhotoUrl()).fit().centerCrop().into(imgUserPhoto);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tvUserEmail.setText(USER.getEmail());
        }
        else {
            Picasso.get().load(Utils.getDefaultPhotoUrl()).fit().centerCrop().into(imgUserPhoto);
            tvUserName.setText("Guest");
            tvUserEmail.setText("No email");
            tvUserEmail.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);
            btnSignin.setVisibility(View.VISIBLE);
        }

        btnSignOut.setOnClickListener(view -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            AUTH.signOut();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finishAffinity();
        });

        btnSignin.setOnClickListener(view -> {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finishAffinity();
        });
        // header

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Adopt");

        MaterialAlertDialogBuilder dialogLoginRequired = new MaterialAlertDialogBuilder(UserActivity.this);
        dialogLoginRequired.setTitle("Sign in required");
        dialogLoginRequired.setMessage("You need an account to access this feature.");
        dialogLoginRequired.setPositiveButton("Log in", (dialogInterface, i) -> {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        });
        dialogLoginRequired.setNeutralButton("Cancel", (dialogInterface, i) -> { });
        dialogLoginRequired.setOnDismissListener(dialogInterface -> navView.getMenu().getItem(0).setChecked(true));

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.miAdopt) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Adopt");
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, new AdoptFragment())
                        .addToBackStack(null)
                        .commit();
            }
            else if (item.getItemId() == R.id.miFavorites) {
                if (USER != null) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Favorites");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                            .replace(R.id.frameLayout, new FavoritesFragment())
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    dialogLoginRequired.show();
                }
            }
            else if (item.getItemId() == R.id.miApplications) {
                if (USER != null) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Applications");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                            .replace(R.id.frameLayout, new ApplicationsFragment())
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    dialogLoginRequired.show();
                }
            }
            else if (item.getItemId() == R.id.miMessages) {
                if (USER != null) {
                    Bundle userUid = new Bundle();
                    userUid.putString("user_uid", USER.getUid());

                    ViewMessageFragment viewMessageFragment = new ViewMessageFragment();
                    viewMessageFragment.setArguments(userUid);

                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                            .replace(R.id.frameLayout, viewMessageFragment, "VIEW_MESSAGE")
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    dialogLoginRequired.show();
                }
            }
            else if (item.getItemId() == R.id.miProfile) {
                if (USER != null) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                            .replace(R.id.frameLayout, new ProfileFragment(), "PROFILE")
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    dialogLoginRequired.show();
                }
            }
            else {
                Toast.makeText(this, "Not added yet", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.close();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }
}