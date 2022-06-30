package com.petsadoption.Fragments;

import static com.petsadoption.Utils.Utils.getFileExtension;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.petsadoption.Activities.AdminActivity;
import com.petsadoption.Objects.Pet;
import com.petsadoption.R;
import com.petsadoption.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AddPetFragment extends Fragment {

    private static final FirebaseDatabase PETS_DB = FirebaseDatabase.getInstance();
    private static final FirebaseStorage PETS_ST = FirebaseStorage.getInstance();
    private DatabaseReference dbPets = PETS_DB.getReference("pets");
    private StorageReference stPets = PETS_ST.getReference("petPhotos");

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    private View view;
    private ChipGroup cgAnimals;
    private TextInputEditText etPetName, etBreed, etAgeYears, etAgeMonths, etWeight, etLocation, etPersonality, etHealthNotes;
    private ImageView imgPet;
    private MaterialButton btnGallery, btnSave;
    private AutoCompleteTextView menuSex;
    MaterialCardView cvLoading;

    private Uri uriLocalImage;
    private Context context;

    String[] sexes;
    ArrayAdapter<String> adapterSex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        initialize();
        initializeSpinner();

        btnGallery.setOnClickListener(view -> {
            openGallery();
        });

        btnSave.setOnClickListener(view -> {
            try {
                savePet();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        return view;
    }

    private void initialize() {
        cgAnimals = view.findViewById(R.id.cgAnimals);
        etPetName = view.findViewById(R.id.etPetName);
        etBreed = view.findViewById(R.id.etBreed);
        etAgeYears = view.findViewById(R.id.etAgeYears);
        etAgeMonths = view.findViewById(R.id.etAgeMonths);
        etWeight = view.findViewById(R.id.etWeight);
        etLocation = view.findViewById(R.id.etLocation);
        etPersonality = view.findViewById(R.id.etPersonality);
        etHealthNotes = view.findViewById(R.id.etHealthNotes);
        imgPet = view.findViewById(R.id.imgPet);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnSave = view.findViewById(R.id.btnSave);
        cvLoading = view.findViewById(R.id.cvLoading);

        context = getActivity();
    }

    private void initializeSpinner() {
        sexes = new String[] {"Male", "Female"};
        adapterSex = new ArrayAdapter<>(context, R.layout.list_item, sexes);
        menuSex = view.findViewById(R.id.menuSex);
        menuSex.setAdapter(adapterSex);
    }

    private void openGallery() {
        Intent iGallery = new Intent();
        iGallery.setType("image/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(iGallery, PICK_IMAGE_REQUEST);
    }

    private void savePet() throws IOException {
        // hide keyboard
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // hide keyboard
        cvLoading.setVisibility(View.VISIBLE);
        if (uriLocalImage != null) {
            if (TextUtils.isEmpty(etPetName.getText().toString().trim())) {
                Toast.makeText(context, "The pet's name is required", Toast.LENGTH_SHORT).show();
                cvLoading.setVisibility(View.GONE);
                return;
            }
            String fileName = System.currentTimeMillis() + "." + getFileExtension(context, uriLocalImage);
            StorageReference refPetImage = stPets.child(fileName);

            // image compressor
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriLocalImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos); // 15% quality
            byte[] data = baos.toByteArray();
            // image compressor

            refPetImage.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        refPetImage.getDownloadUrl().addOnCompleteListener(task -> {
                            String urlPetImage = task.getResult().toString();
                            String animal = "Dog";

                            int checkedChip = cgAnimals.getCheckedChipId();
                            if (checkedChip == R.id.chipDog) {
                                animal = "Dog";
                            }
                            else if (checkedChip == R.id.chipCat) {
                                animal = "Cat";
                            }
                            else if (checkedChip == R.id.chipRabbit) {
                                animal = "Rabbit";
                            }
                            else if (checkedChip == R.id.chipBird) {
                                animal = "Bird";
                            }

                            DatabaseReference dbNewPet = dbPets.push();
                            String petUid = dbNewPet.getKey();
                            Pet newPet = new Pet( petUid, // pet unique identifier
                                    animal, // animal
                                    urlPetImage, // photo
                                    etPetName.getText().toString().trim(), // name
                                    etBreed.getText().toString().trim(), // breed
                                    etAgeYears.getText().toString().trim(), // age years
                                    etAgeMonths.getText().toString().trim(), // age months
                                    menuSex.getText().toString().trim(), // sex
                                    etWeight.getText().toString().trim(), // weight
                                    etLocation.getText().toString().trim(), // location
                                    etPersonality.getText().toString().trim(), // personality
                                    etHealthNotes.getText().toString().trim(), // healthNotes
                                    fileName); // fileName
                            dbNewPet.setValue(newPet);

                            Toast.makeText(context, "Pet information has been saved", Toast.LENGTH_SHORT).show();
                            cvLoading.setVisibility(View.GONE);
                            AdminActivity.navView.setCheckedItem(R.id.miAdopt);
                            getActivity().getSupportFragmentManager().popBackStack();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: "+ e, Toast.LENGTH_SHORT).show();
                        cvLoading.setVisibility(View.GONE);
                    });
        }
        else {
            Toast.makeText(context, "No pet photo was selected", Toast.LENGTH_SHORT).show();
            cvLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            uriLocalImage = data.getData();
            Picasso.get().load(uriLocalImage).fit().centerCrop().into(imgPet);
        }
    }
}