package com.example.hampo.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.hampo.presentacion.PreferenciasActivity;

public class CasosUsoActividades {

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
}