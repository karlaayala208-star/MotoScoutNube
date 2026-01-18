package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importaciones para Mapas y Ubicación
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.HttpURLConnection;
import java.net.URL;

public class serve extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serve);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar cliente de ubicación (GPS)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 2. Cargar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Configuración para que el mapa se vea detallado
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        
        // Cuando el mapa esté listo, activamos la ubicación
        activarUbicacionEnTiempoReal();
    }

    private void activarUbicacionEnTiempoReal() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Habilitar el punto azul
            mMap.setMyLocationEnabled(true);

            // Obtener la última ubicación conocida y mover la cámara
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                double lat = location.getLatitude();
                                double lng = location.getLongitude();

                                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                                // Zoom de 16f para ver calles claramente
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16f));

                                enviarUbicacionServidor(lat, lng);
                            }
                        }

                        private void enviarUbicacionServidor(double lat, double lng) {
                            new Thread(() -> {
                                try {
                                    // Leer el ID del mecánico logueado
                                    int idUsuario = getSharedPreferences("session", MODE_PRIVATE)
                                            .getInt("id_usuario", -1);

                                    if (idUsuario == -1) return;

                                    URL url = new URL(Constantes.SERVER_URL + "guardar_ubicacion.php");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                    String postData =
                                            "id_usuario=" + idUsuario +
                                                    "&latitud=" + lat +
                                                    "&longitud=" + lng;

                                    conn.getOutputStream().write(postData.getBytes());
                                    conn.getOutputStream().flush();
                                    conn.getOutputStream().close();

                                    conn.getResponseCode(); // fuerza el envío
                                    conn.disconnect();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    });

        } else {
            // Pedir permisos si no los tiene
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Respuesta del usuario al permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activarUbicacionEnTiempoReal();
            } else {
                Toast.makeText(this, "Permiso de ubicación necesario", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void PerfilMec(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMec.class);
        startActivity(intent);
    }

    public void VerUbic(View view) {
        Intent intent = new Intent(getApplicationContext(), Mecanico.class);
        startActivity(intent);
    }
}