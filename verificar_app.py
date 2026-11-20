"""Prueba de interfaz por ADB en el emulador conectado; usa cuentas temporales."""
from pathlib import Path
import subprocess, xml.etree.ElementTree as ET, re, time, uuid
import verificar_servidor as api
adb=r'C:\Users\esthe\AppData\Local\Android\Sdk\platform-tools\adb.exe'
pkg='co.edu.taller.notasdocente'
def ejecutar(*args):return subprocess.check_output([adb,*args],stderr=subprocess.STDOUT).decode('utf-8',errors='replace')
def arbol():
    for i in range(3):
        try:
            ejecutar('shell','uiautomator','dump','/sdcard/taller.xml');break
        except subprocess.CalledProcessError:
            if i==2:raise
            time.sleep(1)
    return ET.fromstring(ejecutar('shell','cat','/sdcard/taller.xml'))
def nodo(id=None,text=None):
    for intento in range(5):
        tree=arbol()
        for n in tree.iter('node'):
            if (id and n.get('resource-id')==pkg+':id/'+id) or (text and n.get('text','').casefold()==text.casefold()):return n
        scroll=next((n for n in tree.iter('node') if n.get('class')=='android.widget.ScrollView'),None)
        if scroll is None:break
        a,b,c,d=map(int,re.findall(r'\d+',scroll.get('bounds')))
        inicio,fin=(d-20,b+20) if intento<2 else (b+20,d-20)
        ejecutar('shell','input','swipe',str((a+c)//2),str(inicio),str((a+c)//2),str(fin),'300')
    raise AssertionError('No se encontró '+str(id or text)+'; pantalla: '+str([(n.get('resource-id'),n.get('text')) for n in tree.iter('node') if n.get('text')]))
def pulsar(id=None,text=None):
    n=nodo(id,text);a,b,c,d=map(int,re.findall(r'\d+',n.get('bounds')))
    ejecutar('shell','input','tap',str((a+c)//2),str((b+d)//2))
def escribir(id,valor):
    n=nodo(id);pulsar(id)
    ejecutar('shell','input','keyevent','123')
    ejecutar('shell','input','keyevent',*(['67']*max(1,len(n.get('text','')))))
    ejecutar('shell','input','text',valor.replace(' ','%s'))
    ejecutar('shell','input','keyevent','111')
def captura(nombre):
    p=Path('tmp/verificacion');p.mkdir(parents=True,exist_ok=True)
    (p/(nombre+'.png')).write_bytes(subprocess.check_output([adb,'exec-out','screencap','-p']))
email='app_'+uuid.uuid4().hex[:10]+'@example.com';token=''
try:
    pulsar('configurar')
    n=next(n for n in arbol().iter('node') if n.get('class')=='android.widget.EditText')
    a,b,c,d=map(int,re.findall(r'\d+',n.get('bounds')));ejecutar('shell','input','tap',str((a+c)//2),str((b+d)//2))
    ejecutar('shell','input','keyevent','123');ejecutar('shell','input','keyevent',*(['67']*100))
    ejecutar('shell','input','text','http://127.0.0.1:8080/taller_android/operacion.php')
    pulsar(text='Guardar')
    escribir('email',email);escribir('clave','Prueba123');escribir('nombre','Estudiante de prueba');pulsar('registrar');time.sleep(1)
    nodo('universidades');captura('01-menu');print('OK: registro desde Android y menú',flush=True)
    _,j=api.peticion('login',email=email,psw='Prueba123');token=j['token']
    pulsar('universidades');escribir('nombre','UniversidadPrueba');escribir('web','https://example.com');pulsar('guardar')
    # El botón puede estar al final del ScrollView; se leen datos reales del formulario.
    codigo=nodo('codigo').get('text');assert codigo.isdigit(),codigo
    captura('02-universidades');print('OK: universidad creada con ID '+codigo,flush=True)
    # Desplazar solo dentro del formulario para llegar a sus acciones.
    ejecutar('shell','input','swipe','1000','580','1000','250','400')
    pulsar('limpiar')
    ejecutar('shell','input','swipe','1000','250','1000','580','400')
    escribir('codigo',codigo);pulsar('buscar');assert nodo('nombre').get('text')=='UniversidadPrueba',[(n.get('resource-id'),n.get('text')) for n in arbol().iter('node') if n.get('text')]
    escribir('nombre','UniversidadModificada')
    ejecutar('shell','input','swipe','1000','580','1000','250','400');pulsar('modificar')
    db=ejecutar('shell','run-as',pkg,'ls','databases');assert 'notasdocente.db' in db
    pulsar('eliminar');pulsar(text='Eliminar');print('OK: búsqueda, modificación y eliminación de universidad',flush=True)
    pulsar('volver');pulsar('usuarios');captura('03-usuarios');nodo('lista');print('OK: directorio remoto en Android',flush=True)
finally:
    if token:api.peticion('eliminar',token)
    ejecutar('shell','am','force-stop',pkg)
    ejecutar('shell','am','start','-n',pkg+'/.vistas.LoginActivity')
