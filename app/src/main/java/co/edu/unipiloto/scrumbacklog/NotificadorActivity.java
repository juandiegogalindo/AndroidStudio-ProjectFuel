package co.edu.unipiloto.scrumbacklog;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class NotificadorActivity extends AppCompatActivity {

    Spinner spCiudad, spZona;
    Button btnVerificar, btnVolver;
    TextView txtAlerta;

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
        setContentView(R.layout.activity_notificador);
        // XML
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnVerificar = findViewById(R.id.btnVerificar);
        btnVolver = findViewById(R.id.btnVolver);
        txtAlerta = findViewById(R.id.txtAlerta);

        // BASE DATOS
        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();

        cargarCiudadesSpinner();

        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                cargarZonasSpinner(spCiudad.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnVerificar.setOnClickListener(view -> {

            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();

            // ✅ MÉTODO CORRECTO
            double diesel = inventarioDAO.obtenerInventario("Diesel", ciudad, zona);
            double corriente = inventarioDAO.obtenerInventario("Corriente", ciudad, zona);
            double extra = inventarioDAO.obtenerInventario("Extra", ciudad, zona);

            String mensaje = "";

            if (diesel < 1000) {
                mensaje += "⚠ Diesel en nivel crítico\n";
            }

            if (corriente < 1000) {
                mensaje += "⚠ Corriente en nivel crítico\n";
            }

            if (extra < 1000) {
                mensaje += "⚠ Extra en nivel crítico\n";
            }

            if (mensaje.isEmpty()) {
                mensaje = "Inventario en niveles normales";
            }

            txtAlerta.setText(mensaje);
        });

        btnVolver.setOnClickListener(view -> finish());
    }

    private void cargarCiudadesSpinner() {
        ArrayList<String> ciudades = ubicacionDAO.obtenerCiudades();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ciudades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(adapter);
    }

    private void cargarZonasSpinner(String ciudad) {
        ArrayList<String> zonas = ubicacionDAO.obtenerZonas(ciudad);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, zonas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spZona.setAdapter(adapter);
    }
}