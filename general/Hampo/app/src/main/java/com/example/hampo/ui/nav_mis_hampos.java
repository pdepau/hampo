package com.example.hampo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.AdapterHamposFirestoreUI;
import com.example.hampo.Aplicacion;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.modelo.Hampo;
import com.example.hampo.R;
import com.example.hampo.presentacion.CreateHampoActivity;
import com.example.hampo.presentacion.MiHampo;
import com.example.hampo.presentacion.SpacesItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class nav_mis_hampos extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<Hampo> listHampos;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static AdapterHamposFirestoreUI adaptador;



    public nav_mis_hampos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_nav_mis_hampos, container, false);
        listHampos = new ArrayList<>();
        adaptador = ((Aplicacion) getActivity().getApplication()).adaptador;
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        llenarLista();

        recyclerView.setAdapter(adaptador);
        adaptador.startListening();

        //asignas escuchador para cada sitio y visualizar el sitio que aprietes
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int pos = recyclerView.getChildLayoutPosition(v);
                Intent i = new Intent(getContext(), MiHampo.class);
                i.putExtra("id",adaptador.getKey(pos));
                startActivity(i);
            }
        });

        FloatingActionButton fab = vista.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzarCrearHampo();
            }
        });

        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        adaptador.startListening();
    }

    public void lanzarCrearHampo(){
        Intent i = new Intent(getContext(), CreateHampoActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

    private void llenarLista() {

        CollectionReference hampos = db.collection("hampos");

        // seleccionar hampos que tienen la uid del usuario
    }

}