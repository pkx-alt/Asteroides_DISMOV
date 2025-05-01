package com.example.asteroides;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.asteroides.presentacion.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreferenciasFragment extends PreferenceFragmentCompat {

    final int REQUEST_EXTERNAL_STORAGE = 0;
    boolean permiso_concedido = false;
    private String valorPendienteAlmacenamiento = null;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferencias, rootKey);
        // Manejo de la preferencia de fragmentos
        final EditTextPreference fragmentos = findPreference("fragmentos");
        final ListPreference almacenamiento = findPreference("almacenamiento");

        boolean dosTarjetas = true;

        if (fragmentos != null) {
            String valor_guardado = fragmentos.getText();
            if (valor_guardado != null) {
                fragmentos.setSummary("Limita el número de valores que se muestran (" + valor_guardado + ")");
            }

            fragmentos.setOnPreferenceChangeListener((preference, newValue) -> {
                int valor;
                try {
                    valor = Integer.parseInt((String) newValue);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Ha de ser un número", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (valor >= 2 && valor <= 5) {
                    fragmentos.setSummary("Limita el número de valores que se muestran (" + valor + ")");
                    return true;
                } else if(valor>5){
                    Toast.makeText(getActivity(), "Valor Máximo 5", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else {
                    Toast.makeText(getActivity(), "Valor mínimo 2", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        almacenamiento.setOnPreferenceChangeListener((preference, newValue) -> {
            String valor = (String) newValue;

            if ("3".equals(valor) || "4".equals(valor)) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    permiso_concedido = true;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        List<File> opcionesValidas = new ArrayList<>();

                        if (dosTarjetas) {
                            opcionesValidas.add(new File("/storage/sdcard0"));
                            opcionesValidas.add(new File("/storage/sdcard1"));
                        } else {
                            File[] dirs = getActivity().getExternalFilesDirs(null);
                            for (File dir : dirs) {
                                if (dir != null && Environment.isExternalStorageRemovable(dir) &&
                                        Environment.getExternalStorageState(dir).equals(Environment.MEDIA_MOUNTED)) {
                                    opcionesValidas.add(dir);
                                }
                            }
                        }

                        if (!opcionesValidas.isEmpty()) {
                            mostrarSelectorDeAlmacenamiento(opcionesValidas.toArray(new File[0]), valor);
                            return false;
                        }
                    }

                    return true;
                } else {
                    valorPendienteAlmacenamiento = valor;
                    solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "Sin el permiso de almacenamiento externo no puedo acceder al almacenamiento externo.",
                            REQUEST_EXTERNAL_STORAGE, getActivity());
                    return false;
                }
            }

            return true;
        });


    }

    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)) {
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int
                                        whichButton) {
                                    ActivityCompat.requestPermissions(actividad,
                                            new String[]{permiso}, requestCode);
                                }
                            }).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }
    public boolean hayTarjetaSDReal() {
        File[] dirs = getActivity().getExternalFilesDirs(null);
        return dirs.length > 1 && dirs[1] != null &&
                Environment.isExternalStorageRemovable(dirs[1]) &&
                Environment.getExternalStorageState(dirs[1]).equals(Environment.MEDIA_MOUNTED);
    }

    public void actualizarAlmacenamiento(String nuevoValor) {
        ListPreference almacenamiento = findPreference("almacenamiento");
        if (almacenamiento != null) {
            almacenamiento.setValue(nuevoValor);
        }
    }
    public String getValorPendienteAlmacenamiento() {
        return valorPendienteAlmacenamiento;
    }

    private void mostrarSelectorDeAlmacenamiento(File[] opciones, String valorAlmacenamiento) {
        String[] nombres = new String[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            nombres[i] = "Almacenamiento " + (i + 1) + ": " + opciones[i].getAbsolutePath();
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Selecciona almacenamiento externo")
                .setItems(nombres, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File seleccion = opciones[which];
                        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                        prefs.edit().putString("directorioSD", seleccion.getAbsolutePath()).apply();

                        Toast.makeText(getContext(), "Seleccionado: " + seleccion.getAbsolutePath(), Toast.LENGTH_LONG).show();

                        actualizarAlmacenamiento(valorAlmacenamiento);
                        valorPendienteAlmacenamiento = null;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



}
