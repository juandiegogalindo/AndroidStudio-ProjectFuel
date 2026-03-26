package co.edu.unipiloto.scrumbacklog;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class ConsultaActivity extends AppCompatActivity {
    // XML
    Spinner spTipoCombustible, spCiudad, spZona;
    Button btnCalcular, btnCalcularGalones, btnVolver;
    TextView txtResultado, txtResultadoGalones;
    EditText etGalones;

    //Base Datos
    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        // Base Datos
        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();

        // XML
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
                combustibleDAO.obtenerCombustibles()
        );
        adapterComb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCombustible.setAdapter(adapterComb);

        // ================= CIUDADES =================
        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ubicacionDAO.obtenerCiudades()
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
                        ubicacionDAO.obtenerZonas(ciudad)
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

            double precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);

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

            double precio = precioDAO.obtenerPrecioZona(tipo, ciudad, zona);
            double total = galones * precio;

            txtResultadoGalones.setText("Total: $" + total);
        });

        btnVolver.setOnClickListener(view -> finish());
    }
}