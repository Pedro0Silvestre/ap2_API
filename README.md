# 🛒 Sistema de Gerenciamento de Pedidos - API REST

Este projeto consiste em uma API REST robusta desenvolvida em **Java** com o framework **Spring Boot** , **LOMBOK** para a avaliação AP2. O sistema foi projetado utilizando persistência de dados com **JDBC Puro** (`JdbcTemplate`), banco de dados **MySQL** e documentação automatizada via **Swagger (OpenAPI)**.

---

## 📝 1. O Mini-Mundo

O sistema aborda o fluxo comercial básico de um e-commerce ou sistema de vendas interno. Ele gerencia o relacionamento entre três entidades principais:

* **Clientes:** Usuários que realizam compras no sistema. Cada cliente possui um identificador único, nome e e-mail (que deve ser estritamente exclusivo).
* **Produtos:** Itens disponíveis no catálogo da loja. Cada produto possui um identificador, nome e preço de venda (armazenado com precisão monetária).
* **Pedidos:** O fechamento de uma compra. Um pedido vincula **um único cliente** a **um ou múltiplos produtos** (caracterizando um relacionamento de Muitos-para-Muitos ($N:N$)), além de registrar automaticamente a data/hora exata da transação e o status atual do pedido (ex: `AGUARDANDO_PAGAMENTO`).

---

## 📁 2. Estrutura do Projeto e Arquitetura

O projeto adota o padrão de arquitetura em camadas (**Layered Architecture**). A separação de responsabilidades foi dividida estritamente em quatro pacotes principais dentro de `com.example.ap2`:

```text
src/main/java/com/example/ap2/
├── controller/     # Porta de entrada da API (Exposição dos Endpoints HTTP)
├── service/        # O cérebro do sistema (Regras de Negócio e Validações)
├── repository/     # Comunicação com o banco de dados (Consultas SQL via JDBC)
└── model/          # Representação das entidades e dados (Objetos de Domínio)
