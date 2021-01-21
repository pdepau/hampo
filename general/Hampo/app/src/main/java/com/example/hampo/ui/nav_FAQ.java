package com.example.hampo.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hampo.R;

import org.w3c.dom.Text;


public class nav_FAQ extends Fragment {


    View contenedor;
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = getResources().getIdentifier("FAQs_respuesta"+v.getTag(), "id", getContext().getPackageName());
            View view = contenedor.findViewById(id);
            if(view.getVisibility()==View.GONE){
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    };
    LinearLayout linearLayout;
    public nav_FAQ() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //llamar actividad
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        linearLayout = getActivity().findViewById(R.id.linearLayout);
        // Inflate the layout for this fragment

        contenedor = inflater.inflate(R.layout.fragment_nav_faq, container, false);
        contenedor.findViewById(R.id.FAQs_link_pregunta1).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta2).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta3).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta4).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta5).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta6).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta7).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta8).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta9).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta10).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta11).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta12).setOnClickListener(onClick);
        contenedor.findViewById(R.id.FAQs_link_pregunta13).setOnClickListener(onClick);

        return contenedor;
    }

}