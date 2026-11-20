package co.edu.taller.notasdocente.datos;

public class Universidad {
    public final long id;
    public final String nombre, www;
    public Universidad(long id, String nombre, String www) {
        this.id = id; this.nombre = nombre; this.www = www;
    }
    @Override public String toString() { return id + " · " + nombre + (www.isEmpty() ? "" : "\n" + www); }
}
