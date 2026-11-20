<?php
declare(strict_types=1);
// Ajustes de XAMPP local. Para otra instalación, crear config.local.php.
$config = ['host'=>'127.0.0.1', 'port'=>3306, 'database'=>'taller_android', 'user'=>'root', 'password'=>''];
if (is_file(__DIR__.'/config.local.php')) $config = array_replace($config, require __DIR__.'/config.local.php');
$pdo = new PDO("mysql:host={$config['host']};port={$config['port']};dbname={$config['database']};charset=utf8mb4", $config['user'], $config['password'], [PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION, PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC, PDO::ATTR_EMULATE_PREPARES=>false]);
