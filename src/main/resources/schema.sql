-- =========================================================
-- BANCO DE DADOS
-- =========================================================

CREATE DATABASE IF NOT EXISTS manutencao_db -- cria o banco, somente se ele não existir
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE manutencao_db;

-- a ordem de criação das tabelas importam por causa das chaves estrangeiras
-- =========================================================
-- TABELA EMPRESA
-- =========================================================

CREATE TABLE IF NOT EXISTS empresa (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18) NOT NULL,
    endereco VARCHAR(255),
    segmento VARCHAR(100),
    status ENUM('ATIVADA', 'DESATIVADA') NOT NULL,

    CONSTRAINT pk_empresa
        PRIMARY KEY (id),

    CONSTRAINT uk_empresa_cnpj
        UNIQUE (cnpj)
) ENGINE = InnoDB;


-- =========================================================
-- TABELA USUÁRIO
-- =========================================================

CREATE TABLE IF NOT EXISTS usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    tipo_usuario ENUM(
        'ADMINISTRADOR',
        'GESTOR',
        'TECNICO'
    ) NOT NULL,

    CONSTRAINT pk_usuario
        PRIMARY KEY (id),

    CONSTRAINT uk_usuario_email
        UNIQUE (email)
) ENGINE = InnoDB;


-- =========================================================
-- TABELA ADMINISTRADOR
-- =========================================================

CREATE TABLE IF NOT EXISTS administrador (
    usuario_id INT NOT NULL,
    nivel_acesso ENUM('TOTAL', 'RESTRITO') NOT NULL DEFAULT 'TOTAL',
    departamento VARCHAR(100) NOT NULL,

    CONSTRAINT pk_administrador
        PRIMARY KEY (usuario_id),

    CONSTRAINT fk_administrador_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- =========================================================
-- TABELA GESTOR
-- =========================================================

CREATE TABLE IF NOT EXISTS gestor (
    usuario_id INT NOT NULL,
    area_responsavel VARCHAR(100) NOT NULL,

    CONSTRAINT pk_gestor
        PRIMARY KEY (usuario_id),

    CONSTRAINT fk_gestor_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- =========================================================
-- TABELA TÉCNICO
-- =========================================================

CREATE TABLE IF NOT EXISTS tecnico (
    usuario_id INT NOT NULL,
    especialidade VARCHAR(100) NOT NULL,

    CONSTRAINT pk_tecnico
        PRIMARY KEY (usuario_id),

    CONSTRAINT fk_tecnico_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- =========================================================
-- TABELA EQUIPAMENTO
-- =========================================================

CREATE TABLE IF NOT EXISTS equipamento (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    codigo_patrimonio VARCHAR(100) NOT NULL,
    data_aquisicao DATE NOT NULL,
    empresa_id INT NOT NULL,

    CONSTRAINT pk_equipamento
        PRIMARY KEY (id),

    CONSTRAINT uk_equipamento_codigo_patrimonio
        UNIQUE (codigo_patrimonio),

    CONSTRAINT fk_equipamento_empresa
        FOREIGN KEY (empresa_id)
        REFERENCES empresa (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;


-- =========================================================
-- TABELA MANUTENÇÃO
-- =========================================================

CREATE TABLE IF NOT EXISTS manutencao (
    id INT NOT NULL AUTO_INCREMENT,
    tipo_manutencao ENUM(
        'PREVENTIVA',
        'CORRETIVA'
    ) NOT NULL,

    data_inicio DATE NOT NULL,
    data_fim DATE NULL,
    descricao TEXT NOT NULL,
    custo DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    status ENUM(
        'ANDAMENTO',
        'CONCLUIDA',
        'CANCELADA'
    ) NOT NULL DEFAULT 'ANDAMENTO',

    equipamento_id INT NOT NULL,
    tecnico_id INT NOT NULL,

    CONSTRAINT pk_manutencao
        PRIMARY KEY (id),

    CONSTRAINT fk_manutencao_equipamento
        FOREIGN KEY (equipamento_id)
        REFERENCES equipamento (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_manutencao_tecnico
        FOREIGN KEY (tecnico_id)
        REFERENCES tecnico (usuario_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;