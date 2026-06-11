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
````

## O que fica em cada pasta e o porquê desta estratégia?
*model/* O que fica: Classes Java puras (POJOs/Entities) como Cliente, Produto e Pedido.
- O porquê: Representam as tabelas do banco de dados e os objetos que trafegam entre as camadas. Centralizam a estrutura de dados da aplicação.
  
*repository/* O que fica: Classes de persistência executando queries SQL brutas através do JdbcTemplate.
- O porquê: Isolar o código SQL do restante da aplicação. Se no futuro o banco de dados mudar de MySQL para PostgreSQL ou migrar para um ORM (como Hibernate), apenas este pacote precisará ser alterado, sem impactar as regras de negócio.
  
*service/* O que fica: As regras de negócio, checagens de dados e validações (ex: impedir preços negativos ou e-mails duplicados).
- O porquê: Evita que dados corrompidos cheguem ao banco de dados. Centralizar a lógica aqui impede que o Controller fique sobrecarregado com regras de validação e que o Repository precise entender o contexto do negócio.
  
*controller/* O que fica: Endpoints REST que escutam os verbos HTTP (GET, POST, PUT, DELETE).
- O porquê: Atua como o intermediário entre a Web e o sistema. Sua única função é receber o JSON da requisição, repassar para o Service tratar e devolver o código de status HTTP correto (ex: 201 Created, 400 Bad Request, 404 Not Found).

## 🏛️ 3. Princípios de Engenharia de Software Implementados
Para garantir a manutenibilidade, legibilidade e extensibilidade exigidas em ambiente profissional, o projeto implementa rigorosamente padrões de projeto (Design Patterns), princípios SOLID e práticas de Clean Code.
### 🔄 Design Patterns (Padrões de Projeto)
- A. Dependency Injection (Injeção de Dependência)Em vez de permitir que as classes instanciem suas próprias dependências usando o operador new (o que geraria alto acoplamento e impossibilitaria testes unitários), o framework Spring injeta automaticamente as instâncias necessárias através do construtor.
Exemplo:
````code
(PedidoController.java):Java@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    // O Spring localiza e injeta o PedidoService de forma automática aqui (SOLID - DIP)
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
}
````
B. Data Mapper / RowMapperUtilizado para traduzir as linhas tabulares retornadas do banco de dados relacional (ResultSet do SQL) diretamente em objetos Java fortemente tipados, isolando a infraestrutura de dados do domínio da aplicação.Exemplo no Código:
````code
(PedidoRepository.java):JavaList<Produto> produtos = jdbcTemplate.query(sqlProdutos, (rsProd, rowProd) -> {
    Produto prod = new Produto();
    prod.setId(rsProd.getLong("id"));
    prod.setNome(rsProd.getString("nome"));
    prod.setPreco(rsProd.getBigDecimal("preco"));
    return prod;
}, pedido.getId());
````

### 🧮 Princípios SOLID
*Single Responsibility Principle - SRP* (Princípio da Responsabilidade Única): Cada classe possui um único motivo para mudar. O Controller não escreve SQL, o Repository não valida dados de negócio e o Service não conhece rotas HTTP ou anotações do Swagger.
- Exemplo Prático: Se a regra de validação do preço do produto mudar, apenas a classe ProdutoService é editada. O ProdutoController e o ProdutoRepository permanecem intactos.
- *Dependency Inversion Principle - DIP (Princípio da Inversão de Dependência)* Módulos de alto nível (Controllers) não dependem de módulos de baixo nível (Repositories) de forma direta; ambos dependem da camada intermediária de abstração e negócio (Services). O acoplamento é direcionado para o topo da arquitetura, facilitando a manutenção.

## 🧼 Práticas de Clean Code (Código Limpo)
A. Fail-Fast (Falhe Rápido) e Proteção contra NullPointerExceptionAs validações são colocadas logo no início dos métodos. Se houver alguma inconsistência ou campo nulo obrigatório, o sistema interrompe o processamento imediatamente disparando uma exceção, poupando recursos e evitando chamadas inválidas na memória.Exemplo no Código: 

````code
(ProdutoService.java):Javapublic void cadastrar(Produto produto) {
    if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
        throw new IllegalArgumentException("nome e um campo obrigatorio");
    }

    // Operador curto-circuito protege contra NullPointerException antes de chamar o compareTo
    if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("precos devem ser maiores que zero");
    }

    produtoRepository.create(produto);
}
````
B. Atualização Parcial com Mesclagem Inteligente (Merge de Estado)Em rotas de atualização (PUT), se o usuário omitir um campo no JSON por querer alterar apenas uma propriedade específica, o sistema busca o registro atual estável do banco e preenche as lacunas nulas automaticamente. Isso impede que o SQL estático sobrescreva dados existentes com NULL devido à restrição NOT NULL do banco de dados.Exemplo no Código
````code
(ClienteService.java):Javapublic void atualizar(Long id, Cliente clienteAtualizado) {
    // Valida a existência e captura o estado atual do registro no MySQL
    Cliente clienteSalvoNoBanco = this.buscarPorId(id);

    Cliente clienteFinal = new Cliente();
    clienteFinal.setId(id);

    // Foco na Validação: Se veio nome novo, usa o novo. Se veio null, resgata o do banco!
    if (clienteAtualizado.getNome() != null && !clienteAtualizado.getNome().trim().isEmpty()) {
        clienteFinal.setNome(clienteAtualizado.getNome());
    } else {
        clienteFinal.setNome(clienteSalvoNoBanco.getNome());
    }

    // Repete o comportamento protetor para o e-mail
    if (clienteAtualizado.getEmail() != null && !clienteAtualizado.getEmail().trim().isEmpty()) {
        clienteFinal.setEmail(clienteAtualizado.getEmail());
    } else {
        clienteFinal.setEmail(clienteSalvoNoBanco.getEmail());
    }

    clienteRepository.updateById(id, clienteFinal);
}
````

C. Orquestração Segura de Dados Relacionais $N:N$  Ao criar um pedido, o PedidoService assume o controle total da integridade da requisição: ele intercepta os IDs crus passados no JSON do Swagger, valida se pertencem a entidades reais e automatiza o preenchimento de campos protegidos pelo servidor antes de delegar a persistência à tabela associativa.Exemplo no Código
````code
(PedidoService.java):JavapedidoEnviado.setCliente(clienteBanco);
pedidoEnviado.setProdutos(produtosValidados);

// Controle rigoroso de dados gerados internamente pelo backend
pedidoEnviado.setDataPedido(LocalDateTime.now()); 
pedidoEnviado.setStatusPedido("AGUARDANDO_PAGAMENTO");
````
## 🛠️ 4. Como Executar o Projeto
Certifique-se de ter o Java 17 (ou superior) instalado e o servidor MySQL ativo na sua máquina.Configure o seu esquema e credenciais locais no arquivo src/main/resources/application.properties:Propertiesspring.datasource.url=jdbc:mysql://localhost:3306/loja?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=seu_usuario_root
spring.datasource.password=sua_senha_do_mysql
Execute o projeto através do botão de Play na sua IDE de preferência (IntelliJ IDEA ou VS Code).Certifique-se de que o console exibiu a mensagem de sucesso indicando a inicialização do Tomcat na porta 8080.Abra o seu navegador e acesse a documentação interativa do Swagger para realizar os testes das rotas:👉 http://localhost:8080/swagger-ui/index.html
