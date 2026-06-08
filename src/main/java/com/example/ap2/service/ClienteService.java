package com.example.ap2.service;

import com.example.ap2.model.Cliente;
import com.example.ap2.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {
//aplicacao de validacoes e regras de negocion

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //REGRAS DE CADASTRO
    public void cadastrar(Cliente cliente) {
        //1- campos obrigatorios email e nome
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()){
            throw new IllegalArgumentException("nome do cliente obrigatorio");
        }
        if (cliente.getEmail() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("email do cliente obrigatorio");
        }

        //REGRA DE NEGOCIO NAO PODEM TER EMAILS DUPLICADOS
        List<Cliente> clientes = clienteRepository.readAll();

        for (Cliente c : clientes) {
            if (c.getEmail().equalsIgnoreCase(cliente.getEmail())) {
                throw new RuntimeException("Email ja cadastrado");
            }
        }

        clienteRepository.create(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.readAll();
    }

    public Cliente buscarPorId(Long id) {
        // verificar se id esta cadastrado no banco
        Cliente cliente = clienteRepository.readById(id);

        if (cliente == null) {
            throw  new RuntimeException("Id nao registrado");
        }

        return cliente;
    }

    public void atualizar(Long id, Cliente clienteAtualizado) {
        //verificar se cliente esta no banco
        this.buscarPorId(id);

        int linhasAfetadas = clienteRepository.updateById(id, clienteAtualizado);
        if (linhasAfetadas == 0) {
            throw new RuntimeException("Nao foi possivel atualizar clinete");
        }
    }

    public void deletar(Long id) {
        //verificar se cliente existe
        this.buscarPorId(id);

        int linhasAfetadas = clienteRepository.deleteById(id);
        if (linhasAfetadas == 0) {
            throw new RuntimeException("Nao foi possivel excluir cliente");
        }
    }
}
