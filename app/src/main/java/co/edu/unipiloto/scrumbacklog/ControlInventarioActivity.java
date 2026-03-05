package co.edu.unipiloto.scrumbacklog;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.unipiloto.scrumbacklog.Spinner.SimpleItemSelected;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;

public class ControlInventarioActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private LinearLayout layoutInventario, layoutHistorial;
    private Spinner spFiltroCombustible, spFiltroUbicacion;
    private Button btnVolver;

    private ArrayList<String> ubicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_inventario);

        db = new DatabaseHelper(this);

        btnVolver = findViewById(R.id.btnVolver);
        layoutInventario = findViewById(R.id.layoutInventario);
        layoutHistorial = findViewById(R.id.layoutHistorial);
        spFiltroCombustible = findViewById(R.id.spFiltroCombustible);
        spFiltroUbicacion = findViewById(R.id.spFiltroUbicacion);

        String[] combustibles = {"Todos", "Corriente", "Extra", "Diesel"};
        spFiltroCombustible.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, combustibles));

        // Spinner de ubicaciones dinámico desde la DB
        ubicaciones = db.obtenerCiudades();
        ArrayAdapter<String> adapterUbic = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ubicaciones);
        spFiltroUbicacion.setAdapter(adapterUbic);

        refrescarVista();

        // Filtrar al cambiar spinner
        spFiltroCombustible.setOnItemSelectedListener(new SimpleItemSelected(this::refrescarVista));
        spFiltroUbicacion.setOnItemSelectedListener(new SimpleItemSelected(this::refrescarVista));

        btnVolver.setOnClickListener(v -> finish());
    }

    private void refrescarVista() {
        mostrarInventario();
        mostrarHistorial();
    }

    private void mostrarInventario() {
        layoutInventario.removeAllViews();

        String filtroCombustible = spFiltroCombustible.getSelectedItem().toString();
        String filtroUbicacion = spFiltroUbicacion.getSelectedItem().toString();

        String[] combustibles = filtroCombustible.equals("Todos") ? new String[]{"Corriente", "Extra", "Diesel"} : new String[]{filtroCombustible};

        for (String tipo : combustibles) {
            double cantidad = db.obtenerInventarioPorUbicacion(tipo, filtroUbicacion);

            TextView tv = new TextView(this);
            tv.setText(tipo + ": " + cantidad + " galones");
            tv.setTextSize(16f);

            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(10000);
            pb.setProgress((int) cantidad);
            pb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40));

            if (cantidad >= 5000) pb.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            else if (cantidad >= 2000) pb.getProgressDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            else pb.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

            layoutInventario.addView(tv);
            layoutInventario.addView(pb);
        }
    }

    private void mostrarHistorial() {
        layoutHistorial.removeAllViews();
        String ubicacion = spFiltroUbicacion.getSelectedItem().toString();

        ArrayList<String> movimientos = db.obtenerMovimientosPorUbicacion(ubicacion);

        int count = 0;
        for (String mov : movimientos) {
            if (count >= 10) break;
            TextView tv = new TextView(this);
            tv.setText(mov);
            layoutHistorial.addView(tv);
            count++;
        }
    }
}