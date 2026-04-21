# ToolsChallenge

Este projeto implementa uma API de pagamentos baseada em um cenário de operações com cartão de crédito. A aplicação foi
desenvolvida com **Spring Boot**, utilizando banco de dados em memória **H2** e arquitetura em camadas (controller,
service, repository, DTOs e assemblers).

A API permite realizar pagamentos, consultar transações, listar transações com paginação e efetuar estornos. O objetivo
foi simular um sistema de adquirência de forma simples, mantendo clareza nas regras de negócio e no tratamento de erros.

---

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- JUnit
- Mockito

---

## Endpoints

### Pagamento de uma transação

POST /transacoes/pagamento

### Consulta de uma transação por ID

GET /transacoes/{id}

### Consulta de todas as transações com paginação

GET /transacoes?page=0

### Estorno de uma transação

POST /transacoes/estorno/{id}

---

## Testes

Em caso de erros de entrada (valor em formato inválido ou campos obrigatórios ausentes) é lançada uma exception
específica e o fluxo é interrompido.

Em casos onde as informações estão "corretas", porém há erros nas regras de negócio (valor menor ou igual a zero,
inconsistência entre tipo de pagamento e parcelas) a transação é processada normalmente porém com status NEGADO.

### Foram implementados testes unitários para a camada de service:

- autorização de transações válidas
- negação por regras de negócio
- exceções para dados inválidos
- idempotência
- fluxo de estorno

### Executar testes

```bash
  mvn test
```
