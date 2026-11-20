package co.edu.taller.notasdocente.vistas;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.EditText;
import android.text.InputType;
import co.edu.taller.notasdocente.R;
import co.edu.taller.notasdocente.controladores.*;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); pantalla(R.layout.login);
        findViewById(R.id.entrar).setOnClickListener(v->autenticar(false));
        findViewById(R.id.registrar).setOnClickListener(v->autenticar(true));
        findViewById(R.id.configurar).setOnClickListener(v->{
            EditText url=new EditText(this); url.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI); url.setText(ServicioHttp.direccion(this));
            new AlertDialog.Builder(this).setTitle("Dirección del servicio PHP").setView(url).setPositiveButton("Guardar",(d,w)->{
                String valor=url.getText().toString().trim();
                if(!valor.startsWith("http://")&&!valor.startsWith("https://")){mensaje("La dirección debe comenzar por http:// o https://");return;}
                getSharedPreferences("config",0).edit().putString("url",valor).apply(); mensaje("Dirección del servidor guardada");
            }).setNegativeButton("Cancelar",null).show();
        });
    }
    private void autenticar(boolean registro) {
        String email=texto(R.id.email), clave=((EditText)findViewById(R.id.clave)).getText().toString(), nombre=texto(R.id.nombre);
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()||clave.isEmpty()){mensaje("Escribe un correo válido y una contraseña");return;}
        if(registro&&(nombre.isEmpty()||clave.length()<6)){mensaje("Para registrarte escribe tu nombre y una contraseña de al menos 6 caracteres");return;}
        tarea(()->ServicioHttp.solicitar(this,registro?"registrar":"login","email",email,"psw",clave,"nombre",nombre),j->{
            try {JSONObject u=j.getJSONObject("usuario"); Sesion.abrir(j.getString("token"),u.getString("nombre"),u.getString("email"));
                startActivity(new Intent(this,MenuActivity.class)); finish();
            }catch(Exception e){mensaje("Respuesta del servidor incompleta");}
        });
    }
}
