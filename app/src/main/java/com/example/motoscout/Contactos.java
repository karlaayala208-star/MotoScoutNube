package com.example.motoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Contactos extends AppCompatActivity {
    EditText txtNom2;
    EditText txtNum;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contactos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNom2 = findViewById(R.id.txtNom2);
        txtNum = findViewById(R.id.txtNum);

        executor = Executors.newSingleThreadExecutor();
    }

    public void GuardarDts(View view) {
        String nombre = txtNom2.getText().toString().trim();
        String telefono = txtNum.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Datos faltantes por favor llene los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener id_usuario de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int id_usuario = prefs.getInt("id_usuario", -1);

        if (id_usuario == -1) {
            Toast.makeText(getApplicationContext(), "No se encontró sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL("http://192.168.100.99/motoscout/add_contacto_emergencia.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "id_usuario=" + URLEncoder.encode(String.valueOf(id_usuario), "UTF-8") +
                        "&nombre_contacto=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&telefono_contacto=" + URLEncoder.encode(telefono, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer respuesta si quieres validar
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                        // Puedes volver a la lista o limpiar campos
                        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);
                        startActivity(intent);
                        finish(); // O regresar a la lista de contactos
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    public void Registro(View view) {
        // Si quieres que este botón vaya a BtnPanico
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);
        startActivity(intent);
        finish();

    }
}
