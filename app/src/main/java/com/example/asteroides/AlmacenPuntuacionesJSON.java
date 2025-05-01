package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.asteroides.presentacion.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesJSON implements AlmacenPuntuaciones {
    private static final String PREFS_NAME = "puntuaciones";
    private static final String KEY_PUNTUACIONES = "jsonPuntuaciones";
    private String string; // Almacena puntuaciones en formato JSON
    private SharedPreferences preferences;

    public AlmacenPuntuacionesJSON(MainActivity mainActivity) {
        preferences = mainActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Verificar si el JSON está vacío o es nulo
        if (leerString().equals("[]")) { // Si está vacío, agregamos puntuaciones de prueba
            guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
            guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());
        }
    }



    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
         string = leerString();
        List<Puntuacion> puntuaciones = leerJson(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJson(puntuaciones);
         guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        // string = LeerFichero();
        List<Puntuacion> puntuaciones = leerJson(string);
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    private String guardarJson(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacion> leerJson(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        try {
            if (string != null && !string.isEmpty()) {  // Verificar si el string no es nulo ni vacío
                JSONArray json_array = new JSONArray(string);
                for (int i = 0; i < json_array.length(); i++) {
                    JSONObject objeto = json_array.getJSONObject(i);
                    puntuaciones.add(new Puntuacion(
                            objeto.getInt("puntos"),
                            objeto.getString("nombre"),
                            objeto.getLong("fecha")
                    ));
                }
            } else {
                Log.e("AlmacenPuntuacionesJSON", "El JSON está vacío o nulo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
    }


    private void guardarString(String json) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PUNTUACIONES, json);
        editor.apply();
    }

    // Lee el JSON desde SharedPreferences
    private String leerString() {
        String json = preferences.getString(KEY_PUNTUACIONES, "[]");  // Devuelve una lista vacía si no existe nada
        Log.d("string", json);
        return json;
    }


}
