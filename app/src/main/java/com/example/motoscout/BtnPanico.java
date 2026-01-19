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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        obtenerContactosEmergencia();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        aceleracion = Math.sqrt(x * x + y * y + z * z);

        if (aceleracion > 30 && !alertaActiva) {
            alertaActiva = true;
            registrarCaidaEnBD(); // Registrar inmediatamente en la BD
            mostrarDialogoDeAlerta();
        }
    }

    private void registrarCaidaEnBD() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) return;

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            try {
                URL url = new URL(Constantes.SERVER_URL + "registrar_caida.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject postData = new JSONObject();
                postData.put("id_usuario", idUsuario);
                postData.put("fecha", fecha);
                postData.put("hora", hora);

                OutputStream os = conn.getOutputStream();
                os.write(postData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Caída registrada con éxito
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarDialogoDeAlerta() {
        int tiempoEspera = 15000; 

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("¡IMPACTO DETECTADO!")
                .setMessage("¿Te encuentras bien? Se enviará un SMS de emergencia en " + (tiempoEspera/1000) + " segundos.")
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
        if (!alertaActiva) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (numerosEmergencia.isEmpty()) {
            return;
        }

        try {
            SmsManager sms = SmsManager.getDefault();
            for (String numero : numerosEmergencia) {
                sms.sendTextMessage(numero, null, mensajeEmergencia, null, null);
            }
            Toast.makeText(this, "¡Alerta enviada a tus contactos!", Toast.LENGTH_LONG).show();
            alertaActiva = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtenerContactosEmergencia() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        
        if (idUsuario == -1) return;

        new Thread(() -> {
            try {
                URL url = new URL(Constantes.SERVER_URL + "get_contactos_emergencia.php?id_usuario=" + idUsuario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
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
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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