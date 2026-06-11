package com.example.ap2.controller;

import com.example.ap2.model.Produto;
import com.example.ap2.repository.ProdutoRepository;
import com.example.ap2.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento do catálogo de produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    //cadastro de produto
    @PostMapping
    @Operation(summary = "Cadastrar um novo produto", description = "Insere um produto no banco após validar o nome e o preço")
    public ResponseEntity<String> cadastrar(@RequestBody Produto produto) {
        try {
            produtoService.cadastrar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Produto cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            // Retorna 400 se o preço for menor/igual a zero ou nome estiver vazio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2. ROTA PARA LISTAR TODOS (GET)
    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    // 3. ROTA PARA BUSCAR POR ID (GET BY ID)
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os dados de um produto específico informando o ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Produto produto = produtoService.buscarPorId(id);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 4. ROTA PARA ATUALIZAR (PUT)
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto", description = "Permite atualização total ou parcial (mesclagem inteligente via Service)")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        try {
            produtoService.atualizar(id, produto);
            return ResponseEntity.ok("Produto atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 5. ROTA PARA EXCLUIR (DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um produto", description = "Remove o produto do catálogo do banco de dados")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            produtoService.excluir(id);
            return ResponseEntity.ok("Produto removido com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
