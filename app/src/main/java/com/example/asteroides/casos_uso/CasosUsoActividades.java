package com.example.asteroides.casos_uso;

import static com.example.asteroides.presentacion.MainActivity.REQUEST_CODE_PREFERENCIAS;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.asteroides.Juego;
import com.example.asteroides.presentacion.AcercaDeActivity;
import com.example.asteroides.presentacion.PreferenciasActivity;
import com.example.asteroides.presentacion.Puntuaciones;

public class CasosUsoActividades {
    private Activity actividad;
    public static final int ACTIV_JUEGO = 0;

    public CasosUsoActividades(Activity actividad) {
        this.actividad = actividad;
    }

    public void lanzarJuego(View view) {
        Intent i = new Intent(actividad, Juego.class);
        actividad.startActivityForResult(i, ACTIV_JUEGO); // Corrección aquí
    }

    public void lanzarPreferencias(View view) {
        Intent intent = new Intent(actividad, PreferenciasActivity.class);
        actividad.startActivityForResult(intent, REQUEST_CODE_PREFERENCIAS);
    }


    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(actividad, Puntuaciones.class);
        actividad.startActivity(i);
    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(actividad, AcercaDeActivity.class);
        actividad.startActivity(i);
    }
}
