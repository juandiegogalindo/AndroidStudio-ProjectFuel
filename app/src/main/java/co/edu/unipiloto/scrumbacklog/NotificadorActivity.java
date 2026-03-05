package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;

public class NotificadorActivity extends AppCompatActivity {

    Spinner spCiudad, spZona;
    Button btnVerificar, btnVolver;
    TextView txtAlerta;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificador);

        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnVerificar = findViewById(R.id.btnVerificar);
        btnVolver = findViewById(R.id.btnVolver);
        txtAlerta = findViewById(R.id.txtAlerta);

        dbHelper = new DatabaseHelper(this);

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

            double diesel = dbHelper.obtenerInventarioPorUbicacionAlerta("Diesel", ciudad, zona);
            double corriente = dbHelper.obtenerInventarioPorUbicacionAlerta("Corriente", ciudad, zona);
            double extra = dbHelper.obtenerInventarioPorUbicacionAlerta("Extra", ciudad, zona);

            String mensaje = "";

            if(diesel < 1000){
                mensaje += "⚠ Diesel en nivel crítico\n";
            }

            if(corriente < 1000){
                mensaje += "⚠ Corriente en nivel crítico\n";
            }

            if(extra < 1000){
                mensaje += "⚠ Extra en nivel crítico\n";
            }

            if(mensaje.isEmpty()){
                mensaje = "Inventario en niveles normales";
            }

            txtAlerta.setText(mensaje);
        });

        btnVolver.setOnClickListener(view -> finish());
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
}