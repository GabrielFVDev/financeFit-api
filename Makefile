# Makefile para FinanceFit API
.PHONY: help install start stop restart logs build clean status shell test

# Valores padrão
SERVICE ?= financefit-api

help: ## Mostrar esta ajuda
	@echo "🐳 FinanceFit API - Docker Commands"
	@echo "=================================="
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

install: ## Verificar se Docker está instalado
	@echo "🔍 Verificando instalação do Docker..."
	@docker --version || (echo "❌ Docker não instalado. Veja INSTALL-DOCKER.md" && exit 1)
	@docker-compose --version || (echo "❌ Docker Compose não instalado. Veja INSTALL-DOCKER.md" && exit 1)
	@echo "✅ Docker instalado corretamente!"

start: install ## Iniciar todos os containers
	@echo "🚀 Iniciando containers..."
	@docker-compose up -d
	@echo "✅ Containers iniciados!"
	@echo "📱 API: http://localhost:8080"
	@echo "🗄️  MySQL: localhost:3307"

stop: ## Parar todos os containers
	@echo "⏹️  Parando containers..."
	@docker-compose stop
	@echo "✅ Containers parados!"

restart: ## Reiniciar todos os containers
	@echo "🔄 Reiniciando containers..."
	@docker-compose restart
	@echo "✅ Containers reiniciados!"

logs: ## Mostrar logs (make logs SERVICE=mysql-db)
	@echo "📋 Mostrando logs de $(SERVICE)..."
	@docker-compose logs -f $(SERVICE)

build: install ## Reconstruir imagens
	@echo "🔨 Reconstruindo imagens..."
	@docker-compose build --no-cache
	@echo "✅ Build concluído!"

clean: ## Limpar containers, imagens e volumes
	@echo "🧹 Limpando containers e volumes..."
	@docker-compose down -v
	@docker system prune -f
	@echo "✅ Limpeza concluída!"

status: ## Mostrar status dos containers
	@echo "📊 Status dos containers:"
	@docker-compose ps

shell: ## Abrir shell no container (make shell SERVICE=mysql-db)
	@echo "🐚 Abrindo shell no $(SERVICE)..."
	@docker-compose exec $(SERVICE) /bin/bash

test: ## Testar API após inicialização
	@echo "🧪 Testando API..."
	@sleep 5  # Aguardar inicialização
	@curl -f http://localhost:8080/actuator/health > /dev/null 2>&1 && echo "✅ API está funcionando!" || echo "❌ API não responde"

dev: start ## Iniciar em modo desenvolvimento (com logs visíveis)
	@echo "💻 Iniciando em modo desenvolvimento..."
	@docker-compose up

deploy: build start test ## Build completo + deploy + teste
	@echo "🚀 Deploy completo realizado!"

backup: ## Fazer backup do banco de dados
	@echo "💾 Fazendo backup do banco..."
	@docker-compose exec mysql-db mysqldump -u financefit -pfinancefit123 financefit > backup-$(shell date +%Y%m%d-%H%M%S).sql
	@echo "✅ Backup salvo!"

# Comandos de desenvolvimento
watch: ## Acompanhar logs em tempo real
	@docker-compose logs -f

quick-start: ## Início rápido (limpar + build + start + test)
	@make clean
	@make build
	@make start
	@make test
