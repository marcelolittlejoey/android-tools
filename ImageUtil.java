package br.com.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil{
    private String imagePath;

    private static final String IMAGE_UTIL = "ImageUtil";

    public ImageUtil(String imagePath){
        this.imagePath = imagePath;
    }

    public Bitmap getBitMap(){
        try {

            URL imageUrl = new URL("file://"+imagePath);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(is);

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            return bitmap;
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;

    }

    public Bitmap resizeImage(Bitmap bitmap) {
        int width;
        int height;
        Integer lateral = 640; // Final size of longer dimension
        try {

            //define um indice = 1 pois se der erro vai manter a imagem como está.
            Integer idx = 1;

            width = bitmap.getWidth();
            height = bitmap.getHeight();

            // Define the resduction index
            if (width >= height) {
                idx = width / lateral;
            } else {
                idx = height / lateral;
            }

            // Set the reduction index on the new dimensions
            width = width / idx;
            height = height / idx;

            // Create a new resized image.
            bitmap =  Bitmap.createScaledBitmap(bitmap, width, height, true);
        }catch (Exception e){
            Log.e(IMAGE_UTIL, "Resize image error");
        }
        return bitmap;
    }

    public Bitmap ajustImageOrientation(Bitmap bm) {
        int width = 0;
        int height = 0;
        Matrix mtx = new Matrix();

        try {
            width = bm.getWidth();
            height = bm.getHeight();
            mtx = new Matrix();

            ExifInterface exif = new ExifInterface(imagePath);

            // Get the original orientation of image
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // Rotate the imagem
            switch(orientation) {
                case 3: // ORIENTATION_ROTATE_180
                    mtx.postRotate(180);
                    break;
                case 6: //ORIENTATION_ROTATE_90
                    mtx.postRotate(90);
                    break;
                case 8: //ORIENTATION_ROTATE_270
                    mtx.postRotate(270);
                    break;
                default: //ORIENTATION_ROTATE_0
                    mtx.postRotate(0);
                    break;
            }
        }catch (Exception e){
            Log.e(IMAGE_UTIL, "Ajust image orientation fail");
        }

        // Create a new rotated image
        return Bitmap.createBitmap(bm, 0, 0, width, height, mtx, true);
    }

    public Bitmap getTreatedImage(){
        Bitmap bitmap = getBitMap();
        bitmap = ajustImageOrientation(bitmap);
        return resizeImage(bitmap);

    }

}
