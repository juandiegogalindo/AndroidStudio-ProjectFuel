package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class PedidoDAO {

    private SQLiteDatabase db;

    public PedidoDAO(SQLiteDatabase db){
        this.db = db;
    }

    public void crearPedido(int idUbicacion,int idDistribuidor,int idCombustible,double cantidad,String fecha){

        ContentValues values = new ContentValues();
        values.put("id_ubicacion", idUbicacion);
        values.put("id_distribuidor", idDistribuidor);
        values.put("id_combustible", idCombustible);
        values.put("cantidad", cantidad);
        values.put("fecha", fecha);
        values.put("estado", "PENDIENTE");

        db.insert("pedido", null, values);
    }
}