package com.example.hampo;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.bumptech.glide.Glide;
import com.example.hampo.modelo.Tienda;
import com.example.hampo.presentacion.GpsActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyViewHolder> implements View.OnClickListener{
    private List<Tienda> lista;
    private Aplicacion apps;
    protected View.OnClickListener onClickListener;
    MyViewHolder vh;
    @Override
    public void onClick(View v) {
        Log.d("pruebaa","cosa");
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout textView;

        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Adaptador(List<Tienda> lista) {
        this.lista = lista;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Adaptador.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_lista, parent, false);
         vh = new MyViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Aplicacion.getContext(),GpsActivity.class);
                TextView t=v.findViewById(R.id.nombreTienda);
                i.putExtra("nombre",t.getText().toString());
                 t=v.findViewById(R.id.latitud);
                i.putExtra("latitud",t.getText().toString());
                t=v.findViewById(R.id.longitud);
                i.putExtra("longitud",t.getText().toString());
                Log.d("adrian",t.getText().toString());
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                Aplicacion.getContext().startActivity(i);
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView t = holder.itemView.findViewById(R.id.nombreTienda);
        t.setText(lista.get(position).getNombre());

        t = holder.itemView.findViewById(R.id.descripcionTienda);
        t.setText(lista.get(position).getDescripcion());

        t = holder.itemView.findViewById(R.id.horarioTienda);
        t.setText(lista.get(position).getHorario());

        t = holder.itemView.findViewById(R.id.ubicacionTienda);
        t.setText(lista.get(position).getUbicacion());

        t = holder.itemView.findViewById(R.id.longitud);
        t.setText(lista.get(position).getLongitud());

        t = holder.itemView.findViewById(R.id.latitud);
        t.setText(lista.get(position).getLatitud());

        Glide.with(Aplicacion.getContext())
                .load(lista.get(position).getFotoTienda())
                .centerCrop()
                .into((ImageView) holder.itemView.findViewById(R.id.fotoTienda));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lista.size();
    }
    //TODO PAU DEL FUTURO
    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;

    }
    public String getLatitud(int pos) {
        return lista.get(pos).getLatitud();
    }
    public String getLongitud(int pos) {
        return lista.get(pos).getLongitud();
    }
    public String getNombre(int pos) {
        return lista.get(pos).getNombre();
    }
}

