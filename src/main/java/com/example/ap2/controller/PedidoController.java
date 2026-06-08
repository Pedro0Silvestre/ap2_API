package com.example.ap2.controller;

import com.example.ap2.model.Pedido;
import com.example.ap2.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento e fechamento de pedidos (Vendas)")
public class PedidoController {

    private final PedidoService pedidoService;

    // Injeção de dependência via construtor (SOLID)
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // 1. ROTA PARA FECHAR UM PEDIDO (POST)
    @PostMapping
    @Operation(summary = "Criar/Fechar um novo pedido", description = "Recebe um cliente e uma lista de produtos (basta enviar os IDs) para fechar a venda")
    public ResponseEntity<String> criarPedido(@RequestBody Pedido pedido) {
        try {
            pedidoService.cadastrar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pedido gerado com sucesso e aguardando pagamento!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 2. ROTA PARA LISTAR TODOS OS PEDIDOS COM SEUS DETALHES (GET)
    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Retorna os pedidos trazendo os dados completos do cliente e os produtos de cada carrinho")
    public ResponseEntity<List<Pedido>> listarTodos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    // 3. ROTA PARA ATUALIZAR APENAS O STATUS DO PEDIDO (PUT)
    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de um pedido", description = "Altera o status do pedido informando o ID na URL e o novo status no corpo (Ex: PAGO, ENVIADO, CANCELADO)")
    public ResponseEntity<String> atualizarStatus(@PathVariable int id, @RequestBody String novoStatus) {
        try {
            pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok("Status do pedido atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 4. ROTA PARA EXCLUIR UM PEDIDO (DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um pedido por ID", description = "Remove o pedido e limpa seus vínculos na tabela associativa item_pedido")
    public ResponseEntity<String> excluir(@PathVariable int id) {
        try {
            pedidoService.excluir(id);
            return ResponseEntity.ok("Pedido excluído com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}