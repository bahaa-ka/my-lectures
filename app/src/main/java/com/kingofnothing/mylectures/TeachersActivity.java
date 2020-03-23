package com.kingofnothing.mylectures;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;


public class TeachersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener , NavigationView.OnNavigationItemSelectedListener {
    ListView teachersListView;
    ArrayList<GridViewItem> teachers;
    ListViewAdapter adapter;
    SharedPreferencesConfig sharedPreferences;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);

        LinkXMLAndInitilaize();
        AttachToolbar();

        // Set On Click Listener For Navigation Menu Items
        navigationView.setNavigationItemSelectedListener(this);
        // Get SharedPrefernces in order to get username and password to make http request
        sharedPreferences = new SharedPreferencesConfig(TeachersActivity.this);

        // Set Adapter For Teachers ListView
        teachersListView.setAdapter(adapter);
        // Set On Item Click Listener For List View
        teachersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get The Clicked Item Object
                GridViewItem clicked = (GridViewItem) parent.getAdapter().getItem(position);
                // Create an Intent To Start Content Activity Provided With Proper Data
                Intent next = new Intent(TeachersActivity.this,ContentActivity.class);
                next.putExtra("CURRENT_HREF",clicked.getTagHref());
                next.putExtra("ACTIVITY_LABEL",clicked.getTagName());
                startActivity(next);
            }
        });

        // Fetch Teachers Data From textuploader Server
        Url = getIntent().getStringExtra("faculty_file");
        new GetTeachersAsyncTask().execute(Url);
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.checkForCrashes(TeachersActivity.this);
    }

    /**
     * Open NavaigationView Menu By Clicking on Item in the Toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() != R.id.action_search){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Method Responsible To Handle Click Events on NavigationView Menu
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reload:
                drawerLayout.closeDrawers();
                if(teachersListView.getAdapter().isEmpty())
                    new GetTeachersAsyncTask().execute(Url);
                else
                    Toast.makeText(TeachersActivity.this,"No Need To Reload All Teachers Available",Toast.LENGTH_LONG).show();
                return true;
            case R.id.check_update:
                drawerLayout.closeDrawers();
                Toast.makeText(TeachersActivity.this,"Looking for updates may take a while ..!",Toast.LENGTH_SHORT).show();
                CheckForUpdates();
                return true;
            case R.id.feedback:
                drawerLayout.closeDrawers();
                FeedbackManager.register(this);
                FeedbackManager.showFeedbackActivity(TeachersActivity.this);
                return true;
            case R.id.news:
                drawerLayout.closeDrawers();
                GetNews();
                return true;
        }
        return true;
    }

    /**
     * This Class Is Resposible To Connect To Lectures.kalamoon.edu.sy And Fetch Data From it
     * Then Add Data To GridView Asynchronously
     */
    public class GetTeachersAsyncTask extends AsyncTask<String, String, String> {

        ListViewAdapter adapter;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            // Prepare Progress Dialog and Show It To User
            dialog = new ProgressDialog(TeachersActivity.this);
            dialog.setTitle("Fetch Teachers Accounts");
            dialog.setCancelable(false);
            dialog.setMessage("Downloading in progress");
            dialog.show();

            // Get The GridView Adapter in order to Add GridViewItems to it using AsyncTask Class
            adapter = (ListViewAdapter) teachersListView.getAdapter();
        }
        @Override
        protected String doInBackground(String... voids) {
            Document doc = null;
            try {
                doc = Jsoup.connect(voids[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
            String text =  doc.text();
            String[] hrefs = text.split("\\,");
            for (String href:hrefs) {
                publishProgress(href);
            }
            return "ok";
        }
        @Override
        protected void onProgressUpdate(String... values) {
            String[] temp = values[0].split("\\.");

            // Get href
            String tagHref = AppConstants.WEB_SITE + values[0];

            // Turn Them Into GridViewItem Object
            GridViewItem obj = new GridViewItem();
            obj.setTagName(StringFormatter.CapFirstLetter(temp[0])+ " " + StringFormatter.CapFirstLetter(StringFormatter.TrimEnd(temp[1])));
            obj.setTagHref(tagHref);
            obj.setTagImageResourceID(R.drawable.teacher);
            // Add The Object To Adapter (to Grid View)
            adapter.add(obj);
        }
        @Override
        protected void onPostExecute(String message) {
            dialog.hide();
            if(message.equals("error")){
                Toast.makeText(TeachersActivity.this,"An error occured while fetch operation check your internet connection",Toast.LENGTH_SHORT).show();
                Toast.makeText(TeachersActivity.this,"Hit Reload in side menu to try again",Toast.LENGTH_LONG).show();
            }
        }
    }
    public class GetNewsAsyncTask extends AsyncTask<Void,String,String>{

        AlertDialog.Builder builder;
        ProgressDialog p_dialog;
        @Override
        protected void onPreExecute() {
            builder = new AlertDialog.Builder(TeachersActivity.this);
            builder.setIcon(R.drawable.ic_update_dialog)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            p_dialog = new ProgressDialog(TeachersActivity.this);
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

    /*
        Methods Responsible To Implement Search Functionality in The GridView
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<GridViewItem> newList = new ArrayList<GridViewItem>();

        for(GridViewItem item : teachers){
            if(item.getTagName().toLowerCase().contains(userInput))
                newList.add(item);
        }
        adapter.updateList(newList);
        return true;
    }

    /**
     * Check For In App Updates
     */
    public void CheckForUpdates() {
        UpdateManager.register(TeachersActivity.this, AppConstants.APP_ID, new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TeachersActivity.this);
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
    public void AttachToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Teachers List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }
    public void LinkXMLAndInitilaize(){
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        teachersListView = (ListView) findViewById(R.id.teacher_grid_view);
        teachers = new ArrayList<GridViewItem>();
        adapter = new ListViewAdapter(TeachersActivity.this, R.layout.layout_card, teachers);
    }
    private void GetNews() {
        new GetNewsAsyncTask().execute();
    }
}

