package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Serv_pendt extends AppCompatActivity {

    RecyclerView recyclerView;
    SolicitudAdapter adapter;
    ArrayList<SolicitudServicio> listaSolicitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_pendt);

        recyclerView = findViewById(R.id.rvSolicitudes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SolicitudAdapter(listaSolicitudes, solicitud -> {
            Intent intent = new Intent(Serv_pendt.this, detalle_solicitud.class);
            intent.putExtra("id_servicio", solicitud.getIdServicio());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        cargarSolicitudesDesdeServidor();
    }

    private void cargarSolicitudesDesdeServidor() {
        new Thread(() -> {
            try {
                URL url = new URL(Constantes.SERVER_URL + "get_solicitudes.php");
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

                    JSONObject json = new JSONObject(responseStr.toString());
                    if (json.getBoolean("success")) {
                        JSONArray array = json.getJSONArray("solicitudes");
                        listaSolicitudes.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            int idServicio = obj.getInt("id_servicio");
                            String moto = obj.getString("moto");
                            String tipoServicio = obj.getString("nombre_servicio");
                            String fechaSolicitud = obj.getString("fecha_solicitud");
                            String estado = obj.getString("estado");

                            SolicitudServicio solicitud = new SolicitudServicio(
                                    idServicio, moto, tipoServicio, fechaSolicitud, estado);
                            listaSolicitudes.add(solicitud);
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Serv_pendt.this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
