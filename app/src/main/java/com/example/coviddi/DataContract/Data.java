package com.example.coviddi.DataContract;

import android.provider.BaseColumns;

public class Data {
    public static final class DateData implements BaseColumns {
        public final static String TABLE_NAME = "StatForDate";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_COUNTRY = "Country";
        public final static String COLUMN_DATE = "Date";
        public final static String COLUMN_CONFIRMED = "Confirmed";
        public final static String COLUMN_RECOVERED = "Recovered";
        public final static String COLUMN_DEATHS = "Deaths";

    }

    public static final class DataGraphin implements BaseColumns {
        public final static String TABLE_NAME = "StatForGraph";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_COUNTRY = "Country";
        public final static String COLUMN_DATE = "Date";
        public final static String COLUMN_CONFIRMED = "Confirmed";

    }
}
