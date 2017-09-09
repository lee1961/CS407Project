package com.example.ezclassapp.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static Bitmap decodeBase64AndSetImage(String completeImageData, int reqHeight, int reqWidth) {
        // Check if input is null
        if (completeImageData == null) return null;
        // In case we store it in a place that has extensions
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(',') + 1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        // First decode with inJustDecodeBounds to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        // Must reset inputStream, it is consumed by BitmapFactory.decodeStream()
        try {
            stream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d("StringImageConverter", "inSampleSize: " + Integer.toString(options.inSampleSize));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(stream, null, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("StringImageConverter", "height: " + Integer.toString(height));
        Log.d("StringImageConverter", "width: " + Integer.toString(width));

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void getDimensions(final View view, final StringImageConverter.setDimensionsListener listener) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.d("StringImageConverter", "getting circleImageView dimensions");
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = view.getMeasuredHeight();
                int width = view.getMeasuredWidth();
                listener.onComplete(height, width);
                Log.d("StringImageConverter", "height: " + Integer.toString(height) + " , width: " + Integer.toString(width));
                return false;
            }
        });


    }

    public interface setDimensionsListener {
        void onComplete(int height, int width);
    }
}
