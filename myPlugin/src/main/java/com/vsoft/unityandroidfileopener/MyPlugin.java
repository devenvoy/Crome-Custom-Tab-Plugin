package com.vsoft.unityandroidfileopener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class MyPlugin {
    private static final String LOGTAG = "UnityFileOpener";
    private static MyPlugin instance = new MyPlugin();
    private static Activity mainActivity;

    private MyPlugin() {
        Log.i(LOGTAG, "MyPlugin initialized.");
    }

    public static MyPlugin getInstance() {
        return instance;
    }

    // Set from Unity when activity is available
    public static void setActivity(Activity activity) {
        mainActivity = activity;
    }

    /**
     * Opens a file with the specified MIME type in an external app.
     *
     * @param filePath Path to the file (relative to internal storage or full path)
     * @param mimeType MIME type (e.g., "application/pdf", "image/png", etc.)
     */
    public void openFile(String filePath, String mimeType) {
        try {
            if (mainActivity == null) {
                Log.e(LOGTAG, "Main activity is null");
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                Log.e(LOGTAG, "File does not exist: " + filePath);
                return;
            }

            Uri uri = FileProvider.getUriForFile(
                    mainActivity,
                    mainActivity.getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            mainActivity.startActivity(intent);
        } catch (Exception e) {
            Log.e(LOGTAG, "Error opening file: " + e.getMessage(), e);
        }
    }
}
