package com.example.hampo;

import android.app.Application;

import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Aplicacion extends Application {
    public HamposAsinc hampos;
    public AdapterHamposFirestoreUI adaptador;


    @Override
    public void onCreate() {
        super.onCreate();
        hampos = new HamposFirestore();
        Query query = FirebaseFirestore.getInstance()
                .collection("lugares")
                .limit(50);
        FirestoreRecyclerOptions<Hampo> opciones = new FirestoreRecyclerOptions
                .Builder<Hampo>().setQuery(query, Hampo.class).build();
        adaptador = new AdapterHamposFirestoreUI(opciones, this);
    }
}