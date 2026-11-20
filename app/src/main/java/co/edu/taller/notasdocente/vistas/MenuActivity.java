package co.edu.taller.notasdocente.vistas;
import android.os.Bundle;
import android.content.Intent;
import co.edu.taller.notasdocente.R;
import co.edu.taller.notasdocente.controladores.*;
public class MenuActivity extends BaseActivity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b); if(!requiereSesion())return; pantalla(R.layout.menu);
        poner(R.id.bienvenida,"Hola, " + Sesion.nombre());
        findViewById(R.id.universidades).setOnClickListener(v->startActivity(new Intent(this,UniversidadesActivity.class)));
        findViewById(R.id.usuarios).setOnClickListener(v->startActivity(new Intent(this,UsuariosActivity.class)));
        findViewById(R.id.salir).setOnClickListener(v->tarea(()->ServicioHttp.solicitar(this,"salir"),j->{Sesion.cerrar(this); requiereSesion();}));
    }
    @Override protected void onResume(){super.onResume(); if(requiereSesion())poner(R.id.bienvenida,"Hola, " + Sesion.nombre());}
}
