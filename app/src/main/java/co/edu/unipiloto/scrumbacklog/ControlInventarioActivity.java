package co.edu.unipiloto.scrumbacklog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.unipiloto.scrumbacklog.Spinner.SimpleItemSelected;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class ControlInventarioActivity extends AppCompatActivity {

    private LinearLayout layoutInventario, layoutHistorial;
    private Spinner spFiltroCombustible, spFiltroCiudad, spFiltroEstacion;
    private Button btnVolver;

    private ArrayList<String> ciudades;
    private ArrayList<String> estaciones;

    DAOFactory factory;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacionUsuario;

    boolean inicializado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_inventario);

        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        movimientoDAO = factory.getMovimientoDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        spFiltroCombustible = findViewById(R.id.spFiltroCombustible);
        spFiltroCiudad = findViewById(R.id.spFiltroCiudad);
        spFiltroEstacion = findViewById(R.id.spFiltroEstacion);
        layoutInventario = findViewById(R.id.layoutInventario);
        layoutHistorial = findViewById(R.id.layoutHistorial);
        btnVolver = findViewById(R.id.btnVolver);

        configurarPorRol();
        cargarCombustibles();

        configurarListeners();

        inicializado = true;

        btnVolver.setOnClickListener(v -> finish());
    }

    // =========================================================
    // CONFIGURACIÓN POR ROL
    // =========================================================
    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("CLIENTE")) {
            finish();
            return;
        }

        // =========================
        // ADMIN / DISTRIBUIDOR
        // =========================
        if (rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("DISTRIBUIDOR")) {

            cargarCiudades();

            spFiltroCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ciudad = spFiltroCiudad.getSelectedItem().toString();
                    cargarEstaciones(ciudad);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spFiltroEstacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (inicializado) refrescarVista();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            return;
        }

        // =========================
        // OPERADOR / ESTACIÓN
        // =========================
        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacionUsuario);

            if (ubicacion != null) {

                String ciudad = ubicacion[0];
                String estacion = ubicacion[1];

                // CIUDAD FIJA
                spFiltroCiudad.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(ciudad)
                ));
                spFiltroCiudad.setEnabled(false);

                // ESTACIÓN FIJA
                spFiltroEstacion.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Collections.singletonList(estacion)
                ));
                spFiltroEstacion.setEnabled(false);
            }
        }
    }

    // =========================================================
    // COMBUSTIBLES
    // =========================================================
    private void cargarCombustibles() {

        String[] combustibles = {"Todos", "Corriente", "Extra", "Diesel"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                combustibles
        );

        spFiltroCombustible.setAdapter(adapter);
    }

    // =========================================================
    // CIUDADES (ADMIN / DISTRIBUIDOR)
    // =========================================================
    private void cargarCiudades() {

        ciudades = ubicacionDAO.obtenerCiudades();

        if (ciudades == null) ciudades = new ArrayList<>();

        spFiltroCiudad.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ciudades
        ));
    }

    // =========================================================
    // ESTACIONES POR CIUDAD
    // =========================================================
    private void cargarEstaciones(String ciudad) {

        estaciones = ubicacionDAO.obtenerZonas(ciudad);

        if (estaciones == null) estaciones = new ArrayList<>();

        spFiltroEstacion.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                estaciones
        ));
    }

    // =========================================================
    // LISTENERS
    // =========================================================
    private void configurarListeners() {

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (inicializado) refrescarVista();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spFiltroCombustible.setOnItemSelectedListener(listener);
    }

    // =========================================================
    // REFRESCAR VISTA
    // =========================================================
    private void refrescarVista() {

        if (spFiltroCiudad.getSelectedItem() == null ||
                spFiltroEstacion.getSelectedItem() == null) return;

        mostrarInventario();
        mostrarHistorial();
    }

    // =========================================================
    // INVENTARIO
    // =========================================================
    private void mostrarInventario() {

        layoutInventario.removeAllViews();

        String combustible = spFiltroCombustible.getSelectedItem().toString();
        String ciudad = spFiltroCiudad.getSelectedItem().toString();
        String estacion = spFiltroEstacion.getSelectedItem().toString();

        int idUbicacion = ubicacionDAO.obtenerIdUbicacion(ciudad, estacion);

        String[] tipos = combustible.equals("Todos")
                ? new String[]{"Corriente", "Extra", "Diesel"}
                : new String[]{combustible};

        for (String tipo : tipos) {

            double cantidad = inventarioDAO.obtenerInventario(tipo, ciudad, estacion);

            TextView tv = new TextView(this);
            tv.setText(tipo + ": " + cantidad + " galones");
            tv.setTextSize(16f);

            layoutInventario.addView(tv);
        }
    }

    // =========================================================
    // HISTORIAL
    // =========================================================
    private void mostrarHistorial() {

        layoutHistorial.removeAllViews();

        String ciudad = spFiltroCiudad.getSelectedItem().toString();
        String estacion = spFiltroEstacion.getSelectedItem().toString();

        int idUbicacion = ubicacionDAO.obtenerIdUbicacion(ciudad, estacion);

        ArrayList<String> movimientos =
                movimientoDAO.obtenerMovimientosPorUbicacion(idUbicacion);

        if (movimientos == null) return;

        int limit = Math.min(movimientos.size(), 10);

        for (int i = 0; i < limit; i++) {

            TextView tv = new TextView(this);
            tv.setText(movimientos.get(i));

            layoutHistorial.addView(tv);
        }
    }
}