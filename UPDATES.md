# 🚀 Backend API Improvements for FinanceFit

This document outlines suggested improvements and missing endpoints in the FinanceFit API to better support the frontend application's functionality.

## 1. Income Management

**Current Status:** The API currently only supports expense (`Despesa`) creation and listing. There is no explicit support for managing income.

**Suggested Endpoints:**

*   **Create Income:**
    *   **Endpoint:** `POST /receitas`
    *   **Description:** Allows authenticated users to record income.
    *   **Request Body Example:**
        ```json
        {
          "valor": 1000.00,
          "data": "YYYY-MM-DD",
          "descricao": "Salário mensal",
          "idUsuario": 1, // Or derive from JWT
          "idCategoria": 5 // Optional, for income categories
        }
        ```
    *   **Response Example (201 Created):**
        ```json
        {
          "id": 1,
          "valor": 1000.00,
          "data": "YYYY-MM-DD",
          "descricao": "Salário mensal",
          "usuario": { ... },
          "categoria": { ... }
        }
        ```

*   **List User Incomes:**
    *   **Endpoint:** `GET /receitas/usuario/{idUsuario}`
    *   **Description:** Retrieves a list of all income records for a specific user.
    *   **Response Example:**
        ```json
        [
          {
            "id": 1,
            "valor": 1000.00,
            "data": "YYYY-MM-DD",
            "descricao": "Salário mensal",
            "usuario": { ... },
            "categoria": { ... }
          }
        ]
        ```

*   **Update Income:**
    *   **Endpoint:** `PUT /receitas/{id}`
    *   **Description:** Updates an existing income record.
    *   **Request Body Example:** (Similar to create, but with ID)

*   **Delete Income:**
    *   **Endpoint:** `DELETE /receitas/{id}`
    *   **Description:** Deletes an income record.

**Impact on Frontend:** Implementing these endpoints would allow the frontend to:
*   Provide a dedicated option to add income in the "Nova Transação" form.
*   Display income separately from expenses in transaction lists and summaries.
*   Accurately calculate total income and overall balance.

## 2. Enhanced Financial Summary

**Current Status:** The `/usuarios/{id}/resumo` and `/usuarios/{id}/resumo/{mes}/{ano}` endpoints provide `totalDespesas`, `metaMensal`, `saldo`, and `percentualGasto`. The `saldo` is calculated based on expenses and `metaMensal`.

**Suggested Improvement:**

*   **Include `totalReceitas` in Summary Endpoints:**
    *   **Endpoint:** `GET /usuarios/{id}/resumo` and `GET /usuarios/{id}/resumo/{mes}/{ano}`
    *   **Description:** Modify the existing summary endpoints to also return `totalReceitas` (total income) for the period. This would allow for a more accurate and explicit calculation of the balance on the frontend.
    *   **Response Example (Updated):**
        ```json
        {
          "totalDespesas": 1500.50,
          "totalReceitas": 2000.00, // New field
          "metaMensal": 2000.00,
          "saldo": 499.50, // Should now be totalReceitas - totalDespesas
          "percentualGasto": 75.03
        }
        ```

**Impact on Frontend:** This would enable the frontend to:
*   Display total income explicitly in the dashboard summary cards.
*   Ensure the `saldo` calculation is based on both income and expenses, not just expenses against a monthly goal.

## 3. Transaction Type in Expense/Income Response

**Current Status:** The `Despesa` entity does not explicitly carry a "type" field (e.g., 'income' or 'expense'). The frontend currently infers this based on the endpoint used (e.g., `/despesas` implies 'expense').

**Suggested Improvement:**

*   **Add `tipo` field to `Despesa` and `Receita` entities:**
    *   **Description:** Include a `tipo` (type) field (e.g., `String` or `Enum` with values like "DESPESA", "RECEITA") in the `Despesa` and `Receita` entities. This would make it explicit whether a transaction is an income or an expense directly from the API response.
    *   **Impact on Frontend:** Simplifies rendering logic for transaction lists, allowing a single list to display both incomes and expenses with clear differentiation.

## 4. CRUD Operations for Categories

**Current Status:** The API currently supports `POST /categorias` (Create) and `GET /categorias` (List).

**Suggested Endpoints:**

*   **Update Category:**
    *   **Endpoint:** `PUT /categorias/{id}`
    *   **Description:** Updates an existing category's name.
    *   **Request Body Example:**
        ```json
        {
          "nome": "Nova Categoria"
        }
        ```

*   **Delete Category:**
    *   **Endpoint:** `DELETE /categorias/{id}`
    *   **Description:** Deletes a category. (Consider cascading effects on associated expenses/incomes).

**Impact on Frontend:** Allows users to fully manage their categories, including renaming and removing them.

## 5. CRUD Operations for Expenses

**Current Status:** The API currently supports `POST /despesas` (Create) and `GET /despesas/usuario/{idUsuario}` (List).

**Suggested Endpoints:**

*   **Update Expense:**
    *   **Endpoint:** `PUT /despesas/{id}`
    *   **Description:** Updates an existing expense record.
    *   **Request Body Example:** (Similar to create, but with ID)

*   **Delete Expense:**
    *   **Endpoint:** `DELETE /despesas/{id}`
    *   **Description:** Deletes an expense record.

**Impact on Frontend:** Enables users to edit and remove their financial transactions, providing full control over their data.
