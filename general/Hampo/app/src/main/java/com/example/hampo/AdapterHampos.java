package com.example.hampo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hampo.modelo.Hampo;

import java.util.ArrayList;

public class AdapterHampos extends RecyclerView.Adapter<AdapterHampos.ViewHolderHampos> {

    ArrayList<Hampo> listHampos;

    public AdapterHampos(ArrayList<Hampo> listHampos) {
        this.listHampos = listHampos;
    }

    @NonNull
    @Override
    public ViewHolderHampos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hampo_lista,null,false);
        return new ViewHolderHampos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHampos holder, int position) {
        holder.nombre.setText(listHampos.get(position).getNombre());
       // holder.foto.setImageResource(listHampos.get(position).getImagenId());

    }



    @Override
    public int getItemCount() {
        return listHampos.size();
    }

    public static class ViewHolderHampos extends RecyclerView.ViewHolder {

        TextView nombre, comida, bebida;
        ImageView foto;

        public ViewHolderHampos(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.hampoNombre);
            foto = itemView.findViewById(R.id.hampoFoto);
        }

    }
}
