package co.edu.taller.notasdocente.vistas;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import co.edu.taller.notasdocente.R;
import co.edu.taller.notasdocente.controladores.Sesion;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class BaseActivity extends Activity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private boolean ocupado=false;
    protected void pantalla(int layout) {
        setContentView(layout);
        View root=findViewById(R.id.root);
        root.setOnApplyWindowInsetsListener((v,i)-> {v.setPadding(i.getSystemWindowInsetLeft()+24,i.getSystemWindowInsetTop()+16,i.getSystemWindowInsetRight()+24,i.getSystemWindowInsetBottom()+16); return i;});
    }
    protected void mensaje(String t) { ((TextView)findViewById(R.id.estado)).setText(t); }
    protected String texto(int id) { return ((TextView)findViewById(id)).getText().toString().trim(); }
    protected void poner(int id, String t) { ((TextView)findViewById(id)).setText(t); }
    protected boolean requiereSesion() {
        if(!Sesion.token(this).isEmpty()) return true;
        startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); return false;
    }
    protected <T> void tarea(Callable<T> trabajo, Consumer<T> terminado) {
        if(ocupado) return;
        ocupado=true; mensaje("Conectando con el servidor…");
        findViewById(R.id.progreso).setVisibility(View.VISIBLE);
        worker.execute(()-> {
            try { T resultado=trabajo.call(); runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){fin(); terminado.accept(resultado);}}); }
            catch(Exception e) { runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){fin(); mensaje(e.getMessage()==null?"No se pudo conectar. Revisa el servidor y la URL.":e.getMessage());}}); }
        });
    }
    private void fin(){ ocupado=false; findViewById(R.id.progreso).setVisibility(View.GONE); }
    @Override protected void onDestroy(){ worker.shutdownNow(); super.onDestroy(); }
}
