#!/bin/bash

# Script para gerenciar os containers do FinanceFit
echo "🐳 FinanceFit Docker Manager 🐳"
echo "================================"

case "$1" in
    "start")
        echo "🚀 Iniciando containers..."
        docker-compose up -d
        echo "✅ Containers iniciados!"
        echo "📱 API disponível em: http://localhost:8080"
        echo "🗄️  MySQL disponível em: localhost:3307"
        ;;
    "stop")
        echo "⏹️  Parando containers..."
        docker-compose stop
        echo "✅ Containers parados!"
        ;;
    "restart")
        echo "🔄 Reiniciando containers..."
        docker-compose restart
        echo "✅ Containers reiniciados!"
        ;;
    "logs")
        echo "📋 Mostrando logs..."
        if [ -n "$2" ]; then
            docker-compose logs -f "$2"
        else
            docker-compose logs -f
        fi
        ;;
    "build")
        echo "🔨 Fazendo build dos containers..."
        docker-compose build --no-cache
        echo "✅ Build concluído!"
        ;;
    "clean")
        echo "🧹 Limpando containers e volumes..."
        docker-compose down -v
        docker system prune -f
        echo "✅ Limpeza concluída!"
        ;;
    "status")
        echo "📊 Status dos containers:"
        docker-compose ps
        ;;
    "shell")
        if [ -n "$2" ]; then
            echo "🐚 Abrindo shell no container $2..."
            docker-compose exec "$2" /bin/bash
        else
            echo "❌ Especifique o serviço: ./docker.sh shell financefit-api"
        fi
        ;;
    *)
        echo "📖 Uso: $0 {start|stop|restart|logs|build|clean|status|shell}"
        echo ""
        echo "Comandos disponíveis:"
        echo "  start   - Inicia todos os containers"
        echo "  stop    - Para todos os containers"
        echo "  restart - Reinicia todos os containers"
        echo "  logs    - Mostra logs (opcional: especificar serviço)"
        echo "  build   - Reconstrói as imagens"
        echo "  clean   - Remove containers, imagens e volumes"
        echo "  status  - Mostra status dos containers"
        echo "  shell   - Abre shell em um container"
        echo ""
        echo "Exemplos:"
        echo "  $0 start"
        echo "  $0 logs financefit-api"
        echo "  $0 shell mysql-db"
        exit 1
        ;;
esac
