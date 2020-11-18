package com.example.hampo.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.example.hampo.presentacion.PreferenciasActivity;

public class CasosUsoActividades extends ActivityCompat{

    private Activity actividad;

    public CasosUsoActividades(Activity actividad){
        this.actividad = actividad;
    }

    public void lanzarPreferencias(View v){
        Intent i = new Intent(this.actividad.getBaseContext(), PreferenciasActivity.class);
        actividad.startActivity(i);
    }
    /*public void lanzarFAQ(View v){
        Intent i = new Intent(this.actividad.getBaseContext(), FAQActivity.class);
        actividad.startActivity(i);
    }
    public void lanzarMiPerfil(View v){
        Intent i = new Intent(this.actividad.getBaseContext(), MiPerfilActivity.class);
        actividad.startActivity(i);
    }*/

    //String de ejemplo -> Manifest.permission.READ_EXTERNAL_STORAGEz
    public static boolean checkPermission(Activity actividad, String androidManifPermission){
        if (ActivityCompat.checkSelfPermission(actividad, androidManifPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void askUserPermission(Activity actividad, String androidManifPermission, int requestCode){
        requestPermissions(actividad, new String[] { androidManifPermission }, requestCode);
    }
}