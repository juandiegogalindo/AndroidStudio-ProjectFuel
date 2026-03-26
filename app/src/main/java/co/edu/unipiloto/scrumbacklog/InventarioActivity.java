package co.edu.unipiloto.scrumbacklog;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class InventarioActivity extends AppCompatActivity {

    Spinner spCombustible, spCiudad, spZona;
    EditText etCantidad;
    Button btnAgregar, btnVolver;

    TextView txtInventarioTotal, txtInventarioDiesel, txtInventarioCorriente, txtInventarioExtra;

    // Base Datos
    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        // BASE DE DATOS
        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();

        // XML
        spCombustible = findViewById(R.id.spCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);

        etCantidad = findViewById(R.id.etCantidad);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnVolver = findViewById(R.id.btnVolver);

        txtInventarioTotal = findViewById(R.id.txtInventarioTotal);
        txtInventarioDiesel = findViewById(R.id.txtInventarioDiesel);
        txtInventarioCorriente = findViewById(R.id.txtInventarioCorriente);
        txtInventarioExtra = findViewById(R.id.txtInventarioExtra);

        cargarCombustiblesSpinner();
        cargarCiudadesSpinner();

        // 🔹 Cambio de ciudad → actualizar zonas + inventario
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ciudad = spCiudad.getSelectedItem().toString();
                cargarZonasSpinner(ciudad);
                actualizarInventariosUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAgregar.setOnClickListener(view -> registrarEntrada());

        btnVolver.setOnClickListener(view -> finish());
    }

    // 🔹 REGISTRO DE ENTRADA (corregido)
    private void registrarEntrada() {

        if (spCombustible.getSelectedItem() == null ||
                spCiudad.getSelectedItem() == null ||
                spZona.getSelectedItem() == null) {

            Toast.makeText(this, "Seleccione todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadTexto = etCantidad.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;

        try {
            cantidad = Double.parseDouble(cantidadTexto);
        } catch (Exception e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = spCombustible.getSelectedItem().toString();
        String ciudad = spCiudad.getSelectedItem().toString();
        String zona = spZona.getSelectedItem().toString();

        double precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        boolean resultado = movimientoDAO.registrarEntrada(tipo, cantidad, precio, fecha, ciudad, zona);

        if (resultado) {
            actualizarInventariosUI();
            etCantidad.setText("");
            Toast.makeText(this, "Entrada registrada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al registrar entrada", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCombustiblesSpinner() {
        ArrayList<String> combustibles = combustibleDAO.obtenerCombustibles();

        if (combustibles == null || combustibles.isEmpty()) {
            combustibles = new ArrayList<>();
            combustibles.add("Sin datos");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, combustibles);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombustible.setAdapter(adapter);
    }

    private void cargarCiudadesSpinner() {
        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();

        if (ciudades == null || ciudades.isEmpty()) {
            ciudades = new ArrayList<>();
            ciudades.add("Sin datos");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ciudades);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapter);
    }

    private void cargarZonasSpinner(String ciudad) {
        ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);

        if (zonas == null || zonas.isEmpty()) {
            zonas = new ArrayList<>();
            zonas.add("Sin zonas");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, zonas);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZona.setAdapter(adapter);
    }

    // 🔹 ACTUALIZACIÓN SEGURA DE INVENTARIO
    private void actualizarInventariosUI() {

        if (spCiudad.getSelectedItem() == null) return;

        String ciudad = spCiudad.getSelectedItem().toString();

        double diesel = inventarioDAO.obtenerInventarioTotalPorCiudad("Diesel", ciudad);
        double corriente = inventarioDAO.obtenerInventarioTotalPorCiudad("Corriente", ciudad);
        double extra = inventarioDAO.obtenerInventarioTotalPorCiudad("Extra", ciudad);
        double total = diesel + corriente + extra;

        txtInventarioDiesel.setText("Diesel: " + diesel + " gal");
        txtInventarioCorriente.setText("Corriente: " + corriente + " gal");
        txtInventarioExtra.setText("Extra: " + extra + " gal");
        txtInventarioTotal.setText("Total: " + total + " gal");
    }
}