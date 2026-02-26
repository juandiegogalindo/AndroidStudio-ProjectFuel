package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ConsultaActivity extends AppCompatActivity {

    Spinner spTipoCombustible;
    Button btnCalcular;
    Button btnVolver;

    Button btnCalcularGalones;
    TextView txtResultado;
    TextView txtResultadoGalones;;
    EditText etGalones;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        spTipoCombustible = findViewById(R.id.spTipoCombustible);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnVolver = findViewById(R.id.btnVolver);
        btnCalcularGalones = findViewById(R.id.calcularGalones);

        etGalones = findViewById(R.id.etGalones);
        txtResultadoGalones = findViewById(R.id.txtResultadoGalones);
        txtResultado = findViewById(R.id.txtResultado);

        String[] tipos = {"Corriente", "Extra", "Diesel"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapter);

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipoSeleccionado = spTipoCombustible.getSelectedItem().toString();
                double precio = 0;

                if (tipoSeleccionado.equals("Corriente")) {
                    precio = 15.991;
                } else if (tipoSeleccionado.equals("Extra")) {
                    precio = 22.673;
                } else if (tipoSeleccionado.equals("Diesel")) {
                    precio = 11.276;
                }

                txtResultado.setText("Precio: $" + precio);
            }
        });

        btnCalcularGalones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipoSeleccionado = spTipoCombustible.getSelectedItem().toString();
                double galonesDeseados = Integer.parseInt(etGalones.getText().toString());
                double respuestaGalones = 0;

                if (tipoSeleccionado.equals("Corriente")) {
                    respuestaGalones = 15.991 * galonesDeseados;
                } else if (tipoSeleccionado.equals("Extra")) {
                    respuestaGalones = 22.673 * galonesDeseados;
                } else if (tipoSeleccionado.equals("Diesel")) {
                    respuestaGalones = 11.276 * galonesDeseados;
                }
                txtResultadoGalones.setText("Precio: $" + respuestaGalones);
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