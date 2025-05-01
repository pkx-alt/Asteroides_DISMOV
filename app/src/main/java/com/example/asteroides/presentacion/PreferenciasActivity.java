package com.example.asteroides.presentacion;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.asteroides.PreferenciasFragment;
public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new
                        PreferenciasFragment())
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISO CONCEDIDO", Toast.LENGTH_SHORT).show();

                PreferenciasFragment fragment = (PreferenciasFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
                if (fragment != null) {
                    String preferenciaSeleccionada = fragment.getValorPendienteAlmacenamiento();

                    if ("4".equals(preferenciaSeleccionada)) {
                        fragment.actualizarAlmacenamiento("4");
                    } else {
                        fragment.actualizarAlmacenamiento("3");
                    }
                }
            } else {
                Toast.makeText(this, "PERMISO DENEGADO", Toast.LENGTH_SHORT).show();
            }
        }
    }




}