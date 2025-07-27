-- Script para adicionar colunas faltantes na tabela associado
ALTER TABLE associado ADD COLUMN IF NOT EXISTS tipo VARCHAR(20) DEFAULT 'ASSOCIADO';
ALTER TABLE associado ADD COLUMN IF NOT EXISTS foto_perfil VARCHAR(255);

-- Atualizar registros existentes se houver
UPDATE associado SET tipo = CASE 
    WHEN admin = true THEN 'ADMIN' 
    ELSE 'ASSOCIADO' 
END WHERE tipo IS NULL;

-- Opcional: remover a coluna admin antiga (descomente se quiser)
-- ALTER TABLE associado DROP COLUMN IF EXISTS admin;
