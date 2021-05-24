package com.example.coviddi;

import android.content.Context;

import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import com.example.coviddi.DataPresenter.model;
import com.example.coviddi.ViewInterface.MainInterface;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.abs;

public class Presenter {
    private MainInterface view;
    TextView DateText;
    GraphView graphView;
    private final com.example.coviddi.DataPresenter.model model;

    public Presenter(MainInterface view, TextView DateText, GraphView graphView) {
        this.DateText = DateText;
        this.graphView = graphView;
        this.view = view;
        model = new model(this);
    }

    public void detachView() {
        view = null;
    }

    public void loadInfos(int selected) {
        Log.e("Зашел в презентер", selected + "");
        String country = view.getCountry()[selected];
        Date dateNow = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date DateYers = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        model.getInfoTodays(country, "confirmed", formatForDateNow.format(DateYers), formatForDateNow.format(dateNow));
        model.getInfoTodays(country, "recovered", formatForDateNow.format(DateYers), formatForDateNow.format(dateNow));
        model.getInfoTodays(country, "deaths", formatForDateNow.format(DateYers), formatForDateNow.format(dateNow));

    }

    public void showInfo(Map<String, String> map) {
        for (Map.Entry<String, String> pair : map.entrySet()) {
            String key = pair.getKey();                      //ключ
            switch (key) {
                case "confirmed":
                    view.ShowNumbConf(pair.getValue());
                    break;
                case "recovered":
                    view.ShowNumbRecov(pair.getValue());
                    break;
                case "deaths":
                    view.ShowNumbDeath(pair.getValue());
                    break;
            }
        }


    }

    public Context getContexts() {

        return view.getApplicationContext();
    }

    public void loadInfoGraph(int selected) {
        String country = view.getCountry()[selected];
        Date date;
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd");
        String[] dates = new String[8];
        ArrayList<String> DateMas = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {

            dates[i] = formatForDateNow.format(new Date(System.currentTimeMillis() - (i + 1) * 24 * 60 * 60 * 1000));
            Log.e("Даты", dates[i]);
            DateMas.add(dates[i]);


        }

        model.getInfoTodayGraph(country, DateMas);
    }

    private Toast toastMessage;
    private PointsGraphSeries<DataPoint> dotSeries;

    public void releaseGraph(ArrayList<String> TipoMap) {
        graphView.removeAllSeries();
        Log.e(TipoMap.size() + "", "Размер мапки");
        Map<Calendar, Integer> graphMap = new HashMap<Calendar, Integer>();

        for (int i = 0; i < TipoMap.size(); i++) {
            int dayOfMonth1 = Integer.parseInt(TipoMap.get(i).split("%")[0].split("-")[2]);
            graphMap.put(new GregorianCalendar(2021, 3, dayOfMonth1), Integer.parseInt(TipoMap.get(i).split("%")[1]));
        }
        Map<Calendar, Integer> sortedMap = new TreeMap<>(graphMap);
        DataPoint[] Data = new DataPoint[sortedMap.size()];
        int i = 0;
        for (Map.Entry<Calendar, Integer> pair : sortedMap.entrySet()) {

            Calendar date = pair.getKey();
            Integer confirmed = pair.getValue();
            Log.e(date.toString(), confirmed + "");
            Data[i] = new DataPoint(date.get(Calendar.DAY_OF_MONTH), confirmed);
            i++;
        }

        LineGraphSeries series = new LineGraphSeries<>(Data);
        graphView.addSeries(series);

        dotSeries = new PointsGraphSeries<>();
        graphView.addSeries(dotSeries);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {


                int cases = (int) dataPoint.getY();
                int day = (int) dataPoint.getX();
                int lastDay = (int) series.getHighestValueX();
                int difference = lastDay - day;
                String date;
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
                date = formatForDateNow.format(new Date(System.currentTimeMillis() - (difference + 1) * 24 * 60 * 60 * 1000));
                String msg = date + "\n" + Integer.toString(cases);
                DataPoint[] Data = new DataPoint[1];
                Data[0] = new DataPoint(dataPoint.getX(), dataPoint.getY());
                dotSeries.resetData(Data);
                if (toastMessage != null) toastMessage.cancel();
                toastMessage = Toast.makeText(getContexts(), msg, Toast.LENGTH_LONG);
                toastMessage.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toastMessage.show();
                Data = null;


            }
        });
        graphView.getGridLabelRenderer().setNumHorizontalLabels(Data.length);
        graphMap.clear();
        sortedMap.clear();

        graphView.getViewport().setXAxisBoundsManual(true);
    }


    public void setDatesGraph() {
        String date1, date2;
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        date1 = formatForDateNow.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        date2 = formatForDateNow.format(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        DateText.setText(date2 + "-" + date1);
    }

    public void Settings_Open() {

    }

    public void loadCache(int selected) {
        String country = view.getCountry()[selected];
        if (model.getFromSQL(country) == false) {
            loadInfos(selected);
            Log.e("Данные для статистики", "Взяты из сети");
        }
        if (model.getFromSQLGraph(country) == false) {
            loadInfoGraph(selected);
            Log.e("Данные для Графика", "Взяты из сети");
        }

    }

    public void onDestroy() {
        this.view = null;
    }


}
