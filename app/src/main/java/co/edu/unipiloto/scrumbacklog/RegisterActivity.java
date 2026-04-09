package co.edu.unipiloto.scrumbacklog;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etUsuario, etCorreo, etDireccion, etPassword, etConfirm, etFecha;
    private RadioGroup rgGenero;
    private Spinner spinnerRol;
    private Button btnRegistrar, btnVolver;

    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ✅ CORRECCIÓN CLAVE: crear DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        usuarioDAO = new UsuarioDAO(dbHelper);

        etNombre = findViewById(R.id.etNombre);
        etUsuario = findViewById(R.id.etUsuario);
        etCorreo = findViewById(R.id.etCorreo);
        etDireccion = findViewById(R.id.etDireccion);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etPasswordConfirm);
        etFecha = findViewById(R.id.etFecha);
        rgGenero = findViewById(R.id.rgGenero);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver);
        String[] roles = {"Administrador","Distribuidor", "Vendedor", "Usuario"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> registrar());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrar() {

        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        // Validación básica
        if (nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty() ||
                direccion.isEmpty() || password.isEmpty() || confirm.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            etConfirm.setError("Contraseñas no coinciden");
            return;
        }

        if (!esMayorDeEdad(fecha)) {
            Toast.makeText(this, "Debe ser mayor de edad", Toast.LENGTH_SHORT).show();
            return;
        }

        int generoId = rgGenero.getCheckedRadioButtonId();
        if (generoId == -1) {
            Toast.makeText(this, "Seleccione género", Toast.LENGTH_SHORT).show();
            return;
        }

        String genero = ((RadioButton) findViewById(generoId)).getText().toString();

        // Coordenadas por defecto (puedes luego usar GPS)
        double lat = 4.6097;
        double lon = -74.0817;

        Usuario u = new Usuario(
                nombre, usuario, correo, direccion,
                password, rol,
                fecha, genero,
                lat, lon,
                0, generarCodigo() // ✅ usar método correcto
        );

        long res = usuarioDAO.insertarUsuario(u);

        if (res > 0) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error en registro", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean esMayorDeEdad(String fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date f = sdf.parse(fecha);

            Calendar hoy = Calendar.getInstance();
            Calendar nac = Calendar.getInstance();
            nac.setTime(f);

            int edad = hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR);

            // Ajuste por mes/día (más preciso)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nac.get(Calendar.DAY_OF_YEAR)) {
                edad--;
            }

            return edad >= 18;

        } catch (Exception e) {
            return false;
        }
    }

    private String generarCodigo() {
        Random r = new Random();
        return String.valueOf(100000 + r.nextInt(900000));
    }
}