$ErrorActionPreference = 'Stop'
$xampp = 'C:\xampp'
$destino = Join-Path $xampp 'htdocs\taller_android'
if (-not (Test-Path -LiteralPath (Join-Path $xampp 'php\php.exe'))) { throw 'No se encontró XAMPP en C:\xampp.' }
if (-not (Test-Path -LiteralPath $destino)) { New-Item -ItemType Directory -Path $destino | Out-Null }
foreach ($nombre in @('conexion_bd.php','operacion.php','base_datos.sql','.htaccess')) {
    Copy-Item -LiteralPath (Join-Path $PSScriptRoot "servidor\$nombre") -Destination (Join-Path $destino $nombre)
}
if (-not (Get-Process mysqld -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath (Join-Path $xampp 'mysql\bin\mysqld.exe') -ArgumentList '--defaults-file=C:\xampp\mysql\bin\my.ini','--standalone' -WorkingDirectory (Join-Path $xampp 'mysql\bin') -WindowStyle Hidden
}
$mysql = Join-Path $xampp 'mysql\bin\mysql.exe'
$listo = $false
for ($i=0; $i -lt 20; $i++) {
    & $mysql --host=127.0.0.1 --user=root --execute='SELECT 1' 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) { $listo=$true; break }
    Start-Sleep -Milliseconds 500
}
if (-not $listo) { throw 'MySQL no responde con el usuario root sin contraseña. Ajusta la configuración según tu instalación.' }
$sql = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'servidor\base_datos.sql') -Raw
$sql | & $mysql --host=127.0.0.1 --user=root --default-character-set=utf8mb4
if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear la base de datos.' }
if (-not (Get-Process httpd -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath (Join-Path $xampp 'apache\bin\httpd.exe') -WorkingDirectory (Join-Path $xampp 'apache\bin') -WindowStyle Hidden
}
Write-Host 'Servidor listo: http://localhost/taller_android/operacion.php'
Write-Host 'En Android usa Configurar servidor. Para el emulador oficial: http://10.0.2.2/taller_android/operacion.php'
