package com.example.asteroides.presentacion;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AcercaDeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView texto = new TextView(this);
        texto.setText("Acerca de esta aplicación...");
        texto.setTextSize(20);
        setContentView(texto);
    }
}
