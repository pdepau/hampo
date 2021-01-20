package com.example.hampo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.example.hampo.presentacion.LoginActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Aplicacion extends Application {
    public HamposAsinc hampos;
    public AdapterHamposFirestoreUI adaptador;
    private static Context context;
    public FirebaseAuth auth;
    public String id;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        refrescar();
    }

    public void refrescar(){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            id = auth.getUid();
            hampos = new HamposFirestore(id);
            Query query = FirebaseFirestore.getInstance()
                    .collection(id);
            FirestoreRecyclerOptions<Hampo> opciones = new FirestoreRecyclerOptions
                    .Builder<Hampo>().setQuery(query, Hampo.class).build();
            adaptador = new AdapterHamposFirestoreUI(opciones, this);
        }else{
            Intent i = new Intent(this.getBaseContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }
    public static void setContext(Context cntxt) {
        context = cntxt;
    }

    public static Context getContext() {
        return context;
    }

}