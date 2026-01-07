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
    String mensajeEmergencia = "¡Posible caída detectada! Necesito ayuda.";

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

        // Solicita permiso en tiempo de ejecución si no está concedido
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

        aceleracion = Math.sqrt(x * x + y * y + z * z);

        if (aceleracion > 25 && !alertaActiva) {
            alertaActiva = true;
            mostrarDialogoDeAlerta();
        }
    }

    private void mostrarDialogoDeAlerta() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Posible caída detectada")
                .setMessage("¿Estás bien? Se enviará un SMS de emergencia en 40 segundos si no respondes.")
                .setCancelable(false)
                .setPositiveButton("Estoy bien", (d, w) -> {
                    alertaActiva = false;
                    handler.removeCallbacks(runnable);
                    Toast.makeText(this, "Alarma cancelada", Toast.LENGTH_SHORT).show();
                })
                .create();
        dialog.show();

        runnable = this::enviarSMS;
        handler.postDelayed(runnable, 40000); // 40 segundos
    }

    private void enviarSMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso para enviar SMS no concedido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contactosCargados || numerosEmergencia.isEmpty()) {
            Toast.makeText(this, "Contactos de emergencia no cargados, intenta de nuevo", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager sms = SmsManager.getDefault();
            for (String numero : numerosEmergencia) {
                Toast.makeText(this, "Enviando a: " + numero, Toast.LENGTH_SHORT).show();
                sms.sendTextMessage(numero, null, mensajeEmergencia, null, null);
            }
            Toast.makeText(this, "SMS enviados a contactos de emergencia", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al enviar SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        numerosEmergencia.clear();
                        JSONArray contactos = json.getJSONArray("contactos");
                        for (int i = 0; i < contactos.length(); i++) {
                            JSONObject contacto = contactos.getJSONObject(i);
                            String telefono = contacto.getString("telefono_contacto");
                            numerosEmergencia.add(telefono);
                        }
                        contactosCargados = true;
                        runOnUiThread(() -> Toast.makeText(BtnPanico.this, "Contactos de emergencia cargados", Toast.LENGTH_SHORT).show());
                    }
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(BtnPanico.this, "Error al cargar contactos", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    // Permiso SMS resultado
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso para enviar SMS concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso para enviar SMS denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Navegación

    public void AgrContac(View view) {
        startActivity(new Intent(getApplicationContext(), Contactos.class));
        finish();
    }

    public void VerContact(View view) {
        startActivity(new Intent(getApplicationContext(), VerContac.class));
        finish();
    }

    public void Alarm(View view) {
        startActivity(new Intent(getApplicationContext(), Alarma.class));
    }

    public void Moto(View view) {
        startActivity(new Intent(getApplicationContext(), Manual.class));
    }

    public void ContMec(View view) {
        startActivity(new Intent(getApplicationContext(), Ubicacion.class));
    }

    public void Perfil(View view) {
        startActivity(new Intent(getApplicationContext(), UserMtc.class));
    }

    public void Recordatorio(View view) {
        startActivity(new Intent(getApplicationContext(), Recordatorios.class));
    }

    public void compartir(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Me siento en peligro");
        startActivity(intent);
    }
}
