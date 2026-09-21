-- ============================================================
-- Jhon Barbearia — Dados Iniciais de Exemplo
-- Execute após o schema.sql ou após o Hibernate criar as tabelas
-- ============================================================

USE barbearia;

-- Serviços
INSERT INTO servicos (nome, preco, duracao_minutos) VALUES
('Corte Masculino', 35.00, 30),
('Barba', 25.00, 30),
('Corte + Barba', 55.00, 60),
('Sobrancelha', 15.00, 15),
('Pigmentação', 80.00, 45),
('Hidratação Capilar', 40.00, 30);

-- Barbeiros
INSERT INTO barbeiros (nome, especialidade) VALUES
('João', 'Corte Masculino e Degradê'),
('Pedro', 'Barba e Acabamento');

-- Escalas do João (segunda a sábado)
INSERT INTO barbeiro_escalas (barbeiro_id, dia_semana, hora_inicio, hora_fim) VALUES
(1, 'SEGUNDA', '09:00:00', '19:00:00'),
(1, 'TERCA',   '09:00:00', '20:00:00'),
(1, 'QUARTA',  '09:00:00', '21:00:00'),
(1, 'QUINTA',  '09:00:00', '21:00:00'),
(1, 'SEXTA',   '09:00:00', '21:00:00'),
(1, 'SABADO',  '09:00:00', '20:00:00');

-- Escalas do Pedro (terça a sexta)
INSERT INTO barbeiro_escalas (barbeiro_id, dia_semana, hora_inicio, hora_fim) VALUES
(2, 'TERCA',   '10:00:00', '19:00:00'),
(2, 'QUARTA',  '10:00:00', '20:00:00'),
(2, 'QUINTA',  '10:00:00', '20:00:00'),
(2, 'SEXTA',   '10:00:00', '20:00:00');
