package com.example.motoscout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Recordatorios extends AppCompatActivity {
    RecyclerView recyclerView;
    RecordatorioAdaptador adapter;

    Handler handler;
    Executor executor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recordatorios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_recordatorio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        // Ejecutar la carga en segundo plano
        executor.execute(() -> {
            List<Record_Lista> recordatorios = cargarRecordatorios();

            handler.post(() -> {
                adapter = new RecordatorioAdaptador(recordatorios, Recordatorios.this);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private List<Record_Lista> cargarRecordatorios() {
        // Simulación de datos (puedes conectar esto a base de datos o servicio web)
        List<Record_Lista> lista = new ArrayList<>();
        lista.add(new Record_Lista(R.mipmap.campana, "Seguro vencido", "01/08/2025"));
        lista.add(new Record_Lista(R.mipmap.campana, "Mantenimiento", "15/09/2025"));
        return lista;
    }

    // Métodos de navegación
    public void Regresar(View view) {
        startActivity(new Intent(this, BtnPanico.class));
        finish();
    }

    public void EditRec(View view) {
        startActivity(new Intent(this, Recordatorio.class));
        finish();
    }

    public void Alarma(View view) {
        startActivity(new Intent(this, Campana.class));
        finish();
    }
}
