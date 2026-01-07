package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PerfilMec extends AppCompatActivity {
    RecyclerView recyclerViewdos;
    Adaptador adapter;
    List<Comentario> listaComentarios;

    // Aquí debes pasar el id del mecánico para filtrar sus calificaciones
    int idMecanico = 2; // ejemplo fijo, o pásalo por intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_mec);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewdos = findViewById(R.id.rcycmtdos);
        recyclerViewdos.setLayoutManager(new LinearLayoutManager(this));

        listaComentarios = new ArrayList<>();
        adapter = new Adaptador(listaComentarios);
        recyclerViewdos.setAdapter(adapter);

        cargarCalificaciones();
    }

    private void cargarCalificaciones() {
        new Thread(() -> {
            try {
                // Cambia la IP y ruta a la de tu servidor y archivo PHP
                URL url = new URL(Constantes.SERVER_URL + "get_calificaciones.php?id_mecanico=" + idMecanico);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseStr = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseStr.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(responseStr.toString());

                    if (jsonResponse.getBoolean("success")) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("calificaciones");

                        listaComentarios.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String usuario = obj.getString("usuario");
                            float calificacion = (float) obj.getDouble("calificacion");
                            String comentario = obj.getString("comentario");

                            listaComentarios.add(new Comentario(usuario, calificacion, comentario));
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "No se encontraron calificaciones", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar calificaciones", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void Regreso(View view) {
        Intent intent = new Intent(getApplicationContext(), ServMec.class);
        startActivity(intent);
    }
}
