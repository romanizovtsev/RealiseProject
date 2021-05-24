package com.example.coviddi.DataContract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DataDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "Data.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор {@link DataDbHelper}.
     *
     * @param context Контекст приложения
     */
    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_DATA_TABLE = "CREATE TABLE " + Data.DateData.TABLE_NAME + " ("
                + Data.DateData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Data.DateData.COLUMN_COUNTRY + " TEXT NOT NULL, "
                + Data.DateData.COLUMN_DATE + " TEXT NOT NULL, "
                + Data.DateData.COLUMN_CONFIRMED + " INTEGER NOT NULL DEFAULT 0, "
                + Data.DateData.COLUMN_RECOVERED + " INTEGER NOT NULL DEFAULT 0, "
                + Data.DateData.COLUMN_DEATHS + " INTEGER NOT NULL DEFAULT 0);";
        String SQL_CREATE_DATA_TABLE2 = "CREATE TABLE " + Data.DataGraphin.TABLE_NAME + " ("
                + Data.DateData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Data.DateData.COLUMN_COUNTRY + " TEXT NOT NULL, "
                + Data.DateData.COLUMN_DATE + " TEXT NOT NULL, "
                + Data.DateData.COLUMN_CONFIRMED + " INTEGER NOT NULL DEFAULT 0);";


        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_DATA_TABLE);
        db.execSQL(SQL_CREATE_DATA_TABLE2);
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        //db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_NAME);
        //onCreate(db);
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE " + DATABASE_NAME);
            onCreate(db);
        }

    }
}

