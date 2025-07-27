-- Script para adicionar um usuário admin temporário
-- Execute este script no pgAdmin para conseguir fazer login como admin

-- 1. Verificar se já existe um admin com este email
SELECT id, nome, email, is_admin FROM associado WHERE email = 'admin@clubesportivo.com';

-- 2. Se não existir, inserir Admin temporário
INSERT INTO associado (nome, cpf, email, senha, telefone, is_admin, foto_perfil) 
SELECT 'Admin Sistema', '12345678901', 'admin@clubesportivo.com', 'admin123', '(11) 98765-4321', true, null
WHERE NOT EXISTS (SELECT 1 FROM associado WHERE email = 'admin@clubesportivo.com');

-- 3. Atualizar usuários existentes para garantir que is_admin seja false para não-admins
UPDATE associado SET is_admin = false WHERE is_admin IS NULL;

-- 4. Verificar usuários disponíveis
SELECT id, nome, email, senha, is_admin FROM associado ORDER BY id;
