package com.example.hampo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.AdapterHampos;
import com.example.hampo.Hampo;
import com.example.hampo.R;
import com.example.hampo.SpacesItemDecoration;


import java.util.ArrayList;


public class nav_mis_hampos extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<Hampo> listHampos;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public nav_mis_hampos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nav_mis_hampos.
     */
    // TODO: Rename and change types and number of parameters
    public static nav_mis_hampos newInstance(String param1, String param2) {
        nav_mis_hampos fragment = new nav_mis_hampos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_nav_mis_hampos, container, false);
        listHampos = new ArrayList<>();
        recyclerView = (RecyclerView) vista.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        llenarLista();
        AdapterHampos adapter = new AdapterHampos(listHampos);
        recyclerView.setAdapter(adapter);

        return vista;
    }


    private void llenarLista() {
        listHampos.add(new Hampo("Crear Hampo", R.drawable.ic_add_hampo, "", "",""));
        listHampos.add(new Hampo("Remi", R.drawable.foto_remi,"🍖: 75%", "💧: 90%","🌡: 22ºC"));
        listHampos.add(new Hampo("Remo", R.drawable.foto_remo,"🍖: 80%", "💧: 80%","🌡: 20ºC"));
        listHampos.add(new Hampo("Remulo", R.drawable.foto_remulo,"🍖: 48%", "💧: 46%","🌡: 24ºC"));
    }

}