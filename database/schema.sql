-- ============================================================
-- Jhon Barbearia — Schema do Banco de Dados (MySQL 8+)
-- ============================================================

CREATE DATABASE IF NOT EXISTS barbearia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE barbearia;

-- 1. Clientes
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    papel VARCHAR(20) DEFAULT 'CLIENTE',
    ativo BOOLEAN DEFAULT TRUE,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cliente_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. Barbeiros
CREATE TABLE barbeiros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especialidade VARCHAR(100),
    foto VARCHAR(200)
) ENGINE=InnoDB;

-- 3. Serviços
CREATE TABLE servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    duracao_minutos INT DEFAULT 30
) ENGINE=InnoDB;

-- 4. Agendamentos
CREATE TABLE agendamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    barbeiro_id BIGINT NOT NULL,
    data_hora DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'Agendado',
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_agendamento_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiros(id),
    CONSTRAINT uk_agendamento_barbeiro_horario UNIQUE (barbeiro_id, data_hora)
) ENGINE=InnoDB;

-- 5. Tabela de junção: Agendamento ↔ Serviços
CREATE TABLE agendamento_servicos (
    agendamento_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    PRIMARY KEY (agendamento_id, servico_id),
    CONSTRAINT fk_as_agendamento FOREIGN KEY (agendamento_id) REFERENCES agendamentos(id),
    CONSTRAINT fk_as_servico FOREIGN KEY (servico_id) REFERENCES servicos(id)
) ENGINE=InnoDB;

-- 6. Escalas dos Barbeiros
CREATE TABLE barbeiro_escalas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    barbeiro_id BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    CONSTRAINT fk_escala_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES barbeiros(id)
) ENGINE=InnoDB;
