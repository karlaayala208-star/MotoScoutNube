package com.example.motoscout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Registro extends AppCompatActivity {

    EditText TxtNomb, TxtApell, TxtEm, TxtNumb, TxtPass, TxtDate, TxtExperiencia, TxtUbicacion;
    private Executor executor;
    private Handler handler;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tipoUsuario = getIntent().getStringExtra("tipo_usuario");

        TxtNomb = findViewById(R.id.TxtNomb);
        TxtApell = findViewById(R.id.TxtApell);
        TxtEm = findViewById(R.id.TxtEm);
        TxtNumb = findViewById(R.id.TxtNumb);
        TxtPass = findViewById(R.id.TxtPass);
        TxtDate = findViewById(R.id.TxtDate);
        TxtExperiencia = findViewById(R.id.TxtExperiencia);
        TxtUbicacion = findViewById(R.id.TxtUbicacion);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Mostrar u ocultar campos adicionales si es mecánico
        if ("mecanico".equals(tipoUsuario)) {
            TxtExperiencia.setVisibility(View.VISIBLE);
            TxtUbicacion.setVisibility(View.VISIBLE);
        } else {
            TxtExperiencia.setVisibility(View.GONE);
            TxtUbicacion.setVisibility(View.GONE);
        }

        // Configurar DatePicker para el campo fecha
        TxtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Registro.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        TxtDate.setText(fechaFormateada);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    public void Inicio(View view) {
        executor.execute(() -> {
            String nombre = TxtNomb.getText().toString().trim();
            String apellido = TxtApell.getText().toString().trim();
            String email = TxtEm.getText().toString().trim();
            String telefono = TxtNumb.getText().toString().trim();
            String password = TxtPass.getText().toString().trim();
            String fecha = TxtDate.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                    telefono.isEmpty() || password.isEmpty() || fecha.isEmpty()) {

                handler.post(() -> Toast.makeText(getApplicationContext(),
                        "Datos faltantes, por favor llene todos los campos.",
                        Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                URL url = new URL("http://192.168.100.99/motoscout/registro_usuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String experiencia = "", ubicacion = "";
                if ("mecanico".equals(tipoUsuario)) {
                    experiencia = TxtExperiencia.getText().toString().trim();
                    ubicacion = TxtUbicacion.getText().toString().trim();
                }

                String postData = "nombre=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&apellido=" + URLEncoder.encode(apellido, "UTF-8")
                        + "&email=" + URLEncoder.encode(email, "UTF-8")
                        + "&telefono=" + URLEncoder.encode(telefono, "UTF-8")
                        + "&contrasena=" + URLEncoder.encode(password, "UTF-8")
                        + "&fecha_nacimiento=" + URLEncoder.encode(fecha, "UTF-8")
                        + "&tipo_usuario=" + URLEncoder.encode(tipoUsuario, "UTF-8")
                        + "&experiencia=" + URLEncoder.encode(experiencia, "UTF-8")
                        + "&ubicacion=" + URLEncoder.encode(ubicacion, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer respuesta del servidor
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String serverResponse = response.toString();
                    handler.post(() -> {
                        Toast.makeText(getApplicationContext(), "Respuesta: " + serverResponse, Toast.LENGTH_LONG).show();
                        // Aquí podrías hacer algo si es éxito, por ejemplo cerrar actividad:
                        if (serverResponse.contains("\"status\":\"ok\"")) {
                            finish();
                        }
                    });
                } else {
                    handler.post(() -> Toast.makeText(getApplicationContext(), "Error del servidor", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
