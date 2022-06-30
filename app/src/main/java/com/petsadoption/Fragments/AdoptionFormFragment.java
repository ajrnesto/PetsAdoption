package com.petsadoption.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.petsadoption.Activities.AdminActivity;
import com.petsadoption.Objects.Application;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;

import java.util.Objects;

public class AdoptionFormFragment extends Fragment {

    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private DatabaseReference dbUser, dbApplications;

    View view;

    private TextInputEditText etFirstName, etLastName, etEmail, etMobile, etAddress;
    private TextInputLayout tilPetNames, tilPetBreeds, tilPetDisposition, tilVetMobile, tilIsYardFenced, tilChildrenAges;
    private TextInputEditText etPetNames, etPetBreeds, etPetDisposition, etVetMobile, etIsYardFenced, etChildrenAges;
    private AutoCompleteTextView menuIsPetOwner, menuIsHomeOwner, menuHasYard, menuHasChildren;
    private MaterialButton btnSubmit;

    String[] isPetOwner, isHomeOwner, hasYard, hasChildren;
    ArrayAdapter<String> adapterIsPetOwner, adapterIsHomeOwner, adapterHasYard, adapterHasChildren;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adoption_form, container, false);

        initialize();
        initializeSpinners();
        spinnerHandlers();
        loadUserInformation();

        btnSubmit.setOnClickListener(view -> validateApplicationForm());

        return view;
    }

    private void initialize() {
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etAddress = view.findViewById(R.id.etAddress);
        etPetNames = view.findViewById(R.id.etPetNames);
        etPetBreeds = view.findViewById(R.id.etPetBreeds);
        etPetDisposition = view.findViewById(R.id.etPetDisposition);
        etVetMobile = view.findViewById(R.id.etVetMobile);
        etIsYardFenced = view.findViewById(R.id.etIsYardFenced);
        etChildrenAges = view.findViewById(R.id.etChildrenAges);
        tilPetNames = view.findViewById(R.id.tilPetNames);
        tilPetBreeds = view.findViewById(R.id.tilPetBreeds);
        tilPetDisposition = view.findViewById(R.id.tilPetDisposition);
        tilVetMobile = view.findViewById(R.id.tilVetMobile);
        tilIsYardFenced = view.findViewById(R.id.tilIsYardFenced);
        tilChildrenAges = view.findViewById(R.id.tilChildrenAges);

        context = getActivity();
    }

    private void initializeSpinners() {
        isPetOwner = new String[] {"Yes", "No"};
        adapterIsPetOwner = new ArrayAdapter<>(context, R.layout.list_item, isPetOwner);
        menuIsPetOwner = view.findViewById(R.id.menuIsPetOwner);
        menuIsPetOwner.setAdapter(adapterIsPetOwner);

        isHomeOwner = new String[] {"Own", "Rent"};
        adapterIsHomeOwner = new ArrayAdapter<>(context, R.layout.list_item, isHomeOwner);
        menuIsHomeOwner = view.findViewById(R.id.menuIsHomeOwner);
        menuIsHomeOwner.setAdapter(adapterIsHomeOwner);

        hasYard = new String[] {"Yes", "No"};
        adapterHasYard = new ArrayAdapter<>(context, R.layout.list_item, hasYard);
        menuHasYard = view.findViewById(R.id.menuHasYard);
        menuHasYard.setAdapter(adapterHasYard);

        hasChildren = new String[] {"Yes", "No"};
        adapterHasChildren = new ArrayAdapter<>(context, R.layout.list_item, hasChildren);
        menuHasChildren = view.findViewById(R.id.menuHasChildren);
        menuHasChildren.setAdapter(adapterHasChildren);
    }

    private void spinnerHandlers() {
        // menuIsPetOwner, menuIsHomeOwner, menuHasYard, menuHasChildren;
        menuIsPetOwner.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) { // 0 = Yes, 1 = No
                tilPetNames.setEnabled(true);
                tilPetBreeds.setEnabled(true);
                tilPetDisposition.setEnabled(true);
                tilVetMobile.setEnabled(true);
            }
            else {
                tilPetNames.setEnabled(false);
                Objects.requireNonNull(etPetNames.getText()).clear();
                tilPetBreeds.setEnabled(false);
                Objects.requireNonNull(etPetBreeds.getText()).clear();
                tilPetDisposition.setEnabled(false);
                Objects.requireNonNull(etPetDisposition.getText()).clear();
                tilVetMobile.setEnabled(false);
                Objects.requireNonNull(etVetMobile.getText()).clear();
            }
        });

        menuHasYard.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) { // 0 = Yes, 1 = No
                tilIsYardFenced.setEnabled(true);
            }
            else {
                tilIsYardFenced.setEnabled(false);
                Objects.requireNonNull(etIsYardFenced.getText()).clear();
            }
        });

        menuHasChildren.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) { // 0 = Yes, 1 = No
                tilChildrenAges.setEnabled(true);
            }
            else {
                tilChildrenAges.setEnabled(false);
                Objects.requireNonNull(etChildrenAges.getText()).clear();
            }
        });
    }

    private void loadUserInformation() {
        dbUser = PETS_DB.getReference("user_"+ Objects.requireNonNull(USER).getUid());
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                String mobile = Objects.requireNonNull(snapshot.child("mobile").getValue()).toString();

                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etMobile.setText(mobile);
                etEmail.setText(USER.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateApplicationForm() {
        // required fields
        String firstName = Objects.requireNonNull(etFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(etLastName.getText()).toString();
        String email = Objects.requireNonNull(etEmail.getText()).toString();
        String mobile = Objects.requireNonNull(etMobile.getText()).toString();
        String address = Objects.requireNonNull(etAddress.getText()).toString();
        String isPetOwner = Objects.requireNonNull(menuIsPetOwner.getText()).toString();
        String isHomeOwner = Objects.requireNonNull(menuIsHomeOwner.getText()).toString();
        String hasYard = Objects.requireNonNull(menuHasYard.getText()).toString();
        String hasChildren = Objects.requireNonNull(menuHasChildren.getText()).toString();

        // optional fields
        String petNames = Objects.requireNonNull(etPetNames.getText()).toString();
        String petBreeds = Objects.requireNonNull(etPetBreeds.getText()).toString();
        String petDisposition = Objects.requireNonNull(etPetDisposition.getText()).toString();
        String vetMobile = Objects.requireNonNull(etVetMobile.getText()).toString();
        String isYardFenced = Objects.requireNonNull(etIsYardFenced.getText()).toString();
        String childrenAges = Objects.requireNonNull(etChildrenAges.getText()).toString();

        if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(mobile) ||
                TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(isPetOwner) ||
                TextUtils.isEmpty(isHomeOwner) ||
                TextUtils.isEmpty(hasYard) ||
                TextUtils.isEmpty(hasChildren)) {
            MaterialAlertDialogBuilder dialogFillAllRequired = new MaterialAlertDialogBuilder(requireActivity());
            dialogFillAllRequired.setMessage("Please fill in all required fields");
            dialogFillAllRequired.setPositiveButton("Okay", (dialogInterface, i) -> {
            });
            dialogFillAllRequired.setOnCancelListener(dialogInterface -> {
            });
            dialogFillAllRequired.show();
            return;
        }

        if (isPetOwner.equals("Yes")) {
            if (TextUtils.isEmpty(petNames) ||
                    TextUtils.isEmpty(petBreeds) ||
                    TextUtils.isEmpty(petDisposition) ||
                    TextUtils.isEmpty(vetMobile)) {
                MaterialAlertDialogBuilder dialogFillAllRequired = new MaterialAlertDialogBuilder(requireActivity());
                dialogFillAllRequired.setMessage("Please fill in all required information about your house pets.");
                dialogFillAllRequired.setPositiveButton("Okay", (dialogInterface, i) -> {
                });
                dialogFillAllRequired.setOnCancelListener(dialogInterface -> {
                });
                dialogFillAllRequired.show();
                return;
            }
        }

        if (hasYard.equals("Yes")) {
            if (TextUtils.isEmpty(isYardFenced)) {
                MaterialAlertDialogBuilder dialogFillAllRequired = new MaterialAlertDialogBuilder(requireActivity());
                dialogFillAllRequired.setMessage("Please describe the fencing of your yard.");
                dialogFillAllRequired.setPositiveButton("Okay", (dialogInterface, i) -> {
                });
                dialogFillAllRequired.setOnCancelListener(dialogInterface -> {
                });
                dialogFillAllRequired.show();
                return;
            }
        }

        if (hasChildren.equals("Yes")) {
            if (TextUtils.isEmpty(childrenAges)) {
                MaterialAlertDialogBuilder dialogFillAllRequired = new MaterialAlertDialogBuilder(requireActivity());
                dialogFillAllRequired.setMessage("Please list the ages of the children in your home.");
                dialogFillAllRequired.setPositiveButton("Okay", (dialogInterface, i) -> {
                });
                dialogFillAllRequired.setOnCancelListener(dialogInterface -> {
                });
                dialogFillAllRequired.show();
                return;
            }
        }

        MaterialAlertDialogBuilder dialogAgreement = new MaterialAlertDialogBuilder(requireActivity(), R.style.PetsAdoption_Dialog);
        dialogAgreement.setTitle("Terms of Agreement");
        dialogAgreement.setMessage("By clicking the submit button, I agree to PetsAdoption adoption process, will undergo a home check, and interview at the discretion of PetsAdoption. By clicking the submit button, I understand PetsAdoption will check my references including veterinary and personal.  By clicking the submit button, I understand there is an adoption donation associated with adoption of a pet from PetsAdoption and that it is tax deductible according to IRS 501(c)3 guidelines.  I understand this donation will ensure the organization is equipped to rescue another homeless pet.  By clicking the submit button, I understand there is no \"cooling off\" period, and that if I no longer want or can no longer care for my adopted pet, I agree to notify PetsAdoption BY EMAIL and provide a 14 day period to allow PetsAdoption to make arrangents for my pet to be taken back into rescue.  By clicking the submit button, I agree to indemnify and hold harmless PetsAdoption against any losses, lawsuits, claims, injury, damages incurred by me or to any persons or property by my adopted pet, once adoption has been completed.   By clicking the submit button, I understand that PetsAdoption will disclose any of the pet's health or behavior issues known by the above named rescue group before adoption is completed.  By clicking the submit button, I understand that if I no longer want my pet, or am no longer able to care for my adopted pet, I will be directed to surrender my pet to PetsAdoption and provide transport to where PetsAdoption deems appropriate. By clicking the submit button, I verify all of the above information is true and accurate.");
        dialogAgreement.setPositiveButton("I agree", (dialogInterface, i) -> {

            submitApplicationForm();

            MaterialAlertDialogBuilder dialogFillAllRequired = new MaterialAlertDialogBuilder(requireActivity(), R.style.PetsAdoption_Dialog);
            dialogFillAllRequired.setTitle("Application Submitted");
            dialogFillAllRequired.setMessage("Your adoption application will be reviewed. Please wait for our agent to call the number you have provided for further questions regarding your adoption.");
            dialogFillAllRequired.setPositiveButton("Okay", (dialogInterface2, i2) -> {
            });
            dialogFillAllRequired.setOnCancelListener(dialogInterface2 -> {
            });
            dialogFillAllRequired.show();

        });
        dialogAgreement.setNeutralButton("I do not agree", (dialogInterface, i) -> {

        });
        dialogAgreement.setOnCancelListener(dialogInterface -> {
        });
        dialogAgreement.show();
        // apili ni og status
    }

    private void submitApplicationForm() {
        // hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // hide keyboard

        // required fields
        String firstName = Objects.requireNonNull(etFirstName.getText()).toString();
        String lastName = Objects.requireNonNull(etLastName.getText()).toString();
        String email = Objects.requireNonNull(etEmail.getText()).toString();
        String mobile = Objects.requireNonNull(etMobile.getText()).toString();
        String address = Objects.requireNonNull(etAddress.getText()).toString();
        String isPetOwner = Objects.requireNonNull(menuIsPetOwner.getText()).toString();
        String isHomeOwner = Objects.requireNonNull(menuIsHomeOwner.getText()).toString();
        String hasYard = Objects.requireNonNull(menuHasYard.getText()).toString();
        String hasChildren = Objects.requireNonNull(menuHasChildren.getText()).toString();

        // optional fields
        String petNames = Objects.requireNonNull(etPetNames.getText()).toString();
        String petBreeds = Objects.requireNonNull(etPetBreeds.getText()).toString();
        String petDisposition = Objects.requireNonNull(etPetDisposition.getText()).toString();
        String vetMobile = Objects.requireNonNull(etVetMobile.getText()).toString();
        String isYardFenced = Objects.requireNonNull(etIsYardFenced.getText()).toString();
        String childrenAges = Objects.requireNonNull(etChildrenAges.getText()).toString();

        dbApplications = PETS_DB.getReference("applications").push();
        String applicationUid = dbApplications.getKey();

        Application application = new Application( applicationUid,
                Objects.requireNonNull(USER).getUid(),
                requireArguments().getString("pet_uid"),
                firstName,
                lastName,
                email,
                mobile,
                address,
                isPetOwner,
                isHomeOwner,
                hasYard,
                hasChildren,
                petNames,
                petBreeds,
                petDisposition,
                vetMobile,
                isYardFenced,
                childrenAges,
                0,
                System.currentTimeMillis());
        dbApplications.setValue(application);

        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.remove(AdoptionFormFragment.this);
        transaction.commit();
        fm.popBackStack();
        fm.popBackStack();
    }
}