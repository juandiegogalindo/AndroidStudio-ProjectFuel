package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private SQLiteDatabase db;

    public InventarioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // CONSULTA POR ZONA
    public double obtenerInventario(String tipo, String ciudad, String zona){

        Cursor cursor = null;
        double resultado = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT cantidad FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "WHERE c.nombre=? AND u.ciudad=? AND u.zona=?",
                    new String[]{tipo, ciudad, zona}
            );

            if(cursor.moveToFirst()){
                resultado = cursor.getDouble(0);
            }

        } finally {
            if(cursor != null) cursor.close();
        }

        return resultado;
    }

    // CONSULTA POR CIUDAD
    public double obtenerInventarioTotalPorCiudad(String tipo, String ciudad){

        Cursor cursor = null;
        double resultado = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT SUM(i.cantidad) " +
                            "FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "WHERE c.nombre=? AND u.ciudad=?",
                    new String[]{tipo, ciudad}
            );

            if(cursor.moveToFirst()){
                // 🔥 Manejo de NULL
                if(!cursor.isNull(0)){
                    resultado = cursor.getDouble(0);
                }
            }

        } finally {
            if(cursor != null) cursor.close();
        }

        return resultado;
    }

    // INSERTAR INVENTARIO
    public long insertarInventario(int cantidad, int idCombustible, int idUbicacion) {

        ContentValues values = new ContentValues();
        values.put("cantidad", cantidad);
        values.put("id_combustible", idCombustible);
        values.put("id_ubicacion", idUbicacion);

        return db.insert("inventario", null, values);
    }

    // LISTAR HISTORIAL SIMPLE
    public List<String> obtenerHistorial() {

        List<String> lista = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT i.cantidad, c.nombre, u.ciudad, u.zona " +
                            "FROM inventario i " +
                            "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                            "JOIN ubicacion u ON i.id_ubicacion=u.id_ubicacion " +
                            "ORDER BY i.id DESC",
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    int cantidad = cursor.getInt(0);
                    String combustible = cursor.getString(1);
                    String ciudad = cursor.getString(2);
                    String zona = cursor.getString(3);

                    lista.add("+" + cantidad + " | " + combustible + " | " + ciudad + " - " + zona);

                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }
}