package co.edu.unipiloto.scrumbacklog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnConsulta, btnInventario, btnSalidas, btnNotificador,
            btnRegulador, btnControl, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias
        btnConsulta = findViewById(R.id.btnConsulta);
        btnInventario = findViewById(R.id.btnInventario);
        btnSalidas = findViewById(R.id.btnSalidas);
        btnNotificador = findViewById(R.id.btnNotificador);
        btnRegulador = findViewById(R.id.btnRegulador);
        btnControl = findViewById(R.id.btnControl);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // 🔥 CONFIGURAR PERMISOS POR ROL
        configurarAccesoPorRol();

        // Navegación
        btnConsulta.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultaActivity.class)));

        btnInventario.setOnClickListener(v ->
                startActivity(new Intent(this, InventarioActivity.class)));

        btnSalidas.setOnClickListener(v ->
                startActivity(new Intent(this, SalidasActivity.class)));

        btnNotificador.setOnClickListener(v ->
                startActivity(new Intent(this, NotificadorActivity.class)));

        btnRegulador.setOnClickListener(v ->
                startActivity(new Intent(this, ReguladorPreciosActivity.class)));

        btnControl.setOnClickListener(v ->
                startActivity(new Intent(this, ControlInventarioActivity.class)));

        btnCerrarSesion.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // =========================
    // 🔐 CONTROL DE ACCESO POR ROL
    // =========================
    private void configurarAccesoPorRol() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        // 🔒 Primero deshabilitamos TODO
        btnConsulta.setEnabled(false);
        btnInventario.setEnabled(false);
        btnSalidas.setEnabled(false);
        btnNotificador.setEnabled(false);
        btnRegulador.setEnabled(false);
        btnControl.setEnabled(false);

        if (rol == null) return;

        switch (rol) {

            case "admin":
                btnConsulta.setEnabled(true);
                btnInventario.setEnabled(true);
                btnNotificador.setEnabled(true);
                btnControl.setEnabled(true);
                break;

            case "operador": // estación de servicio
                btnConsulta.setEnabled(true);
                btnInventario.setEnabled(true);
                btnSalidas.setEnabled(true);
                btnNotificador.setEnabled(true);
                btnRegulador.setEnabled(true);
                btnControl.setEnabled(true);
                break;

            case "cliente":
                btnConsulta.setEnabled(true);
                break;

            case "distribuidor":
                btnControl.setEnabled(true);
                btnSalidas.setEnabled(true);
                break;
        }
    }
}