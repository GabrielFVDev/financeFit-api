# 💰 FinanceFit API

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=for-the-badge&logo=springboot)
  ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
  ![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
  ![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens)
  
  **API RESTful para gerenciamento de finanças pessoais com autenticação JWT e suporte Docker**
  
</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Como Executar](#-como-executar)
  - [Docker (Recomendado)](#-com-docker-recomendado)
  - [Execução Local](#-execução-local-sem-docker)
- [Endpoints da API](#-endpoints-da-api)
- [Testes](#-testes)
- [Documentação Adicional](#-documentação-adicional)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🎯 Sobre o Projeto

O **FinanceFit** é uma API REST completa para gerenciamento de finanças pessoais que oferece controle total sobre suas receitas e despesas com segurança e praticidade.

### ✨ Funcionalidades

- 🔐 **Autenticação JWT** - Sistema seguro de autenticação stateless
- 👤 **Gestão de Usuários** - Cadastro, login e gerenciamento de perfil
- 💸 **Controle de Despesas** - Registre e categorize seus gastos
- 💰 **Controle de Receitas** - Acompanhe suas fontes de renda
- 📊 **Categorias** - Organize suas transações
- 🎯 **Metas Mensais** - Defina e acompanhe limites de gastos
- 📈 **Relatórios** - Visualize suas finanças por período
- 🔒 **Segurança** - Spring Security com criptografia BCrypt
- ✅ **Validações** - Bean Validation para garantir integridade dos dados
- 🐳 **Docker Ready** - Deploy simplificado com containers

---

## 🛠 Tecnologias

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 17 | Linguagem de programação |
| **Spring Boot** | 3.5.7 | Framework principal |
| **Spring Security** | - | Autenticação e autorização |
| **Spring Data JPA** | - | Persistência de dados |
| **Hibernate** | - | ORM (Object-Relational Mapping) |
| **MySQL** | 8.0 | Banco de dados relacional |
| **H2 Database** | - | Banco em memória para testes |
| **JWT (jjwt)** | 0.12.5 | Tokens de autenticação |
| **Bean Validation** | - | Validação de dados |
| **Maven** | 3.9+ | Gerenciador de dependências |
| **Docker** | - | Containerização |
| **Docker Compose** | - | Orquestração de containers |

---

## 🚀 Como Executar

### 🐳 Com Docker (Recomendado)

A forma mais rápida e fácil de executar o projeto:

#### 1. Certifique-se de ter o Docker instalado

```bash
docker --version
docker-compose --version
```

#### 2. Inicie os containers

```bash
# Dar permissão ao script
chmod +x docker.sh

# Iniciar aplicação e banco de dados
./docker.sh start
```

#### 3. Acessar a aplicação

- **API**: http://localhost:8080
- **MySQL**: localhost:3307 (usuário: `financefit`, senha: `financefit123`)

#### 4. Parar os containers

```bash
./docker.sh stop
```

#### Outros comandos úteis:

```bash
# Parar o serviço Docker
sudo systemctl stop docker

# Desabilitar Docker na inicialização
sudo systemctl disable docker

# Ver logs da API
docker logs -f financefit-api

# Ver logs do MySQL
docker logs -f financefit-mysql

# Acessar o banco de dados
docker exec -it financefit-mysql mysql -u financefit -pfinancefit123 financefit
```

> 📖 Para mais detalhes sobre Docker, consulte [DOCKER-SETUP.md](./DOCKER-SETUP.md)

---

### 💻 Execução Local (sem Docker)

#### Pré-requisitos

- ☕ Java 17+
- 📦 Maven 3.6+
- 🗄️ MySQL 8.0+

#### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd spring
```

#### 2. Configure o banco de dados MySQL

```sql
CREATE DATABASE financefit;
```

#### 3. Configure o arquivo `application.properties`

Edite `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/financefit
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=SUA_CHAVE_SECRETA_AQUI
jwt.expiration=86400000
```

> ⚠️ **IMPORTANTE**: Altere `SUA_SENHA_AQUI` e `SUA_CHAVE_SECRETA_AQUI` para valores reais!

#### 4. Execute o projeto

```bash
# Compilar
./mvnw clean install

# Executar
./mvnw spring-boot:run
```

A API estará disponível em **http://localhost:8080**

---

## 🔌 Endpoints da API

### 🔐 Autenticação (Públicos)

#### Registrar Usuário
```http
POST /api/auth/register
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "metaMensal": 2000.00
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json
```

**Body:**
```json
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

---

### 👤 Usuários (Autenticados)

> 🔒 **Todos os endpoints abaixo requerem:**  
> `Authorization: Bearer {seu-token-jwt}`

#### Listar todos os usuários
```http
GET /usuarios
```

#### Buscar usuário por ID
```http
GET /usuarios/{id}
```

#### Criar usuário
```http
POST /usuarios
Content-Type: application/json

{
  "nome": "Maria Santos",
  "email": "maria@email.com",
  "senha": "senha456",
  "metaMensal": 1500.00
}
```

**Response (201):**
```json
{
  "id": 2,
  "nome": "Maria Santos",
  "email": "maria@email.com",
  "metaMensal": 1500.00
}
```

#### Buscar usuário por email
```http
GET /usuarios/email/{email}
Authorization: Bearer {token}
```

**Exemplo:** `GET /usuarios/email/joao@email.com`

#### Atualizar usuário
```http
PUT /usuarios/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "João Silva Atualizado",
  "email": "joao.novo@email.com",
  "senha": "novaSenha123"
}
```

#### Deletar usuário
```http
DELETE /usuarios/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

#### Alterar senha
```http
PATCH /usuarios/{id}/senha
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "senha": "novaSenha789"
}
```

#### Atualizar meta mensal
```http
PATCH /usuarios/{id}/meta
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "metaMensal": 3000.00
}
```

#### Resumo financeiro geral
```http
GET /usuarios/{id}/resumo
Authorization: Bearer {token}
```

**Response:**
```json
{
  "totalDespesas": 1500.50,
  "totalReceitas": 3000.00,
  "metaMensal": 2000.00,
  "saldo": 1499.50,
  "percentualGasto": 75.03
}
```

#### Resumo financeiro por período
```http
GET /usuarios/{id}/resumo/{mes}/{ano}
Authorization: Bearer {token}
```

**Exemplo:** `GET /usuarios/1/resumo/11/2025`

**Response:**
```json
{
  "mes": 11,
  "ano": 2025,
  "totalDespesas": 850.00,
  "totalReceitas": 2500.00,
  "metaMensal": 2000.00,
  "saldo": 1650.00,
  "percentualGasto": 42.50
}
```

---

### 📂 Categorias

#### Criar categoria
```http
POST /categorias
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "Alimentação"
}
```

**Response (201 Created):**
```json
{
  "categoriaId": 1,
  "nome": "Alimentação"
}
```

#### Listar todas as categorias
```http
GET /categorias
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "categoriaId": 1,
    "nome": "Alimentação"
  },
  {
    "categoriaId": 2,
    "nome": "Transporte"
  },
  {
    "categoriaId": 3,
    "nome": "Lazer"
  }
]
```

#### Atualizar categoria
```http
PUT /categorias/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "Alimentação e Mercado"
}
```

#### Deletar categoria
```http
DELETE /categorias/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

