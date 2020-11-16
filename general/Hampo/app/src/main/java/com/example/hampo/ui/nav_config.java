package com.example.hampo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hampo.casos_uso.CasosUsoActividades;
import com.example.hampo.presentacion.PreferenciasActivity;
import com.example.hampo.R;


public class nav_config extends Fragment {

    private CasosUsoActividades usoActividades;

    public nav_config() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lanzarPreferencias(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_config, container, false);
    }

    public void lanzarPreferencias(View v){
        usoActividades.lanzarPreferencias(v);
    }

}