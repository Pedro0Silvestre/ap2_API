package com.example.ap2.service;

import com.example.ap2.model.Cliente;
import com.example.ap2.model.Pedido;
import com.example.ap2.model.Produto;
import com.example.ap2.repository.ClienteRepository;
import com.example.ap2.repository.PedidoRepository;
import com.example.ap2.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public void cadastrar(Pedido pedido) {
        //garantir que o cliente esteja associado ao pedido
        if(pedido.getCliente() == null) {
            throw new IllegalArgumentException("pedido precisa de cliente associado");
        }
        //garantir que o cliente esteja no banco
        Cliente cliente = clienteRepository.readById(pedido.getCliente().getId());
        if (cliente == null) {
            throw new RuntimeException("Cliente informado nao existe");
        }
        //pedido deve ter pelo menos um produto
        if(pedido.getProdutos() == null || pedido.getProdutos().isEmpty()){
            throw new IllegalArgumentException("Pedido deve ter pelo menos um pedido associado");
        }
        // que os produtos sxistam
        List<Produto> produtos = pedido.getProdutos();
        for(Produto p : produtos) {
            if(p.getId() < 0) {
                throw new IllegalArgumentException("Id invalido");
            }
            //cadastro
            Produto produtoCadastrado = produtoRepository.readById(p.getId());
            if (produtoCadastrado == null){
                throw new RuntimeException("Produto nao cadastrado no banco");
            }
        }

        //definir data
        pedido.setDataPedido(LocalDateTime.now());
        //status inicial
        pedido.setStatusPedido("CONFIRMADO");

        //se validado pode adicionar
        pedidoRepository.create(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.readAll();
    }

    public void atualizarStatus(Long id, String statusAtualizado) {
        if (statusAtualizado == null || statusAtualizado.trim().isEmpty()) {
            throw new IllegalArgumentException("Status nao pode ser nulo ou vazio");
        }

        String statusFormatado = statusAtualizado.trim().toUpperCase();

        // Reutiliza a lógica de buscar os pedidos para ver se ele existe antes de dar o update
        List<Pedido> todosPedidos = pedidoRepository.readAll();

        // Atualiza o registro enviando o status tratado
        pedidoRepository.updateStatus(id, new Pedido(id, LocalDateTime.now(), statusFormatado, null, null));
    }

    public void excluir(Long id) {
        // Verifica se o pedido existe antes de tentar deletar
        List<Pedido> todosPedidos = pedidoRepository.readAll();
        boolean existe = todosPedidos.stream().anyMatch(p -> p.getId() == id);

        if (!existe) {
            throw new RuntimeException("Pedido com ID " + id + " não encontrado para exclusão.");
        }

        pedidoRepository.deleteById(id);
    }
}

