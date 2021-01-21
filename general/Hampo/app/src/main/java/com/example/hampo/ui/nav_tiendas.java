package com.example.hampo.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.Adaptador;
import com.example.hampo.AdapterHamposFirestoreUI;
import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.modelo.Tienda;
import com.example.hampo.presentacion.GpsActivity;
import com.example.hampo.presentacion.MiHampo;
import com.example.hampo.presentacion.SpacesItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.ArrayList;
import java.util.List;

import static com.example.hampo.ui.nav_mis_hampos.adaptador;

public class nav_tiendas extends Fragment {
    private RecyclerView recyclerView;
    private Adaptador mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Tienda> lista;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //llamar actividad
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         final View root=inflater.inflate(R.layout.fragment_slideshow, container, false);

        lista= new ArrayList<Tienda>();
        recyclerView = root.findViewById(R.id.recyclerTiendas);
        db.collection("Tiendas")
                .orderBy("ubicacion")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                lista.add(new Tienda(document.getData().get("nombre").toString(), document.getData().get("descripcion").toString(), document.getData().get("horario").toString(), document.getData().get("ubicacion").toString(), document.getData().get("latitud").toString(), document.getData().get("longitud").toString(), document.getData().get("fotoTienda").toString()));



                                // use this setting to improve performance if you know that changes
                                // in content do not change the layout size of the RecyclerView
                                recyclerView.setHasFixedSize(true);

                                // use a linear layout manager
                                layoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(layoutManager);

                                // specify an adapter (see also next example)
                                mAdapter = new Adaptador(lista);
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.onClick(root);

                            }
                        } else {
                            Log.w("csos", "Error getting documents.", task.getException());
                        }
                    }
                });
        // da problema con el adpatador pero es arreglr esto y en la vista gps ya lo tendiramos lool
        // el problema es el adaptador
        //buildea, se abre, puuedo ir a tiendas, me las muestra pero si toco en alguna, no hace nada en plan nada
        //ademas, si lo metemos en el for, no se iran sobreescribiendo?
        //no hace nada igualmente
        //ya
        //dice que copies esa parte y que la pases por aqui para que veamos las diferencias
        //TODO Pau del futuro cuando tenga que hacer la vista gps de geolocalizacion


        return root;
    }

/*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View vista = inflater.inflate(R.layout.fragment_slideshow, container, false);
    lista= new ArrayList<Tienda>();
    recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerTiendas);

    recyclerView.setAdapter(mAdapter);

    //mAdapter.startListening();

    //asignas escuchador para cada sitio y visualizar el sitio que aprietes
    mAdapter.setOnItemClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = recyclerView.getChildLayoutPosition(v);
            Intent i = new Intent(getContext(), nav_mapa.class);
            i.putExtra("Longitud", mAdapter.getLongitud(2));
            startActivity(i);
        }
    });


    return vista;
}*/

}