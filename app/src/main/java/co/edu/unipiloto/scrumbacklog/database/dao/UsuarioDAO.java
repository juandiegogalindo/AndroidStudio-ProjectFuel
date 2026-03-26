package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class UsuarioDAO {

    private DatabaseHelper databaseHelper;

    public UsuarioDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public long insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long resultado = -1;

        try {
            ContentValues values = new ContentValues();
            values.put("nombre", usuario.getNombre());
            values.put("correo", usuario.getCorreo());
            values.put("password", usuario.getPassword());
            values.put("rol", usuario.getRol());
            values.put("verificado", usuario.getVerificado());
            values.put("codigo_verificacion", usuario.getCodigoVerificacion());

            resultado = db.insert("usuario", null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM usuario WHERE correo = ?",
                    new String[]{correo}
            );
            return cursor.moveToFirst();

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean validarLogin(String correo, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM usuario WHERE correo = ? AND password = ?",
                    new String[]{correo, password}
            );
            return cursor.moveToFirst();

        } finally {
            if (cursor != null) cursor.close();
        }
    }
}