---

### 💸 Despesas

#### Criar despesa
```http
POST /despesas
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "valor": 150.50,
  "data": "2025-11-16",
  "descricao": "Compras no supermercado",
  "idUsuario": 1,
  "idCategoria": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "valor": 150.50,
  "data": "2025-11-16",
  "descricao": "Compras no supermercado",
  "idUsuario": 1,
  "idCategoria": 1,
  "tipo": "DESPESA"
}
```

#### Listar despesas do usuário
```http
GET /despesas/usuario/{idUsuario}
Authorization: Bearer {token}
```

**Exemplo:** `GET /despesas/usuario/1`

#### Atualizar despesa
```http
PUT /despesas/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "valor": 200.00,
  "data": "2025-11-16",
  "descricao": "Compras no supermercado (atualizado)",
  "idUsuario": 1,
  "idCategoria": 1
}
```

#### Deletar despesa
```http
DELETE /despesas/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

---

### 💰 Receitas

#### Criar receita
```http
POST /receitas
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "valor": 3000.00,
  "data": "2025-11-05",
  "descricao": "Salário",
  "idUsuario": 1,
  "idCategoria": 4
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "valor": 3000.00,
  "data": "2025-11-05",
  "descricao": "Salário",
  "idUsuario": 1,
  "idCategoria": 4,
  "tipo": "RECEITA"
}
```

#### Listar receitas do usuário
```http
GET /receitas/usuario/{idUsuario}
Authorization: Bearer {token}
```

**Exemplo:** `GET /receitas/usuario/1`

#### Atualizar receita
```http
PUT /receitas/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "valor": 3100.00,
  "data": "2025-11-05",
  "descricao": "Salário com bônus",
  "idUsuario": 1,
  "idCategoria": 4
}
```

#### Deletar receita
```http
DELETE /receitas/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

