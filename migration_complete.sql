-- Execute este script no seu PostgreSQL para aplicar as migrações

-- 1. Conectar ao banco (execute manualmente)
-- psql -U postgres -d backclub_db

-- 2. Criar tabela associado se não existir
CREATE TABLE IF NOT EXISTS associado (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    endereco TEXT,
    is_valido BOOLEAN DEFAULT true,
    admin BOOLEAN DEFAULT false
);

-- 3. Adicionar colunas novas
ALTER TABLE associado ADD COLUMN IF NOT EXISTS tipo VARCHAR(20) DEFAULT 'ASSOCIADO';
ALTER TABLE associado ADD COLUMN IF NOT EXISTS foto_perfil VARCHAR(255);

-- 4. Migrar dados existentes
UPDATE associado SET tipo = CASE 
    WHEN admin = true THEN 'ADMIN' 
    ELSE 'ASSOCIADO' 
END WHERE tipo IS NULL OR tipo = '';

-- 5. Inserir usuários de teste se não existirem
INSERT INTO associado (nome, cpf, email, senha, telefone, endereco, tipo, is_valido) 
VALUES 
    ('Admin Sistema', '12345678901', 'admin@clubesportivo.com', 'admin123', '(11) 98765-4321', 'Rua dos Administradores, 100', 'ADMIN', true),
    ('João Silva Santos', '98765432100', 'joao.silva@email.com', 'joao123', '(11) 99999-8888', 'Rua das Flores, 123', 'ASSOCIADO', true)
ON CONFLICT (cpf) DO NOTHING;

-- 6. Verificar se os dados foram inseridos
SELECT id, nome, email, tipo FROM associado;

-- Script executado com sucesso!
