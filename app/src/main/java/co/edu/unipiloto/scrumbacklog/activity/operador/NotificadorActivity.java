package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class NotificadorActivity extends AppCompatActivity {

    Spinner spCiudad, spZona;
    Button btnVerificar, btnVolver;
    TextView txtAlerta;

    DAOFactory factory;
    InventarioDAO inventarioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificador);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnVerificar = findViewById(R.id.btnVerificar);
        btnVolver = findViewById(R.id.btnVolver);
        txtAlerta = findViewById(R.id.txtAlerta);

        factory = new DAOFactory(this);
        inventarioDAO = factory.getInventarioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        configurarPorRol();

        btnVerificar.setOnClickListener(v -> verificarInventario());
        btnVolver.setOnClickListener(v -> finish());
    }

    // =====================================================
    // CONTROL POR ROL
    // =====================================================
    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("CLIENTE")) {
            finish();
            return;
        }

        // =========================
        // ADMIN → acceso total
        // =========================
        if (rol.equalsIgnoreCase("ADMIN")) {

            cargarCiudadesSpinner();

            spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    String ciudad = spCiudad.getSelectedItem().toString();
                    cargarZonasSpinner(ciudad);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            return;
        }

        // =========================
        // OPERADOR → solo su estación
        // =========================
        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {

                String ciudad = ubicacion[0];
                String zona = ubicacion[1];

                spCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ciudad)
                ));
                spCiudad.setEnabled(false);

                spZona.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(zona)
                ));
                spZona.setEnabled(false);
            }
        }
    }

    // =====================================================
    // VERIFICAR INVENTARIO
    // =====================================================
    private void verificarInventario() {

        String ciudad;
        String zona;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);
            ciudad = ubicacion[0];
            zona = ubicacion[1];

        } else {

            if (spCiudad.getSelectedItem() == null || spZona.getSelectedItem() == null) {
                txtAlerta.setText("Seleccione ubicación válida");
                return;
            }

            ciudad = spCiudad.getSelectedItem().toString();
            zona = spZona.getSelectedItem().toString();
        }

        double diesel = inventarioDAO.obtenerInventario("Diesel", ciudad, zona);
        double corriente = inventarioDAO.obtenerInventario("Corriente", ciudad, zona);
        double extra = inventarioDAO.obtenerInventario("Extra", ciudad, zona);

        StringBuilder mensaje = new StringBuilder();

        mensaje.append("📍 ").append(ciudad).append(" - ").append(zona).append("\n\n");

        if (diesel < 1000) {
            mensaje.append("⚠ Diesel crítico: ").append(diesel).append("\n");
        }

        if (corriente < 1000) {
            mensaje.append("⚠ Corriente crítico: ").append(corriente).append("\n");
        }

        if (extra < 1000) {
            mensaje.append("⚠ Extra crítico: ").append(extra).append("\n");
        }

        if (diesel >= 1000 && corriente >= 1000 && extra >= 1000) {
            mensaje.append("✔ Inventario en niveles normales");
        }

        txtAlerta.setText(mensaje.toString());
    }

    // =====================================================
    // SPINNERS ADMIN
    // =====================================================
    private void cargarCiudadesSpinner() {

        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();

        if (ciudades == null) ciudades = new ArrayList<>();

        spCiudad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ciudades
        ));
    }

    private void cargarZonasSpinner(String ciudad) {

        ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);

        if (zonas == null) zonas = new ArrayList<>();

        spZona.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                zonas
        ));
    }
}