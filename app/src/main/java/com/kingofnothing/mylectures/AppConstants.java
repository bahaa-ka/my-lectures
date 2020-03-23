package com.kingofnothing.mylectures;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Base64;

import net.hockeyapp.android.CrashManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Locale;
import java.util.Queue;

public class AppConstants {
    public static String WEB_SITE = "http://lectures.kalamoon.edu.sy/";
    public static String APP_ID = "3da4c598b3374091949f43a41a156211";
    public static String ACCESS_TOKEN = "e9f3471cd085268fe14cec0081c11beccdae94e1";
    SharedPreferencesConfig sharedPreferences;

    public static String Base64Credentials(String username,String password) {
        String login = username + ":" + password;
        String base64login = Base64.encodeToString(login.getBytes(), Base64.DEFAULT);
        return base64login;
    }
    public static int GetFileImageResourceID(String ext){
        if(ext.equals("doc"))
            return R.drawable.doc;
        else if(ext.equals("docx"))
            return R.drawable.docx;
        else if(ext.equals("ppt"))
            return R.drawable.ppt;
        else if(ext.equals("pps"))
            return R.drawable.pps;
        else if(ext.equals("pptx"))
            return R.drawable.pptx;
        else if(ext.equals("exe"))
            return R.drawable.exe;
        else if(ext.equals("pdf"))
            return R.drawable.pdf;
        else if(ext.equals("rar"))
            return R.drawable.rar;
        else if(ext.equals("zip"))
            return R.drawable.zip;
        else if(ext.equals("txt"))
            return R.drawable.txt;
        else if(ext.equals("png"))
            return R.drawable.png;
        else if(ext.equals("jpg"))
            return R.drawable.jpg;
        else if(ext.equals("mp4"))
            return R.drawable.mp4;
        else
            return R.drawable.unknown;
    }
    public static void ForceLeftToRight(Context context){
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, context.getApplicationContext().getResources().getDisplayMetrics());
    }
    public static void checkForCrashes(Context context) {
        CrashManager.register(context);
    }

    public void TraversTeachersFilesTree(){
        Queue<String> myQueue = null;
        String base64login = AppConstants.Base64Credentials(sharedPreferences.GetStringPrefrences("TEMP_USER_NAME"),sharedPreferences.GetStringPrefrences("TEMP_PASSWORD"));

        Document source = null;
        try {
            source = Jsoup.connect(WEB_SITE)
                    .header("Authorization", "Basic " + base64login)
                    .get();
            Elements roots = source.getElementsByTag("href");
            for (Element root:roots) {
                String name = root.text();
                int count = 0;

                Document tree = Jsoup.connect(WEB_SITE + root.text().trim())
                        .header("Authorization", "Basic " + base64login)
                        .get();
                Elements childs = tree.getElementsByTag("href");
                for (Element child:childs) {
                    if(StringFormatter.GetLast(child.text()) == '/'){
                        myQueue.add(child.text());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
