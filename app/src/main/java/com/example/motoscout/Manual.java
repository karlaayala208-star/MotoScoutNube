package com.example.motoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

        // Cargar datos iniciales
        cargarMotos(idUsuario);
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
            URL url = new URL(Constantes.SERVER_URL + "get_motos.php?id_usuario=" + idUsuario);
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
