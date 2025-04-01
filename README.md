# 🏦 AltBank - Sistema de Gerenciamento de Clientes, Contas e Cartões

Este projeto simula um sistema bancário simples, com foco na gestão de **clientes**, **contas bancárias** e **cartões** (físicos e virtuais), além do controle de **entregas de cartões**.

A aplicação foi desenvolvida em **Java 17** utilizando o **Quarkus Framework**, com arquitetura em camadas, separação de responsabilidades e foco em testes automatizados.

---

## ✅ Funcionalidades Implementadas

- Cadastro, consulta, atualização e exclusão lógica de clientes
- Criação e listagem de contas bancárias
- Criação, ativação e inativação de cartões físicos e virtuais
- Controle de entrega física de cartões
- Regras de negócio como:
    - Verificação de entrega antes da criação de cartão virtual
    - Limite de cartões físicos por conta
- Cobertura de testes: unidade e integração

---

## 🛠 Tecnologias Utilizadas

- **Java 17**
- **Quarkus (RESTEasy, Panache ORM, JPA)**
- **MySQL** (via Docker)
- **Flyway** (migração automática de banco de dados)
- **Mockito**, **JUnit 5**
- **RestAssured** (testes HTTP)
- **Swagger (OpenAPI)**
- **Lombok**

---

## 🚀 Como Rodar o Projeto

1. **Clonar o repositório**

   Acesse [https://github.com/eafigueira/altbank-teste](https://github.com/eafigueira/altbank-teste) ou execute:

2. **Subir o banco de dados com Docker**

O projeto já vem com um `docker-compose.yml` que levanta uma instância MySQL:
```bash
docker-compose up -d
```

3. **Executar o projeto**

```bash
./mvnw clean package quarkus:dev
```

4. **Acessar a documentação**

A api tem um swagger em: `/q/swagger-ui/` ou acesse http://localhost:8080/q/swagger-ui/