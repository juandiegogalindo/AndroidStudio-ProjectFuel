package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;

public class SalidasActivity extends AppCompatActivity {

    TextView txtInventarioDisponible;
    Spinner spTipoCombustible, spCiudad, spZona;
    EditText etSalida;
    Button btnRetirar, btnVolver;
    ListView listHistorial;

    DatabaseHelper dbHelper;

    ArrayList<String> historial = new ArrayList<>();
    ArrayAdapter<String> adapterHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salidas);

        dbHelper = new DatabaseHelper(this);

        txtInventarioDisponible = findViewById(R.id.txtInventarioDisponible);
        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        etSalida = findViewById(R.id.etSalida);
        btnRetirar = findViewById(R.id.btnRetirar);
        btnVolver = findViewById(R.id.btnVolver);
        listHistorial = findViewById(R.id.listHistorial);

        // Spinner Combustible
        String[] tipos = {"Corriente", "Extra", "Diesel"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapterTipo);

        // Spinner Ciudad
        ArrayList<String> ciudades = dbHelper.obtenerCiudades();
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ciudades);
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapterCiudad);

        // Spinner Zona
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String ciudad = spCiudad.getSelectedItem().toString();
                ArrayList<String> zonas = dbHelper.obtenerZonas(ciudad);
                ArrayAdapter<String> adapterZona = new ArrayAdapter<>(SalidasActivity.this,
                        android.R.layout.simple_spinner_item, zonas);
                adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spZona.setAdapter(adapterZona);
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spTipoCombustible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spZona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Historial
        adapterHistorial = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, historial);
        listHistorial.setAdapter(adapterHistorial);

        btnRetirar.setOnClickListener(view -> {
            String cantidadTexto = etSalida.getText().toString().trim();
            if (cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double galones = Double.parseDouble(cantidadTexto);
            String tipo = spTipoCombustible.getSelectedItem().toString();
            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();
            double precio = dbHelper.obtenerPrecioZona(tipo, ciudad, zona);

            double inventarioActual = dbHelper.obtenerInventario(tipo, ciudad, zona);
            if (galones > inventarioActual) {
                Toast.makeText(this, "Inventario insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            boolean resultado = dbHelper.registrarSalida(tipo, galones, precio, fecha, ciudad, zona);

            if (resultado) {
                double total = galones * precio;
                String registro = fecha + " | " + tipo + " | " + ciudad + "/" + zona +
                        " | " + galones + " gal | $" + total;
                historial.add(0, registro);
                adapterHistorial.notifyDataSetChanged();
                etSalida.setText("");
                actualizarInventarioUI();
                Toast.makeText(this, "Salida registrada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizarInventarioUI() {
        String tipo = spTipoCombustible.getSelectedItem().toString();
        String ciudad = spCiudad.getSelectedItem().toString();
        String zona = spZona.getSelectedItem().toString();

        double inventario = dbHelper.obtenerInventario(tipo, ciudad, zona);
        txtInventarioDisponible.setText(inventario + " galones disponibles");
    }
}