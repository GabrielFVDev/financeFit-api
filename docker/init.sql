-- Script de inicialização do banco de dados FinanceFit
-- Este arquivo é executado automaticamente quando o container MySQL é criado

-- Garantir que o banco de dados existe
CREATE DATABASE IF NOT EXISTS financefit;

-- Usar o banco de dados
USE financefit;

-- Criar usuário específico para a aplicação (caso não exista)
-- Obs: O docker-compose já cria o usuário, mas este é um backup
CREATE USER IF NOT EXISTS 'financefit'@'%' IDENTIFIED BY 'financefit123';
GRANT ALL PRIVILEGES ON financefit.* TO 'financefit'@'%';
FLUSH PRIVILEGES;

-- Opcional: inserir dados iniciais (descomentar se necessário)
-- INSERT INTO categoria (nome) VALUES
-- ('Alimentação'),
-- ('Transporte'),
-- ('Lazer'),
-- ('Saúde'),
-- ('Educação');

-- Confirmar criação
SELECT 'Banco de dados FinanceFit inicializado com sucesso!' as status;
