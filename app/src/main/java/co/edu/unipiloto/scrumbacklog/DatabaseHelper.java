package co.edu.unipiloto.scrumbacklog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "combustible.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tablaCombustibles = "CREATE TABLE combustibles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT," +
                "precio REAL," +
                "inventario REAL)";

        String tablaMovimientos = "CREATE TABLE movimientos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipoMovimiento TEXT," +
                "tipoCombustible TEXT," +
                "galones REAL," +
                "precioUnitario REAL," +
                "total REAL," +
                "fecha TEXT)";

        db.execSQL(tablaCombustibles);
        db.execSQL(tablaMovimientos);

        db.execSQL("INSERT INTO combustibles (tipo, precio, inventario) VALUES ('Corriente',15000,1000)");
        db.execSQL("INSERT INTO combustibles (tipo, precio, inventario) VALUES ('Extra',18000,800)");
        db.execSQL("INSERT INTO combustibles (tipo, precio, inventario) VALUES ('Diesel',14000,1200)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS combustibles");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        onCreate(db);
    }

    //Metodo para implementar una salida
    public boolean registrarSalida(String tipoCombustible,
                                   double galones,
                                   double precioUnitario,
                                   String fecha) {

        SQLiteDatabase db = this.getWritableDatabase();

        double total = galones * precioUnitario;

        ContentValues values = new ContentValues();
        values.put("tipoMovimiento", "SALIDA");
        values.put("tipoCombustible", tipoCombustible);
        values.put("galones", galones);
        values.put("precioUnitario", precioUnitario);
        values.put("total", total);
        values.put("fecha", fecha);

        long resultado = db.insert("movimientos", null, values);

        if (resultado != -1) {
            descontarInventario(tipoCombustible, galones);
            return true;
        }

        return false;
    }

    // Descontar Inventario
    public void descontarInventario(String tipoCombustible, double galones) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT inventario FROM combustibles WHERE tipo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{tipoCombustible});

        if (cursor.moveToFirst()) {

            double inventarioActual = cursor.getDouble(0);
            double nuevoInventario = inventarioActual - galones;

            ContentValues values = new ContentValues();
            values.put("inventario", nuevoInventario);

            db.update("combustibles",
                    values,
                    "tipo = ?",
                    new String[]{tipoCombustible});
        }

        cursor.close();
    }

    //Metodo ingresar datos
    public void insertarCombustibleInicial(String tipo, double precio, double inventario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tipo", tipo);
        values.put("precio", precio);
        values.put("inventario", inventario);

        db.insert("combustibles", null, values);
    }

    //Leer inventarios desde la base
    public double obtenerInventario(String tipoCombustible) {

        SQLiteDatabase db = this.getReadableDatabase();

        double inventario = 0;

        Cursor cursor = db.rawQuery(
                "SELECT inventario FROM combustibles WHERE tipo = ?",
                new String[]{tipoCombustible}
        );

        if (cursor.moveToFirst()) {
            inventario = cursor.getDouble(0);
        }

        cursor.close();
        return inventario;
    }

    // REGISTRAR ENTRADAS
    public boolean registrarEntrada(String tipoCombustible,
                                    double galones,
                                    double precioUnitario,
                                    String fecha) {

        SQLiteDatabase db = this.getWritableDatabase();

        double total = galones * precioUnitario;

        ContentValues values = new ContentValues();
        values.put("tipoMovimiento", "ENTRADA");
        values.put("tipoCombustible", tipoCombustible);
        values.put("galones", galones);
        values.put("precioUnitario", precioUnitario);
        values.put("total", total);
        values.put("fecha", fecha);

        long resultado = db.insert("movimientos", null, values);

        if (resultado != -1) {

            // SUMAR INVENTARIO
            Cursor cursor = db.rawQuery(
                    "SELECT inventario FROM combustibles WHERE tipo = ?",
                    new String[]{tipoCombustible}
            );

            if (cursor.moveToFirst()) {
                double inventarioActual = cursor.getDouble(0);
                double nuevoInventario = inventarioActual + galones;

                ContentValues updateValues = new ContentValues();
                updateValues.put("inventario", nuevoInventario);

                db.update(
                        "combustibles",
                        updateValues,
                        "tipo = ?",
                        new String[]{tipoCombustible}
                );
            }

            cursor.close();
            return true;
        }

        return false;
    }

    //Movimientos
    public ArrayList<String> obtenerMovimientos() {

        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT tipoMovimiento, tipoCombustible, galones, total, fecha FROM movimientos ORDER BY id DESC",
                null
        );

        while (cursor.moveToNext()) {

            String tipoMov = cursor.getString(0);
            String tipoComb = cursor.getString(1);
            double gal = cursor.getDouble(2);
            double total = cursor.getDouble(3);
            String fecha = cursor.getString(4);

            String registro = fecha +
                    " | " + tipoMov +
                    " | " + tipoComb +
                    " | " + gal + " gal" +
                    " | $" + total;

            lista.add(registro);
        }

        cursor.close();
        return lista;
    }

    //Obtener precio
    public double obtenerPrecio(String tipoCombustible) {

        SQLiteDatabase db = this.getReadableDatabase();
        double precio = 0;

        Cursor cursor = db.rawQuery(
                "SELECT precio FROM combustibles WHERE tipo = ?",
                new String[]{tipoCombustible}
        );

        if (cursor.moveToFirst()) {
            precio = cursor.getDouble(0);
        }

        cursor.close();
        return precio;
    }
}