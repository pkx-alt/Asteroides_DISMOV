package com.example.asteroides;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper implements AlmacenPuntuaciones {

    public AlmacenPuntuacionesSQLite(Context context) {
        super(context, "puntuaciones", null, 1);
    }

    // Métodos de SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "puntos INTEGER, nombre TEXT, fecha BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En caso de una nueva versión habría que actualizar las tablas
    }

    // Métodos de AlmacenPuntuaciones
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO puntuaciones VALUES (null, " + puntos + ", '" + nombre + "', " + fecha + ")");
        db.close();
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] CAMPOS = {"puntos", "nombre"};
        Cursor cursor = db.query("puntuaciones", CAMPOS,null, null, null, null, "puntos DESC",Integer.toBinaryString(cantidad));
//        Cursor cursor = db.rawQuery("SELECT puntos, nombre FROM puntuaciones ORDER BY puntos DESC LIMIT " + cantidad, null);
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(0) + " " + cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }
}
