package com.example.coviddi.DataPresenter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.coviddi.DataContract.Data;
import com.example.coviddi.DataContract.DataDbHelper;
import com.example.coviddi.DataModels.post1;
import com.example.coviddi.Network.NetworkService;
import com.example.coviddi.Presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.abs;

public class model {
    DataDbHelper dh;
    String countryNow = "";
    String DateNow;
    SQLiteDatabase dB;
    Presenter presenter;


    public model(Presenter pres) {
        this.presenter = pres;
        this.dh = new DataDbHelper(presenter.getContexts());
        this.DateNow = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

    }

    Map<String, String> map = new HashMap<>();
    Map<String, Integer> mapGraph = new HashMap<>();
    ArrayList<String> GraphListDate = new ArrayList<>();
    ArrayList<Integer> GraphListValue = new ArrayList<>();
    ArrayList<String> TipoMap = new ArrayList<>();
    int flag = 0;

    public void getInfoTodayGraph(String country, ArrayList<String> List) {
        clearAll(country);
        ArrayList<String> f = new ArrayList<>();
        String status = "confirmed";
        NetworkService.getInstance()
                .getJSONApi()
                .getPost(country, status)
                .enqueue(new Callback<post1>() {
                    @Override
                    public void onResponse(@NonNull Call<post1> call, @NonNull Response<post1> response) {
                        post1 post = response.body();
                        for (int i = 0; i < 8; i++) {
                            GraphListDate.add(List.get(i));
                            GraphListValue.add(Integer.parseInt(post.getAll().getDates().get(List.get(i))));
                        }

                        if (GraphListValue.size() == 8) {
                            int j = 0;
                            for (int i = 0; i < 7; i++) {
                                j = i + 1;
                                TipoMap.add(GraphListDate.get(i + 1) + "%" + abs(GraphListValue.get(j) - GraphListValue.get(i)));
                            }
                            presenter.releaseGraph(TipoMap);
                            putToSQLGraph(country, TipoMap);
                            GraphListDate.clear();
                            mapGraph.clear();
                            GraphListValue.clear();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<post1> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }


    public void getInfoTodays(String country, String status, String DateYers, String DateNow) {


        clearAll(country);
        NetworkService.getInstance()
                .getJSONApi()
                .getPost(country, status)
                .enqueue(new Callback<post1>() {
                    @Override
                    public void onResponse(@NonNull Call<post1> call, @NonNull Response<post1> response) {
                        post1 post = response.body();
                        String YersValue = post.getAll().getDates().get(DateYers);
                        String TodayValue = post.getAll().getDates().get(DateNow);


                        map.put(status, YersValue);
                        map.put(status, abs(Integer.parseInt(TodayValue) - Integer.parseInt(map.get(status))) + "");
                        flag++;

                        if ((map.size() == 3) && (flag == 3)) {
                            presenter.showInfo(map);
                            flag = 0;
                            putToSQL(country, map, DateNow);
                            map.clear();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<post1> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    public void putToSQLGraph(String country, ArrayList<String> TipoMap) {


        dB = dh.getReadableDatabase();
        /*String insertQuery4 = "DROP TABLE " +Data.DataGraphin.TABLE_NAME;
        dB.execSQL(insertQuery4);*/
        int raznost = 0;
        String formatString1 = "= '%s'";

        String insertQuerys2 = String.format(formatString1, country);
        String query = "SELECT * FROM "
                + Data.DataGraphin.TABLE_NAME + " WHERE " + Data.DataGraphin.COLUMN_COUNTRY + insertQuerys2;
        Cursor cursor2 = dB.rawQuery(query, null);
        if (cursor2.moveToNext()) {
            cursor2.moveToLast();
            int j;
            j = 6 - difference(cursor2.getString(cursor2
                    .getColumnIndex(Data.DateData.COLUMN_DATE)), DateNow);
            for (int k = 0; k < 7; k++) {
                if (k > j) {
                    String formatString = " VALUES ('%s','%s','%s')";
                    String insertQuery1 = String.format(formatString, country, TipoMap.get(k).split("%")[0], TipoMap.get(k).split("%")[1]);
                    dB = dh.getWritableDatabase();
                    String insertQuery = "INSERT INTO " +
                            Data.DataGraphin.TABLE_NAME +
                            " (" + Data.DataGraphin.COLUMN_COUNTRY + ","
                            + Data.DataGraphin.COLUMN_DATE + ","
                            + Data.DataGraphin.COLUMN_CONFIRMED + ")" + insertQuery1;
                    dB.execSQL(insertQuery);
                }
            }
        } else {
            for (int k = 0; k < 7; k++) {
                String formatString = " VALUES ('%s','%s','%s')";
                String insertQuery1 = String.format(formatString, country, TipoMap.get(k).split("%")[0],
                        TipoMap.get(k).split("%")[1]);
                dB = dh.getWritableDatabase();
                String insertQuery = "INSERT INTO " +
                        Data.DataGraphin.TABLE_NAME +
                        " (" + Data.DataGraphin.COLUMN_COUNTRY + ","
                        + Data.DataGraphin.COLUMN_DATE + ","
                        + Data.DataGraphin.COLUMN_CONFIRMED + ")" + insertQuery1;
                dB.execSQL(insertQuery);
            }
            TipoMap.clear();
        }
    }

    public void putToSQL(String country, Map<String, String> map, String Date) {
        dB = dh.getReadableDatabase();
        String formatString1 = "= '%s'";
        String insertQuerys1 = String.format(formatString1, DateNow);
        String insertQuerys2 = String.format(formatString1, country);
        String query = "SELECT * FROM "
                + Data.DateData.TABLE_NAME + " WHERE " + Data.DateData.COLUMN_DATE + insertQuerys1 + " AND " + Data.DateData.COLUMN_COUNTRY + insertQuerys2;
        Cursor cursor2 = dB.rawQuery(query, null);
        String confirmed = "", recovered = "", deaths = "";
        if (cursor2.moveToNext()) {
            Log.e("Такая запись", "Уже есть");
        } else {
            for (Map.Entry<String, String> pair : map.entrySet()) {
                String key = pair.getKey();                      //ключ
                switch (key) {
                    case "confirmed":
                        confirmed = pair.getValue();
                        break;
                    case "recovered":
                        recovered = pair.getValue();
                        break;
                    case "deaths":
                        deaths = pair.getValue();
                        break;
                }
            }
            String formatString = " VALUES ('%s','%s','%d','%d','%d')";
            String insertQuery1 = String.format(formatString, country, DateNow, Integer.parseInt(confirmed), Integer.parseInt(recovered), Integer.parseInt(deaths));

            dB = dh.getWritableDatabase();
            String insertQuery = "INSERT INTO " +
                    Data.DateData.TABLE_NAME +
                    " (" + Data.DateData.COLUMN_COUNTRY + ","
                    + Data.DateData.COLUMN_DATE + ","
                    + Data.DateData.COLUMN_CONFIRMED + ","
                    + Data.DateData.COLUMN_RECOVERED + ","
                    + Data.DateData.COLUMN_DEATHS + ")" + insertQuery1;
            dB.execSQL(insertQuery);
        }
    }

    public boolean getFromSQLGraph(String country) {

        ArrayList<String> TipoMap = new ArrayList<>();
        dB = dh.getReadableDatabase();
        String formatString1 = "= '%s'";
        String insertQuerys1 = String.format(formatString1, DateNow);
        String insertQuerys2 = String.format(formatString1, country);
        String query = "SELECT * FROM "
                + Data.DataGraphin.TABLE_NAME + " WHERE " + Data.DataGraphin.COLUMN_COUNTRY + insertQuerys2 + " AND " +
                Data.DataGraphin.COLUMN_DATE + insertQuerys1;
        Cursor cursor2 = dB.rawQuery(query, null);
        if (cursor2.moveToNext()) {
            query = "SELECT * FROM "
                    + Data.DataGraphin.TABLE_NAME + " WHERE " + Data.DataGraphin.COLUMN_COUNTRY + insertQuerys2;
            cursor2 = dB.rawQuery(query, null);
            cursor2.moveToLast();

            for (int i = 0; i < 6; i++) {
                cursor2.moveToPrevious();
            }
            TipoMap.add(cursor2.getString(cursor2
                    .getColumnIndex(Data.DataGraphin.COLUMN_DATE)) + "%" + cursor2.getString(cursor2
                    .getColumnIndex(Data.DataGraphin.COLUMN_CONFIRMED)));
            while (cursor2.moveToNext()) {
                TipoMap.add(cursor2.getString(cursor2
                        .getColumnIndex(Data.DataGraphin.COLUMN_DATE)) + "%" + cursor2.getString(cursor2
                        .getColumnIndex(Data.DataGraphin.COLUMN_CONFIRMED)));
            }
            presenter.releaseGraph(TipoMap);
            cursor2.close();
            return true;
        } else {
            cursor2.close();
            return false;

        }
    }

    public boolean getFromSQL(String country) {
        Map<String, String> map = new HashMap<>();
        dB = dh.getReadableDatabase();
        String formatString = "= '%s'";
        String insertQuery1 = String.format(formatString, DateNow);
        String insertQuery2 = String.format(formatString, country);
        String query = "SELECT " + Data.DateData._ID + ", "
                + Data.DateData.COLUMN_CONFIRMED + ", "
                + Data.DateData.COLUMN_RECOVERED + ", "
                + Data.DateData.COLUMN_DEATHS + " FROM "
                + Data.DateData.TABLE_NAME + " WHERE " + Data.DateData.COLUMN_DATE + insertQuery1 + " AND " + Data.DateData.COLUMN_COUNTRY + insertQuery2;
        Cursor cursor2 = dB.rawQuery(query, null);
        if (cursor2.moveToNext()) {
            int id = cursor2.getInt(cursor2
                    .getColumnIndex(Data.DateData._ID));
            String confirmed = cursor2.getString(cursor2
                    .getColumnIndex(Data.DateData.COLUMN_CONFIRMED));
            String recovered = cursor2.getString(cursor2
                    .getColumnIndex(Data.DateData.COLUMN_RECOVERED));
            String deaths = cursor2.getString(cursor2
                    .getColumnIndex(Data.DateData.COLUMN_DEATHS));
            map.put("confirmed", confirmed);
            map.put("recovered", recovered);
            map.put("deaths", deaths);
            presenter.showInfo(map);
            map.clear();
            cursor2.close();
            return true;
        } else {
            cursor2.close();
            return false;
        }
    }

    public void clearAll(String country) {
        if (countryNow == country) {
            map.clear();
            mapGraph.clear();
            GraphListDate.clear();
            GraphListValue.clear();
        }
        countryNow = country;
    }

    public int difference(String date1, String date2) {
        Date date11 = new Date();
        Date date22 = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("yyyy-MM-dd");
        try {
            date22 = format.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            date11 = format.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (date22.getTime() - date11.getTime()) / (1000 * 60 * 60 * 24);
    }

}
