package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
public class PedidoDAO {

    private SQLiteDatabase db;

    public PedidoDAO(SQLiteDatabase db){
        this.db = db;
    }


    public Cursor obtenerPedidosPendientes() {
        return db.rawQuery(
                "SELECT id_pedido AS _id, * FROM pedido WHERE estado = 'PENDIENTE'",
                null
        );
    }

    public void marcarComoEntregado(int idPedido) {
        ContentValues values = new ContentValues();
        values.put("estado", "ENTREGADO");

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void aceptarPedido(int idPedido) {
        ContentValues values = new ContentValues();
        values.put("estado", "ACEPTADO");

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    public void cancelarPedido(int idPedido, String motivo) {
        ContentValues values = new ContentValues();
        values.put("estado", "CANCELADO");
        values.put("motivo_cancelacion", motivo);

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }

    // 🔍 OBTENER PEDIDOS CANCELADOS
    public Cursor obtenerPedidosCancelados() {
        return db.rawQuery(
                "SELECT id_pedido AS _id, * FROM pedido WHERE estado = 'CANCELADO'",
                null
        );
    }

    // 🔄 REAGENDAR PEDIDO
    public void reagendarPedido(int idPedido, String nuevaFecha) {

        ContentValues values = new ContentValues();
        values.put("fecha", nuevaFecha);
        values.put("estado", "PENDIENTE");
        values.put("motivo_cancelacion", (String) null); // limpia el motivo

        db.update("pedido", values, "id_pedido = ?", new String[]{String.valueOf(idPedido)});
    }
    public void crearPedido(int idUbicacion ,int idCombustible,double cantidad,String fecha){

        ContentValues values = new ContentValues();
        values.put("id_ubicacion", idUbicacion);
        values.put("id_combustible", idCombustible);
        values.put("cantidad", cantidad);
        values.put("fecha", fecha);
        values.put("estado", "PENDIENTE");

        db.insert("pedido", null, values);
    }


}