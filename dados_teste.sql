-- Inserir dados de teste para associado
-- Admin padrão do sistema
INSERT INTO associado (nome, cpf, email, senha, telefone, endereco, tipo, foto_perfil, ativo, is_valido) VALUES
('Admin Sistema', '12345678901', 'admin@clubesportivo.com', 'admin123', '(11) 98765-4321', 'Rua dos Administradores, 100', 'ADMIN', null, true, true),
('João Silva Santos', '98765432100', 'joao.silva@email.com', 'joao123', '(11) 99999-8888', 'Rua das Flores, 123', 'ASSOCIADO', null, true, true)
ON CONFLICT (cpf) DO NOTHING;

-- Inserir dados de teste para quadra
INSERT INTO quadra (numero, modalidade, qtd_pessoas, img, disponivel) VALUES
(1, 'Futebol', 10, 'futebol.jpg', true),
(2, 'Basquete', 8, 'basquete.jpg', true),
(3, 'Vôlei', 12, 'volei.jpg', true)
ON CONFLICT (numero) DO NOTHING;

-- Inserir dados de teste para horários
INSERT INTO horario (data, hora_inicio, hora_fim, quadra_id) VALUES
('2025-07-03', 14.0, 15.0, 1),
('2025-07-05', 16.0, 17.0, 2),
('2025-07-10', 18.0, 19.0, 3);

-- Inserir dados de teste para reservas
INSERT INTO reserva (usuario_id, quadra_id, horario_id, membros) VALUES
(1, 1, 1, ARRAY['João', 'Maria']),
(1, 2, 2, ARRAY['Pedro', 'Ana']),
(1, 3, 3, ARRAY['Carlos', 'Lucia']);
