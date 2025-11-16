# 💰 FinanceFit API

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=for-the-badge&logo=springboot)
  ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
  ![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens)
  
  **API RESTful para controle de finanças pessoais com autenticação JWT**
  
</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Endpoints da API](#-endpoints-da-api)
  - [Autenticação](#autenticação)
  - [Usuários](#usuários)
  - [Categorias](#categorias)
  - [Despesas](#despesas)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🎯 Sobre o Projeto

O **FinanceFit** é uma API completa para gerenciamento de finanças pessoais que permite:

✅ Cadastro e autenticação de usuários com JWT  
✅ Gerenciamento de despesas e categorias  
✅ Definição de metas mensais de gastos  
✅ Relatórios financeiros por período  
✅ Controle total das suas finanças  

---

## 🛠 Tecnologias

Este projeto foi desenvolvido com as seguintes tecnologias:

- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.7** - Framework principal
- **Spring Security** - Segurança e autenticação
- **JWT (JSON Web Token)** - Autenticação stateless
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **MySQL 8.0** - Banco de dados
- **Maven** - Gerenciador de dependências
- **Bean Validation** - Validação de dados

---

## 📋 Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:

- ☕ [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou superior
- 📦 [Maven 3.6+](https://maven.apache.org/download.cgi)
- 🗄️ [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/)
- 🔧 Git

---

## 🚀 Instalação e Configuração

### 1️⃣ Clone o repositório

```bash
git clone <url-do-repositorio>
cd spring
```

### 2️⃣ Configure o banco de dados

**Crie o banco de dados no MySQL:**

```sql
CREATE DATABASE financefit;
```

### 3️⃣ Configure as variáveis de ambiente

Edite o arquivo `src/main/resources/application.properties`:

```properties
spring.application.name=financeFit

# Configuração do banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/financefit
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=SUA_CHAVE_SECRETA_AQUI
jwt.expiration=86400000
```

> ⚠️ **IMPORTANTE:** Em produção, use variáveis de ambiente para dados sensíveis!

### 4️⃣ Compile e execute o projeto

```bash
# Dar permissão ao Maven Wrapper (Linux/Mac)
chmod +x mvnw

# Compilar o projeto
./mvnw clean install

# Executar a aplicação
./mvnw spring-boot:run
```

A API estará disponível em: **`http://localhost:8080`**

---

## 🔌 Endpoints da API

### 🔐 Autenticação

#### Registrar novo usuário
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

**Response (200 OK):**
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

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

---

### 👤 Usuários

> 🔒 **Nota:** Todos os endpoints abaixo requerem autenticação JWT no header:  
> `Authorization: Bearer {seu-token-aqui}`

#### Criar usuário
```http
POST /usuarios
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "Maria Santos",
  "email": "maria@email.com",
  "senha": "senha456"
}
```

#### Buscar usuário por ID
```http
GET /usuarios/{id}
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

#### Listar todos os usuários
```http
GET /usuarios
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "email": "maria@email.com"
  }
]
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
  "metaMensal": 2000.00,
  "saldo": 499.50,
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
  "metaMensal": 2000.00,
  "saldo": 1150.00,
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

---

### 💸 Despesas

#### Criar despesa
```http
POST /despesas?idUsuario={id}&idCategoria={id}
Authorization: Bearer {token}
Content-Type: application/json
```

**Exemplo:** `POST /despesas?idUsuario=1&idCategoria=1`

**Body:**
```json
{
  "valor": 150.50,
  "data": "2025-11-16",
  "descricao": "Compras no supermercado"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "valor": 150.50,
  "data": "2025-11-16",
  "descricao": "Compras no supermercado",
  "usuario": {
    "userId": 1,
    "nome": "João Silva",
    "email": "joao@email.com"
  },
  "categoria": {
    "categoriaId": 1,
    "nome": "Alimentação"
  }
}
```

#### Listar despesas do usuário
```http
GET /despesas/usuario/{idUsuario}
Authorization: Bearer {token}
```

**Exemplo:** `GET /despesas/usuario/1`

**Response:**
```json
[
  {
    "id": 1,
    "valor": 150.50,
    "data": "2025-11-16",
    "descricao": "Compras no supermercado",
    "usuario": {
      "userId": 1,
      "nome": "João Silva"
    },
    "categoria": {
      "categoriaId": 1,
      "nome": "Alimentação"
    }
  },
  {
    "id": 2,
    "valor": 50.00,
    "data": "2025-11-15",
    "descricao": "Uber",
    "usuario": {
      "userId": 1,
      "nome": "João Silva"
    },
    "categoria": {
      "categoriaId": 2,
      "nome": "Transporte"
    }
  }
]
```

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

```bash
curl -X POST http://localhost:8080/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "nome": "Transporte"
  }'
```

#### 4. Registrar uma despesa
```bash
curl -X POST "http://localhost:8080/despesas?idUsuario=1&idCategoria=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "valor": 150.50,
    "data": "2025-11-16",
    "descricao": "Compras no supermercado"
  }'
```

#### 5. Listar todas as despesas do usuário
```bash
curl -X GET http://localhost:8080/despesas/usuario/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

#### 6. Ver resumo financeiro do mês
```bash
curl -X GET http://localhost:8080/usuarios/1/resumo/11/2025 \
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
│   │       │   └── UsuarioController.java          # Gerenciamento de usuários
│   │       ├── dtos/                                # Data Transfer Objects
│   │       │   ├── AuthResponseDTO.java            # Resposta de autenticação
│   │       │   ├── LoginDTO.java                   # Dados de login
│   │       │   ├── RegisterDTO.java                # Dados de registro
│   │       │   └── UsuarioDTO.java                 # Dados de usuário
│   │       ├── entities/                            # Entidades JPA
│   │       │   ├── Categoria.java                  # Entidade Categoria
│   │       │   ├── Despesa.java                    # Entidade Despesa
│   │       │   └── Usuario.java                    # Entidade Usuario
│   │       ├── exception/                           # Tratamento de exceções
│   │       │   └── GlobalExceptionHandler.java     # Handler global
│   │       ├── repositories/                        # Camada de persistência
│   │       │   ├── CategoriaRepository.java
│   │       │   ├── DespesaRepository.java
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
| categoriaId | Integer (PK) | ID único da categoria |
| nome | String | Nome da categoria |

### Entidade: Despesa
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Integer (PK) | ID único da despesa |
| valor | BigDecimal | Valor da despesa |
| data | LocalDate | Data da despesa |
| descricao | String | Descrição opcional |
| usuario_id | Integer (FK) | Referência ao usuário |
| categoria_id | Integer (FK) | Referência à categoria |

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

- [ ] Paginação nos endpoints de listagem
- [ ] Filtros avançados de despesas (por período, categoria, valor)
- [ ] Dashboard com gráficos de gastos
- [ ] Exportação de relatórios (PDF, Excel)
- [ ] Notificações quando ultrapassar a meta
- [ ] Categorias customizadas por usuário
- [ ] Receitas além de despesas
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

