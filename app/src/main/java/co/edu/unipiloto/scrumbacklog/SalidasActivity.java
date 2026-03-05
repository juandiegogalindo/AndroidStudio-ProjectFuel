package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SalidasActivity extends AppCompatActivity {

    TextView txtInventarioDisponible;
    Spinner spTipoCombustible;
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
        etSalida = findViewById(R.id.etSalida);
        btnRetirar = findViewById(R.id.btnRetirar);
        btnVolver = findViewById(R.id.btnVolver);
        listHistorial = findViewById(R.id.listHistorial);

        String[] tipos = {"Corriente", "Extra", "Diesel"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapter);

        // Mostrar inventario cuando cambia el tipo
        spTipoCombustible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                actualizarInventarioUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapterHistorial = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                historial
        );

        listHistorial.setAdapter(adapterHistorial);

        btnRetirar.setOnClickListener(view -> {

            String cantidadTexto = etSalida.getText().toString().trim();

            if (cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double galones = Double.parseDouble(cantidadTexto);
            String tipo = spTipoCombustible.getSelectedItem().toString();
            double precio = obtenerPrecio(tipo);

            // VALIDAR INVENTARIO REAL DESDE BD
            double inventarioActual = dbHelper.obtenerInventario(tipo);

            if (galones > inventarioActual) {
                Toast.makeText(
                        this,
                        "Inventario insuficiente",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String fecha = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            boolean resultado = dbHelper.registrarSalida(
                    tipo,
                    galones,
                    precio,
                    fecha
            );

            if (resultado) {

                double total = galones * precio;

                String registro = fecha +
                        " | " + tipo +
                        " | " + galones + " gal" +
                        " | $" + total;

                historial.add(0, registro);
                adapterHistorial.notifyDataSetChanged();

                etSalida.setText("");

                actualizarInventarioUI(); // ACTUALIZA INVENTARIO EN PANTALLA

                Toast.makeText(
                        this,
                        "Salida registrada correctamente",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Error al registrar",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizarInventarioUI() {

        String tipo = spTipoCombustible.getSelectedItem().toString();
        double inventario = dbHelper.obtenerInventario(tipo);

        txtInventarioDisponible.setText(inventario + " galones disponibles");
    }

    private double obtenerPrecio(String tipo) {

        switch (tipo) {
            case "Corriente":
                return 15991;
            case "Extra":
                return 22673;
            case "Diesel":
                return 11276;
            default:
                return 0;
        }
    }
}