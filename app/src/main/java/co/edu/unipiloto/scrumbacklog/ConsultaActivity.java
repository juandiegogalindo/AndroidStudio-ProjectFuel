package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;

public class ConsultaActivity extends AppCompatActivity {

    Spinner spTipoCombustible, spCiudad, spZona;
    Button btnCalcular, btnCalcularGalones, btnVolver;
    TextView txtResultado, txtResultadoGalones;
    EditText etGalones;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        dbHelper = new DatabaseHelper(this);

        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);

        btnCalcular = findViewById(R.id.btnCalcular);
        btnVolver = findViewById(R.id.btnVolver);
        btnCalcularGalones = findViewById(R.id.calcularGalones);

        etGalones = findViewById(R.id.etGalones);
        txtResultadoGalones = findViewById(R.id.txtResultadoGalones);
        txtResultado = findViewById(R.id.txtResultado);

        // ================= COMBUSTIBLES =================
        ArrayAdapter<String> adapterComb = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dbHelper.obtenerCombustibles()
        );
        adapterComb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapterComb);

        // ================= CIUDADES =================
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dbHelper.obtenerCiudades()
        );
        spCiudad.setAdapter(adapterCiudad);

        // ================= ZONAS DINÁMICAS =================
        spCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String ciudad = spCiudad.getSelectedItem().toString();

                ArrayAdapter<String> adapterZona = new ArrayAdapter<>(
                        ConsultaActivity.this,
                        android.R.layout.simple_spinner_item,
                        dbHelper.obtenerZonas(ciudad)
                );

                spZona.setAdapter(adapterZona);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ================= BOTÓN PRECIO =================
        btnCalcular.setOnClickListener(view -> {

            String tipo = spTipoCombustible.getSelectedItem().toString();
            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();

            double precio = dbHelper.obtenerPrecioZona(tipo, ciudad, zona);

            txtResultado.setText("Precio: $" + precio);
        });

        // ================= BOTÓN TOTAL =================
        btnCalcularGalones.setOnClickListener(view -> {

            String galonesTexto = etGalones.getText().toString().trim();

            if (galonesTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese galones", Toast.LENGTH_SHORT).show();
                return;
            }

            double galones = Double.parseDouble(galonesTexto);

            String tipo = spTipoCombustible.getSelectedItem().toString();
            String ciudad = spCiudad.getSelectedItem().toString();
            String zona = spZona.getSelectedItem().toString();

            double precio = dbHelper.obtenerPrecioZona(tipo, ciudad, zona);
            double total = galones * precio;

            txtResultadoGalones.setText("Total: $" + total);
        });

        btnVolver.setOnClickListener(view -> finish());
    }
}