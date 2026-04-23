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

    // ✅ PEDIDOS PENDIENTES (CON JOIN Y SIN *)
    public Cursor obtenerPedidosPendientes() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, p.fecha " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'PENDIENTE'",
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

    // ✅ CANCELADOS (CORREGIDO TAMBIÉN)
    public Cursor obtenerPedidosCancelados() {
        return db.rawQuery(
                "SELECT p.id_pedido AS _id, " +
                        "u.nombre AS ubicacion, " +
                        "c.nombre AS combustible, " +
                        "p.cantidad, p.fecha, p.motivo_cancelacion " +
                        "FROM pedido p " +
                        "JOIN ubicacion u ON p.id_ubicacion = u.id_ubicacion " +
                        "JOIN combustible c ON p.id_combustible = c.id_combustible " +
                        "WHERE p.estado = 'CANCELADO'",
                null
        );
    }

    public void reagendarPedido(int idPedido, String nuevaFecha) {
        ContentValues values = new ContentValues();
        values.put("fecha", nuevaFecha);
        values.put("estado", "PENDIENTE");
        values.put("motivo_cancelacion", (String) null);

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