---

## 🧪 Testes

O projeto inclui testes automatizados para garantir a qualidade do código.

### Executar todos os testes

```bash
./mvnw test
```

### Executar testes com relatório de cobertura

```bash
./mvnw clean test
```

### Testes Implementados

O projeto contém testes unitários e de integração para:

- ✅ **Controllers**: Testes de API (UsuarioController)
- ✅ **Services**: Lógica de negócio (UsuarioService, CategoriaService, DespesaService, ReceitaService)
- ✅ **Integração**: Testes de contexto da aplicação

### Configuração de Testes

Os testes utilizam:
- **H2 Database** - Banco de dados em memória
- **Spring Boot Test** - Framework de testes
- **JUnit 5** - Framework de testes unitários
- **Mockito** - Mock de dependências

Configurações em `src/test/resources/application-test.properties`

---

## 🔒 Segurança

### Autenticação JWT

1. **Registre-se ou faça login** para obter um token JWT
2. **Inclua o token** em todas as requisições autenticadas:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
3. **Token válido por 24 horas** (86400000 ms)

### Endpoints Públicos

Apenas 2 endpoints são públicos (não requerem autenticação):
- `POST /api/auth/register` - Registro de novos usuários
- `POST /api/auth/login` - Login

Todos os outros endpoints requerem autenticação JWT.

### Criptografia

- Senhas são criptografadas com **BCrypt**
- Tokens JWT assinados com chave secreta configurável
- Configuração via `application.properties`

---

## 📂 Estrutura do Projeto

```
spring/
├── src/
│   ├── main/
│   │   ├── java/com/financefit/financeFit/
│   │   │   ├── controllers/           # Endpoints REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CategoriaController.java
│   │   │   │   ├── DespesaController.java
│   │   │   │   ├── ReceitaController.java
│   │   │   │   └── UsuarioController.java
│   │   │   ├── dtos/                  # Data Transfer Objects
│   │   │   ├── entities/              # Entidades JPA
│   │   │   ├── exception/             # Tratamento de exceções
│   │   │   ├── repositories/          # Acesso a dados
│   │   │   ├── security/              # Configurações de segurança
│   │   │   ├── services/              # Lógica de negócio
│   │   │   └── FinanceFitApplication.java
│   │   └── resources/
│   │       ├── application.properties        # Configuração padrão
│   │       └── application-docker.properties # Configuração Docker
│   └── test/                          # Testes automatizados
│       ├── java/com/financefit/financeFit/
│       │   ├── controllers/
│       │   └── services/
│       └── resources/
│           └── application-test.properties
├── docker/
│   └── init.sql                       # Script de inicialização do BD
├── target/                            # Arquivos compilados
├── docker-compose.yml                 # Configuração Docker Compose
├── docker-compose.override.yml        # Sobrescritas do Docker
├── docker.sh                          # Script auxiliar Docker
├── Dockerfile                         # Imagem Docker da aplicação
├── pom.xml                            # Dependências Maven
├── DOCKER-SETUP.md                    # Documentação Docker
└── README.md                          # Este arquivo
```

---

## 📚 Documentação Adicional

