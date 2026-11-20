package co.edu.taller.notasdocente.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Modelo local del taller 2. Las claves foráneas se validan en cada conexión. */
public class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context) { super(context, "notasdocente.db", null, 1); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Universidades(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, www TEXT NOT NULL DEFAULT '')");
        db.execSQL("CREATE TABLE Programas(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, director TEXT, email TEXT, universidades_id INTEGER NOT NULL REFERENCES Universidades(id))");
        db.execSQL("CREATE TABLE Alumnos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, email TEXT, programas_id INTEGER NOT NULL REFERENCES Programas(id))");
        db.execSQL("CREATE TABLE Asignaturas(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, programas_id INTEGER NOT NULL REFERENCES Programas(id))");
        db.execSQL("CREATE TABLE Calificaciones(alumnos_id INTEGER NOT NULL REFERENCES Alumnos(id), asignaturas_id INTEGER NOT NULL REFERENCES Asignaturas(id), nota REAL, PRIMARY KEY(alumnos_id,asignaturas_id))");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Se requiere una migración para esta versión de la base de datos");
    }
}
