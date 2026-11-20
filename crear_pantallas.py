"""Genera los layouts XML del proyecto. No es necesario ejecutarlo para usar la app."""
from pathlib import Path
from xml.sax.saxutils import escape

out=Path(__file__).parent/'app/src/main/res/layout'
out.mkdir(parents=True,exist_ok=True)
def texto(value,id=None,size=16):
    return '<TextView '+(f'android:id="@+id/{id}" ' if id else '')+f'android:layout_width="match_parent" android:layout_height="wrap_content" android:text="{escape(value)}" android:textSize="{size}sp" android:textColor="@color/text_primary" android:layout_marginBottom="12dp" />'
def campo(id,hint,tipo='text',editable=True):
    ejemplos={'textEmailAddress':'Ejemplo: tu@correo.com','textPassword':'Escribe tu contraseña','textPersonName':'Escribe tu nombre','textCapWords':'Escribe el nombre de la universidad','textUri':'Ejemplo: https://universidad.edu','number':'Escribe el ID para buscar'}
    etiqueta=f'<TextView style="@style/AppFieldLabel" android:layout_width="match_parent" android:layout_height="wrap_content" android:labelFor="@+id/{id}" android:text="{escape(hint)}" />'
    return etiqueta+f'<EditText style="@style/AppField" android:id="@+id/{id}" android:layout_width="match_parent" android:layout_height="wrap_content" android:hint="{escape(ejemplos.get(tipo,hint))}" android:inputType="{tipo}" android:maxLines="1" android:importantForAutofill="no" '+('android:enabled="false" ' if not editable else '')+'/>'
def boton(id,value):
    return f'<Button style="@style/AppButton" android:id="@+id/{id}" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="{escape(value)}" />'
estado='<ProgressBar android:id="@+id/progreso" style="?android:attr/progressBarStyleHorizontal" android:layout_width="match_parent" android:layout_height="4dp" android:indeterminate="true" android:visibility="gone" />'+texto('','estado')
lista='<ListView android:id="@+id/lista" android:layout_width="match_parent" android:layout_height="0dp" android:layout_weight="1" android:background="@color/field_background" android:divider="@color/button_border" android:dividerHeight="1dp" />'
def guardar(nombre,title,desc,contenido,footer=''):
    xml='<?xml version="1.0" encoding="utf-8"?>\n<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:id="@+id/root" android:orientation="vertical" android:padding="24dp" android:background="@color/screen_background" android:layout_width="match_parent" android:layout_height="match_parent">'
    xml+='<ScrollView android:layout_width="match_parent" android:layout_height="'+('0dp" android:layout_weight="3' if footer else 'match_parent')+'" android:fillViewport="true"><LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="vertical">'
    xml+=texto('NOTAS DOCENTE',size=13)+texto(title,size=28)+texto(desc)+contenido+estado+'</LinearLayout></ScrollView>'+footer+'</LinearLayout>\n'
    from xml.dom import minidom
    (out/(nombre+'.xml')).write_text(minidom.parseString(xml).toprettyxml(indent='    ',encoding='utf-8').decode('utf-8'),encoding='utf-8')
guardar('login','Tu espacio académico','Inicia sesión o completa los tres campos para crear tu cuenta.',campo('email','Correo electrónico','textEmailAddress')+campo('clave','Contraseña','textPassword')+campo('nombre','Nombre (para registrarte)','textPersonName')+boton('entrar','Iniciar sesión')+boton('registrar','Crear mi cuenta')+boton('configurar','Configurar servidor'))
guardar('menu','Panel principal','Universidades en el dispositivo · Usuarios en el servidor',texto('','bienvenida',22)+boton('universidades','Administrar universidades')+boton('usuarios','Usuarios y mi perfil')+boton('salir','Cerrar sesión'))
guardar('universidades','Universidades','Guarda y consulta universidades en este dispositivo.',campo('codigo','ID (se genera al guardar)','number')+campo('nombre','Nombre de la universidad','textCapWords')+campo('web','Sitio web','textUri')+boton('guardar','Guardar nueva')+boton('buscar','Buscar por ID')+boton('modificar','Modificar seleccionada')+boton('eliminar','Eliminar seleccionada')+boton('limpiar','Limpiar formulario'),texto('','conteo')+lista+boton('volver','Volver al menú'))
guardar('usuarios','Usuarios y mi perfil','Edita tu cuenta y consulta los usuarios registrados.',campo('email','Correo','textEmailAddress',False)+campo('nombre','Tu nombre','textPersonName')+campo('clave','Nueva contraseña (opcional)','textPassword')+boton('modificar','Guardar mi perfil')+boton('eliminar','Eliminar mi cuenta')+boton('actualizar','Actualizar listado'),lista+boton('volver','Volver al menú'))
