package com.example.asteroides;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class Juego extends AppCompatActivity {

    private VistaJuego vistaJuego;

    private boolean sensoresActivos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.juego);

        vistaJuego = findViewById(R.id.VistaJuego);
        sensoresActivos = vistaJuego.isOpcionSensores();
        vistaJuego.setPadre(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vistaJuego != null) {
            vistaJuego.getThread().reanudar();
            if (sensoresActivos){vistaJuego.activarSensores();}

        }
    }

    @Override
    protected void onPause() {
        if (vistaJuego != null) {
            vistaJuego.getThread().pausar();
            if (sensoresActivos){vistaJuego.desactivarSensores();}
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (vistaJuego != null) {
            vistaJuego.getThread().detener();
            if (sensoresActivos){vistaJuego.desactivarSensores();}
        }
        super.onDestroy();
    }



}
