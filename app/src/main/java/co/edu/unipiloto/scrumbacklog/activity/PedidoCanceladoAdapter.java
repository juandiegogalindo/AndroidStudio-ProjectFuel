package co.edu.unipiloto.scrumbacklog.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.distribuidor.ProgramarPedidoActivity;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidoCanceladoAdapter extends CursorAdapter {

    private PedidoDAO pedidoDAO;

    public PedidoCanceladoAdapter(Context context, Cursor cursor, PedidoDAO pedidoDAO) {
        super(context, cursor, 0);
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pedido_cancelado, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvInfo = view.findViewById(R.id.tvInfoCancelado);
        TextView tvMotivo = view.findViewById(R.id.tvMotivo);
        Button btnReagendar = view.findViewById(R.id.btnReagendar);
        Button btnVolver = view.findViewById(R.id.btnVolver);

        // ✅ ID correcto
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        // ✅ AHORA SON STRINGS (no IDs)
        String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"));
        String combustible = cursor.getString(cursor.getColumnIndexOrThrow("combustible"));

        double cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
        String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo_cancelacion"));

        tvInfo.setText("Pedido #" + id +
                "\nUbicación: " + ubicacion +
                "\nCombustible: " + combustible +
                "\nCantidad: " + cantidad +
                "\nFecha: " + fecha);

        tvMotivo.setText("Motivo: " + motivo);

        // 🔥 BOTÓN REAGENDAR
        btnReagendar.setOnClickListener(v ->  {
            Intent intent = new Intent(context, ProgramarPedidoActivity.class);

            // ⚠️ YA NO TIENES LOS IDs → aquí hay un detalle importante
            intent.putExtra("ubicacion", ubicacion);
            intent.putExtra("combustible", combustible);
            intent.putExtra("cantidad", cantidad);

            context.startActivity(intent);
        });

        // ⚠️ ESTE BOTÓN ES PROBLEMÁTICO AQUÍ
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }
}