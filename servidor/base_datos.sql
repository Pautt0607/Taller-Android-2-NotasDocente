CREATE DATABASE IF NOT EXISTS taller_android CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE taller_android;
CREATE TABLE IF NOT EXISTS Usuarios (
    email VARCHAR(190) PRIMARY KEY,
    nombre VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS Sesiones (
    token_hash CHAR(64) PRIMARY KEY,
    email VARCHAR(190) NOT NULL,
    vence DATETIME NOT NULL,
    FOREIGN KEY (email) REFERENCES Usuarios(email) ON DELETE CASCADE,
    INDEX(vence)
) ENGINE=InnoDB;
