package co.edu.unipiloto.scrumbacklog;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class SalidasActivity extends AppCompatActivity {
    // XML
    TextView txtInventarioDisponible;
    Spinner spTipoCombustible, spCiudad, spZona;
    EditText etSalida;
    Button btnRetirar, btnVolver;
    ListView listHistorial;

    // Base Datos
    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;

    ArrayList<String> historial = new ArrayList<>();
    ArrayAdapter<String> adapterHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salidas);
        // Base Datos
        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();
        // XML
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
        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ciudades);
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapterCiudad);

        // Spinner Zona
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String ciudad = spCiudad.getSelectedItem().toString();
                ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);
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
            double precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

            double inventarioActual = inventarioDAO.obtenerInventario(tipo, ciudad, zona);
            if (galones > inventarioActual) {
                Toast.makeText(this, "Inventario insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            boolean resultado = movimientoDAO.registrarSalida(tipo, galones, precio, fecha, ciudad, zona);

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

        double inventario = inventarioDAO.obtenerInventario(tipo, ciudad, zona);
        txtInventarioDisponible.setText(inventario + " galones disponibles");
    }
}