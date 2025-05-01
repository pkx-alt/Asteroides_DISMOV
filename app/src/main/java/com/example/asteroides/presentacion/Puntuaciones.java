package com.example.asteroides.presentacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asteroides.MiAdaptador;
import com.example.asteroides.R;

public class Puntuaciones extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MiAdaptador adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);

        recyclerView = findViewById(R.id.recyclerView);
        adaptador = new MiAdaptador(this, MainActivity.almacen.listaPuntuaciones(10));

        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                String s = MainActivity.almacen.listaPuntuaciones(10).get(pos);
                Toast.makeText(Puntuaciones.this, "Selección: " + pos + " - " + s, Toast.LENGTH_LONG).show();
            }
        });

    }
}
