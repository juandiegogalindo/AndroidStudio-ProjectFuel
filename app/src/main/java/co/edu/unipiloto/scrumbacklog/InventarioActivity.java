package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;

public class InventarioActivity extends AppCompatActivity {

    Spinner spCombustible, spCiudad, spZona;
    EditText etCantidad;
    Button btnAgregar, btnVolver;

    TextView txtInventarioTotal, txtInventarioDiesel, txtInventarioCorriente, txtInventarioExtra;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        dbHelper = new DatabaseHelper(this);

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

        // Actualizar zonas según ciudad seleccionada
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                cargarZonasSpinner(spCiudad.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        actualizarInventariosUI();

        btnAgregar.setOnClickListener(view -> {
            String cantidadTexto = etCantidad.getText().toString().trim();
            if (cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(cantidadTexto);
            String tipo = spCombustible.getSelectedItem().toString();
            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();
            double precio = dbHelper.obtenerPrecioZona(tipo, ciudad, zona);
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            boolean resultado = dbHelper.registrarEntrada(tipo, cantidad, precio, fecha, ciudad, zona);

            if (resultado) {
                actualizarInventariosUI();
                etCantidad.setText("");
                Toast.makeText(this, "Entrada registrada correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(view -> finish());
    }

    private void cargarCombustiblesSpinner() {
        ArrayList<String> combustibles = dbHelper.obtenerCombustibles();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, combustibles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombustible.setAdapter(adapter);
    }

    private void cargarCiudadesSpinner() {
        ArrayList<String> ciudades = dbHelper.obtenerCiudades();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ciudades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapter);
    }

    private void cargarZonasSpinner(String ciudad) {
        ArrayList<String> zonas = dbHelper.obtenerZonas(ciudad);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, zonas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZona.setAdapter(adapter);
    }

    private void actualizarInventariosUI() {
        String ciudad = spCiudad.getSelectedItem() != null ? spCiudad.getSelectedItem().toString() : "";
        double diesel = dbHelper.obtenerInventarioPorUbicacion("Diesel", ciudad);
        double corriente = dbHelper.obtenerInventarioPorUbicacion("Corriente", ciudad);
        double extra = dbHelper.obtenerInventarioPorUbicacion("Extra", ciudad);
        double total = diesel + corriente + extra;

        txtInventarioDiesel.setText("Diesel: " + diesel + " gal");
        txtInventarioCorriente.setText("Corriente: " + corriente + " gal");
        txtInventarioExtra.setText("Extra: " + extra + " gal");
        txtInventarioTotal.setText("Total: " + total + " gal");
    }
}