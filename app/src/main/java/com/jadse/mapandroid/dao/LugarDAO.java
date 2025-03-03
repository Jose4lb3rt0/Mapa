package com.jadse.mapandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jadse.mapandroid.db.DatabaseHelper;
import com.jadse.mapandroid.model.Lugar;

import java.util.ArrayList;
import java.util.List;

public class LugarDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public LugarDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertarLugar(Lugar lugar) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, lugar.getId());
        values.put(DatabaseHelper.COLUMN_DESCRIPCION, lugar.getDescripcion());
        values.put(DatabaseHelper.COLUMN_LATITUD, lugar.getLatitud());
        values.put(DatabaseHelper.COLUMN_LONGITUD, lugar.getLongitud());
        values.put(DatabaseHelper.COLUMN_IMAGEN, lugar.getImagen());

        database.insert(DatabaseHelper.TABLE_LUGARES, null, values);
    }

    public List<Lugar> obtenerLugares() {
        List<Lugar> lugares = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_LUGARES, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            lugares.add(new Lugar(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGEN)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUD)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUD))
            ));
        }
        cursor.close();
        return lugares;
    }
}
