package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BtnPanico extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    double aceleracion;
    boolean alertaActiva = false;
    Handler handler = new Handler();
    Runnable runnable;

    List<String> numerosEmergencia = new ArrayList<>();
    String mensajeEmergencia = "¡Motoscout Alerta! Se ha detectado una caída de mi parte. Necesito ayuda urgente en mi ubicación actual.";

    boolean contactosCargados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_btn_panico);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Solicitar permisos de SMS si no están concedidos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        obtenerContactosEmergencia(); // carga contactos al inicio
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        // Cálculo de magnitud de aceleración
        aceleracion = Math.sqrt(x * x + y * y + z * z);

        // Umbral de caída simulado (un valor alto indica impacto brusco)
        if (aceleracion > 30 && !alertaActiva) {
            alertaActiva = true;
            mostrarDialogoDeAlerta();
        }
    }

    private void mostrarDialogoDeAlerta() {
        // Reducimos el tiempo a 15 segundos para que la simulación sea más rápida en pruebas
        int tiempoEspera = 15000; 

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("¡IMPACTO DETECTADO!")
                .setMessage("¿Te encuentras bien? Se enviará un SMS de emergencia a tus contactos en " + (tiempoEspera/1000) + " segundos.")
                .setCancelable(false)
                .setPositiveButton("ESTOY BIEN", (d, w) -> {
                    alertaActiva = false;
                    handler.removeCallbacks(runnable);
                    Toast.makeText(this, "Alerta de caída cancelada.", Toast.LENGTH_SHORT).show();
                })
                .create();
        dialog.show();

        runnable = this::enviarSMS;
        handler.postDelayed(runnable, tiempoEspera);
    }

    private void enviarSMS() {
        if (!alertaActiva) return; // Si el usuario ya canceló, no enviar

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sin permiso para enviar SMS", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numerosEmergencia.isEmpty()) {
            Toast.makeText(this, "No tienes contactos de emergencia registrados.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager sms = SmsManager.getDefault();
            for (String numero : numerosEmergencia) {
                // Enviamos el mensaje real a cada número
                sms.sendTextMessage(numero, null, mensajeEmergencia, null, null);
                Toast.makeText(this, "SMS enviado a: " + numero, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "¡Alerta enviada a todos tus contactos!", Toast.LENGTH_LONG).show();
            alertaActiva = false;
        } catch (Exception e) {
            Toast.makeText(this, "Fallo al enviar SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void obtenerContactosEmergencia() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        
        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no iniciada para cargar contactos.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.100.99/motoscout/get_contactos_emergencia.php?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder res = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) res.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(res.toString());
                    if (json.getBoolean("success")) {
                        numerosEmergencia.clear();
                        JSONArray contactos = json.getJSONArray("contactos");
                        for (int i = 0; i < contactos.length(); i++) {
                            numerosEmergencia.add(contactos.getJSONObject(i).getString("telefono_contacto"));
                        }
                        contactosCargados = true;
                        runOnUiThread(() -> Toast.makeText(BtnPanico.this, "Contactos listos para emergencia", Toast.LENGTH_SHORT).show());
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onPause() {
        super.onPause();
        // Detenemos el sensor al salir de la pantalla para ahorrar batería
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reactivamos el sensor al volver
        Sensor acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso SMS activado", Toast.LENGTH_SHORT).show();
        }
    }

    // Navegación
    public void AgrContac(View view) {
        startActivity(new Intent(this, Contactos.class));
    }
    public void VerContact(View view) {
        startActivity(new Intent(this, VerContac.class));
    }
    public void Alarm(View view) {
        startActivity(new Intent(this, Alarma.class));
    }
    public void Moto(View view) {
        startActivity(new Intent(this, Manual.class));
    }
    public void ContMec(View view) {
        startActivity(new Intent(this, Ubicacion.class));
    }
    public void Perfil(View view) {
        startActivity(new Intent(this, UserMtc.class));
    }
    public void Recordatorio(View view) {
        startActivity(new Intent(this, Recordatorios.class));
    }
    public void compartir(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "¡Alerta! Me siento en peligro.");
        startActivity(intent);
    }
}