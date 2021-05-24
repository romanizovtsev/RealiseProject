    package com.example.coviddi;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coviddi.DataContract.DataDbHelper;
import com.jjoe64.graphview.GraphView;

import java.util.Locale;

    public class MainActivity extends AppCompatActivity  {
    private String[] AllArray;
    private Presenter presenter;
    private int selected1;
    TextView NumbConf,NumbRecov,NumbDeath,DateText;
    GraphView graphView;
    ImageButton button_settings;
    Button Read_info;
    int flagOpen;
    Button Start_test;
    SharedPreferences sPref;
        Spinner spinner;

    int flagcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        flagOpen=0;

        if(savedInstanceState!=null)
            switch(savedInstanceState.getInt("flagOpen"))
            { case 1:
                Intent intent = new Intent( MainActivity.this, Settings_Activity.class);
                startActivity(intent); finish();
                break;
            }
        getLocale();
        flagcome=0;
        DataDbHelper dh=new DataDbHelper(this);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        button_settings=(ImageButton)findViewById(R.id.Settings_Button);
        Read_info=(Button)findViewById(R.id.onfoBut);
        Start_test=(Button)findViewById(R.id.TestBut);
        NumbConf=findViewById(R.id.NumbConf);
        NumbRecov=findViewById(R.id.NumbRecov);
        NumbDeath=findViewById(R.id.NumbDeath);
        DateText=findViewById(R.id.Date);

        graphView=(GraphView) findViewById(R.id.graphView);
        presenter=new Presenter(this);

        AllArray= getResources().getStringArray(R.array.Country);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, AllArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        presenter.setDatesGraph();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
            if(flagcome==0)
            {
                getcountry();
                presenter.loadCache(selected1);
                flagcome=1;
            }
            else {
                selected1 = selectedItemPosition;
                presenter.loadCache(selected1);
                sPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor eds = sPref.edit();
                Log.e("Кладу страну",selected1+"");
                eds.putInt("selecteds", selected1);
                eds.apply();
            }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            getcountry();
            }
        });
        //обработка переходов
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    flagOpen=1;
                    Intent intent = new Intent( MainActivity.this, Settings_Activity.class);
                    startActivity(intent); finish();

                }catch (Exception e){
                }
            }

        });


        Read_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{flagOpen=2;
                    Intent intent = new Intent(MainActivity.this,  InfoActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                }
            }

        });

        Start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    flagOpen=3;
                    Intent intent = new Intent(MainActivity.this,  Test_activity.class);
                    startActivity(intent);
                }catch (Exception e){
                }
            }

        });
    }

    public void ShowNumbConf(String value)
    {
        NumbConf.setText(value);
    }
    public void ShowNumbRecov(String value)
    {
        NumbRecov.setText(value);
    }
    public void ShowNumbDeath(String value)
    {
        NumbDeath.setText(value);
    }
    public String[] getCountry()
    {
        return getResources()
                .getStringArray(R.array.CountryEn);
    }

    //кпопки для перехода



    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
public void getcountry()
{
    sPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

Log.e(sPref.getInt("selecteds",1)+"","Страна");
    spinner.setSelection(sPref.getInt("selecteds",0));

}

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            Log.e("FFFFFFFFFFFFFFFFF","Берем локаль");
            outState.putInt("flagOpen",2);
        }

        public void getLocale()
{
    Log.e("Берем локаль","Берем локаль");
    sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

    String savedText = sPref.getString("loc", "NO");
    Toast.makeText(MainActivity.this, savedText, Toast.LENGTH_SHORT).show();
    Resources res = getResources();
    // Change locale settings in the app.
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = new Locale(savedText);
    res.updateConfiguration(conf, dm);
    this.setContentView(R.layout.main_activity);


}
    /*    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data == null) {
                return;
            }
            Log.e("Вернулся",data.getStringExtra("locale"));
           if(data.getStringExtra("locale")!=locale)
           {
               Log.e("Внутри",data.getStringExtra("locale"));
               Resources res = this.getResources();
               // Change locale settings in the app.
               DisplayMetrics dm = res.getDisplayMetrics();
               Configuration conf = res.getConfiguration();
               conf.locale = new Locale(data.getStringExtra("locale"));
               res.updateConfiguration(conf, dm);
               Intent intent = getIntent();
               finish();
               startActivity(intent);

               //this.onRestart();
              // this.onRestart();
           }

        }*/
}