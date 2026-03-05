package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InventarioActivity extends AppCompatActivity {

    Spinner spCombustible;
    EditText etCantidad;
    Button btnAgregar, btnVolver;

    TextView txtInventarioTotal;
    TextView txtInventarioDiesel;
    TextView txtInventarioCorriente;
    TextView txtInventarioExtra;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        dbHelper = new DatabaseHelper(this);

        spCombustible = findViewById(R.id.spCombustible);
        etCantidad = findViewById(R.id.etCantidad);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnVolver = findViewById(R.id.btnVolver);

        txtInventarioTotal = findViewById(R.id.txtInventarioTotal);
        txtInventarioDiesel = findViewById(R.id.txtInventarioDiesel);
        txtInventarioCorriente = findViewById(R.id.txtInventarioCorriente);
        txtInventarioExtra = findViewById(R.id.txtInventarioExtra);

        String[] combustibles = {"Diesel", "Corriente", "Extra"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                combustibles
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombustible.setAdapter(adapter);

        actualizarInventariosUI();

        btnAgregar.setOnClickListener(view -> {

            String cantidadTexto = etCantidad.getText().toString().trim();

            if (cantidadTexto.isEmpty()) {
                Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(cantidadTexto);
            String tipo = spCombustible.getSelectedItem().toString();

            double precio = dbHelper.obtenerPrecio(tipo);

            String fecha = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            boolean resultado = dbHelper.registrarEntrada(
                    tipo,
                    cantidad,
                    precio,
                    fecha
            );

            if (resultado) {

                actualizarInventariosUI();

                etCantidad.setText("");

                Toast.makeText(
                        this,
                        "Entrada registrada correctamente",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnVolver.setOnClickListener(view -> finish());
    }

    private void actualizarInventariosUI() {

        double diesel = dbHelper.obtenerInventario("Diesel");
        double corriente = dbHelper.obtenerInventario("Corriente");
        double extra = dbHelper.obtenerInventario("Extra");

        double total = diesel + corriente + extra;

        txtInventarioDiesel.setText("Diesel: " + diesel + " gal");
        txtInventarioCorriente.setText("Corriente: " + corriente + " gal");
        txtInventarioExtra.setText("Extra: " + extra + " gal");
        txtInventarioTotal.setText("Total: " + total + " gal");
    }
}