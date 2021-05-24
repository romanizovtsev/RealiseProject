package com.example.coviddi.ViewInterface;

import android.content.Context;

public interface MainInterface {
    void ShowNumbConf(String value);

    void ShowNumbRecov(String value);

    void ShowNumbDeath(String value);

    String[] getCountry();

    Context getApplicationContext();
}
