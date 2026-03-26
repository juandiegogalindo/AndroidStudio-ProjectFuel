package co.edu.unipiloto.scrumbacklog.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PrecioDAO {

    private SQLiteDatabase db;

    public PrecioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public double obtenerPrecioZona(String tipo,String ciudad,String zona){
        Cursor cursor = db.rawQuery(
                "SELECT precio FROM precio_combustible pc " +
                        "JOIN combustible c ON pc.id_combustible=c.id_combustible " +
                        "JOIN ubicacion u ON pc.id_ubicacion=u.id_ubicacion " +
                        "WHERE c.nombre=? AND u.ciudad=? AND u.zona=?",
                new String[]{tipo,ciudad,zona}
        );

        if(cursor.moveToFirst()){
            double precio = cursor.getDouble(0);
            cursor.close();
            return precio;
        }

        cursor.close();
        return 0;
    }
}