package com.example.motoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class VerContac extends AppCompatActivity {

    private RecyclerView recyclerContactos;
    private ContactoAdapter adapter;
    private List<ContactoEmergencia> contactosList;

    private int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_contac);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener id_usuario desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        id_usuario = prefs.getInt("id_usuario", -1);

        if (id_usuario == -1) {
            Toast.makeText(this, "Sesión no encontrada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        recyclerContactos = findViewById(R.id.recyclerContactos);
        recyclerContactos.setLayoutManager(new LinearLayoutManager(this));
        contactosList = new ArrayList<>();
        adapter = new ContactoAdapter(contactosList);
        recyclerContactos.setAdapter(adapter);

        new CargarContactosTask().execute(id_usuario);
    }

    private class CargarContactosTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int userId = params[0];
            String urlString = "http://192.168.100.99/motoscout/get_contactos_emergencia.php?id_usuario=" + userId;
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString == null) {
                Toast.makeText(VerContac.this, "Error al cargar contactos", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                boolean success = jsonObject.getBoolean("success");

                if (success) {
                    contactosList.clear();

                    JSONArray contactosJson = jsonObject.getJSONArray("contactos");
                    for (int i = 0; i < contactosJson.length(); i++) {
                        JSONObject contactoObj = contactosJson.getJSONObject(i);

                        int idContacto = contactoObj.getInt("id_contacto");
                        int idUsuario = contactoObj.getInt("id_usuario");
                        String nombreContacto = contactoObj.getString("nombre_contacto");
                        String telefonoContacto = contactoObj.getString("telefono_contacto");

                        contactosList.add(new ContactoEmergencia(idContacto, idUsuario, nombreContacto, telefonoContacto));
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    String message = jsonObject.getString("message");
                    Toast.makeText(VerContac.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(VerContac.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void aceptar(View view) {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);
        startActivity(intent);
        finish();
    }
}
