# FinanceFit API 💰

API RESTful para controle de finanças pessoais desenvolvida com Spring Boot com autenticação JWT.

## 🏗️ Tecnologias

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security 6.x**
- **JWT (JSON Web Token)**
- **JPA/Hibernate**
- **MySQL 8.0**
- **Maven**
- **Bean Validation**

## 📋 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **MySQL 8.0+** (ou outro banco de dados compatível)
- **Git** (para clonar o repositório)

## 🚀 Como executar o projeto

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd financeFit
```

### 2. Configure o banco de dados

**Crie o banco de dados no MySQL:**
```sql
CREATE DATABASE financefit;
```

**Configure o arquivo `src/main/resources/application.properties`:**

```properties
spring.application.name=financeFit

# Configuracao do banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/financefit
spring.datasource.username=root
spring.datasource.password=sua_senha_aqui

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=financefit_secret_key_2025_muito_segura_e_complexa_para_producao_mudar_isso
jwt.expiration=86400000
```

⚠️ **IMPORTANTE**: 
- Altere `sua_senha_aqui` pela senha do seu MySQL
- Em produção, altere a `jwt.secret` para uma chave mais segura

### 3. Execute o projeto

#### Usando Maven Wrapper (Linux/Mac):
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

#### Usando Maven Wrapper (Windows):
```bash
mvnw.cmd spring-boot:run
```

#### Usando Maven instalado:
```bash
mvn spring-boot:run
```

### 4. Acesse a aplicação

A API estará disponível em: `http://localhost:8080`

---

## 🔐 Autenticação JWT

A API utiliza JWT (JSON Web Token) para autenticação. Todas as rotas, exceto as de autenticação, requerem um token válido.

### 📍 Endpoints de Autenticação (Públicos)

#### 🔓 Registrar usuário
```http
POST /api/auth/register
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "metaMensal": 5000.00
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

#### 🔓 Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

---

## 📚 Endpoints Protegidos (Requerem Token)

**Para acessar os endpoints abaixo, inclua o token no header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 👤 Usuários

#### Listar todos os usuários
```http
GET /api/usuarios
Authorization: Bearer {seu_token}
```

#### Buscar usuário por ID
```http
GET /api/usuarios/{id}
Authorization: Bearer {seu_token}
```

#### Atualizar usuário
```http
PUT /api/usuarios/{id}
Authorization: Bearer {seu_token}
Content-Type: application/json

{
  "nome": "João Silva Santos",
  "email": "joao.santos@email.com",
  "senha": "novaSenha123"
}
```

#### Deletar usuário
```http
DELETE /api/usuarios/{id}
Authorization: Bearer {seu_token}
```

### 🏷️ Categorias

#### Listar categorias
```http
GET /api/categorias
Authorization: Bearer {seu_token}
```

#### Criar categoria
```http
POST /api/categorias
Authorization: Bearer {seu_token}
Content-Type: application/json

{
  "nome": "Alimentação",
  "descricao": "Gastos com alimentação"
}
```

### 💸 Despesas

#### Listar despesas
```http
GET /api/despesas
Authorization: Bearer {seu_token}
```

#### Criar despesa
```http
POST /api/despesas
Authorization: Bearer {seu_token}
Content-Type: application/json

{
  "descricao": "Almoço",
  "valor": 25.50,
  "data": "2025-11-15",
  "categoriaId": 1,
  "usuarioId": 1
}
```

---

## 🧪 Testando a API

### 1. **Teste com cURL**

**Registrar usuário:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Test User",
    "email": "test@example.com", 
    "senha": "123456",
    "metaMensal": 3000.0
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "senha": "123456"
  }'
```

**Usar token (substitua `SEU_TOKEN` pelo token recebido):**
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer SEU_TOKEN"
```

### 2. **Teste com Postman**

1. Importe a collection (se disponível)
2. Configure o ambiente com a URL base: `http://localhost:8080`
3. Faça login e copie o token
4. Adicione o token no header `Authorization` como `Bearer {token}`

---

## 🛠️ Resolução de Problemas

### ❌ Erro: "Access denied for user 'root'@'localhost'"

**Causa**: Credenciais do MySQL incorretas.

**Solução**:
```bash
# 1. Verifique se o MySQL está rodando
sudo systemctl status mysql

# 2. Teste a conexão
mysql -u root -p

# 3. Atualize as credenciais em application.properties
```

