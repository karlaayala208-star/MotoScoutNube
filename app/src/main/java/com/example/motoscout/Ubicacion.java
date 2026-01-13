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

// Importaciones de Mapas y Ubicación
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ubicacion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar el cliente de ubicación (GPS)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        habilitarUbicacionEnTiempoReal();
    }

    private void habilitarUbicacionEnTiempoReal() {
        // 1. Verificar si tenemos permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Si hay permiso: Activamos el punto azul y buscamos la posición
            mMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();

        } else {
            // Si NO hay permiso: Lo pedimos al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void obtenerUbicacionActual() {
        // Verificar permiso nuevamente (requerido por Android Studio)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Obtener la última ubicación conocida del GPS
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // "location" puede ser nulo si el GPS estaba apagado
                        if (location != null) {
                            LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                            // Mover la cámara a TU ubicación real
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f));
                            Toast.makeText(Ubicacion.this, "Ubicación encontrada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Ubicacion.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Manejar la respuesta del usuario cuando le pedimos permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                habilitarUbicacionEnTiempoReal();
            } else {
                Toast.makeText(this, "Se necesita permiso para mostrar tu ubicación", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void Buscar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Aceptar.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Moto(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Manual.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void BtnPan(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Perfil(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Recordatorio(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Recordatorios.class);//intent es el nombre del intento
        startActivity(intent);
    }
}