- 📖 [**DOCKER-SETUP.md**](./DOCKER-SETUP.md) - Guia completo sobre Docker
- 📝 [**DTO_VALIDATION_FIXES.md**](./DTO_VALIDATION_FIXES.md) - Validações implementadas
- 🔄 [**UPDATES.md**](./UPDATES.md) - Histórico de atualizações
- 🐳 [**README-Docker.md**](./README-Docker.md) - Informações adicionais sobre Docker

---

## 💡 Exemplos de Uso

### Fluxo Completo de Uso

#### 1. Registrar-se

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "metaMensal": 2000.00
  }'
```

#### 2. Criar uma Categoria

```bash
curl -X POST http://localhost:8080/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "nome": "Alimentação"
  }'
```

#### 3. Registrar uma Despesa

```bash
curl -X POST http://localhost:8080/despesas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "valor": 150.50,
    "data": "2025-11-27",
    "descricao": "Compras no supermercado",
    "idUsuario": 1,
    "idCategoria": 1
  }'
```

#### 4. Registrar uma Receita

```bash
curl -X POST http://localhost:8080/receitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "valor": 3000.00,
    "data": "2025-11-05",
    "descricao": "Salário",
    "idUsuario": 1,
    "idCategoria": 4
  }'
```

#### 5. Ver Resumo Financeiro

```bash
curl -X GET http://localhost:8080/usuarios/1/resumo/11/2025 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### Script de Teste

Um script bash está disponível para testar os endpoints:

```bash
chmod +x test-endpoints.sh
./test-endpoints.sh
```

### Coleção Postman

Importe a coleção `FinanceFit-API.postman_collection.json` no Postman para testar todos os endpoints facilmente.

---

## ⚙️ Variáveis de Ambiente

Para produção, configure as seguintes variáveis de ambiente:

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `SPRING_DATASOURCE_URL` | URL do banco MySQL | `jdbc:mysql://localhost:3306/financefit` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `financefit` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `senha_segura_aqui` |
| `JWT_SECRET` | Chave secreta JWT | `chave_muito_segura_e_longa_aqui` |
| `JWT_EXPIRATION` | Tempo de expiração (ms) | `86400000` (24h) |

---

## 🐛 Troubleshooting

### Erro: "Access denied for user"
- Verifique as credenciais do MySQL no `application.properties`
- Certifique-se de que o usuário tem permissões no banco

### Erro: "Port 8080 already in use"
```bash
# Encontrar processo na porta 8080
sudo lsof -i :8080

# Matar o processo
sudo kill -9 <PID>
```

### Erro ao conectar com o Docker
```bash
# Verificar status do Docker
sudo systemctl status docker

# Iniciar Docker
sudo systemctl start docker

# Verificar containers
docker ps -a
```

### Banco de dados não inicializa
```bash
# Verificar logs do MySQL
docker logs financefit-mysql

# Recriar volumes
docker-compose down -v
docker-compose up --build
```

---

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

## 👨‍💻 Autor

Desenvolvido com ☕ e 💚 usando Spring Boot

---

## 📞 Contato e Suporte

- 📧 Email: [seu-email@exemplo.com](mailto:seu-email@exemplo.com)
- 🐛 Issues: Use a aba Issues do GitHub para reportar bugs
- 💬 Discussões: Use a aba Discussions para perguntas

---

<div align="center">
  
  **⭐ Se este projeto foi útil, considere dar uma estrela!**
  
  ![Spring Boot](https://img.shields.io/badge/Made%20with-Spring%20Boot-brightgreen?style=flat-square&logo=springboot)
  ![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
  ![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)
  
</div>
> - Não é possível recuperar os dados após a exclusão

---

## 📝 Exemplos de Uso

### Fluxo completo de uso da API

#### 1. Registrar um novo usuário
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "metaMensal": 2000.00
  }'
```

#### 2. Fazer login e obter o token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

**Salve o token retornado!**

#### 3. Criar categorias (usando o token)
```bash
curl -X POST http://localhost:8080/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "nome": "Alimentação"
  }'
```

#### 4. Registrar uma despesa
```bash
curl -X POST "http://localhost:8080/despesas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "valor": 150.50,
    "data": "2025-11-16",
    "descricao": "Compras no supermercado",
    "idUsuario": 1,
    "idCategoria": 1
  }'
