package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

// Importaciones necesarias para Mapas y Ubicación
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class SerMot extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // Cliente para obtener la ubicación real del GPS
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ser_mot);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar el servicio de ubicación (GPS)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Cargar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Configuración para ver calles y controles
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // 2. Activar la ubicación en tiempo real del Mecánico (Usuario actual)
        activarMiUbicacionGPS();
    }

    private void activarMiUbicacionGPS() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Si hay permiso: Encender el punto azul
            mMap.setMyLocationEnabled(true);

            // Obtener la posición actual y mover la cámara
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng miUbicacionReal = new LatLng(location.getLatitude(), location.getLongitude());

                                // Colocar el marcador fijo de "Karla" (Destino) cerca del usuario para simular
                                LatLng ubicacionKarla = new LatLng(location.getLatitude() + 0.003, location.getLongitude() + 0.002);
                                mMap.addMarker(new MarkerOptions()
                                        .position(ubicacionKarla)
                                        .title("Ubicación de Karla (Cliente)"));

                                // Dibujar ruta simulada
                                mMap.addPolyline(new PolylineOptions()
                                        .add(miUbicacionReal, ubicacionKarla)
                                        .width(12)
                                        .color(Color.RED)
                                        .geodesic(true));

                                // Mover la cámara a donde está el mecánico realmente con buen zoom
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacionReal, 16f));
                            }
                        }
                    });

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activarMiUbicacionGPS();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void Cancelar(View view) {
        Intent intent = new Intent(getApplicationContext(), Mecanico.class);
        startActivity(intent);
    }

    public void Terminar(View view) {
        Intent intent = new Intent(getApplicationContext(), Serv_pendt.class);
        startActivity(intent);
    }
}