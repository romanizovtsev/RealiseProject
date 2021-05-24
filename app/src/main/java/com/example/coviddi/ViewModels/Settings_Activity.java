package com.example.coviddi.ViewModels;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.coviddi.MainActivity;
import com.example.coviddi.R;

import java.util.Locale;

public class Settings_Activity extends AppCompatActivity {
    Dialog dialog;//Окно диалога
    private MainActivity view;
    Intent intent;
    Resources res;
    DisplayMetrics dm;
    SharedPreferences sPref;
    String langu = "ru";
    android.content.res.Configuration conf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button About_us = findViewById(R.id.About_app);
        Button Feedback = findViewById(R.id.FeedBack);
        Button rus = (Button) findViewById(R.id.rus);
        Button en = (Button) findViewById(R.id.en);

        //вспоминает язык


        About_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FragmentManager manager = getSupportFragmentManager();
                    About_App_Fragment myDialogFragment = new About_App_Fragment();
                    myDialogFragment.show(manager, "myDialog");

                } catch (Exception e) {
                }
            }
        });

        Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FragmentManager manager = getSupportFragmentManager();
                    FeedBack_Fragment myDialogFragment = new FeedBack_Fragment();
                    myDialogFragment.show(manager, "myDialog");

                } catch (Exception e) {
                }
            }
        });

        rus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    res = getResources();
                    // Change locale settings in the app.
                    dm = res.getDisplayMetrics();
                    conf = res.getConfiguration();
                    conf.locale = new Locale("ru");
                    res.updateConfiguration(conf, dm);
                    langu = "ru";
                    sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("loc", "ru");
                    ed.apply();
                    intent = getIntent();
                    finish();
                    startActivity(intent);
                    intent = null;
                } catch (Exception e) {
                }
            }
        });

        en.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Resources res = getResources();
                // Change locale settings in the app.
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.locale = new Locale("en");
                res.updateConfiguration(conf, dm);
                langu = "en";
                sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("loc", "en");
                ed.apply();
                intent = getIntent();
                finish();
                startActivity(intent);
                intent = null;
            }
        });


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(Settings_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }
}


