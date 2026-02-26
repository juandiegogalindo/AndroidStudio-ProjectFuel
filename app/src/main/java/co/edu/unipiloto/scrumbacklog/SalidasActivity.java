package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.view.View;
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

    double inventarioDisponible = 40000;

    ArrayList<String> historial = new ArrayList<>();
    ArrayAdapter<String> adapterHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salidas);

        txtInventarioDisponible = findViewById(R.id.txtInventarioDisponible);
        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        etSalida = findViewById(R.id.etSalida);
        btnRetirar = findViewById(R.id.btnRetirar);
        btnVolver = findViewById(R.id.btnVolver);
        listHistorial = findViewById(R.id.listHistorial);

        txtInventarioDisponible.setText(inventarioDisponible + " galones");

        String[] tipos = {"Corriente", "Extra", "Diesel"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapter);

        adapterHistorial = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                historial
        );

        listHistorial.setAdapter(adapterHistorial);

        btnRetirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cantidadTexto = etSalida.getText().toString();

                if (!cantidadTexto.isEmpty()) {

                    double galones = Double.parseDouble(cantidadTexto);
                    String tipo = spTipoCombustible.getSelectedItem().toString();

                    double precio = obtenerPrecio(tipo);

                    if (galones <= inventarioDisponible) {

                        inventarioDisponible -= galones;
                        txtInventarioDisponible.setText(inventarioDisponible + " galones");

                        double total = galones * precio;

                        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                                Locale.getDefault()).format(new Date());

                        String registro = fecha +
                                " | " + tipo +
                                " | " + galones + " gal" +
                                " | $" + total;

                        historial.add(0, registro);
                        adapterHistorial.notifyDataSetChanged();

                        etSalida.setText("");

                    } else {
                        Toast.makeText(SalidasActivity.this,
                                "Inventario insuficiente",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnVolver.setOnClickListener(v -> finish());
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