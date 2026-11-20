package co.edu.taller.notasdocente.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import co.edu.taller.notasdocente.datos.Universidad;
import java.util.ArrayList;
import java.util.List;

/** Consultas parametrizadas y cierre automático de los cursores. */
public class DAOUniversidad implements AutoCloseable {
    private final BaseDatos helper;
    public DAOUniversidad(Context context) { helper = new BaseDatos(context); }
    private ContentValues valores(String nombre, String www) {
        if (nombre.trim().isEmpty()) throw new IllegalArgumentException("Escribe el nombre de la universidad");
        ContentValues v = new ContentValues(); v.put("nombre", nombre.trim()); v.put("www", www.trim()); return v;
    }
    public long agregar(String nombre, String www) { return helper.getWritableDatabase().insertOrThrow("Universidades", null, valores(nombre, www)); }
    public boolean editar(long id, String nombre, String www) { return helper.getWritableDatabase().update("Universidades", valores(nombre, www), "id=?", new String[]{""+id}) == 1; }
    public boolean eliminar(long id) { return helper.getWritableDatabase().delete("Universidades", "id=?", new String[]{""+id}) == 1; }
    public Universidad buscar(long id) {
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT id,nombre,www FROM Universidades WHERE id=?", new String[]{""+id})) {
            return c.moveToFirst() ? new Universidad(c.getLong(0), c.getString(1), c.getString(2)) : null;
        }
    }
    public List<Universidad> listar() {
        List<Universidad> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT id,nombre,www FROM Universidades ORDER BY nombre COLLATE NOCASE", null)) {
            while(c.moveToNext()) lista.add(new Universidad(c.getLong(0), c.getString(1), c.getString(2)));
        }
        return lista;
    }
    @Override public void close() { helper.close(); }
}
