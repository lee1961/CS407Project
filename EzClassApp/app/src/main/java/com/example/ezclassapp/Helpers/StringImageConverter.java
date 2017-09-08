package com.example.ezclassapp.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tychan on 9/5/17.
 */

public class StringImageConverter {
    public static String getBase64String(String currentPhotoPath) {
        // give your image file url in currrentPhotoPath
        Log.d("StringImageConverter", currentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress images by 40%
        final int COMPRESS_FACTOR = 40;
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_FACTOR, byteArrayOutputStream);
        byte byteArray[] = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64AndSetImage(String completeImageData) {
        // Check if input is null
        if (completeImageData == null) return null;

        // In case we store it in a place that has extensions
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(',') + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        return BitmapFactory.decodeStream(stream);
    }
}
