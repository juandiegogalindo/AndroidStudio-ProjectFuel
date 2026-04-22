package co.edu.unipiloto.scrumbacklog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class ReguladorPreciosActivity extends AppCompatActivity {

    private Spinner spCiudad, spLocalidad, spCombustible;
    private TextView txtPrecioActual;
    private EditText etNuevoPrecio;
    private Button btnActualizarPrecio, btnVolver;

    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;
    UsuarioDAO usuarioDAO;

    String rol;
    int idUbicacion;

    boolean inicializado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulador_precios);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        factory = new DAOFactory(this);
        combustibleDAO = factory.getCombustibleDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        usuarioDAO = factory.getUsuarioDAO();

        spCiudad = findViewById(R.id.spCiudad);
        spLocalidad = findViewById(R.id.spZona);
        spCombustible = findViewById(R.id.spCombustible);

        txtPrecioActual = findViewById(R.id.txtPrecioActual);
        etNuevoPrecio = findViewById(R.id.etNuevoPrecio);

        btnActualizarPrecio = findViewById(R.id.btnActualizarPrecio);
        btnVolver = findViewById(R.id.btnVolver);

        cargarCombustibles();

        configurarSegunRol();

        configurarListeners();

        inicializado = true;

        btnActualizarPrecio.setOnClickListener(v -> actualizarPrecio());
        btnVolver.setOnClickListener(v -> finish());
    }

    // ---------------- ROLES ----------------

    private void configurarSegunRol() {

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String[] ubicacion = usuarioDAO.obtenerUbicacionUsuario(idUbicacion);

            if (ubicacion != null) {
                String ciudad = ubicacion[0];
                String localidad = ubicacion[1];

                spCiudad.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(ciudad)));

                spLocalidad.setAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList(localidad)));

                spCiudad.setEnabled(false);
                spLocalidad.setEnabled(false);
            }

        }
    }

    // ---------------- LISTENERS ----------------

    private void configurarListeners() {

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (inicializado) {
                    mostrarPrecioActual();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spCiudad.setOnItemSelectedListener(listener);
        spLocalidad.setOnItemSelectedListener(listener);
        spCombustible.setOnItemSelectedListener(listener);
    }

    // ---------------- LOGICA ----------------

    private void cargarCombustibles() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                combustibleDAO.obtenerCombustibles()
        );
        spCombustible.setAdapter(adapter);
    }

    private void mostrarPrecioActual() {

        if (spCombustible.getSelectedItem() == null) return;

        String combustible = spCombustible.getSelectedItem().toString();
        double precio;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            precio = precioDAO.obtenerPrecioPorUbicacion(combustible, idUbicacion);

        } else {

            if (spCiudad.getSelectedItem() == null || spLocalidad.getSelectedItem() == null)
                return;

            String ciudad = spCiudad.getSelectedItem().toString();
            String localidad = spLocalidad.getSelectedItem().toString();

            precio = precioDAO.obtenerPrecioZona(combustible, ciudad, localidad);
        }

        if (precio < 0) {
            txtPrecioActual.setText("Precio no disponible");
        } else {
            txtPrecioActual.setText("Precio actual: $" + precio);
        }
    }

    // ---------------- UPDATE ----------------

    private void actualizarPrecio() {

        if (spCombustible.getSelectedItem() == null) return;

        String combustible = spCombustible.getSelectedItem().toString();

        double nuevoPrecio;

        try {
            nuevoPrecio = Double.parseDouble(etNuevoPrecio.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            ok = actualizarPrecioPorUbicacion(combustible, idUbicacion, nuevoPrecio);

        } else {

            if (spCiudad.getSelectedItem() == null || spLocalidad.getSelectedItem() == null)
                return;

            ok = actualizarPrecioPorZona(
                    combustible,
                    spCiudad.getSelectedItem().toString(),
                    spLocalidad.getSelectedItem().toString(),
                    nuevoPrecio
            );
        }

        Toast.makeText(this,
                ok ? "Actualizado" : "Error",
                Toast.LENGTH_SHORT).show();

        mostrarPrecioActual();
    }

    private boolean actualizarPrecioPorZona(String tipo, String ciudad, String localidad, double precio) {
        try {
            SQLiteDatabase db = factory.getDatabase();

            db.execSQL(
                    "UPDATE precio_combustible SET precio=? " +
                            "WHERE id_combustible=(SELECT id_combustible FROM combustible WHERE nombre=?) " +
                            "AND id_ubicacion=(SELECT id_ubicacion FROM ubicacion WHERE ciudad=? AND localidad=?)",
                    new Object[]{precio, tipo, ciudad, localidad}
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean actualizarPrecioPorUbicacion(String tipo, int idUbicacion, double precio) {
        try {
            SQLiteDatabase db = factory.getDatabase();

            db.execSQL(
                    "UPDATE precio_combustible SET precio=? " +
                            "WHERE id_combustible=(SELECT id_combustible FROM combustible WHERE nombre=?) " +
                            "AND id_ubicacion=?",
                    new Object[]{precio, tipo, idUbicacion}
            );

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
