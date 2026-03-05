package co.edu.unipiloto.scrumbacklog.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "combustible.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA COMBUSTIBLE
        db.execSQL("CREATE TABLE combustible (" +
                "id_combustible INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        // TABLA UBICACION
        db.execSQL("CREATE TABLE ubicacion (" +
                "id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ciudad TEXT," +
                "zona TEXT)");

        // TABLA PRECIOS
        db.execSQL("CREATE TABLE precio_combustible (" +
                "id_precio INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "precio REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        // TABLA INVENTARIO
        db.execSQL("CREATE TABLE inventario (" +
                "id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "cantidad REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible))");

        // TABLA MOVIMIENTOS
        db.execSQL("CREATE TABLE movimientos (" +
                "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "tipo_movimiento TEXT," +
                "galones REAL," +
                "precio_unitario REAL," +
                "total REAL," +
                "fecha TEXT," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible))");

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS movimientos");
        db.execSQL("DROP TABLE IF EXISTS inventario");
        db.execSQL("DROP TABLE IF EXISTS precio_combustible");
        db.execSQL("DROP TABLE IF EXISTS ubicacion");
        db.execSQL("DROP TABLE IF EXISTS combustible");

        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {

        // COMBUSTIBLES
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Corriente')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Extra')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Diesel')");

        // UBICACIONES
        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Suba')");
        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Engativa')");
        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Centro')");

        // INVENTARIO INICIAL
        db.execSQL("INSERT INTO inventario (id_combustible,cantidad) VALUES (1,10000)");
        db.execSQL("INSERT INTO inventario (id_combustible,cantidad) VALUES (2,8000)");
        db.execSQL("INSERT INTO inventario (id_combustible,cantidad) VALUES (3,7500)");

        // PRECIOS POR ZONA
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,1,16000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,1,22700)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,1,13200)");

        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,2,15900)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,2,22600)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,2,13100)");

        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,3,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,3,22500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,3,13000)");
    }

    // OBTENER ID COMBUSTIBLE
    private int obtenerIdCombustible(String nombre){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_combustible FROM combustible WHERE nombre=?",
                new String[]{nombre});

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }

        cursor.close();
        return -1;
    }

    // OBTENER PRECIO GENERAL (usado por historias antiguas)

    public double obtenerPrecio(String tipo){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT precio FROM precio_combustible pc " +
                        "JOIN combustible c ON pc.id_combustible=c.id_combustible " +
                        "WHERE c.nombre=? LIMIT 1",
                new String[]{tipo});

        if(cursor.moveToFirst()){
            double precio = cursor.getDouble(0);
            cursor.close();
            return precio;
        }

        cursor.close();
        return 0;
    }

    // OBTENER PRECIO POR CIUDAD Y ZONA

    public double obtenerPrecioZona(String tipo,String ciudad,String zona){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT precio FROM precio_combustible pc " +
                        "JOIN combustible c ON pc.id_combustible=c.id_combustible " +
                        "JOIN ubicacion u ON pc.id_ubicacion=u.id_ubicacion " +
                        "WHERE c.nombre=? AND u.ciudad=? AND u.zona=?",
                new String[]{tipo,ciudad,zona});

        if(cursor.moveToFirst()){
            double precio = cursor.getDouble(0);
            cursor.close();
            return precio;
        }

        cursor.close();
        return 0;
    }

    // OBTENER INVENTARIO

    public double obtenerInventario(String tipo){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT cantidad FROM inventario i " +
                        "JOIN combustible c ON i.id_combustible=c.id_combustible " +
                        "WHERE c.nombre=?",
                new String[]{tipo});

        if(cursor.moveToFirst()){
            double inv = cursor.getDouble(0);
            cursor.close();
            return inv;
        }

        cursor.close();
        return 0;
    }

    // REGISTRAR ENTRADA

    public boolean registrarEntrada(String tipo,double galones,double precio,String fecha){

        SQLiteDatabase db = this.getWritableDatabase();

        int idComb = obtenerIdCombustible(tipo);

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible",idComb);
        values.put("tipo_movimiento","ENTRADA");
        values.put("galones",galones);
        values.put("precio_unitario",precio);
        values.put("total",total);
        values.put("fecha",fecha);

        long res = db.insert("movimientos",null,values);

        if(res!=-1){

            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad+? WHERE id_combustible=?",
                    new Object[]{galones,idComb});

            return true;
        }

        return false;
    }

    // REGISTRAR SALIDA

    public boolean registrarSalida(String tipo,double galones,double precio,String fecha){

        SQLiteDatabase db = this.getWritableDatabase();

        int idComb = obtenerIdCombustible(tipo);

        double total = galones * precio;

        ContentValues values = new ContentValues();
        values.put("id_combustible",idComb);
        values.put("tipo_movimiento","SALIDA");
        values.put("galones",galones);
        values.put("precio_unitario",precio);
        values.put("total",total);
        values.put("fecha",fecha);

        long res = db.insert("movimientos",null,values);

        if(res!=-1){

            db.execSQL(
                    "UPDATE inventario SET cantidad=cantidad-? WHERE id_combustible=?",
                    new Object[]{galones,idComb});

            return true;
        }

        return false;
    }

    // HISTORIAL MOVIMIENTOS

    public ArrayList<String> obtenerMovimientos(){

        ArrayList<String> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT m.tipo_movimiento,c.nombre,m.galones,m.total,m.fecha " +
                        "FROM movimientos m " +
                        "JOIN combustible c ON m.id_combustible=c.id_combustible " +
                        "ORDER BY m.id_movimiento DESC",
                null);

        while(cursor.moveToNext()){

            String mov = cursor.getString(0);
            String comb = cursor.getString(1);
            double gal = cursor.getDouble(2);
            double total = cursor.getDouble(3);
            String fecha = cursor.getString(4);

            lista.add(fecha+" | "+mov+" | "+comb+" | "+gal+" gal | $"+total);
        }

        cursor.close();
        return lista;
    }
}