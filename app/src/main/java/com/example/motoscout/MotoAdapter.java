package com.example.motoscout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MotoAdapter extends RecyclerView.Adapter<MotoAdapter.MotoViewHolder> {

    private List<Moto> listaMotos;
    private Context context;

    public MotoAdapter(List<Moto> listaMotos, Context context) {
        this.listaMotos = listaMotos;
        this.context = context;
    }

    @NonNull
    @Override
    public MotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_moto, parent, false);
        return new MotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotoViewHolder holder, int position) {
        Moto moto = listaMotos.get(position);

        holder.tvMarca.setText("Marca: " + moto.getMarca());
        holder.tvModelo.setText("Modelo: " + moto.getModelo());
        holder.tvAnio.setText("Año: " + moto.getAnio());
        holder.etKilometraje.setText(String.valueOf(moto.getKilometraje()));

        String baseUrl = "http://10.0.2.2/"; // Cambia según tu servidor
        Glide.with(holder.imgMoto.getContext())
                .load(baseUrl + moto.getImagenUrl()) // Asumo que tienes este metodo que devuelve la ruta relativa, p. ej "images/abc123.jpg"
                .placeholder(R.mipmap.garage)
                .error(R.mipmap.garage)
                .into(holder.imgMoto);
    }


    @Override
    public int getItemCount() {
        return listaMotos.size();
    }

    public static class MotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMoto;
        TextView tvMarca, tvModelo, tvAnio;
        EditText etKilometraje;

        public MotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMoto = itemView.findViewById(R.id.img_moto);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            tvModelo = itemView.findViewById(R.id.tvModelo);
            tvAnio = itemView.findViewById(R.id.tvAnio);
            etKilometraje = itemView.findViewById(R.id.etKilometraje);
        }
    }
}
