# Notas Docente

Aplicación Android desarrollada en Java con interfaces XML. Integra la administración local de universidades con SQLite y el registro e inicio de sesión de usuarios mediante HTTP, PHP y MySQL.

## Funciones

- Registro e inicio de sesión.
- Crear, buscar por ID, modificar, eliminar y listar universidades en SQLite.
- Consultar el directorio de usuarios registrados en el servidor.
- Editar el perfil y eliminar la propia cuenta.
- Cerrar sesión.
- Interfaces claras con etiquetas permanentes en los formularios.

Las universidades se guardan en el dispositivo. Las cuentas se guardan en MySQL; no se sincronizan universidades con el servidor. Las tablas de programas, alumnos, asignaturas y calificaciones están definidas como base, pero sus pantallas no están implementadas.

## Abrir en Android Studio

1. Clonar o descargar este repositorio.
2. En Android Studio, seleccionar **Open** y abrir la carpeta raíz.
3. Esperar a que Gradle sincronice el proyecto.
4. Seleccionar un emulador o dispositivo y ejecutar el módulo **app**.

El proyecto utiliza Java 17, Android Gradle Plugin 9.0.0, Gradle 9.3.0 y compile SDK 37.0. Requiere Android 7.0 o posterior. Se recomienda el JDK incluido en Android Studio. AGP muestra una advertencia por el SDK 37.0; la compilación fue verificada con esa combinación.

## Servidor local con XAMPP

Con XAMPP instalado en `C:\xampp`, ejecutar en PowerShell desde la raíz:

```powershell
powershell -ExecutionPolicy Bypass -File .\iniciar-servidor.ps1
```

El script copia el servicio a `C:\xampp\htdocs\taller_android`, crea la base de datos si no existe e inicia Apache y MySQL. Usa la configuración local de MySQL con `root` sin contraseña.

También se puede copiar manualmente la carpeta `servidor` a `htdocs\taller_android` e importar `servidor/base_datos.sql` con phpMyAdmin. Para otras credenciales, crear `config.local.php` junto al servicio:

```php
<?php
return ['user' => 'usuario_local', 'password' => 'clave_local', 'port' => 3306];
```

Ese archivo está excluido del control de versiones. La práctica utiliza HTTP local en la variante debug; un despliegue externo requiere HTTPS y configuración propia.

## Conectar la aplicación

En la pantalla inicial, pulsar **Configurar servidor**:

- Emulador oficial de Android Studio: `http://10.0.2.2/taller_android/operacion.php`.
- MEmu o un dispositivo conectado por ADB: ejecutar `adb reverse tcp:8080 tcp:80` y usar `http://127.0.0.1:8080/taller_android/operacion.php`.
- Dispositivo en la misma red: usar la dirección IP local del equipo que ejecuta Apache.

La dirección debe terminar en `operacion.php`. El servicio recibe peticiones POST y devuelve JSON. Abrir esa dirección en un navegador devuelve un mensaje indicando que debe utilizarse POST.

## Estructura

- `app/src/main/java/co/edu/taller/notasdocente/datos`: entidad Universidad.
- `app/src/main/java/co/edu/taller/notasdocente/modelo`: base de datos SQLite y DAO.
- `app/src/main/java/co/edu/taller/notasdocente/controladores`: conexión HTTP y sesión.
- `app/src/main/java/co/edu/taller/notasdocente/vistas`: actividades y eventos.
- `app/src/main/res`: interfaces, colores y estilos.
- `servidor`: servicio PHP y esquema MySQL.

## Comprobaciones

```powershell
.\gradlew.bat assembleDebug lintDebug
```

Las pruebas HTTP y de interfaz utilizadas durante el desarrollo están en `verificar_servidor.py` y `verificar_app.py`. Requieren Python; la prueba de interfaz utiliza ADB y crea cuentas temporales de prueba. Las instrucciones de uso detalladas están en `LEEME.txt`.
