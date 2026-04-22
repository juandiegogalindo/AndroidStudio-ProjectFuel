package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.database.Cursor;
import android.view.*;
import android.widget.*;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidoAdapter extends CursorAdapter {

    private PedidoDAO pedidoDAO;

    public PedidoAdapter(Context context, Cursor cursor, PedidoDAO pedidoDAO) {
        super(context, cursor, 0);
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfo);
        Button btnEntregado = view.findViewById(R.id.btnEntregado);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));
        int ubicacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion"));
        int distribuidor = cursor.getInt(cursor.getColumnIndexOrThrow("id_distribuidor"));
        int combustible = cursor.getInt(cursor.getColumnIndexOrThrow("id_combustible"));
        double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

        // 🔥 Convertir IDs a nombres (igual que en spinner)
        String nombreUbicacion = obtenerUbicacion(ubicacion);
        String nombreDistribuidor = obtenerDistribuidor(distribuidor);
        String nombreCombustible = obtenerCombustible(combustible);

        String texto = "Ubicación: " + nombreUbicacion +
                "\nDistribuidor: " + nombreDistribuidor +
                "\nCombustible: " + nombreCombustible +
                "\nCantidad: " + cantidad +
                "\nFecha: " + fecha;

        tvInfo.setText(texto);

        btnEntregado.setOnClickListener(v -> {
            pedidoDAO.marcarComoEntregado(id);

            Toast.makeText(context, "Pedido entregado", Toast.LENGTH_SHORT).show();

            // refrescar lista
            Cursor nuevoCursor = pedidoDAO.obtenerPedidosPendientes();
            changeCursor(nuevoCursor);
        });
    }

    // 🔽 Métodos para mostrar nombres
    private String obtenerUbicacion(int id) {
        switch (id) {
            case 1: return "Estación Suba";
            case 2: return "Estación Engativá";
            case 3: return "Estación Centro";
            default: return "Desconocido";
        }
    }

    private String obtenerDistribuidor(int id) {
        switch (id) {
            case 1: return "Distribuidor Central";
            case 2: return "Fuel Supply SAS";
            default: return "Desconocido";
        }
    }

    private String obtenerCombustible(int id) {
        switch (id) {
            case 1: return "Corriente";
            case 2: return "Extra";
            case 3: return "Diesel";
            default: return "Desconocido";
        }
    }
}