### ❌ Erro: "Unknown database 'financefit'"

**Causa**: Banco de dados não existe.

**Solução**:
```sql
-- Conecte no MySQL e execute:
CREATE DATABASE financefit;
```

### ❌ Erro: "Port 8080 is already in use"

**Causa**: Porta 8080 já está sendo usada.

**Solução**:
```bash
# Opção 1: Mate o processo na porta 8080
sudo kill -9 $(sudo lsof -t -i:8080)

# Opção 2: Use outra porta em application.properties
server.port=8081
```

### ❌ Erro: "Invalid JWT token"

**Causa**: Token expirado ou inválido.

**Solução**:
1. Faça login novamente para obter um novo token
2. Verifique se está incluindo "Bearer " antes do token
3. Token expira em 24 horas por padrão

### ❌ Erro: "Java 17 or higher required"

**Causa**: Versão do Java incompatível.

**Solução**:
```bash
# Verifique a versão do Java
java -version

# Instale o Java 17 (Ubuntu/Debian)
sudo apt install openjdk-17-jdk

# Configure JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### ❌ Erro de Compilação

**Causa**: Dependências ou código com problemas.

**Solução**:
```bash
# Limpe e recompile
./mvnw clean compile

# Se persistir, limpe o cache do Maven
rm -rf ~/.m2/repository
./mvnw clean compile
```

---

## 🔒 Segurança

- **Senhas**: Criptografadas com BCrypt
- **JWT**: Tokens assinados com chave secreta
- **Validação**: Bean Validation em todos os DTOs
- **CORS**: Configure conforme necessário para produção
- **HTTPS**: Recomendado para produção

---

## 📁 Estrutura do Projeto

```
src/main/java/com/financefit/financeFit/
├── controllers/          # Endpoints da API
├── dtos/                # Data Transfer Objects
├── entities/            # Entidades JPA
├── repositories/        # Repositórios de dados
├── security/           # Configuração JWT e Security
├── services/           # Lógica de negócio
└── exception/          # Tratamento de exceções
```

---

## 👥 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença [MIT](LICENSE).

---

## 📞 Suporte

Em caso de dúvidas ou problemas:
1. Verifique a seção de **Resolução de Problemas**
2. Consulte os logs da aplicação
3. Abra uma issue no repositório

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com"
  }
]
```

#### Buscar usuário por ID
```http
GET /usuarios/1
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

**Resposta de Erro (404 Not Found):**
```json
{
  "status": 404,
  "message": "Usuário não encontrado com ID: 1",
  "timestamp": "2025-11-09T10:30:00"
}
```

#### Buscar usuário por email
```http
GET /usuarios/email/joao@email.com
```

#### Atualizar usuário
```http
PUT /usuarios/1
Content-Type: application/json

{
  "nome": "João Silva Santos",
  "email": "joao.novo@email.com",
  "senha": "novaSenha123"
}
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva Santos",
  "email": "joao.novo@email.com"
}
```

#### Deletar usuário
```http
DELETE /usuarios/1
```

**Resposta (204 No Content):** *(Sem corpo de resposta)*

#### Alterar senha
```http
PATCH /usuarios/1/senha
Content-Type: application/json

{
  "senha": "novaSenha123"
}
```

**Resposta de Erro (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Senha não pode ser vazia",
  "timestamp": "2025-11-09T10:30:00"
}
```

#### Atualizar meta mensal
```http
PATCH /usuarios/1/meta
Content-Type: application/json

{
  "metaMensal": 3000.00
}
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

#### Resumo financeiro (mês atual)
```http
GET /usuarios/1/resumo
```

**Resposta (200 OK):**
```json
{
  "totalGasto": 2400.50,
  "metaMensal": 3000.00,
  "percentualUsado": 80.02,
  "statusMeta": "ALERTA: Próximo do limite!",
  "mes": 11,
  "ano": 2025
}
```

#### Resumo financeiro (período específico)
```http
GET /usuarios/1/resumo/10/2025
```

**Resposta de Erro (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Mês deve estar entre 1 e 12",
  "timestamp": "2025-11-09T10:30:00"
}
```

---

### 📂 Categorias (`/categorias`)

#### Criar categoria
```http
POST /categorias
Content-Type: application/json

{
  "nome": "Alimentação"
}
```

