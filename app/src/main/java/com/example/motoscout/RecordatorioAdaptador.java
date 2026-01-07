package com.example.motoscout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordatorioAdaptador extends RecyclerView.Adapter<RecordatorioAdaptador.RecordatorioViewHolder> {

    private List<Record_Lista> recordatorios;
    private Context context;

    public RecordatorioAdaptador(List<Record_Lista> recordatorios, Context context) {
        this.recordatorios = recordatorios;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordatorioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rceordatorios_item, parent, false);
        return new RecordatorioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordatorioViewHolder holder, int position) {
        Record_Lista item = recordatorios.get(position);
        holder.imgRecordatorio.setImageResource(item.getImageResource());
        holder.txt_serv.setText(item.getFecseg());
        holder.txt_serv.setText(item.getFecseg());
    }

    @Override
    public int getItemCount() {
        return recordatorios.size();
    }

    public static class RecordatorioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecordatorio;
        TextView txt_serv, fecseg;;


        public RecordatorioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecordatorio = itemView.findViewById(R.id.img_serv);
            txt_serv = itemView.findViewById(R.id.txt_serv);
            fecseg = itemView.findViewById(R.id.fecseg);
        }
    }
}
