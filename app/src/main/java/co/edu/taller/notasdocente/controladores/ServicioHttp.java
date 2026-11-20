package co.edu.taller.notasdocente.controladores;

import android.content.Context;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Servicio HTTP del taller de integración. Ejecutar siempre fuera del hilo de interfaz. */
public class ServicioHttp {
    public static String direccion(Context c) { return c.getSharedPreferences("config",0).getString("url", "http://10.0.2.2/taller_android/operacion.php"); }
    public static JSONObject solicitar(Context c, String accion, String... pares) throws Exception {
        StringBuilder datos = new StringBuilder("accion=" + URLEncoder.encode(accion, "UTF-8"));
        for (int i=0; i<pares.length; i+=2) datos.append('&').append(URLEncoder.encode(pares[i],"UTF-8")).append('=').append(URLEncoder.encode(pares[i+1],"UTF-8"));
        HttpURLConnection con = (HttpURLConnection)new URL(direccion(c)).openConnection();
        con.setRequestMethod("POST"); con.setConnectTimeout(10000); con.setReadTimeout(10000); con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String token = Sesion.token(c);
        if (!token.isEmpty()) con.setRequestProperty("Authorization", "Bearer " + token);
        try {
            try(java.io.OutputStream salida = con.getOutputStream()) { salida.write(datos.toString().getBytes(StandardCharsets.UTF_8)); }
            int status = con.getResponseCode();
            InputStream stream = status < 400 ? con.getInputStream() : con.getErrorStream();
            if (stream == null) throw new Exception("El servidor no devolvió una respuesta (HTTP " + status + ")");
            StringBuilder respuesta = new StringBuilder();
            try(java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String linea; while((linea=reader.readLine())!=null) respuesta.append(linea);
            }
            JSONObject json;
            try { json = new JSONObject(respuesta.toString()); }
            catch(Exception e) { throw new Exception("El servidor no devolvió JSON. Revisa la URL y PHP."); }
            if(status==401) Sesion.cerrar(c);
            if(status>=400 || !json.optBoolean("ok")) throw new Exception(json.optString("mensaje", "Error HTTP " + status));
            return json;
        } finally { con.disconnect(); }
    }
}
