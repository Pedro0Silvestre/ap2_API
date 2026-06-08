package com.example.ap2.controller;

import com.example.ap2.model.Cliente;
import com.example.ap2.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes") // Organização do Swagger
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //cadastro
    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente", description = "Insere um cliente no banco após validar o e-mail")
    public ResponseEntity<String> cadastrar(@RequestBody Cliente cliente) {
        try {
            clienteService.cadastrar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cliente cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    //listar Todos
    @GetMapping
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes do banco")
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    // BUSCAR POR ID (GET BY ID)
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente específico informando o ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 4. ROTA PARA ATUALIZAR (PUT)
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente", description = "Permite atualização total ou parcial (apenas nome ou apenas e-mail)")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            clienteService.atualizar(id, cliente);
            return ResponseEntity.ok("Cliente atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 5. ROTA PARA EXCLUIR (DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um cliente", description = "Remove o cliente do banco de dados informando o ID")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            clienteService.deletar(id);
            return ResponseEntity.ok("Cliente removido com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
