"""Prueba real del contrato HTTP. Crea cuentas temporales y las elimina al terminar."""
import json, urllib.request, urllib.parse, urllib.error, uuid
URL='http://127.0.0.1/taller_android/operacion.php'
def peticion(accion, token='', **datos):
    req=urllib.request.Request(URL,data=urllib.parse.urlencode(dict(accion=accion,**datos)).encode(),headers={'Authorization':'Bearer '+token} if token else {})
    try:
        with urllib.request.urlopen(req,timeout=15) as r:return r.status,json.load(r)
    except urllib.error.HTTPError as e:return e.code,json.load(e)
def verificar():
    email='prueba_'+uuid.uuid4().hex+'@example.com'
    email2='prueba_'+uuid.uuid4().hex+'@example.com'
    token=token2=''
    try:
        assert peticion('listar')[0]==401
        s,j=peticion('registrar',email=email,psw='Prueba123!',nombre='Prueba automática'); assert s==200,j;token=j['token']
        s,j=peticion('registrar',email=email2,psw='Prueba123!',nombre='Segunda prueba');assert s==200,j;token2=j['token']
        assert peticion('registrar',email=email,psw='Prueba123!',nombre='Repetido')[0]==409
        assert peticion('login',email=email,psw='incorrecta')[0]==401
        s,j=peticion('listar',token);assert s==200 and any(u['email']==email for u in j['usuarios'])
        assert all('password_hash' not in u and 'password' not in u for u in j['usuarios'])
        assert peticion('editar',token,nombre='Cuenta actualizada',psw='Nueva123!',email=email2)[0]==200
        s,j=peticion('listar',token2);assert any(u['email']==email2 and u['nombre']=='Segunda prueba' for u in j['usuarios'])
        assert peticion('login',email=email,psw='Prueba123!')[0]==401
        s,j=peticion('login',email=email,psw='Nueva123!');assert s==200; nuevo=j['token']
        assert peticion('salir',nuevo)[0]==200
        assert peticion('listar',nuevo)[0]==401
        assert peticion('eliminar',token2)[0]==200;token2=''
        assert peticion('eliminar',token)[0]==200;token=''
        assert peticion('login',email=email,psw='Nueva123!')[0]==401
        print('OK: registro, duplicados, login válido/inválido, listado, edición propia, cambio de clave, cierre y eliminación; cuentas de prueba eliminadas.')
    finally:
        if token2:peticion('eliminar',token2)
        if token:peticion('eliminar',token)

if __name__ == '__main__': verificar()
