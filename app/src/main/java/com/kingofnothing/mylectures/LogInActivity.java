package com.kingofnothing.mylectures;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class LogInActivity extends AppCompatActivity {

    EditText edtxt_username;
    EditText edtxt_password;
    Button btn_signIn;
    CheckBox ck_remember;
    SharedPreferencesConfig sharedPreferences;

    /**
     * Android Activity LifeCycle Methods
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        AppConstants.ForceLeftToRight(LogInActivity.this);
        // Linking XML elements to JAVA objects
        LinkXML2Objects();

        // Checking User Data Existence
        sharedPreferences = new SharedPreferencesConfig(LogInActivity.this);
        sharedPreferences.GetCredentials();
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.checkForCrashes(LogInActivity.this);
    }
    /**
     * LogInAsyncTask Responsible To Make An Http Request To Kalamonn Server In order To Authenticate The User
     */
    public class LogInAsyncTask extends AsyncTask<String, String, String> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            // Customize Progress Dialog that will be show the progress of Log In Operation
            dialog = new ProgressDialog(LogInActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Authorizing");
            dialog.setMessage("Login to server");
            dialog.show();

        }
        @Override
        protected String doInBackground(String... params) {

            // Customize The Request with proper Data
            Connection connection = Jsoup
                    .connect("http://lectures.kalamoon.edu.sy")
                    .header("Authorization", "Basic " + AppConstants.Base64Credentials(params[0],params[1]))
                    .ignoreHttpErrors(true);
            Connection.Response response = null;
            try {
                // Perform The Request and Receive The Response To See The Result
                connection.execute();
                response = connection.response();

                // Checking Status Code (Status Code is indicator For The Result of The Request)
                switch (response.statusCode()) {
                    case 200:
                        if(ck_remember.isChecked()) {
                            sharedPreferences.WriteBasicCredentials(params[0], params[1], ck_remember.isChecked());
                            sharedPreferences.WriteTempCredentials(params[0],params[1],ck_remember.isChecked());
                        }
                        else
                            sharedPreferences.WriteTempCredentials(params[0], params[1],ck_remember.isChecked());
                        return response.statusMessage();
                    case 401:
                        return response.statusMessage();
                    case 403:
                        return response.statusMessage();
                    case 503:
                        return response.statusMessage();
                }
            } catch (IOException e) {
                return e.getMessage();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            try{
                if (result.equals("OK")) {
                    dialog.hide();
                    startActivity(new Intent(LogInActivity.this,MainActivity.class));
                    finish();
                } else {
                    dialog.hide();
                    Toast.makeText(LogInActivity.this,result,Toast.LENGTH_LONG).show();
                }
            }catch (Exception ex){
                dialog.hide();
                Toast.makeText(LogInActivity.this,"Server Side Problem ,Try Again ..!",Toast.LENGTH_LONG).show();
            }

        }
    }
    public void SignIn(View view) {
        String username = edtxt_username.getText().toString();
        String password = edtxt_password.getText().toString();
        new LogInAsyncTask().execute(username, password);

    }
    // Initialize Java Objects
    public void LinkXML2Objects(){
        edtxt_username = (EditText) findViewById(R.id.edtxt_username);
        edtxt_password = (EditText) findViewById(R.id.edtxt_password);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);
        ck_remember = (CheckBox) findViewById(R.id.ck_remember);
    }
}
