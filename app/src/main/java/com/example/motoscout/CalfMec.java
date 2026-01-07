package com.example.motoscout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalfMec extends AppCompatActivity {

    RecyclerView recyclerView;
    Adaptador adapter;
    List<Comentario> listaComentarios = new ArrayList<>();

    private Handler handler;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calf_mec);

        recyclerView = findViewById(R.id.rcyCmt);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        // Ejecutar la carga en segundo plano
        executor.execute(() -> {
            List<Comentario> comentariosCargados = obtenerComentarios(); // Simulamos una tarea lenta

            handler.post(() -> {
                adapter = new Adaptador(comentariosCargados);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private List<Comentario> obtenerComentarios() {
        // Simulación de tarea de carga de datos (puedes poner delay o llamada de red aquí)
        List<Comentario> comentarios = new ArrayList<>();
        comentarios.add(new Comentario("Ana", 5.0f, "¡Muy profesional!"));
        comentarios.add(new Comentario("Carlos", 4.0f, "Buen trabajo, pero algo tarde."));
        comentarios.add(new Comentario("Sofía", 3.5f, "Le faltó limpieza al terminar."));
        return comentarios;
    }

    public void Regresar(View view) {
        finish();
    }
}
