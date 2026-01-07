package com.example.motoscout;

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

public class Serv extends AppCompatActivity {
    RecyclerView recycler_servicios;
    ServAdapter adapter;

    Handler handler;
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serv);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recycler_servicios = findViewById(R.id.recycler_servicios);
        recycler_servicios.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Simular carga de servicios
            List<serv_item> lista = cargarServicios();

            handler.post(() -> {
                adapter = new ServAdapter(lista, Serv.this);
                recycler_servicios.setAdapter(adapter);
            });
        });
    }

    private List<serv_item> cargarServicios() {
        List<serv_item> listaServicios = new ArrayList<>();
        listaServicios.add(new serv_item(R.mipmap.serv, "TESCo", "19/01/2025 6:00PM", "Cambio de bujía", "$100"));
        listaServicios.add(new serv_item(R.mipmap.serv, "Casa karlita", "22/01/2025 10:00AM", "Alineación", "$150"));
        return listaServicios;
    }

    public void Regresar(View view) {
        startActivity(new Intent(this, UserMtc.class));
        finish();
    }
}