**Resposta (201 Created):**
```json
{
  "categoriaId": 1,
  "nome": "Alimentação"
}
```

**Resposta de Erro (500 Internal Server Error):**
```json
{
  "status": 500,
  "message": "Erro ao criar categoria: constraint violation",
  "timestamp": "2025-11-09T10:30:00"
}
```

#### Listar todas as categorias
```http
GET /categorias
```

**Resposta (200 OK):**
```json
[
  {
    "categoriaId": 1,
    "nome": "Alimentação"
  },
  {
    "categoriaId": 2,
    "nome": "Transporte"
  }
]
```

---

### 💸 Despesas (`/despesas`)

#### Criar despesa
```http
POST /despesas?idUsuario=1&idCategoria=1
Content-Type: application/json

{
  "valor": 150.50,
  "data": "2025-11-09",
  "descricao": "Compras no supermercado"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "valor": 150.50,
  "data": "2025-11-09",
  "descricao": "Compras no supermercado",
  "usuario": {
    "userId": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "dataCriacao": "2025-11-01",
    "metaMensal": 3000.0
  },
  "categoria": {
    "categoriaId": 1,
    "nome": "Alimentação"
  }
}
```

**Resposta de Erro (400 Bad Request):**
```json
{
  "status": 400,
  "message": "ID do usuário inválido",
  "timestamp": "2025-11-09T10:30:00"
}
```

#### Listar despesas do usuário
```http
GET /despesas/usuario/1
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "valor": 150.50,
    "data": "2025-11-09",
    "descricao": "Compras no supermercado",
    "usuario": {
      "userId": 1,
      "nome": "João Silva",
      "email": "joao@email.com",
      "dataCriacao": "2025-11-01",
      "metaMensal": 3000.0
    },
    "categoria": {
      "categoriaId": 1,
      "nome": "Alimentação"
    }
  }
]
```

---

## 🔧 Tecnologias utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **Maven**
- **Jakarta Validation**

---

## 📊 Status Codes da API

| Código | Descrição |
|--------|-----------|
| `200` | **OK** - Requisição bem-sucedida |
| `201` | **Created** - Recurso criado com sucesso |
| `204` | **No Content** - Requisição bem-sucedida sem conteúdo de resposta |
| `400` | **Bad Request** - Dados inválidos ou faltando |
| `404` | **Not Found** - Recurso não encontrado |
| `500` | **Internal Server Error** - Erro interno do servidor |

---

## 🗂️ Estrutura do Projeto

```
financeFit/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/financefit/financeFit/
│   │   │       ├── controllers/        # Endpoints REST
│   │   │       ├── dtos/               # Data Transfer Objects
│   │   │       ├── entities/           # Entidades JPA
│   │   │       ├── exception/          # Tratamento de exceções
│   │   │       ├── repositories/       # Repositories JPA
│   │   │       ├── services/           # Regras de negócio
│   │   │       └── FinanceFitApplication.java
│   │   └── resources/
│   │       └── application.properties  # Configurações
│   └── test/
├── pom.xml                             # Dependências Maven
└── README.md
```

---

## 🧪 Testando a API

Você pode testar a API usando ferramentas como:

- **Postman**: https://www.postman.com/
- **Insomnia**: https://insomnia.rest/
- **cURL** (linha de comando)

### Exemplo com cURL:

```bash
# Criar usuário
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123"
  }'

# Listar categorias
curl http://localhost:8080/categorias

# Criar despesa
curl -X POST "http://localhost:8080/despesas?idUsuario=1&idCategoria=1" \
  -H "Content-Type: application/json" \
  -d '{
    "valor": 150.50,
    "data": "2025-11-09",
    "descricao": "Compras no supermercado"
  }'
```

---

## 🐛 Troubleshooting

### Erro de conexão com o banco de dados
- Verifique se o MySQL está rodando
- Confirme as credenciais no `application.properties`
- Certifique-se de que o banco de dados existe ou use `createDatabaseIfNotExist=true` na URL

### Porta 8080 já está em uso
Altere a porta no `application.properties`:
```properties
server.port=8081
```

### Erro de compilação
Execute:
```bash
mvn clean install
```

---

## 📝 Licença

Este projeto é de código aberto e está disponível sob a licença MIT.

---

## 👨‍💻 Autor

Desenvolvido por Gabriel

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

