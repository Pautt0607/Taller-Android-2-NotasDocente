package co.edu.taller.notasdocente.vistas;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.*;
import co.edu.taller.notasdocente.R;
import co.edu.taller.notasdocente.datos.Universidad;
import co.edu.taller.notasdocente.modelo.DAOUniversidad;
import java.util.List;

public class UniversidadesActivity extends BaseActivity {
    private DAOUniversidad dao;
    @Override public void onCreate(Bundle b){
        super.onCreate(b); if(!requiereSesion())return; pantalla(R.layout.universidades); dao=new DAOUniversidad(this);
        findViewById(R.id.guardar).setOnClickListener(v->operar(()->{long id=dao.agregar(texto(R.id.nombre),texto(R.id.web)); mostrar(dao.buscar(id)); return "Universidad guardada. ID: " + id;}));
        findViewById(R.id.buscar).setOnClickListener(v->operar(()->{Universidad u=dao.buscar(id()); if(u==null)throw new Exception("No existe una universidad con ese ID");mostrar(u);return "Universidad encontrada";}));
        findViewById(R.id.modificar).setOnClickListener(v->operar(()->{if(!dao.editar(id(),texto(R.id.nombre),texto(R.id.web)))throw new Exception("No existe una universidad con ese ID");return "Universidad modificada";}));
        findViewById(R.id.eliminar).setOnClickListener(v->{
            try{long id=id(); Universidad u=dao.buscar(id);if(u==null){mensaje("No existe una universidad con ese ID");return;}
                new AlertDialog.Builder(this).setTitle("Eliminar universidad").setMessage("¿Eliminar " + u.nombre + "?").setPositiveButton("Eliminar",(d,w)->operar(()->{if(!dao.eliminar(id))throw new Exception("La universidad ya no existe"); limpiar();return "Universidad eliminada";})).setNegativeButton("Cancelar",null).show();
            }catch(Exception e){mensaje(e.getMessage());}
        });
        findViewById(R.id.limpiar).setOnClickListener(v->{limpiar();mensaje("Formulario listo para una universidad nueva");});
        findViewById(R.id.volver).setOnClickListener(v->finish()); listar();
    }
    private long id(){try{long id=Long.parseLong(texto(R.id.codigo));if(id>0)return id;}catch(NumberFormatException ignored){}throw new IllegalArgumentException("Escribe un ID válido o selecciona una universidad de la lista");}
    private void mostrar(Universidad u){poner(R.id.codigo,""+u.id);poner(R.id.nombre,u.nombre);poner(R.id.web,u.www);}
    private void limpiar(){poner(R.id.codigo,"");poner(R.id.nombre,"");poner(R.id.web,"");}
    private void listar(){List<Universidad> datos=dao.listar(); ListView lista=findViewById(R.id.lista);lista.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datos));lista.setOnItemClickListener((p,v,pos,i)->{mostrar(datos.get(pos));mensaje("Universidad seleccionada");}); poner(R.id.conteo,datos.size()+" universidades · toca una para seleccionarla");}
    private void operar(java.util.concurrent.Callable<String> op){try{mensaje(op.call());listar();}catch(Exception e){mensaje(e.getMessage());}}
    @Override protected void onDestroy(){if(dao!=null)dao.close();super.onDestroy();}
}
