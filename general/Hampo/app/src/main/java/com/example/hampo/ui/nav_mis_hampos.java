package com.example.hampo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.AdapterHampos;
import com.example.hampo.AdapterHamposFirestoreUI;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.datos.HamposAsinc;
import com.example.hampo.modelo.Hampo;
import com.example.hampo.R;
import com.example.hampo.SpacesItemDecoration;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;


public class nav_mis_hampos extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<Hampo> listHampos;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static AdapterHamposFirestoreUI adaptador;
    private HamposAsinc hampos;
    private CasosUsoHampo usoHampo;


    public nav_mis_hampos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Query query = FirebaseFirestore.getInstance()
                .collection("lugares")
                .limit(50);
        FirestoreRecyclerOptions<Hampo> opciones = new FirestoreRecyclerOptions
                .Builder<Hampo>().setQuery(query, Hampo.class).build();
        adaptador = new AdapterHamposFirestoreUI(opciones, this.getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_nav_mis_hampos, container, false);
        listHampos = new ArrayList<>();
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        llenarLista();

        recyclerView.setAdapter(adaptador);
        adaptador.startListening();

        //asignas escuchador para cada sitio y visualizar el sitio que aprietes
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                //usoHampo.mostrar(pos);
            }
        });

        return vista;
    }

    @Override public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        adaptador.startListening();
    }
    @Override public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

    private void llenarLista() {

        db.collection("hampos");
        listHampos.add(new Hampo("Crear Hampo", R.drawable.ic_add_hampo, "", "",""));

    }

}