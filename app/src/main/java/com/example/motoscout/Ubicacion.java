package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText etBuscador;
    private Map<String, JSONObject> mechanicDataMap = new HashMap<>();

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

        etBuscador = findViewById(R.id.et_buscador);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        habilitarUbicacionEnTiempoReal();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        JSONObject mechanicJson = mechanicDataMap.get(marker.getId());
        if (mechanicJson != null) {
            try {
                Intent intent = new Intent(this, Aceptar.class);
                intent.putExtra("nombre_mecanico", mechanicJson.getString("nombre") + " " + mechanicJson.getString("apellido"));
                intent.putExtra("foto_mecanico", mechanicJson.optString("foto", ""));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void buscarUbicacion(View view) {
        String ubicacionBuscada = etBuscador.getText().toString();
        if (ubicacionBuscada.isEmpty()) return;

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> listaDirecciones = geocoder.getFromLocationName(ubicacionBuscada, 1);
            if (listaDirecciones != null && !listaDirecciones.isEmpty()) {
                mMap.clear();
                mechanicDataMap.clear();
                Address dir = listaDirecciones.get(0);
                LatLng latLng = new LatLng(dir.getLatitude(), dir.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Destino: " + ubicacionBuscada));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                
                // CARGAR MECÁNICOS REALES DESDE LA BD
                obtenerMecanicosDesdeBD();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void obtenerMecanicosDesdeBD() {
        new Thread(() -> {
            try {
                URL url = new URL(Constantes.SERVER_URL + "get_mecanicos.php");
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
                        JSONArray mecanicos = json.getJSONArray("mecanicos");
                        runOnUiThread(() -> mostrarMecanicosEnMapa(mecanicos));
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarMecanicosEnMapa(JSONArray mecanicos) {
        for (int i = 0; i < mecanicos.length(); i++) {
            try {
                JSONObject m = mecanicos.getJSONObject(i);
                String ubi = m.getString("ubicacion"); // "lat,lng"
                if (ubi.contains(",")) {
                    String[] parts = ubi.split(",");
                    LatLng pos = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                    
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .title("Mecánico: " + m.getString("nombre"))
                            .snippet("Taller disponible - Toca para ver perfil")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    
                    mechanicDataMap.put(marker.getId(), m);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void habilitarUbicacionEnTiempoReal() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 14f));
                    obtenerMecanicosDesdeBD(); // Cargar inicial
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void Moto(View view) { startActivity(new Intent(this, Manual.class)); }
    public void BtnPan(View view) { startActivity(new Intent(this, BtnPanico.class)); }
    public void Perfil(View view) { startActivity(new Intent(this, UserMtc.class)); }
    public void Recordatorio(View view) { startActivity(new Intent(this, Recordatorios.class)); }
}