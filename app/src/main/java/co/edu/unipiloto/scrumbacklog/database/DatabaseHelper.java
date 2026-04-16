package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 17; // 🔥 IMPORTANTE

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // COMBUSTIBLE
        // =========================
        db.execSQL("CREATE TABLE combustible (" +
                "id_combustible INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        // =========================
        // UBICACION (ESTACIONES)
        // =========================
        db.execSQL("CREATE TABLE ubicacion (" +
                "id_ubicacion INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "ciudad TEXT," +
                "localidad TEXT)");

        // =========================
        // INVENTARIO
        // =========================
        db.execSQL("CREATE TABLE inventario (" +
                "id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "cantidad REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "UNIQUE(id_combustible,id_ubicacion))");

        // =========================
        // PRECIOS
        // =========================
        db.execSQL("CREATE TABLE precio_combustible (" +
                "id_precio INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "precio REAL," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible)," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        // =========================
        // MOVIMIENTOS
        // =========================
        db.execSQL("CREATE TABLE movimientos (" +
                "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_combustible INTEGER," +
                "id_ubicacion INTEGER," +
                "tipo_movimiento TEXT," +
                "galones REAL," +
                "precio_unitario REAL," +
                "total REAL," +
                "fecha TEXT)");

        // =========================
        // ROLES
        // =========================
        db.execSQL("CREATE TABLE rol (" +
                "id_rol INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE)");

        // =========================
        // USUARIO (CON ESTACION)
        // =========================
        db.execSQL("CREATE TABLE usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "usuario TEXT NOT NULL," +
                "correo TEXT NOT NULL UNIQUE," +
                "direccion TEXT," +
                "password TEXT NOT NULL," +
                "rol TEXT NOT NULL," +
                "id_ubicacion INTEGER," +
                "fecha_nacimiento TEXT," +
                "genero TEXT," +
                "latitud REAL," +
                "longitud REAL," +
                "verificado INTEGER DEFAULT 0," +
                "codigo_verificacion TEXT," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion))");

        // =========================
        // DISTRIBUIDOR
        // =========================
        db.execSQL("CREATE TABLE distribuidor (" +
                "id_distribuidor INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "contacto TEXT)");

        // =========================
        // PEDIDOS (REABASTECIMIENTO)
        // =========================
        db.execSQL("CREATE TABLE pedido (" +
                "id_pedido INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_ubicacion INTEGER," +
                "id_distribuidor INTEGER," +
                "id_combustible INTEGER," +
                "cantidad REAL," +
                "fecha TEXT," +
                "estado TEXT," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "FOREIGN KEY(id_distribuidor) REFERENCES distribuidor(id_distribuidor)," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible))");

        // =========================
        // ALERTAS
        // =========================
        db.execSQL("CREATE TABLE alerta (" +
                "id_alerta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_ubicacion INTEGER," +
                "id_combustible INTEGER," +
                "nivel_minimo REAL," +
                "activa INTEGER DEFAULT 1," +
                "FOREIGN KEY(id_ubicacion) REFERENCES ubicacion(id_ubicacion)," +
                "FOREIGN KEY(id_combustible) REFERENCES combustible(id_combustible))");

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS alerta");
        db.execSQL("DROP TABLE IF EXISTS pedido");
        db.execSQL("DROP TABLE IF EXISTS distribuidor");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS rol");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        db.execSQL("DROP TABLE IF EXISTS inventario");
        db.execSQL("DROP TABLE IF EXISTS precio_combustible");
        db.execSQL("DROP TABLE IF EXISTS ubicacion");
        db.execSQL("DROP TABLE IF EXISTS combustible");

        onCreate(db);
    }

    // =========================
    // DATOS INICIALES
    // =========================
    private void insertarDatosIniciales(SQLiteDatabase db) {

        // Combustibles
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Corriente')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Extra')");
        db.execSQL("INSERT INTO combustible (nombre) VALUES ('Diesel')");

        // Estaciones (Bogotá por localidades)
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad) VALUES ('Estación Suba','Bogota','Suba')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad) VALUES ('Estación Engativa','Bogota','Engativa')");
        db.execSQL("INSERT INTO ubicacion (nombre, ciudad, localidad) VALUES ('Estación Centro','Bogota','Centro')");

        // Inventario inicial
        for(int u = 1; u <= 3; u++){
            db.execSQL("INSERT INTO inventario VALUES (NULL,1,"+u+",10000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,2,"+u+",8000)");
            db.execSQL("INSERT INTO inventario VALUES (NULL,3,"+u+",7500)");
        }

        // Precios por estación
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,1,16000)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,1,22700)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,1,13200)");

        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,2,15900)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,2,22600)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,2,13100)");

        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,1,3,15800)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,2,3,22500)");
        db.execSQL("INSERT INTO precio_combustible VALUES (NULL,3,3,13000)");

        // Distribuidores
        db.execSQL("INSERT INTO distribuidor (nombre, contacto) VALUES ('Distribuidor Central','3001234567')");
        db.execSQL("INSERT INTO distribuidor (nombre, contacto) VALUES ('Fuel Supply SAS','3109876543')");
    }
}