CREATE DATABASE IF NOT EXISTS soul_up
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE soul_up;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS treinos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    exercicio VARCHAR(80) NOT NULL,
    repeticoes INT NOT NULL,
    duracao INT NOT NULL,
    feedback TEXT NOT NULL,
    data_treino DATETIME NOT NULL,
    CONSTRAINT fk_treinos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);

CREATE INDEX idx_treinos_usuario_data
    ON treinos (usuario_id, data_treino DESC);
