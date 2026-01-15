package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Variable para el campo de texto
    private EditText etBuscador;

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

        // 1. Vincular el EditText del buscador
        etBuscador = findViewById(R.id.et_buscador);

        // Inicializar GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Al cargar, intenta ir a la ubicación real del usuario
        habilitarUbicacionEnTiempoReal();
    }

    //  LÓGICA DE BÚSQUEDA
    public void buscarUbicacion(View view) {
        // 1. Obtener el texto que escribió el usuario
        String ubicacionBuscada = etBuscador.getText().toString();

        if (ubicacionBuscada.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa una ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Usar Geocoder para buscar coordenadas
        Geocoder geocoder = new Geocoder(this);
        List<Address> listaDirecciones = null;

        try {
            // Buscamos 1 resultado
            listaDirecciones = geocoder.getFromLocationName(ubicacionBuscada, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al buscar: revisa tu internet", Toast.LENGTH_SHORT).show();
        }

        // 3. Si encontramos algo, movemos el mapa
        if (listaDirecciones != null && !listaDirecciones.isEmpty()) {
            Address direccionEncontrada = listaDirecciones.get(0);
            LatLng latLng = new LatLng(direccionEncontrada.getLatitude(), direccionEncontrada.getLongitude());

            // Agregar un marcador en lo que se encontró
            mMap.addMarker(new MarkerOptions().position(latLng).title(ubicacionBuscada));

            // Mover la cámara
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

            Toast.makeText(this, "Ubicación encontrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
        }
    }


    private void habilitarUbicacionEnTiempoReal() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f));
                        }
                    }
                });
    }

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


    public void Buscar(View view) {
        Intent intent = new Intent(getApplicationContext(), Aceptar.class);
        startActivity(intent);
    }
    public void Moto(View view) {
        Intent intent = new Intent(getApplicationContext(), Manual.class);
        startActivity(intent);
    }
    public void BtnPan(View view) {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);
        startActivity(intent);
    }
    public void Perfil(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);
        startActivity(intent);
    }
    public void Recordatorio(View view) {
        Intent intent = new Intent(getApplicationContext(), Recordatorios.class);
        startActivity(intent);
    }
}