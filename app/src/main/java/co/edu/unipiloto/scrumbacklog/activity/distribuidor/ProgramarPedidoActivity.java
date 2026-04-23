package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class ProgramarPedidoActivity extends AppCompatActivity {

    private EditText etUbicacion, etCombustible, etCantidad, etFecha;
    private Button btnGuardar, btnFecha, btnVolver;
    private Spinner spUbicacion, spCombustible;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private PedidoDAO pedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programar_pedido);

        // Referencias UI
        spUbicacion = findViewById(R.id.spUbicacion);
        spCombustible = findViewById(R.id.spCombustible);

        etCantidad = findViewById(R.id.etCantidad);
        etFecha = findViewById(R.id.etFecha);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnFecha = findViewById(R.id.btnSeleccionarFecha);
        btnVolver = findViewById(R.id.btnVolver);


        // Base de datos
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        pedidoDAO = new PedidoDAO(db);
        cargarSpinners();

        // Selector de fecha
        btnFecha.setOnClickListener(v -> mostrarDatePicker());

        // Guardar pedido
        btnGuardar.setOnClickListener(v -> guardarPedido());
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ProgramarPedidoActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void cargarSpinners() {

        // Ubicaciones
        String[] ubicaciones = {
                "Estación Suba",
                "Estación Engativá",
                "Estación Centro"
        };

        ArrayAdapter<String> adapterUbicacion = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ubicaciones);
        adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUbicacion.setAdapter(adapterUbicacion);

        // Combustibles
        String[] combustibles = {
                "Corriente",
                "Extra",
                "Diesel"
        };

        ArrayAdapter<String> adapterCombustible = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, combustibles);
        adapterCombustible.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCombustible.setAdapter(adapterCombustible);
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {

                    String fecha = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    etFecha.setText(fecha);

                }, year, month, day);

        datePickerDialog.show();
    }

    private void guardarPedido() {
        try {
            int idUbicacion = spUbicacion.getSelectedItemPosition() + 1;
            int idCombustible = spCombustible.getSelectedItemPosition() + 1;
            double cantidad = Double.parseDouble(etCantidad.getText().toString());
            String fecha = etFecha.getText().toString();

            if (fecha.isEmpty()) {
                Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            pedidoDAO.crearPedido(idUbicacion, idCombustible, cantidad, fecha);

            Toast.makeText(this, "Pedido programado correctamente", Toast.LENGTH_LONG).show();

            limpiarCampos();

        } catch (Exception e) {
            Toast.makeText(this, "Error: Verifique los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etUbicacion.setText("");
        etCombustible.setText("");
        etCantidad.setText("");
        etFecha.setText("");
    }
}