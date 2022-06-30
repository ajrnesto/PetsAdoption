package com.petsadoption.Fragments;

import static com.petsadoption.Utils.Utils.getFileExtension;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petsadoption.Activities.AdminActivity;
import com.petsadoption.Activities.UserActivity;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage PETS_ST = FirebaseStorage.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference stUsers = PETS_ST.getReference("userPhotos");
    private final DatabaseReference dbUser = PETS_DB.getReference("user_"+USER.getUid());

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    private View view;
    private MaterialCardView cvLoading;
    private TextInputEditText etFirstName, etLastName, etMobile;
    private ImageView imgUserPhoto;
    private MaterialButton btnGallery, btnSave;

    private Uri uriLocalImage;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialize();
        loadUserInformation();

        btnGallery.setOnClickListener(view -> {
            openGallery();
        });

        btnSave.setOnClickListener(view -> {
            try {
                saveProfile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private void initialize() {
        imgUserPhoto = view.findViewById(R.id.imgUserPhoto);
        btnGallery = view.findViewById(R.id.btnGallery);
        cvLoading = view.findViewById(R.id.cvLoading);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etMobile = view.findViewById(R.id.etMobile);

        btnSave = view.findViewById(R.id.btnSave);
        context = getContext();
    }

    private void loadUserInformation() {
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();
                String mobile = Objects.requireNonNull(snapshot.child("mobile").getValue()).toString();

                etFirstName.setText(firstName);
                etLastName.setText(lastName);
                etMobile.setText(mobile);

                if (snapshot.child("photoUrl").exists()) {
                    Picasso.get().load(snapshot.child("photoUrl").getValue().toString()).fit().centerCrop().into(imgUserPhoto);
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

    private void openGallery() {
        Intent iGallery = new Intent();
        iGallery.setType("image/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(iGallery, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            uriLocalImage = data.getData();
            Picasso.get().load(uriLocalImage).fit().centerCrop().into(imgUserPhoto);
        }
    }

    private void saveProfile() throws IOException {
        // hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // hide keyboard
        cvLoading.setVisibility(View.VISIBLE);

        if (uriLocalImage != null) {

            if (TextUtils.isEmpty(etFirstName.getText().toString().trim()) ||
                    TextUtils.isEmpty(etLastName.getText().toString().trim()) ||
                    TextUtils.isEmpty(etMobile.getText().toString().trim())) {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                cvLoading.setVisibility(View.GONE);
                return;
            }
            String fileName = System.currentTimeMillis() + "." + getFileExtension(context, uriLocalImage);
            StorageReference refUserImage = stUsers.child(fileName);

            // image compressor
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriLocalImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos); // 15% quality
            byte[] data = baos.toByteArray();
            // image compressor

            refUserImage.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        refUserImage.getDownloadUrl().addOnCompleteListener(task -> {
                            String urlUserImage = task.getResult().toString();
                            dbUser.child("firstName").setValue(etFirstName.getText().toString());
                            dbUser.child("lastName").setValue(etLastName.getText().toString());
                            dbUser.child("mobile").setValue(etMobile.getText().toString());
                            dbUser.child("fileName").setValue(fileName);
                            dbUser.child("photoUrl").setValue(urlUserImage);

                            Toast.makeText(context, "User information has been updated", Toast.LENGTH_SHORT).show();
                            cvLoading.setVisibility(View.GONE);
                            UserActivity.navView.setCheckedItem(R.id.miAdopt);
                            getActivity().getSupportFragmentManager().popBackStack();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: "+ e, Toast.LENGTH_SHORT).show();
                        cvLoading.setVisibility(View.GONE);
                    });

        }
        else {
            dbUser.child("firstName").setValue(etFirstName.getText().toString());
            dbUser.child("lastName").setValue(etLastName.getText().toString());
            dbUser.child("mobile").setValue(etMobile.getText().toString());

            Toast.makeText(context, "User information has been updated", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
            UserActivity.navView.setCheckedItem(R.id.miAdopt);
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}