package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "combustible.db";
    private static final int DATABASE_VERSION = 14; // subir versión (VERSION ACTUAL CON COMMIT 12)

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tablas Actividades Antiguas (6 Primeras)
        db.execSQL("CREATE TABLE combustible (" +
                "id_combustible INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        db.execSQL("CREATE TABLE ubicacion (" +
                "id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ciudad TEXT," +
                "zona TEXT)");

        db.execSQL("CREATE TABLE precio_combustible (" +
                "id_precio INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "precio REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        db.execSQL("CREATE TABLE inventario (" +
                "id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "cantidad REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "UNIQUE(id_combustible,id_ubicacion))");

        db.execSQL("CREATE TABLE movimientos (" +
                "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "tipo_movimiento TEXT," +
                "galones REAL," +
                "precio_unitario REAL," +
                "total REAL," +
                "fecha TEXT)");

        // Nuevas tablas base de datos mejorada
        db.execSQL("CREATE TABLE rol (" +
                "id_rol INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        db.execSQL("CREATE TABLE usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "correo TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL," +
                "verificado INTEGER DEFAULT 0," +
                "codigo_verificacion TEXT)");

        db.execSQL("CREATE TABLE distribuidor (" +
                "id_distribuidor INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT)");

        db.execSQL("CREATE TABLE pedido (" +
                "id_pedido INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fecha TEXT," +
                "estado TEXT)");

        db.execSQL("CREATE TABLE alerta (" +
                "id_alerta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipo TEXT," +
                "mensaje TEXT," +
                "fecha TEXT)");

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS rol");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        db.execSQL("DROP TABLE IF EXISTS inventario");
        db.execSQL("DROP TABLE IF EXISTS precio_combustible");
        db.execSQL("DROP TABLE IF EXISTS ubicacion");
        db.execSQL("DROP TABLE IF EXISTS combustible");
        db.execSQL("DROP TABLE IF EXISTS distribuidor");
        db.execSQL("DROP TABLE IF EXISTS pedido");
        db.execSQL("DROP TABLE IF EXISTS alerta");

        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Corriente')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Extra')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Diesel')");

        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Suba')");
        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Engativa')");
        db.execSQL("INSERT INTO ubicacion (ciudad,zona) VALUES ('Bogota','Centro')");

        for(int u = 1; u <= 3; u++){
            db.execSQL("INSERT INTO inventario VALUES (NULL,1,"+u+",10000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,2,"+u+",8000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,3,"+u+",7500)");
        }

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
}