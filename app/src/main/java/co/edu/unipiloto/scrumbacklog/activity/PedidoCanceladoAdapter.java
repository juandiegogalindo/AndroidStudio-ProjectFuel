package co.edu.unipiloto.scrumbacklog.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;

import java.util.Calendar;

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


        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_pedido"));
        int ubicacion = cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion"));
        int combustible = cursor.getInt(cursor.getColumnIndexOrThrow("id_combustible"));
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

            intent.putExtra("id_ubicacion", ubicacion);
            intent.putExtra("id_combustible", combustible);
            intent.putExtra("cantidad", cantidad);

            context.startActivity(intent);
        });

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
        }
    }