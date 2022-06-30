package com.petsadoption.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.Fragments.AddPetFragment;
import com.petsadoption.Fragments.AdoptFragment;
import com.petsadoption.Fragments.AdoptionFormFragment;
import com.petsadoption.Fragments.ApplicationsFragment;
import com.petsadoption.Fragments.PetDetailsFragment;
import com.petsadoption.Fragments.MessagingUsersFragment;
import com.petsadoption.Fragments.ViewApplicationFragment;
import com.petsadoption.Fragments.ViewMessageFragment;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private FirebaseAuth AUTH;
    private FirebaseUser USER;
    private DatabaseReference refUser;

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    public static NavigationView navView;
    View headerView;
    MaterialToolbar toolbar;
    MenuItem miAdopt, miAddPet, miApplications, miMessages;
    TextView tvUserName, tvUserEmail, tvActivityTitle;
    MaterialButton btnSignOut, btnActionBar;
    MaterialCardView cvActionBar;
    RoundedImageView imgUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

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
        btnActionBar.setOnClickListener(view -> onBackPressed());
    }

    private void initializeMenuDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);
        headerView = navView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        btnSignOut = headerView.findViewById(R.id.btnSignOut);
        miAdopt = findViewById(R.id.miAdopt);
        miAddPet = findViewById(R.id.miAddPet);
        miApplications = findViewById(R.id.miApplications);
        miMessages = findViewById(R.id.miMessages);
        imgUserPhoto = headerView.findViewById(R.id.imgUserPhoto);

        // header
        Picasso.get().load(R.drawable.img_dog).into(imgUserPhoto);
        tvUserName.setText("Administrator");
        tvUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Picasso.get().load(Utils.getAdminPhotoUrl()).fit().centerCrop().into(imgUserPhoto);
        btnSignOut.setOnClickListener(view -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();

            AUTH.signOut();

            startActivity(new Intent(this, AuthenticationActivity.class));
            finishAffinity();
        });
        // header

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.menu_burger_24);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Pets");
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.miAdopt) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Pets");
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, new AdoptFragment(), "ADOPT")
                        .addToBackStack(null)
                        .commit();
            }
            else if (item.getItemId() == R.id.miAddPet) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Add Pet");
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, new AddPetFragment(), "ADD_PET")
                        .addToBackStack(null)
                        .commit();
            }
            else if (item.getItemId() == R.id.miApplications) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Applications");
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, new ApplicationsFragment(), "APPLICATIONS")
                        .addToBackStack(null)
                        .commit();
            }
            else if (item.getItemId() == R.id.miMessages) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Messages");
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in_enter, R.anim.zoom_in_exit, R.anim.zoom_out_enter, R.anim.zoom_out_exit)
                        .replace(R.id.frameLayout, new MessagingUsersFragment(), "MESSAGES")
                        .addToBackStack(null)
                        .commit();
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