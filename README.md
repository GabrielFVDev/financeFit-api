# FinanceFit API 💰

API RESTful para controle de finanças pessoais desenvolvida com Spring Boot.

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

Edite o arquivo `src/main/resources/application.properties`:

```properties
# Configuração do banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/financefit?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 3. Execute o projeto

#### Usando Maven Wrapper (Linux/Mac):
```bash
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

## 📚 Documentação da API

### 👤 Usuários (`/usuarios`)

#### Criar usuário
```http
POST /usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

**Resposta de Erro (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Erro de validação",
  "errors": {
    "email": "deve ser um endereço de e-mail válido",
    "senha": "tamanho deve estar entre 6 e 20"
  },
  "timestamp": "2025-11-09T10:30:00"
}
```

#### Listar todos os usuários
```http
GET /usuarios
```

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

