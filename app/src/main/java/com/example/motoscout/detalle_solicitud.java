package com.example.motoscout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class detalle_solicitud extends AppCompatActivity {

    TextView tvMoto, tvTipoServicio, tvFechaSolicitud, tvEstado;
    EditText etMonto;
    Spinner spinnerFormaPago;
    Button btnFinalizar;

    int idServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_solicitud);

        tvMoto = findViewById(R.id.tvMoto);
        tvTipoServicio = findViewById(R.id.tvTipoServicio);
        tvFechaSolicitud = findViewById(R.id.tvFechaSolicitud);
        tvEstado = findViewById(R.id.tvEstado);
        etMonto = findViewById(R.id.etMonto);
        spinnerFormaPago = findViewById(R.id.spinnerFormaPago);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        idServicio = getIntent().getIntExtra("id_servicio", -1);
        if (idServicio == -1) {
            Toast.makeText(this, "Servicio no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDetalleServicio();

        String[] formasPago = {"Efectivo", "Tarjeta", "Transferencia", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formasPago);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormaPago.setAdapter(adapter);

        btnFinalizar.setOnClickListener(v -> finalizarServicio());
    }

    private void cargarDetalleServicio() {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.100.99/motoscout/get_detalle_solicitud.php?id_servicio=" + idServicio);
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
                    runOnUiThread(() -> {
                        try {
                            if (json.getBoolean("success")) {
                                // Cambiado "servicio" a "solicitud"
                                JSONObject servicio = json.getJSONObject("solicitud");
                                tvMoto.setText("Moto: " + servicio.getString("moto"));
                                tvTipoServicio.setText("Tipo de Servicio: " + servicio.getString("tipo_servicio"));
                                tvFechaSolicitud.setText("Fecha de Solicitud: " + servicio.getString("fecha_solicitud"));
                                tvEstado.setText("Estado: " + servicio.getString("estado"));

                                if (!servicio.isNull("monto_pagado"))
                                    etMonto.setText(servicio.getString("monto_pagado"));

                                if (!servicio.isNull("forma_pago")) {
                                    String formaPago = servicio.getString("forma_pago");
                                    int spinnerPosition = ((ArrayAdapter) spinnerFormaPago.getAdapter()).getPosition(formaPago);
                                    spinnerFormaPago.setSelection(spinnerPosition);
                                }

                            } else {
                                Toast.makeText(this, "Error al cargar detalles", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error en conexión", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error en consulta", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void finalizarServicio() {
        String montoStr = etMonto.getText().toString().trim();
        String formaPago = spinnerFormaPago.getSelectedItem().toString();

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el monto pagado", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.100.99/motoscout/finalizar_servicio.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Construir JSON con fecha_realizacion hoy
                String fechaHoy = LocalDate.now().toString(); // YYYY-MM-DD

                JSONObject postData = new JSONObject();
                postData.put("id_servicio", idServicio);
                postData.put("fecha_realizacion", fechaHoy);
                postData.put("monto_pagado", monto);
                postData.put("forma_pago", formaPago);

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                conn.getOutputStream().write(postData.toString().getBytes("UTF-8"));
                conn.getOutputStream().flush();
                conn.getOutputStream().close();

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
                    runOnUiThread(() -> {
                        try {
                            if (json.getBoolean("success")) {
                                Toast.makeText(this, "Servicio finalizado", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error al finalizar", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error en respuesta", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error en conexión", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al finalizar", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
