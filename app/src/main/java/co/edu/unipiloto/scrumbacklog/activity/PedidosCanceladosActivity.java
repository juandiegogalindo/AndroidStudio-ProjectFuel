package co.edu.unipiloto.scrumbacklog.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;

public class PedidosCanceladosActivity extends AppCompatActivity {

    private ListView listView;
    private PedidoDAO pedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_cancelados);

        listView = findViewById(R.id.listViewCancelados);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        pedidoDAO = new PedidoDAO(db);

        cargarPedidos();
    }

    private void cargarPedidos() {
        Cursor cursor = pedidoDAO.obtenerPedidosCancelados();
        PedidoCanceladoAdapter adapter = new PedidoCanceladoAdapter(this, cursor, pedidoDAO);
        listView.setAdapter(adapter);
    }
}