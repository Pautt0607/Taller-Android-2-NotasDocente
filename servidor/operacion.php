<?php
declare(strict_types=1);
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
function respuesta(int $estado, array $datos): never {
    http_response_code($estado);
    echo json_encode($datos, JSON_UNESCAPED_UNICODE|JSON_THROW_ON_ERROR);
    exit;
}
function falla(int $estado, string $mensaje): never { respuesta($estado, ['ok'=>false,'mensaje'=>$mensaje]); }
function parametro(string $nombre): string {
    $valor = $_POST[$nombre] ?? '';
    if (!is_string($valor)) falla(400, 'Parámetro inválido: '.$nombre);
    return $valor;
}
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { header('Allow: POST'); falla(405, 'Usa una petición POST'); }
try {
    require __DIR__.'/conexion_bd.php';
    $accion = strtolower(trim(parametro('accion')));
    if ($accion === 'registrar' || $accion === 'login') {
        $email = strtolower(trim(parametro('email')));
        $psw = parametro('psw');
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email)>190) falla(400,'Correo inválido');
        if (strlen($psw)>72) falla(400,'La contraseña debe tener como máximo 72 bytes');
        if ($accion === 'registrar') {
            $nombre = trim(parametro('nombre'));
            if ($nombre==='' || strlen($nombre)>160 || strlen($psw)<6) falla(400,'Escribe un nombre y una contraseña de al menos 6 caracteres');
            try {
                $q=$pdo->prepare('INSERT INTO Usuarios(email,nombre,password_hash) VALUES(?,?,?)');
                $q->execute([$email,$nombre,password_hash($psw,PASSWORD_DEFAULT)]);
            } catch(PDOException $e) { if ($e->getCode()==='23000') falla(409,'Este correo ya está registrado'); throw $e; }
        }
        $q=$pdo->prepare('SELECT email,nombre,password_hash FROM Usuarios WHERE email=?'); $q->execute([$email]); $u=$q->fetch();
        if (!$u || !password_verify($psw,$u['password_hash'])) falla(401,'Correo o contraseña incorrectos');
        $token=bin2hex(random_bytes(32));
        $q=$pdo->prepare('INSERT INTO Sesiones(token_hash,email,vence) VALUES(?,?,DATE_ADD(NOW(),INTERVAL 8 HOUR))'); $q->execute([hash('sha256',$token),$email]);
        unset($u['password_hash']);
        respuesta(200,['ok'=>true,'mensaje'=>'OK','token'=>$token,'usuario'=>$u]);
    }
    $autorizacion=$_SERVER['HTTP_AUTHORIZATION']??$_SERVER['REDIRECT_HTTP_AUTHORIZATION']??'';
    if (!preg_match('/^Bearer ([a-f0-9]{64})$/D',$autorizacion,$partes)) falla(401,'Inicia sesión para continuar');
    $hash=hash('sha256',$partes[1]);
    $q=$pdo->prepare('SELECT email FROM Sesiones WHERE token_hash=? AND vence>NOW()');$q->execute([$hash]);$email=$q->fetchColumn();
    if (!$email) falla(401,'La sesión venció. Vuelve a iniciar sesión');
    switch($accion) {
        case 'listar':
            $usuarios=$pdo->query('SELECT email,nombre FROM Usuarios ORDER BY nombre')->fetchAll();
            respuesta(200,['ok'=>true,'usuarios'=>$usuarios]);
        case 'editar':
            $nombre=trim(parametro('nombre')); $psw=parametro('psw');
            if ($nombre==='' || strlen($nombre)>160) falla(400,'Nombre inválido');
            if ($psw!=='' && (strlen($psw)<6 || strlen($psw)>72)) falla(400,'Contraseña inválida: entre 6 y 72 bytes');
            if ($psw==='') { $q=$pdo->prepare('UPDATE Usuarios SET nombre=? WHERE email=?');$q->execute([$nombre,$email]); }
            else { $q=$pdo->prepare('UPDATE Usuarios SET nombre=?,password_hash=? WHERE email=?');$q->execute([$nombre,password_hash($psw,PASSWORD_DEFAULT),$email]); }
            respuesta(200,['ok'=>true,'mensaje'=>'Usuario actualizado']);
        case 'eliminar':
            $q=$pdo->prepare('DELETE FROM Usuarios WHERE email=?');$q->execute([$email]);
            respuesta(200,['ok'=>true,'mensaje'=>'Cuenta eliminada']);
        case 'salir':
            $q=$pdo->prepare('DELETE FROM Sesiones WHERE token_hash=?');$q->execute([$hash]);
            respuesta(200,['ok'=>true,'mensaje'=>'Sesión cerrada']);
        default: falla(400,'Acción desconocida');
    }
} catch(Throwable $e) {
    error_log((string)$e);
    falla(500,'No se pudo completar la operación. Revisa MySQL y la configuración del servidor.');
}
