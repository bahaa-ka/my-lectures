package com.kingofnothing.mylectures;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {

    ListView contentListView;
    ArrayList<GridViewItem> items;
    SharedPreferencesConfig sharedPreferences;
    String CURRENT_HREF;
    String ACTIVITY_LABEL;
    Toolbar toolbar;
    private static int REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        sharedPreferences = new SharedPreferencesConfig(ContentActivity.this);

        // Get Data Sent From The Previous Activity
        CURRENT_HREF = getIntent().getExtras().getString("CURRENT_HREF");
        ACTIVITY_LABEL = getIntent().getExtras().getString("ACTIVITY_LABEL");

        // Set Toolbar Programmatically
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ACTIVITY_LABEL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_refresh_white);

        // Ask Permission At Runtime
        ActivityCompat.requestPermissions(ContentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        // Prepare Content GridView
        items = new ArrayList<GridViewItem>();
        contentListView = (ListView) findViewById(R.id.content_list_view);
        GridViewItemAdapter adapter = new GridViewItemAdapter(ContentActivity.this,R.layout.layout_content_card,items);
        contentListView.setAdapter(adapter);
        // Event Triggered When an Item Clicked
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Clicked Item
                GridViewItem clicked = (GridViewItem) parent.getAdapter().getItem(position);
                // Check Whether The Clicked Item is Folder Or File
                if(clicked.getTagImageResourceID() != R.drawable.folder)
                {
                    // Show Download Dialog for The File
                    DownloadDialog downloadDialog = new DownloadDialog(ContentActivity.this);
                    downloadDialog.setClicked(clicked);
                    downloadDialog.show();
                }
                else
                {
                    // Open New Content Activity For The Clicked Folder
                    Intent next = new Intent(ContentActivity.this,ContentActivity.class);
                    // Send Current Path and Label To New Activity
                    next.putExtra("CURRENT_HREF",CURRENT_HREF + clicked.getTagHref());
                    next.putExtra("ACTIVITY_LABEL",clicked.getTagName());
                    // Start Activity
                    startActivity(next);
                }

            }
        });

        new GetContentAsyncTask().execute();
    }

    /**
     * Async Task Used To Get Folder Content From Server
     */
    public class GetContentAsyncTask extends AsyncTask<Void,String,Void>{

        GridViewItemAdapter adapter;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            // Customize Progress Dialog
            dialog = new ProgressDialog(ContentActivity.this);
            dialog.setTitle("Fetch Folder Content");
            dialog.setMessage("Loading Data");
            dialog.setCancelable(false);
            dialog.show();
            // Get The GridView Adapter To Add Items To It From AsyncTask
            adapter = (GridViewItemAdapter) contentListView.getAdapter();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                // Preparing User Credentials To Be Attached With The Request
                String base64login = AppConstants.Base64Credentials(sharedPreferences.GetStringPrefrences("TEMP_USER_NAME"),sharedPreferences.GetStringPrefrences("TEMP_PASSWORD"));
                // Get The Page Source Code
                Document document = Jsoup
                        .connect(CURRENT_HREF)
                        .header("Authorization", "Basic " + base64login)
                        .get();
                // Get All The Tags Like <a href="adnan.ismail/"> adnan.ismail/</a>
                Elements tags = document.getElementsByTag("a");
                for (Element tag : tags) {
                    // Get file or folder name and href
                    String[] parameters = new String[2];
                    parameters[0] = tag.text();
                    parameters[1] = tag.attr("href");

                    // Ignoring Parent Directory item (Useless)
                    if(parameters[0].equals("Parent Directory"))
                        continue;
                    // Send The Item To The Adapter
                    publishProgress(parameters[0],parameters[1]);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            String[] temp = values[0].split("\\.");

            // Declaring GridViewItem Parameters
            int imageResourceid;
            String tagHref;
            String tagName;

            // Check The Item Whether Folder or a File
            if(StringFormatter.GetLast(values[0]) != '/' )
            {
                // Initialize GridViewItem Parameters With Proper Data
                imageResourceid = AppConstants.GetFileImageResourceID(temp[temp.length - 1]);
                tagName = values[0];
                tagHref = CURRENT_HREF + values[1];

                GridViewItem item = new GridViewItem(tagName,tagHref,imageResourceid);
                // Add Item To Adapter ( to GridView )
                adapter.add(item);
                return;
            }
            else
            {
                // Initialize GridViewItem Parameters With Proper Data
                imageResourceid = R.drawable.folder;
                tagName = StringFormatter.TrimEnd(values[0]);
                tagHref = values[1];

                GridViewItem item = new GridViewItem(tagName,tagHref,imageResourceid);
                // Add Item To Adapter ( to GridView )
                adapter.add(item);
                return;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(contentListView.getAdapter().isEmpty())
            new GetContentAsyncTask().execute();
        else
            Toast.makeText(ContentActivity.this,"No Need To Reload Files And Folders Available",Toast.LENGTH_LONG).show();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.checkForCrashes(ContentActivity.this);
    }
}
