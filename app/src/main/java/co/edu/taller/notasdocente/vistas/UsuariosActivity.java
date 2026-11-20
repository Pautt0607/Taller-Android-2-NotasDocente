package co.edu.taller.notasdocente.vistas;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.*;
import org.json.*;
import co.edu.taller.notasdocente.R;
import co.edu.taller.notasdocente.controladores.*;
import java.util.ArrayList;

/** Los usuarios pueden ver el directorio y editar o eliminar su propia cuenta. */
public class UsuariosActivity extends BaseActivity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b); if(!requiereSesion())return; pantalla(R.layout.usuarios);
        poner(R.id.email,Sesion.email()); poner(R.id.nombre,Sesion.nombre());
        findViewById(R.id.actualizar).setOnClickListener(v->listar());
        findViewById(R.id.modificar).setOnClickListener(v->{
            String nombre=texto(R.id.nombre), clave=((EditText)findViewById(R.id.clave)).getText().toString();
            if(nombre.isEmpty()||(!clave.isEmpty()&&clave.length()<6)){mensaje("Escribe tu nombre; la contraseña nueva debe tener al menos 6 caracteres");return;}
            tarea(()->ServicioHttp.solicitar(this,"editar","nombre",nombre,"psw",clave),j->{Sesion.abrir(Sesion.token(this),nombre,Sesion.email());poner(R.id.clave,"");listar();});
        });
        findViewById(R.id.eliminar).setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Eliminar mi cuenta").setMessage("Se eliminará tu usuario del servidor. Las universidades locales permanecerán en este dispositivo.").setPositiveButton("Eliminar",(d,w)->tarea(()->ServicioHttp.solicitar(this,"eliminar"),j->{Sesion.cerrar(this);requiereSesion();})).setNegativeButton("Cancelar",null).show());
        findViewById(R.id.volver).setOnClickListener(v->finish());listar();
    }
    private void listar(){tarea(()->ServicioHttp.solicitar(this,"listar"),j->{
        ArrayList<String> datos=new ArrayList<>();JSONArray usuarios=j.optJSONArray("usuarios");
        if(usuarios!=null)for(int i=0;i<usuarios.length();i++){JSONObject u=usuarios.optJSONObject(i);if(u!=null)datos.add(u.optString("nombre")+"\n"+u.optString("email"));}
        ((ListView)findViewById(R.id.lista)).setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datos));mensaje(datos.size()+" usuarios registrados");
    });}
}
