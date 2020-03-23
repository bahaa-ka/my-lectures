package com.kingofnothing.mylectures;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinkXML();
        AttachToolbar();
        StyleToolBar();

        // Set Click Listener for navigation menu items
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void openMedicine(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvi0/raw");
        startActivity(intent);
    }
    public void openDentistry(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvi1/raw");
        startActivity(intent);
    }
    public void openPharmacy(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvik/raw");
        startActivity(intent);
    }
    public void openEngineering(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvir/raw");
        startActivity(intent);
    }
    public void openArchitecture(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvia/raw");
        startActivity(intent);

    }
    public void openBusiness(View view) {
        Intent intent = new Intent(MainActivity.this,TeachersActivity.class);
        intent.putExtra("faculty_file","https://textuploader.com/dvvi9/raw");
        startActivity(intent);
    }

    public void LinkXML(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.check_update:
                drawerLayout.closeDrawers();
                Toast.makeText(MainActivity.this,"Looking for updates may take a while ..!",Toast.LENGTH_SHORT).show();
                CheckForUpdates();
                return true;
            case R.id.feedback:
                drawerLayout.closeDrawers();
                FeedbackManager.register(this);
                FeedbackManager.showFeedbackActivity(MainActivity.this);
                return true;
            case R.id.news:
                drawerLayout.closeDrawers();
                GetNews();
                return true;
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() != R.id.action_search){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetNews() {
        new GetNewsAsyncTask().execute();
    }

    public class GetNewsAsyncTask extends AsyncTask<Void,String,String>{
        AlertDialog.Builder builder;
        ProgressDialog p_dialog;
        @Override
        protected void onPreExecute() {
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_update_dialog)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            p_dialog = new ProgressDialog(MainActivity.this);
            p_dialog.setTitle("Connecting");
            p_dialog.setMessage("Loading News");
            p_dialog.setCancelable(false);
            p_dialog.show();
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("http://textuploader.com/dvbt4/raw").get();
                String text = doc.text();
                String[] Lines = text.split("\\.");
                StringBuilder builder = new StringBuilder();
                for (String Line:Lines) {
                    builder.append(Line + "\n");
                }
                return builder.toString();
            } catch (IOException e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            p_dialog.hide();
            if(aVoid.equals("error")){
                builder.setTitle("Connect Failed")
                        .setMessage("Something Went Wrong Try Again Later");
                builder.show();
            }
            else {
                builder.setTitle("News")
                        .setMessage(aVoid);
                builder.show();
            }
        }
    }


    /**
     * Extra Methods
     */
    public void StyleToolBar(){
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }
    public void AttachToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }
    @Deprecated
    public void CheckForUpdates() {
        UpdateManager.register(MainActivity.this, AppConstants.APP_ID, new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("No Update Avilable")
                        .setIcon(R.drawable.ic_update_dialog)
                        .setMessage("You are using the latest version of the App")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
            @Override
            public void onUpdateAvailable(JSONArray data, String url) {
                super.onUpdateAvailable(data, url);
            }
        });
    }


}
