package co.edu.unipiloto.scrumbacklog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPassword, etPasswordConfirm;
    private Spinner spinnerRol;
    private Button btnRegistrar, btnVolver;

    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        usuarioDAO = new UsuarioDAO(databaseHelper);

        // Spinner
        String[] roles = {"Distribuidor", "Vendedor", "Usuario"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        // Botón registrar
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();
        String rol = spinnerRol.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es obligatorio");
            return;
        }

        if (TextUtils.isEmpty(correo) || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            return;
        }

        if (usuarioDAO.existeCorreo(correo)) {
            etCorreo.setError("Este correo ya está registrado");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("No coinciden");
            return;
        }

        String codigo = generarCodigo();

        Usuario usuario = new Usuario(nombre, correo, password, rol, 0, codigo);

        long id = usuarioDAO.insertarUsuario(usuario);

        if (id > 0) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

            // Redirección explícita (MEJOR que solo finish)
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    private String generarCodigo() {
        Random r = new Random();
        return String.valueOf(100000 + r.nextInt(900000));
    }
}