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

        // 🔥 Validación importante (evita crash si DB viene vacía)
        ubicaciones = db.obtenerCiudades();
        if (ubicaciones == null || ubicaciones.isEmpty()) {
            ubicaciones = new ArrayList<>();
            ubicaciones.add("Sin datos");
        }

        ArrayAdapter<String> adapterUbic = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ubicaciones);
        spFiltroUbicacion.setAdapter(adapterUbic);

        refrescarVista();

        spFiltroCombustible.setOnItemSelectedListener(new SimpleItemSelected(this::refrescarVista));
        spFiltroUbicacion.setOnItemSelectedListener(new SimpleItemSelected(this::refrescarVista));

        btnVolver.setOnClickListener(v -> finish());
    }

    private void refrescarVista() {
        if (spFiltroUbicacion.getSelectedItem() == null) return;

        mostrarInventario();
        mostrarHistorial();
    }

    private void mostrarInventario() {
        layoutInventario.removeAllViews();

        String filtroCombustible = spFiltroCombustible.getSelectedItem().toString();
        String filtroUbicacion = spFiltroUbicacion.getSelectedItem().toString();

        String[] combustibles = filtroCombustible.equals("Todos")
                ? new String[]{"Corriente", "Extra", "Diesel"}
                : new String[]{filtroCombustible};

        // 🔹 NIVEL GENERAL (POR CIUDAD)
        for (String tipo : combustibles) {

            double cantidad = db.obtenerInventarioTotalPorCiudad(tipo, filtroUbicacion);

            TextView tv = new TextView(this);
            tv.setText(tipo + ": " + cantidad + " galones");
            tv.setTextSize(16f);

            ProgressBar pb = crearProgressBar(cantidad, 40);

            layoutInventario.addView(tv);
            layoutInventario.addView(pb);
        }

        // 🔹 NIVEL POR ZONA
        ArrayList<String> zonas = db.obtenerZonas(filtroUbicacion);

        if (zonas == null || zonas.isEmpty()) return;

        for (String zona : zonas) {

            TextView tvZonaHeader = new TextView(this);
            tvZonaHeader.setText("Zona: " + zona);
            tvZonaHeader.setTextSize(16f);
            tvZonaHeader.setPadding(0, 12, 0, 4);

            layoutInventario.addView(tvZonaHeader);

            for (String tipo : combustibles) {

                double cantidad = db.obtenerInventario(tipo, filtroUbicacion, zona);

                TextView tv = new TextView(this);
                tv.setText(tipo + ": " + cantidad + " galones");
                tv.setTextSize(14f);
                tv.setPadding(16, 2, 0, 2);

                ProgressBar pb = crearProgressBar(cantidad, 30);

                layoutInventario.addView(tv);
                layoutInventario.addView(pb);
            }
        }
    }

    private ProgressBar crearProgressBar(double cantidad, int alto) {
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(10000);
        pb.setProgress((int) cantidad);
        pb.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, alto
        ));

        if (cantidad >= 5000)
            pb.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        else if (cantidad >= 2000)
            pb.getProgressDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        else
            pb.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        return pb;
    }

    private void mostrarHistorial() {
        layoutHistorial.removeAllViews();

        if (spFiltroUbicacion.getSelectedItem() == null) return;

        String ubicacion = spFiltroUbicacion.getSelectedItem().toString();

        ArrayList<String> movimientos = db.obtenerMovimientosPorUbicacion(ubicacion);

        if (movimientos == null) return;

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