```

#### 5. Registrar uma receita
```bash
curl -X POST "http://localhost:8080/receitas" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "valor": 3000.00,
    "data": "2025-11-05",
    "descricao": "Salário",
    "idUsuario": 1
  }'
```

#### 6. Ver resumo financeiro do mês
```bash
curl -X GET http://localhost:8080/usuarios/1/resumo/11/2025 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

#### 7. Ver seus próprios dados (usuário autenticado)
```bash
curl -X GET http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

#### 8. Atualizar seu nome
```bash
curl -X PATCH http://localhost:8080/usuarios/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "nome": "João Silva Junior"
  }'
```

#### 9. Atualizar seu email e senha
```bash
curl -X PATCH http://localhost:8080/usuarios/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "email": "joao.novo@email.com",
    "senha": "novaSenhaSegura123"
  }'
```

#### 10. Deletar sua conta (CUIDADO!)
```bash
curl -X DELETE http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

---

## 📁 Estrutura do Projeto

```
src/
  ├── main/
  │   ├── java/
  │   │   └── com/financefit/financeFit/
  │   │       ├── FinanceFitApplication.java          # Classe principal
  │   │       ├── controllers/                         # Controladores REST
  │   │       │   ├── AuthController.java             # Autenticação (registro/login)
  │   │       │   ├── CategoriaController.java        # Gerenciamento de categorias
  │   │       │   ├── DespesaController.java          # Gerenciamento de despesas
  │   │       │   ├── ReceitaController.java          # Gerenciamento de receitas
  │   │       │   └── UsuarioController.java          # Gerenciamento de usuários
  │   │       ├── dtos/                                # Data Transfer Objects
  │   │       │   ├── AuthResponseDTO.java            # Resposta de autenticação
  │   │       │   ├── CategoriaDTO.java
  │   │       │   ├── CreateCategoriaDTO.java
  │   │       │   ├── CreateDespesaDTO.java
  │   │       │   ├── CreateReceitaDTO.java
  │   │       │   ├── DespesaDTO.java
  │   │       │   ├── LoginDTO.java                   # Dados de login
  │   │       │   ├── ReceitaDTO.java
  │   │       │   ├── RegisterDTO.java                # Dados de registro
  │   │       │   └── UsuarioDTO.java                 # Dados de usuário
  │   │       ├── entities/                            # Entidades JPA
  │   │       │   ├── Categoria.java                  # Entidade Categoria
  │   │       │   ├── Despesa.java                    # Entidade Despesa
  │   │       │   ├── Receita.java                    # Entidade Receita
  │   │       │   ├── TipoTransacao.java              # Enum para tipo de transação
  │   │       │   └── Usuario.java                    # Entidade Usuario
  │   │       ├── exception/                           # Tratamento de exceções
  │   │       │   └── GlobalExceptionHandler.java     # Handler global
  │   │       ├── repositories/                        # Camada de persistência
  │   │       │   ├── CategoriaRepository.java
  │   │       │   ├── DespesaRepository.java
  │   │       │   ├── ReceitaRepository.java
  │   │       │   └── UsuarioRepository.java
  │   │       ├── security/                            # Configuração de segurança
  │   │       │   ├── CustomUserDetailsService.java   # Service de autenticação
  │   │       │   ├── JwtAuthenticationFilter.java    # Filtro JWT
  │   │       │   ├── JwtUtil.java                    # Utilitário JWT
  │   │       │   └── SecurityConfig.java             # Configuração Spring Security
  │   │       └── services/                            # Camada de negócio
  │   │           ├── AuthService.java
  │   │           ├── CategoriaService.java
  │   │           ├── DespesaService.java
  │   │           ├── ReceitaService.java
  │   │           └── UsuarioService.java
  │   └── resources/
  │       └── application.properties                   # Configurações da aplicação
  └── test/
      └── java/
          └── com/financefit/financeFit/
              └── FinanceFitApplicationTests.java     # Testes
```

---

## 🔒 Segurança

A API implementa as seguintes medidas de segurança:

