package com.jadse.mapandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lugares";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LUGARES = "lugares";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";
    public static final String COLUMN_IMAGEN = "imagen";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_LUGARES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_DESCRIPCION + " TEXT, " +
                    COLUMN_LONGITUD + " REAL, " +
                    COLUMN_LATITUD + " REAL, " +
                    COLUMN_IMAGEN + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGARES);
        onCreate(sqLiteDatabase);
    }
}
