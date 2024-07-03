package com.petsadoption.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private FirebaseAuth AUTH;
    private DatabaseReference refUser;

    ChipGroup cgAuth;
    Chip chipLogin, chipSignup;
    MaterialCardView cvTitle, cvLogin, cvSignup;
    TextInputEditText etLoginEmail, etLoginPassword;
    TextInputEditText etSignupFirstName, etSignupLastName, etSignupMobile, etSignupEmail, etSignupPassword;
    TextView textView;
    MaterialButton btnLogin, btnSignup, btnSkip;

    int focusCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        AUTH = FirebaseAuth.getInstance();

        if (AUTH.getCurrentUser() != null) {
            AUTH.signOut();
        }

        initialize();
        chipHandler();
        loadCards(Collections.singletonList(chipLogin.getId()));
        keyboardListener();

        btnSignup.setOnClickListener(view -> {
            registration();
        });

        btnLogin.setOnClickListener(view -> {
            authentication();
        });

        btnSkip.setOnClickListener(view -> {
            startActivity(new Intent(AuthenticationActivity.this, UserActivity.class));
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
    }

    private void initialize() {
        // header
        textView = findViewById(R.id.textView);
        cvTitle = findViewById(R.id.cvTitle);
        cgAuth = findViewById(R.id.cgAuth);
        chipLogin = findViewById(R.id.chipLogin);
        chipSignup = findViewById(R.id.chipSignup);
        // login
        cvLogin = findViewById(R.id.cvLogin);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSkip = findViewById(R.id.btnSkip);
        // sign up
        cvSignup = findViewById(R.id.cvSignup);
        etSignupFirstName = findViewById(R.id.etSignupFirstName);
        etSignupLastName = findViewById(R.id.etSignupLastName);
        etSignupMobile = findViewById(R.id.etSignupMobile);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);
    }

    private void chipHandler() {
        cgAuth.setOnCheckedStateChangeListener((group, checkedIds) -> {
            loadCards(checkedIds);
        });
    }

    private void loadCards(List<Integer> checkedIds) {
        if (checkedIds.contains(chipLogin.getId())) {
            cvLogin.setVisibility(View.VISIBLE);
            cvSignup.setVisibility(View.GONE);
        }
        else if (checkedIds.contains(chipSignup.getId())) {
            cvLogin.setVisibility(View.GONE);
            cvSignup.setVisibility(View.VISIBLE);
        }
    }

    private void keyboardListener () {
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > Utils.dpToPx(AuthenticationActivity.this, 200)) {
                    // if keyboard visible
                    cvTitle.setVisibility(View.GONE);
                }
                else {
                    cvTitle.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void registration() {
        btnSignup.setEnabled(false);
        String firstName = Objects.requireNonNull(etSignupFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(etSignupLastName.getText()).toString();
        String mobile = Objects.requireNonNull(etSignupMobile.getText()).toString();
        String email = Objects.requireNonNull(etSignupEmail.getText()).toString();
        String password = Objects.requireNonNull(etSignupPassword.getText()).toString();

        if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(mobile) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        if (password.length() < 6) {
            Utils.basicDialog(this, "The password should be at least 6 characters", "Okay");
            btnSignup.setEnabled(true);
            return;
        }

        AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        refUser = PETS_DB.getReference("user_"+ Objects.requireNonNull(AUTH.getCurrentUser()).getUid());
                        refUser.child("uid").setValue(AUTH.getCurrentUser().getUid());
                        refUser.child("firstName").setValue(firstName);
                        refUser.child("lastName").setValue(lastName);
                        refUser.child("mobile").setValue(mobile);
                        Toast.makeText(AuthenticationActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();

                        if (Objects.requireNonNull(email).toLowerCase().contains("admin")) {
                            startActivity(new Intent(this, AdminActivity.class));
                            finish();
                            return;
                        }
                        startActivity(new Intent(this, UserActivity.class));
                        finish();
                    }
                    else {
                        Utils.basicDialog(this, "Something went wrong! Please try again.", "Try again");
                        btnSignup.setEnabled(true);
                    }
                });
    }

    private void authentication() {
        btnLogin.setEnabled(false);
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Utils.basicDialog(this, "All fields are required!", "Okay");
            btnLogin.setEnabled(true);
            return;
        }

        AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {

                       Toast.makeText(this, "Signed in as "+email, Toast.LENGTH_SHORT).show();
                       if (Objects.requireNonNull(email).toLowerCase().contains("admin")) {
                           startActivity(new Intent(this, AdminActivity.class));
                           finish();
                           return;
                       }
                       startActivity(new Intent(this, UserActivity.class));
                       finish();
                   }
                   else {
                       Utils.basicDialog(this, "Email or password is incorrect", "Try again");
                       btnLogin.setEnabled(true);
                   }
                });
    }
}