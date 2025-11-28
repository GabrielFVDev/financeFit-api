#!/bin/bash

# Script para testar os novos endpoints

echo "🧪 Testando endpoints de Despesas e Receitas"
echo "============================================="
echo ""

# Configuração
BASE_URL="http://localhost:8080"
USER_ID=4

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}⚠️  IMPORTANTE: Você precisa de um token JWT válido!${NC}"
echo ""
read -p "Cole seu token JWT aqui: " JWT_TOKEN
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📝 Testando: POST /usuarios/${USER_ID}/receitas${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "${BASE_URL}/usuarios/${USER_ID}/receitas" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": '${USER_ID}',
    "valor": 500,
    "descricao": "Salário de Teste",
    "data": "2025-11-19",
    "idCategoria": 1
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 201 ] || [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✅ Status: $HTTP_CODE OK${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}❌ Status: $HTTP_CODE ERRO${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY"
fi

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📝 Testando: POST /usuarios/${USER_ID}/despesas${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "${BASE_URL}/usuarios/${USER_ID}/despesas" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": '${USER_ID}',
    "valor": 150,
    "descricao": "Supermercado de Teste",
    "data": "2025-11-19",
    "idCategoria": 2
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 201 ] || [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✅ Status: $HTTP_CODE OK${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}❌ Status: $HTTP_CODE ERRO${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY"
fi

echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 Testando: GET /usuarios/${USER_ID}/despesas"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/usuarios/${USER_ID}/despesas" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✅ Status: $HTTP_CODE OK${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}❌ Status: $HTTP_CODE ERRO${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💰 Testando: GET /usuarios/${USER_ID}/receitas"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/usuarios/${USER_ID}/receitas" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✅ Status: $HTTP_CODE OK${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}❌ Status: $HTTP_CODE ERRO${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📈 Testando: GET /usuarios/${USER_ID}/resumo"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}/usuarios/${USER_ID}/resumo" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✅ Status: $HTTP_CODE OK${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}❌ Status: $HTTP_CODE ERRO${NC}"
    echo ""
    echo "📦 Resposta:"
    echo "$BODY"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎯 Resumo dos Testes"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Se todos os endpoints retornaram 200 OK, tudo está funcionando!"
echo "❌ Se algum retornou 500, verifique os logs do Spring Boot"
echo "🔒 Se retornou 401/403, verifique se o token JWT está correto"
echo ""

