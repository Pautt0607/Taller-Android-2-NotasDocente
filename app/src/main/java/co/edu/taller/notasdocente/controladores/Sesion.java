package co.edu.taller.notasdocente.controladores;
import android.content.Context;
/** La sesión vive en memoria: al cerrar el proceso se vuelve a solicitar acceso. */
public class Sesion {
    private static volatile String token="", nombre="", email="";
    public static String token(Context c) { return token; }
    public static String nombre() { return nombre; }
    public static String email() { return email; }
    public static void abrir(String t, String n, String e) { token=t; nombre=n; email=e; }
    public static void cerrar(Context c) { token=""; nombre=""; email=""; }
}
