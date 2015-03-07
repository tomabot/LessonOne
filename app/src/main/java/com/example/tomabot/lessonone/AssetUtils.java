package com.example.tomabot.lessonone;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AssetUtils {
    private static final String LOG_TAG = "AssetUtils";

    public static String readShaderFromFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(fileName);
            final Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\Z");
            return scanner.next();
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("Failed to load shader from file '%s': %s", fileName, e.toString()));
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "Failed to close asset file " + fileName + ": " + e.toString());
                }
            }
        }
    }
}
