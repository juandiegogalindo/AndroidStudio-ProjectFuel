package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MovimientoDAO {

    private SQLiteDatabase db;
    private CombustibleDAO combustibleDAO;
    private UbicacionDAO ubicacionDAO;
    private InventarioDAO inventarioDAO;

    public MovimientoDAO(SQLiteDatabase db) {
        this.db = db;
        combustibleDAO = new CombustibleDAO(db);
        ubicacionDAO = new UbicacionDAO(db);
        inventarioDAO = new InventarioDAO(db);
    }

    public boolean registrarEntrada(String tipo,double galones,double precio,String fecha,String ciudad,String zona){

        int idComb = combustibleDAO.obtenerIdCombustible(tipo);
        int idUbic = ubicacionDAO.obtenerIdUbicacion(ciudad,zona);

        if(idComb == -1 || idUbic == -1) return false;

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible",idComb);
        values.put("id_ubicacion",idUbic);
        values.put("tipo_movimiento","ENTRADA");
        values.put("galones",galones);
        values.put("precio_unitario",precio);
        values.put("total",total);
        values.put("fecha",fecha);

        long res = db.insert("movimientos",null,values);

        if(res!=-1){
            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad+? WHERE id_combustible=? AND id_ubicacion=?",
                    new Object[]{galones,idComb,idUbic}
            );
            return true;
        }

        return false;
    }

    public boolean registrarSalida(String tipo,double galones,double precio,String fecha,String ciudad,String zona){

        int idComb = combustibleDAO.obtenerIdCombustible(tipo);
        int idUbic = ubicacionDAO.obtenerIdUbicacion(ciudad,zona);

        if(idComb == -1 || idUbic == -1) return false;

        double disponible = inventarioDAO.obtenerInventario(tipo, ciudad, zona);

        if(disponible < galones){
            return false; // inventario negativo
        }

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible",idComb);
        values.put("id_ubicacion",idUbic);
        values.put("tipo_movimiento","SALIDA");
        values.put("galones",galones);
        values.put("precio_unitario",precio);
        values.put("total",total);
        values.put("fecha",fecha);

        long res = db.insert("movimientos",null,values);

        if(res!=-1){
            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad-? WHERE id_combustible=? AND id_ubicacion=?",
                    new Object[]{galones,idComb,idUbic}
            );
            return true;
        }

        return false;
    }

    public ArrayList<String> obtenerMovimientosPorUbicacion(String ciudad){
        ArrayList<String> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT m.tipo_movimiento, c.nombre, m.galones, m.total, m.fecha " +
                        "FROM movimientos m " +
                        "JOIN combustible c ON m.id_combustible=c.id_combustible " +
                        "JOIN ubicacion u ON m.id_ubicacion=u.id_ubicacion " +
                        "WHERE u.ciudad=? " +
                        "ORDER BY m.id_movimiento DESC",
                new String[]{ciudad}
        );

        while(cursor.moveToNext()){
            String mov = cursor.getString(0);
            String comb = cursor.getString(1);
            double gal = cursor.getDouble(2);
            double total = cursor.getDouble(3);
            String fecha = cursor.getString(4);

            lista.add(fecha + " | " + mov + " | " + comb + " | " + gal + " gal | $" + total);
        }
        cursor.close();
        return lista;
    }
}