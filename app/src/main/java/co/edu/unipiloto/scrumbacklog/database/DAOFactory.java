package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;

public class DAOFactory {

    private SQLiteDatabase db;

    public DAOFactory(Context context) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        db = manager.openDatabase();
    }

    public CombustibleDAO getCombustibleDAO() {
        return new CombustibleDAO(db);
    }

    public InventarioDAO getInventarioDAO() {
        return new InventarioDAO(db);
    }

    public MovimientoDAO getMovimientoDAO() {
        return new MovimientoDAO(db);
    }

    public PrecioDAO getPrecioDAO() {
        return new PrecioDAO(db);
    }

    public UbicacionDAO getUbicacionDAO() {
        return new UbicacionDAO(db);
    }
}