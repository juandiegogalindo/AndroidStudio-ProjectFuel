package co.edu.unipiloto.scrumbacklog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.unipiloto.scrumbacklog.Spinner.SimpleItemSelected;
import co.edu.unipiloto.scrumbacklog.database.DAOFactory;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class ReguladorPreciosActivity extends AppCompatActivity {
    // XML
    private Spinner spCiudad, spZona, spCombustible;
    private TextView txtPrecioActual;
    private EditText etNuevoPrecio;
    private Button btnActualizarPrecio, btnVolver;

    // Base Datos
    DatabaseHelper db;
    DAOFactory factory;
    CombustibleDAO combustibleDAO;
    InventarioDAO inventarioDAO;
    MovimientoDAO movimientoDAO;
    PrecioDAO precioDAO;
    UbicacionDAO ubicacionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulador_precios);
        // Base Datos
        db = new DatabaseHelper(this);
        factory = new DAOFactory(this);

        inventarioDAO = factory.getInventarioDAO();
        combustibleDAO = factory.getCombustibleDAO();
        movimientoDAO = factory.getMovimientoDAO();
        precioDAO = factory.getPrecioDAO();
        ubicacionDAO = factory.getUbicacionDAO();

        // XML
        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        spCombustible = findViewById(R.id.spCombustible);

        txtPrecioActual = findViewById(R.id.txtPrecioActual);

        etNuevoPrecio = findViewById(R.id.etNuevoPrecio);

        btnActualizarPrecio = findViewById(R.id.btnActualizarPrecio);
        btnVolver = findViewById(R.id.btnVolver);

        cargarSpinners();
        mostrarPrecioActual();

        // Listener reutilizable para actualizar el precio al cambiar cualquier Spinner
        spCiudad.setOnItemSelectedListener(new SimpleItemSelected(this::mostrarPrecioActual));
        spZona.setOnItemSelectedListener(new SimpleItemSelected(this::mostrarPrecioActual));
        spCombustible.setOnItemSelectedListener(new SimpleItemSelected(this::mostrarPrecioActual));

        btnActualizarPrecio.setOnClickListener(v -> actualizarPrecio());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarSpinners() {
        String[] ciudades = {"Bogota"};
        String[] zonas = {"Suba","Engativa","Centro"};
        String[] combustibles = {"Corriente","Extra","Diesel"};

        spCiudad.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ciudades));
        spZona.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, zonas));
        spCombustible.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, combustibles));
    }

    private void mostrarPrecioActual() {
        String ciudad = spCiudad.getSelectedItem().toString();
        String zona = spZona.getSelectedItem().toString();
        String combustible = spCombustible.getSelectedItem().toString();

        double precio = precioDAO.obtenerPrecioZona(combustible, ciudad, zona);

        if(precio > 0){
            txtPrecioActual.setText("Precio actual: $" + precio);
        } else {
            txtPrecioActual.setText("Precio no disponible");
        }
    }

    private void actualizarPrecio() {
        String ciudad = spCiudad.getSelectedItem().toString();
        String zona = spZona.getSelectedItem().toString();
        String combustible = spCombustible.getSelectedItem().toString();
        String nuevoPrecioStr = etNuevoPrecio.getText().toString();

        if (nuevoPrecioStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el nuevo precio", Toast.LENGTH_SHORT).show();
            return;
        }

        double nuevoPrecio;
        try {
            nuevoPrecio = Double.parseDouble(nuevoPrecioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (actualizarPrecioDB(combustible, ciudad, zona, nuevoPrecio)) {
            Toast.makeText(this, "Precio actualizado correctamente", Toast.LENGTH_SHORT).show();
            mostrarPrecioActual();
            etNuevoPrecio.setText("");
        } else {
            Toast.makeText(this, "Error al actualizar precio", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean actualizarPrecioDB(String tipo, String ciudad, String zona, double precio) {
        try {
            android.database.sqlite.SQLiteDatabase dbSQL = db.getWritableDatabase();
            dbSQL.execSQL(
                    "UPDATE precio_combustible SET precio=? " +
                            "WHERE id_combustible=(SELECT id_combustible FROM combustible WHERE nombre=?) " +
                            "AND id_ubicacion=(SELECT id_ubicacion FROM ubicacion WHERE ciudad=? AND zona=?)",
                    new Object[]{precio, tipo, ciudad, zona});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}