package com.kingofnothing.mylectures;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadDialog extends Dialog {

    Button btn_yes;
    Button btn_cancel;
    GridViewItem clicked;
    Context context;
    SharedPreferencesConfig sharedPreferences;


    public DownloadDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        clicked = new GridViewItem();
        sharedPreferences = new SharedPreferencesConfig(context);
    }

    public void setClicked(GridViewItem clicked) {
        this.clicked = clicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_download);

        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        // When User Press Yes (Download The File)
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Parse The File Url Into Uri Then Create Download Request
                Uri uri = Uri.parse(clicked.getTagHref());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                // Prepare Credentials To be Attached With Download Request
                String base64login = AppConstants.Base64Credentials(sharedPreferences.GetStringPrefrences("TEMP_USER_NAME"),sharedPreferences.GetStringPrefrences("TEMP_PASSWORD"));
                request.addRequestHeader("Authorization", "Basic " + base64login);
                // Set The Destination Folder To Be The Default Download Folder Of The System
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, clicked.getTagName());
                // When downloading music and videos they will be listed in the player
                request.allowScanningByMediaScanner();
                // Send Notification When Download Complete
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                // Start download
                DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                // Hide The Dialog
                dismiss();
            }
        });
        // What Happened When User Close The Dialog (Nothing Will Happened Absolutely -_- )
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
