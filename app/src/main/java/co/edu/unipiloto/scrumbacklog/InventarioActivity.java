package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InventarioActivity extends AppCompatActivity {

    Spinner spCombustible;
    EditText etCantidad;
    Button btnAgregar;
    Button btnVolver;
    TextView txtInventarioTotal;
    TextView txtInventarioDiesel;
    TextView txtInventarioCorriente;
    TextView txtInventarioExtra;



    double inventarioTotal = 0;
    double inventarioDiesel = 0;
    double inventarioCorriente = 0;
    double inventarioExtra = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

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

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipoCombustible = spCombustible.getSelectedItem().toString();
                String cantidadTexto = etCantidad.getText().toString();

                if (!cantidadTexto.isEmpty()) {
                    double cantidad = Double.parseDouble(cantidadTexto);

                    if(tipoCombustible.equals("Diesel")){
                        inventarioDiesel += cantidad;
                        txtInventarioDiesel.setText("Inventario Diesel: " + inventarioDiesel);
                    } else if (tipoCombustible.equals("Gasolina")) {
                        inventarioCorriente += cantidad;
                        txtInventarioCorriente.setText("Inventario Corriente: " + inventarioCorriente);
                    } else if (tipoCombustible.equals("Extra")){
                        inventarioExtra += cantidad;
                        txtInventarioExtra.setText("Inventario Extra: " + inventarioExtra);
                    }
                    inventarioTotal += cantidad;
                    txtInventarioTotal.setText("Inventario Total: " + inventarioTotal + " galones");

                    etCantidad.setText("");
                }
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}