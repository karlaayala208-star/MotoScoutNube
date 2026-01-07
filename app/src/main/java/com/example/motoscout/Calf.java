package com.example.motoscout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Calf extends AppCompatActivity {

    RatingBar ratingBar;
    EditText editTextComentario;
    Button buttonEnviar;

    int idServicio = 1;  // Simulado, cambia si quieres
    int idMecanico = 2;  // Simulado, cambia si quieres

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calf);

        ratingBar = findViewById(R.id.ratingBar2);
        editTextComentario = findViewById(R.id.editTextText);
        buttonEnviar = findViewById(R.id.button30);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no válido, por favor inicia sesión nuevamente", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        buttonEnviar.setOnClickListener(v -> {
            float calificacion = ratingBar.getRating();
            String comentario = editTextComentario.getText().toString().trim();

            if (calificacion == 0) {
                Toast.makeText(this, "Por favor, califica el servicio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (comentario.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarCalificacion(idServicio, idUsuario, idMecanico, calificacion, comentario);
        });
    }

    private void enviarCalificacion(int idServicio, int idUsuario, int idMecanico, float calificacion, String comentario) {
        new Thread(() -> {
            try {
                URL url = new URL(Constantes.SERVER_URL + "insertar_calificacion.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id_servicio", idServicio);
                jsonParam.put("id_usuario", idUsuario);
                jsonParam.put("id_mecanico", idMecanico);
                jsonParam.put("calificacion", calificacion);
                jsonParam.put("comentario", comentario);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseStr = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseStr.append(line);
                    }
                    reader.close();

                    JSONObject responseJson = new JSONObject(responseStr.toString());
                    runOnUiThread(() -> {
                        try {
                            if (responseJson.getBoolean("success")) {
                                Toast.makeText(this, "Calificación enviada", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error: " + responseJson.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error en respuesta", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error en conexión al servidor", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al enviar calificación", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