- ✅ **Autenticação JWT stateless** - Tokens seguros para autenticação
- ✅ **Senhas criptografadas** - BCrypt para hash de senhas
- ✅ **Validação de dados** - Bean Validation em todas as entradas
- ✅ **Tratamento global de exceções** - Respostas padronizadas de erro
- ✅ **CORS configurado** - Controle de acesso entre origens
- ✅ **Endpoints protegidos** - Rotas sensíveis requerem autenticação

### Token JWT

O token JWT tem validade de **24 horas** (86400000 ms) e contém:
- Email do usuário
- Data de emissão
- Data de expiração

---

## 📊 Modelo de Dados

### Entidade: Usuario
| Campo | Tipo | Descrição |
|-------|------|-----------|
| userId | Integer (PK) | ID único do usuário |
| nome | String | Nome completo |
| email | String (Unique) | Email para login |
| senha | String | Senha criptografada |
| dataCriacao | LocalDate | Data de cadastro |
| metaMensal | Double | Meta de gastos mensais |

### Entidade: Categoria
| Campo | Tipo | Descrição |
|-------|------|-----------|
| categoriaId | Long (PK) | ID único da categoria |
| nome | String | Nome da categoria |

### Entidade: Despesa
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Integer (PK) | ID único da despesa |
| valor | BigDecimal | Valor da despesa |
| data | LocalDate | Data da despesa |
| descricao | String | Descrição opcional |
| tipo | String | "DESPESA" |
| usuario_id | Integer (FK) | Referência ao usuário |
| categoria_id | Long (FK) | Referência à categoria |

### Entidade: Receita
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Integer (PK) | ID único da receita |
| valor | BigDecimal | Valor da receita |
| data | LocalDate | Data da receita |
| descricao | String | Descrição opcional |
| tipo | String | "RECEITA" |
| usuario_id | Integer (FK) | Referência ao usuário |
| categoria_id | Long (FK) | Referência à categoria (opcional) |

---

## 🧪 Testando a API

### Usando Postman

1. Importe a collection (se disponível)
2. Configure a variável de ambiente `baseUrl` como `http://localhost:8080`
3. Após o login, salve o token na variável `token`
4. Use `{{token}}` no header Authorization

### Usando cURL (exemplos acima)

### Usando Insomnia

Similar ao Postman, configure o environment e utilize o token nos requests.

---

## 🐛 Tratamento de Erros

A API retorna erros padronizados no formato:

```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email é obrigatório",
  "path": "/api/auth/register"
}
```

### Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| 200 | OK - Requisição bem-sucedida |
| 201 | Created - Recurso criado com sucesso |
| 204 | No Content - Requisição bem-sucedida sem conteúdo |
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Não autenticado |
| 403 | Forbidden - Sem permissão |
| 404 | Not Found - Recurso não encontrado |
| 500 | Internal Server Error - Erro no servidor |

---

## 💡 Dicas de Uso

### Validações implementadas

- **Email:** Deve ser válido e único
- **Senha:** Mínimo de 6 caracteres no registro
- **Valores:** Não podem ser negativos
- **Datas:** Formato ISO (YYYY-MM-DD)
- **IDs:** Devem ser positivos e existentes

### Melhores práticas

1. **Sempre use HTTPS em produção**
2. **Armazene o token de forma segura** (localStorage, sessionStorage)
3. **Implemente refresh tokens** para melhor experiência
4. **Configure CORS** adequadamente para seu frontend
5. **Use variáveis de ambiente** para dados sensíveis
6. **Faça backup regular** do banco de dados

---

## 🚀 Próximas Funcionalidades

- [x] Receitas além de despesas
- [ ] Paginação nos endpoints de listagem
- [ ] Filtros avançados de despesas (por período, categoria, valor)
- [ ] Dashboard com gráficos de gastos
- [ ] Exportação de relatórios (PDF, Excel)
- [ ] Notificações quando ultrapassar a meta
- [ ] Categorias customizadas por usuário
- [ ] Múltiplas moedas
- [ ] Refresh token automático

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é open source e está disponível sob a licença MIT.

---

## 👨‍💻 Contato

Dúvidas ou sugestões? Entre em contato!

---

<div align="center">
  
  **Desenvolvido com ☕ e ❤️**
  
  **⭐ Se este projeto te ajudou, considere dar uma estrela!**
  
</div>