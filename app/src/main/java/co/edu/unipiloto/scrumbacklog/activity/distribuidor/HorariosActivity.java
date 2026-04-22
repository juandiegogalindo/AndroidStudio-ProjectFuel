package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class HorariosActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        listView = findViewById(R.id.listViewHorarios);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        UbicacionDAO ubicacionDAO = new UbicacionDAO(db);

        Cursor cursor = ubicacionDAO.obtenerHorarios();

        HorarioAdapter adapter = new HorarioAdapter(this, cursor);
        listView.setAdapter(adapter);
    }
}