package com.example.motoscout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    EditText txtUser, txtPass;
    private Executor executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void capturaDatos(View view) {
        String telefono = txtUser.getText().toString().trim();
        String contrasena = txtPass.getText().toString().trim();

        if (telefono.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Datos faltantes, favor de llenar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL("http://192.168.100.99/motoscout/login_usuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "telefono=" + URLEncoder.encode(telefono, "UTF-8")
                        + "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    conn.disconnect();

                    Log.d("LOGIN_RESPONSE", response.toString()); // <-- VERIFICA RESPUESTA AQUÍ

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean success = jsonResponse.getBoolean("success");

                    handler.post(() -> {
                        if (success) {
                            try {
                                JSONObject userObj = jsonResponse.getJSONObject("user");
                                int idUsuario = userObj.getInt("id_usuario"); // <-- AJUSTADO A 'id'
                                String nombre = userObj.getString("nombre");
                                String tipoUsuario = userObj.getString("tipo_usuario");

                                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("id_usuario", idUsuario); // Guarda el ID
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();

                                Intent intent;
                                if (tipoUsuario.equals("mecanico")) {
                                    intent = new Intent(getApplicationContext(), serve.class);
                                } else {
                                    intent = new Intent(getApplicationContext(), BtnPanico.class);
                                }

                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error al procesar usuario", Toast.LENGTH_SHORT).show();
                                Log.e("LOGIN_ERROR", e.getMessage());
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Credenciales incorrectas");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(() -> Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    public void Registro(View view) {
        startActivity(new Intent(getApplicationContext(), Perfil.class));
    }
}
