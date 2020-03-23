package com.kingofnothing.mylectures;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.security.PublicKey;

public class SharedPreferencesConfig {
    SharedPreferences sharedPreferences;
    Context context;

    public SharedPreferencesConfig(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }
    public void WriteBasicCredentials(String username,String password,boolean remember){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME",username);
        editor.putString("PASSWORD",password);
        editor.putBoolean("REMEMBER",remember);
        editor.apply();
    }
    public void WriteTempCredentials(String username,String password,boolean remember){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TEMP_USER_NAME",username);
        editor.putString("TEMP_PASSWORD",password);
        editor.putBoolean("REMEMBER",remember);
        editor.apply();
    }

    public void GetCredentials(){
        if(sharedPreferences.getBoolean("REMEMBER",false)){
            TextView edtxt_username = ((Activity)this.context).findViewById(R.id.edtxt_username);
            TextView edtxt_password = ((Activity)this.context).findViewById(R.id.edtxt_password);
            CheckBox ck_remember = ((Activity)this.context).findViewById(R.id.ck_remember);

            edtxt_username.setText(sharedPreferences.getString("USER_NAME",""));
            edtxt_password.setText(sharedPreferences.getString("PASSWORD",""));
            ck_remember.setChecked(sharedPreferences.getBoolean("REMEMBER",false));
        }
    }
    public String GetStringPrefrences(String key){
        return sharedPreferences.getString(key,"");
    }
}
