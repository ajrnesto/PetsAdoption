package com.petsadoption.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.petsadoption.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Utils {
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    public static void basicDialog(Context context, String title, String button){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle(title);
        materialAlertDialogBuilder.setPositiveButton(button, (dialogInterface, i) -> { });
        materialAlertDialogBuilder.show();
    }
    public static void simpleDialog(Context context, String title, String message, String button){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setTitle(title);
        materialAlertDialogBuilder.setMessage(message);
        materialAlertDialogBuilder.setPositiveButton(button, (dialogInterface, i) -> { });
        materialAlertDialogBuilder.show();
    }

    public static String getFileExtension(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static File compressImage(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static String ageBuilder(String ageYears, String ageMonths) {
        StringBuilder sbAge = new StringBuilder();
        if (!ageYears.equals("0")) {
            if (ageYears.equals("1")) {
                sbAge.append(ageYears);
                sbAge.append(" year and ");
            }
            else {
                sbAge.append(ageYears);
                sbAge.append(" years and ");
            }
        }
        if (!ageMonths.equals("0")) {
            if (ageMonths.equals("1")) {
                sbAge.append(ageMonths);
                sbAge.append(" month old");
            }
            else {
                sbAge.append(ageMonths);
                sbAge.append(" months old");
            }
        }
        return sbAge.toString();
    }

    public static void renderSex(String sex, MaterialButton button) {
        if (sex.equals("Male")) {
            button.setIconResource(R.drawable.round_male_24);
            button.setIconTintResource(R.color.blue);
        }
        else {
            button.setIconResource(R.drawable.round_female_24);
            button.setIconTintResource(R.color.pink);
        }
    }

    public static String getStatus(int i) {
        String status;
        switch (i) {
            case 1:
                status = "For Interview";
                break;
            case 2:
                status = "Accepted";
                break;
            case 3:
                status = "Rejected";
                break;
            case 4:
                status = "Pet Unavailable";
                break;
            default:
                status = "Pending";
                break;
        }
        return status;
    }

    public static String getDefaultPhotoUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/pets-adoption-28f11.appspot.com/o/userPhotos%2Fwoman.png?alt=media&token=312df3d0-a364-4152-9e85-b9eede83e78c";
    }

    public static String getAdminPhotoUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/pets-adoption-28f11.appspot.com/o/userPhotos%2Fofficial_logo.png?alt=media&token=3e8acbd6-a01c-433f-b476-5dc861bf038e";
    }
}