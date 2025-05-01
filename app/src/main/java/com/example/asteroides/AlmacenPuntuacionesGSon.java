package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.asteroides.presentacion.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class AlmacenPuntuacionesGSon implements AlmacenPuntuaciones {
    private static final String PREFS_NAME = "puntuaciones";
    private static final String KEY_PUNTUACIONES = "gsonPuntuaciones";
    private SharedPreferences preferences;

    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {}.getType();

    public AlmacenPuntuacionesGSon(MainActivity mainActivity) {
        preferences = mainActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Solo agregar datos de ejemplo si no hay puntuaciones
        if (leerString() == null) {
            guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
            guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());
        }
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String json = leerString();
        Clase objeto;
        ArrayList<Puntuacion> puntuaciones;

        if (json == null) {
            objeto = new Clase();
//            puntuaciones = new ArrayList<>();
        } else {
            objeto = gson.fromJson(leerString(),type);
//            puntuaciones = gson.fromJson(json, type);
        }

        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        guardarString(gson.toJson(objeto, type));
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String json = leerString();
//        ArrayList<Puntuacion> puntuaciones;

        Clase objeto;
        if (json == null) {
            objeto = new Clase();
//            puntuaciones = new ArrayList<>();
        } else {
            objeto = gson.fromJson(json, type);
//            puntuaciones = gson.fromJson(json, type);
        }

        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    // Guarda el JSON en SharedPreferences
    private void guardarString(String json) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PUNTUACIONES, json);
        editor.apply();
    }

    // Lee el JSON desde SharedPreferences
    private String leerString() {
        String json = preferences.getString(KEY_PUNTUACIONES, null);
        Log.d("string", json != null ? json : "No hay puntuaciones guardadas aún");
        return json;
    }


    public class Clase
    {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }

}


