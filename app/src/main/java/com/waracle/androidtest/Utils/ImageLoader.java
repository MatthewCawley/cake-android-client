package com.waracle.androidtest.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.waracle.androidtest.MyApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by Riad on 20/05/2015.
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    public ImageLoader() { /**/ }

    /**
     * Simple function for loading a bitmap image from the web
     *
     * @param url       image url
     * @param imageView view to set image too.
     * @param placeHolderResource view to set image too if url does not contain an image.
     */
    public void load(final String url, final ImageView imageView, @DrawableRes final int placeHolderResource) {
        if (TextUtils.isEmpty(url)) {
            throw new InvalidParameterException("URL is empty!");
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final byte[] imageData;
                try {
                    imageData = loadImageData(url);
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            if (imageData.length == 0) {
                                setImageView(imageView, placeHolderResource);

                            } else {
                                setImageView(imageView, convertToBitmap(imageData));
                            }
                        }
                    };
                    mainHandler.post(runnable);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

    private static byte[] loadImageData(String url) throws IOException {

        byte[] data = loadFromCache(url);

        if (data != null && data.length > 0) {
            return data;
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("content-type", "text/plain");
        InputStream inputStream = null;
        try {
            try {
                // Read data from workstation
                inputStream = connection.getInputStream();

                data = StreamUtils.readUnknownFully(inputStream);
                saveToCache(url, data);
            } catch (IOException e) {
                // Read the error from the workstation
                inputStream = connection.getErrorStream();
            }
        } finally {
            // Close the input stream if it exists.
            StreamUtils.close(inputStream);

            // Disconnect the connection
            connection.disconnect();
        }
        return data;
    }

    private static Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static void setImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private static void setImageView(ImageView imageView, @DrawableRes int resId) {
        imageView.setImageResource(resId);
    }

    private static byte[] loadFromCache(String url) {

        File cacheFile = new File(MyApplication.applicationContext.getCacheDir() + "/" + url.hashCode());

        if (cacheFile.exists() && cacheFile.canRead()) {
            int size = (int) cacheFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(cacheFile));
                //noinspection ResultOfMethodCallIgnored
                buf.read(bytes, 0, bytes.length);
                buf.close();
                return bytes;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static void saveToCache(String url, byte[] data) {
        try {

            File cacheFile = new File(MyApplication.applicationContext.getCacheDir() + "/" + url.hashCode());

            if (!cacheFile.exists()) {
                Boolean created = cacheFile.createNewFile();
                if (!created) {
                    return;
                }
            }

            FileOutputStream out = new FileOutputStream(cacheFile);
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
