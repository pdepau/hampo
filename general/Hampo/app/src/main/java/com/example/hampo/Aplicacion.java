package com.example.hampo;

import android.app.Application;

import com.example.hampo.AdapterHamposFirestoreUI;
import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Aplicacion extends Application {
    public HamposAsinc hampos;
    public AdapterHamposFirestoreUI adaptador;

    public FirebaseAuth auth;
    private static String id;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        id = auth.getUid();
        hampos = new HamposFirestore();
        Query query = FirebaseFirestore.getInstance()
                .collection(id);
        FirestoreRecyclerOptions<Hampo> opciones = new FirestoreRecyclerOptions
                .Builder<Hampo>().setQuery(query, Hampo.class).build();
        adaptador = new AdapterHamposFirestoreUI(opciones, this);
    }

    public static String getId() {
        return id;
    }



}