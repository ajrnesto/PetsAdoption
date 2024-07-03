package com.petsadoption.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.petsadoption.R;

import java.util.Locale;
import java.util.Objects;

public class StartupActivity extends AppCompatActivity {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private DatabaseReference refUser;

    RoundedImageView ivLogo;
    MaterialButton btnSignIn, btnSkip;
    MaterialCardView cvGetStarted;

    @Override
    protected void onStart() {
        super.onStart();
        if (USER == null) {
            return;
        }
        if (Objects.requireNonNull(USER.getEmail()).toLowerCase().contains("admin")) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }
        startActivity(new Intent(this, UserActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        initialize();
        startLoading();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initialize() {
        ivLogo = findViewById(R.id.ivLogo);
        cvGetStarted = findViewById(R.id.cvGetStarted);
        btnSignIn = findViewById(R.id.btnSignin);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void startLoading() {
        ivLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale));

        final Handler handler = new Handler();
        handler.postDelayed(this::loadingComplete, 2000);
    }

    private void loadingComplete() {
        ivLogo.setVisibility(View.GONE);
        cvGetStarted.setVisibility(View.VISIBLE);
        btnSignIn.setOnClickListener(view -> {
            startActivity(new Intent(this, AuthenticationActivity.class));
        });
        btnSkip.setOnClickListener(view -> {
            startActivity(new Intent(this, UserActivity.class));
            finish();
        });
    }
}