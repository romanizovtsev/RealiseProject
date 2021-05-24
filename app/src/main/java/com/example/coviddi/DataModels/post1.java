package com.example.coviddi.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class post1 {

    @SerializedName("All")
    @Expose
    private All all;

    public All getAll() {
        return all;
    }

    public void setAll(All all) {
        this.all = all;
    }

    public class All {
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("dates")
        @Expose
        Map<String, String> dates;

        public Map<String, String> getDates() {

            return dates;
        }


    }
}







