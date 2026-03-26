package co.edu.unipiloto.scrumbacklog.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}