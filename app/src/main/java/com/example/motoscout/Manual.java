package com.example.motoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Manual extends AppCompatActivity {

    private static final int REQUEST_ADD_MOTO = 1;

    RecyclerView recyclerView;
    MotoAdapter adapter;
    List<Moto> listaMotos = new ArrayList<>();

    Handler handler;
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_motos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar lista y adapter
        listaMotos = new ArrayList<>();
        adapter = new MotoAdapter(listaMotos, this);
        recyclerView.setAdapter(adapter);

        // Configurar el arrastre para reordenar
        configurarArrastre();

        // Cargar datos iniciales
        cargarMotos(idUsuario);
    }

    private void configurarArrastre() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // Intercambiar en la lista de datos
                Collections.swap(listaMotos, fromPosition, toPosition);
                // Notificar al adaptador
                adapter.notifyItemMoved(fromPosition, toPosition);

                actualizarPrioridadesServidor();

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No implementamos swipe para eliminar aquí
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void actualizarPrioridadesServidor() {
        executor.execute(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                int idUsuario = prefs.getInt("id_usuario", -1);
                if (idUsuario == -1) return;

                // 1. Construir un JSONArray con los IDs en el nuevo orden
                JSONArray jsonOrden = new JSONArray();
                for (Moto moto : listaMotos) {
                    jsonOrden.put(moto.getId());
                }
                String ordenJsonString = jsonOrden.toString(); // Esto genera "[1,2,3]"

                URL url = new URL(Constantes.SERVER_URL + "set_prioridad.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // 2. Enviar datos codificados para evitar problemas con caracteres especiales
                String postData = "id_usuario=" + idUsuario + "&orden=" + java.net.URLEncoder.encode(ordenJsonString, "UTF-8");
                conn.getOutputStream().write(postData.getBytes("UTF-8"));

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void cargarMotos(int idUsuario) {
        executor.execute(() -> {
            List<Moto> motosCargadas = cargarMotosDesdeAPI(idUsuario);
            handler.post(() -> {
                listaMotos.clear();
                listaMotos.addAll(motosCargadas);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private List<Moto> cargarMotosDesdeAPI(int idUsuario) {
        List<Moto> motos = new ArrayList<>();
        try {
            URL url = new URL("http://192.168.100.99/motoscout/get_motos.php?id_usuario=" + idUsuario);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONArray motosJson = jsonObject.getJSONArray("motos");
                    for (int i = 0; i < motosJson.length(); i++) {
                        JSONObject motoObj = motosJson.getJSONObject(i);

                        int id = motoObj.getInt("id_moto");
                        String marca = motoObj.getString("marca");
                        String modelo = motoObj.getString("modelo");
                        int anio = motoObj.getInt("anio");
                        int kilometraje = motoObj.getInt("kilometraje");
                        String imagenUrl = motoObj.getString("imagen"); // Aquí el path que devuelve tu PHP

                        motos.add(new Moto(id, marca, modelo, anio, kilometraje, imagenUrl));
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return motos;
    }

    public void agregarMoto(View view) {
        Intent intent = new Intent(this, Agregarmoto.class);
        startActivityForResult(intent, REQUEST_ADD_MOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MOTO && resultCode == RESULT_OK) {
            // Recarga la lista al volver de agregar moto
            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);
            if (idUsuario != -1) {
                cargarMotos(idUsuario);
            }
        }
    }
    public void BtnPan(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void ContMec(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Ubicacion.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Perfil(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Recordatorio(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Mantenimiento.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Recordatorios(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Recordatorios.class);//intent es el nombre del intento
        startActivity(intent);
    }
}
